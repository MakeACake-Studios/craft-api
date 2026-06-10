package org.makeacake.craft.recipe

import org.makeacake.craft.item.CustomItem

/**
 * A universal base interface for all custom recipes.
 *
 * This allows unifying the registration process and makes it trivial
 * to add new recipe types (e.g., Furnace, Smithing, Brewing) in the future.
 *
 * @author nalart11
 * @since 1.1.0
 */
interface CustomRecipe {
    /**
     * The custom item that serves as the final result of this recipe.
     */
    val item: CustomItem
}