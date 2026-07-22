package org.makeacake.craft.recipe

import io.papermc.paper.potion.PotionMix
import org.bukkit.Bukkit
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
        registerPotionMix(recipe)
        return recipe
    }

    /**
     * Registers a [PotionMix] with the server's [org.bukkit.potion.PotionBrewer].
     *
     * Without this, the vanilla brewing stand slot checks reject any ingredient/base
     * that Minecraft doesn't already recognize as a valid brewing item, so players would
     * be unable to physically place a custom ingredient or base into the stand at all.
     */
    private fun registerPotionMix(recipe: CustomBrewRecipe) {
        val ingredientChoice: RecipeChoice = ingredientCustomItem?.let { custom ->
            PotionMix.createPredicateChoice { stack -> custom.matches(stack) }
        } ?: RecipeChoice.MaterialChoice(ingredientMaterial!!)

        val inputChoice: RecipeChoice = baseCustomItem?.let { custom ->
            PotionMix.createPredicateChoice { stack -> custom.matches(stack) }
        } ?: RecipeChoice.MaterialChoice(baseMaterial!!)

        val resultStack: ItemStack = recipe.item.createItemStack()

        val mix = PotionMix(RecipeRegistry.nextBrewMixKey(), resultStack, inputChoice, ingredientChoice)
        Bukkit.getPotionBrewer().addPotionMix(mix)
    }
}