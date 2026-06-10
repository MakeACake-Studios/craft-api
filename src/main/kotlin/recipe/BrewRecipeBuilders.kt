package org.makeacake.craft.recipe

import org.bukkit.Material
import org.makeacake.craft.action.BrewAction
import org.makeacake.craft.item.CustomItem

class BrewRecipeBuilder(private val item: CustomItem) {

    private var ingredientMaterial: Material? = null
    private var ingredientCustomItem: CustomItem? = null
    private var baseMaterial: Material? = null
    private var baseCustomItem: CustomItem? = null
    private val brewActions = mutableListOf<BrewAction>()

    fun ingredient(material: Material) = apply { this.ingredientMaterial = material }

    fun ingredient(customItem: CustomItem) = apply { this.ingredientCustomItem = customItem }

    fun base(material: Material) = apply { this.baseMaterial = material }

    fun base(customItem: CustomItem) = apply { this.baseCustomItem = customItem }

    fun onBrew(action: BrewAction) = apply { this.brewActions += action }

    fun build(): CustomBrewRecipe {
        require(ingredientMaterial != null || ingredientCustomItem != null)
        require(baseMaterial != null || baseCustomItem != null)

        val recipe = CustomBrewRecipe(
            item = item,
            ingredientMaterial = ingredientMaterial,
            ingredientCustomItem = ingredientCustomItem,
            baseMaterial = baseMaterial,
            baseCustomItem = baseCustomItem,
            brewActions = brewActions.toList()
        )

        RecipeRegistry.register(recipe)
        return recipe
    }
}