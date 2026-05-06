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
package org.makeacake.craft.recipe

import org.bukkit.inventory.Recipe

object RecipeRegistry {

    private val recipes = mutableMapOf<Recipe, CustomRecipe>()

    /**
     * Registers a [CustomRecipe] into the internal storage.
     *
     * @param recipe The custom recipe instance to be indexed.
     */
    fun register(recipe: CustomRecipe) {
        recipes[recipe.recipe] = recipe
    }

    /**
     * Retrieves a [CustomRecipe] by its underlying Bukkit [Recipe] instance.
     *
     * @param recipe The standard Bukkit recipe to look up.
     * @return The associated [CustomRecipe], or null if no mapping exists.
     */
    fun byRecipe(recipe: Recipe?): CustomRecipe? {
        if (recipe == null) return null
        return recipes[recipe]
    }

    /**
     * Extracts all unique [org.bukkit.NamespacedKey]s from registered shaped recipes.
     *
     * This is commonly used to manage recipe discovery and visibility 
     * in the player's recipe book.
     *
     * @return A list of namespaced keys for all registered shaped recipes.
     */
    fun getAllKeys(): List<org.bukkit.NamespacedKey> {
        return recipes.keys.filterIsInstance<org.bukkit.inventory.ShapedRecipe>()
            .map { it.key }
    }
}