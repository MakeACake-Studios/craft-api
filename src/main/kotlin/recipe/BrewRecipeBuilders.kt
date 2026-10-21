package org.makeacake.craft.recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.makeacake.craft.action.BrewAction
import org.makeacake.craft.item.CustomItem

class BrewRecipeBuilder(private val item: CustomItem) {

    private var ingredientMaterial: Material? = null
    private var ingredientCustomItem: CustomItem? = null
    private var baseMaterial: Material? = null
    private var baseCustomItem: CustomItem? = null
    private val brewActions = mutableListOf<BrewAction>()

    fun ingredient(material: Material) = apply {
        ingredientMaterial = material
        ingredientCustomItem = null
    }

    fun ingredient(customItem: CustomItem) = apply {
        ingredientCustomItem = customItem
        ingredientMaterial = null
    }

    fun base(material: Material) = apply {
        baseMaterial = material
        baseCustomItem = null
    }

    fun base(customItem: CustomItem) = apply {
        baseCustomItem = customItem
        baseMaterial = null
    }

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

        registerBrewingRecipe(recipe)
        RecipeRegistry.register(recipe)
        return recipe
    }

    /**
     * Registers a brewing recipe with the API available on the running server.
     *
     * Without this, the vanilla brewing stand slot checks reject any ingredient/base
     * that Minecraft doesn't already recognize as a valid brewing item, so players would
     * be unable to physically place a custom ingredient or base into the stand at all.
     */
    private fun registerBrewingRecipe(recipe: CustomBrewRecipe) {
        val ingredientChoice: RecipeChoice = ingredientCustomItem?.let { custom ->
            BrewingRecipeRegistrar.customChoice(custom)
        } ?: RecipeChoice.MaterialChoice(ingredientMaterial!!)

        val inputChoice: RecipeChoice = baseCustomItem?.let { custom ->
            BrewingRecipeRegistrar.customChoice(custom)
        } ?: RecipeChoice.MaterialChoice(baseMaterial!!)

        val resultStack: ItemStack = recipe.item.createItemStack()

        val key = RecipeRegistry.nextBrewingRecipeKey()
        val modern = BrewingRecipeRegistrar.register(key, resultStack, inputChoice, ingredientChoice)
        RecipeRegistry.registerBrewRecipeKey(key, modern)
    }
}
