package org.makeacake.craft.recipe

/**
 * Defines the category of a custom crafting recipe.
 *
 * This enumeration identifies the structural format of the recipe, determining
 * whether it requires a specific grid pattern (Shaped) or simply a collection 
 * of ingredients (Shapeless).
 *
 * @author nalart11
 * @since 1.0.0
 */

enum class RecipeType {
    SHAPED,
    SHAPELESS
}