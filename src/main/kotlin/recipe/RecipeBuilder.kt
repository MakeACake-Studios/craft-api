package org.makeacake.craft.recipe

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ShapedRecipe
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.CustomItem

class RecipeBuilder(private val item: CustomItem) {

    private val recipe = ShapedRecipe(item.key.namespacedKey, item.createItemStack())
    private val craftActions = mutableListOf<CraftAction>()

    fun shape(vararg rows: String) = apply {
        recipe.shape(*rows)
    }

    fun ingredient(char: Char, material: Material) = apply {
        recipe.setIngredient(char, material)
    }

    fun onCraft(action: CraftAction) = apply {
        craftActions += action
    }

    fun build(): CustomRecipe {
        Bukkit.addRecipe(recipe)

        val customRecipe = CustomRecipe(
            item = item,
            recipe = recipe,
            craftActions = craftActions.toList()
        )

        RecipeRegistry.register(customRecipe)
        return customRecipe
    }
}
