package org.makeacake.craft.recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.makeacake.craft.action.BrewAction
import org.makeacake.craft.item.CustomItem
import org.makeacake.craft.item.ItemRegistry
import recipe.BrewBaseMatcher

class CustomBrewRecipe(
    override val item: CustomItem,
    val ingredientMaterial: Material?,
    val ingredientCustomItem: CustomItem?,
    val baseMaterial: Material?,
    val baseCustomItem: CustomItem?,
    val brewActions: List<BrewAction>
) : CustomRecipe {
    fun matches(ingredient: ItemStack?, base: ItemStack?): Boolean {
        if (ingredient == null || base == null) return false

        val ingredientMatches = when {
            ingredientCustomItem != null -> ingredientCustomItem.matches(ingredient)
            ingredientMaterial != null -> ingredient.type == ingredientMaterial
            else -> false
        }

        val baseMatches = when {
            baseCustomItem != null ->
                baseCustomItem.matches(base)

            baseMaterial != null ->
                BrewBaseMatcher.matches(baseMaterial, base)

            else -> false
        }

        return ingredientMatches && baseMatches
    }
}