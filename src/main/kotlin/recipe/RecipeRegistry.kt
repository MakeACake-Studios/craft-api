package org.makeacake.craft.recipe

import org.bukkit.Bukkit
import org.bukkit.Keyed
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
    private val craftRecipes = mutableMapOf<NamespacedKey, CustomCraftRecipe>()
    private val brewMixKeys = mutableSetOf<NamespacedKey>()
    private val brewingRecipeKeys = mutableSetOf<NamespacedKey>()

    /**
     * Registers a [CustomRecipe] into the internal storage.
     *
     * @param recipe The custom recipe instance to be indexed.
     */
    fun register(recipe: CustomRecipe) {
        if (recipe is CustomCraftRecipe) {
            val key = (recipe.recipe as? Keyed)?.key
                ?: throw IllegalArgumentException("Craft recipe must have a namespaced key")
            require(key !in craftRecipes) { "Recipe $key is already registered" }
            craftRecipes[key] = recipe
        }
        recipes.add(recipe)
    }

    /**
     * Returns a snapshot of all registered custom recipes, including crafting and brewing recipes.
     * Changes to the returned list do not affect the registry.
     */
    fun allRecipes(): List<CustomRecipe> = recipes.toList()

    /**
     * Retrieves a [CustomCraftRecipe] by the namespaced key of a Bukkit [Recipe].
     *
     * @param recipe The standard Bukkit recipe to look up.
     * @return The associated [CustomCraftRecipe], or null if no mapping exists.
     */
    fun byRecipe(recipe: Recipe?): CustomCraftRecipe? {
        if (recipe == null) return null
        return craftRecipes[(recipe as? Keyed)?.key]
    }

    internal fun hasCraftRecipe(key: NamespacedKey): Boolean = key in craftRecipes

    internal fun registerBrewRecipeKey(key: NamespacedKey, modern: Boolean) {
        if (modern) brewingRecipeKeys.add(key) else brewMixKeys.add(key)
    }

    fun matchBrew(ingredient: ItemStack?, base: ItemStack?): CustomBrewRecipe? {
        return recipes.filterIsInstance<CustomBrewRecipe>()
            .firstOrNull { it.matches(ingredient, base) }
    }

    private var brewingRecipeCounter = 0

    /**
     * Generates a unique [NamespacedKey] for registering a brewing recipe.
     *
     * A distinct key is required per mix, since a single result [org.makeacake.craft.item.CustomItem]
     * may be produced by several different (ingredient, base) combinations.
     */
    internal fun nextBrewingRecipeKey(): NamespacedKey {
        return NamespacedKey(CraftAPI.plugin, "brew_mix_${brewingRecipeCounter++}")
    }

    internal fun clear() {
        craftRecipes.keys.forEach(Bukkit::removeRecipe)
        brewingRecipeKeys.forEach(Bukkit::removeRecipe)
        if (brewMixKeys.isNotEmpty()) {
            val brewer = Bukkit.getPotionBrewer()
            brewMixKeys.forEach(brewer::removePotionMix)
        }
        recipes.clear()
        craftRecipes.clear()
        brewMixKeys.clear()
        brewingRecipeKeys.clear()
        brewingRecipeCounter = 0
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
        return craftRecipes.values.mapNotNull { (it.recipe as? ShapedRecipe)?.key }
    }
}
