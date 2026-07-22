package org.makeacake.craft.recipe

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.makeacake.craft.CraftAPI

/**
 * A centralized registry for managing all custom crafting recipes within the server.
 *
 * This singleton facilitates the mapping between standard Bukkit [Recipe] objects
 * and their corresponding [CustomRecipe] wrappers, ensuring that custom logic
 * and metadata are preserved during the crafting process.
 *
 * @author nalart11
 * @since 1.0.0
 */
object RecipeRegistry {

    private val recipes = mutableListOf<CustomRecipe>()
    private val craftRecipes = mutableMapOf<Recipe, CustomCraftRecipe>()

    /**
     * Registers a [CustomRecipe] into the internal storage.
     *
     * @param recipe The custom recipe instance to be indexed.
     */
    fun register(recipe: CustomRecipe) {
        recipes.add(recipe)
        if (recipe is CustomCraftRecipe) {
            craftRecipes[recipe.recipe] = recipe
        }
    }

    /**
     * Retrieves a [CustomCraftRecipe] by its underlying Bukkit [Recipe] instance.
     *
     * @param recipe The standard Bukkit recipe to look up.
     * @return The associated [CustomCraftRecipe], or null if no mapping exists.
     */
    fun byRecipe(recipe: Recipe?): CustomCraftRecipe? {
        if (recipe == null) return null
        return craftRecipes[recipe]
    }

    fun matchBrew(ingredient: ItemStack?, base: ItemStack?): CustomBrewRecipe? {
        return recipes.filterIsInstance<CustomBrewRecipe>()
            .firstOrNull { it.matches(ingredient, base) }
    }

    private var brewMixCounter = 0

    /**
     * Generates a unique [NamespacedKey] for registering a [io.papermc.paper.potion.PotionMix].
     *
     * A distinct key is required per mix, since a single result [org.makeacake.craft.item.CustomItem]
     * may be produced by several different (ingredient, base) combinations.
     */
    internal fun nextBrewMixKey(): NamespacedKey {
        return NamespacedKey(CraftAPI.plugin, "brew_mix_${brewMixCounter++}")
    }

    /**
     * Extracts all unique [org.bukkit.NamespacedKey]s from registered shaped recipes.
     *
     * This is commonly used to manage recipe discovery and visibility
     * in the player's recipe book.
     *
     * @return A list of namespaced keys for all registered shaped recipes.
     */
    fun getAllKeys(): List<NamespacedKey> {
        return craftRecipes.keys.filterIsInstance<ShapedRecipe>()
            .map { it.key }
    }
}