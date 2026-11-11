package org.makeacake.craft.recipe

import io.papermc.paper.potion.PotionMix
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.makeacake.craft.item.CustomItem
import org.makeacake.craft.item.ItemRegistry
import recipe.BrewBaseMatcher

/** Loaded only on Paper versions before BrewingRecipe became available. */
internal object LegacyBrewingRecipeRegistrar {
    fun customChoice(custom: CustomItem): RecipeChoice =
        PotionMix.createPredicateChoice { stack -> custom.matches(stack) }

    fun register(key: NamespacedKey, result: ItemStack, input: RecipeChoice, ingredient: RecipeChoice) {
        Bukkit.getPotionBrewer().addPotionMix(PotionMix(key, result, input, ingredient))
    }

    fun vanillaPotionChoice(): RecipeChoice =
        PotionMix.createPredicateChoice { stack ->
            BrewBaseMatcher.matches(Material.POTION, stack)
        }
}
