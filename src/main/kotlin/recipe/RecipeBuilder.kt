package org.makeacake.craft.recipe

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ShapedRecipe
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.CustomItem

/**
 * A builder class for creating and registering [CustomRecipe] instances.
 *
 * This builder simplifies the process of defining shaped recipes for [CustomItem]s,
 * allowing developers to specify the grid shape, ingredients, and custom actions
 * triggered upon successful crafting.
 *
 * @param item The custom item that will be the result of this recipe.
 * @author nalart11
 * @since 1.0.0
 */
class RecipeBuilder(private val item: CustomItem) {

    private val recipe = ShapedRecipe(item.key.namespacedKey, item.createItemStack())
    private val craftActions = mutableListOf<CraftAction>()

    /**
     * Sets the visual shape of the recipe grid.
     *
     * @param rows Up to three strings, each representing a row in the crafting table.
     * @return This builder instance for chaining.
     */
    fun shape(vararg rows: String) = apply {
        recipe.shape(*rows)
    }

    /**
     * Maps a character in the recipe shape to a specific [Material].
     *
     * @param char The character used in the [shape] method.
     * @param material The Bukkit material corresponding to the character.
     * @return This builder instance for chaining.
     */
    fun ingredient(char: Char, material: Material) = apply {
        recipe.setIngredient(char, material)
    }

    /**
     * Adds a [CraftAction] to be executed when the player crafts this item.
     *
     * @param action The action containing logic for the crafting event.
     * @return This builder instance for chaining.
     */
    fun onCraft(action: CraftAction) = apply {
        craftActions += action
    }

    /**
     * Finalizes the recipe, registers it within the Bukkit server, 
     * and adds it to the internal [RecipeRegistry].
     *
     * @return The constructed [CustomCraftRecipe] instance.
     */
    fun build(): CustomCraftRecipe {
        Bukkit.addRecipe(recipe)

        val customRecipe = CustomCraftRecipe(
            item = item,
            recipe = recipe,
            craftActions = craftActions.toList()
        )

        RecipeRegistry.register(customRecipe)
        return customRecipe
    }
}