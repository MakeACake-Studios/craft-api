package org.makeacake.craft.recipe

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
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
 * @param key The recipe key. Supply a distinct key for each recipe with the same result item.
 * @author nalart11
 * @since 1.0.0
 */
class RecipeBuilder(
    private val item: CustomItem,
    key: NamespacedKey = item.key.namespacedKey
) {

    private val recipe = ShapedRecipe(key, item.createItemStack())
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

    fun onCraft(
        cooldownSeconds: Int? = null,
        handler: (Player, ItemStack, CraftItemEvent) -> Unit
    ) = onCraft(CraftAction(cooldownSeconds, handler))

    /**
     * Finalizes the recipe, registers it within the Bukkit server, 
     * and adds it to the internal [RecipeRegistry].
     *
     * @return The constructed [CustomCraftRecipe] instance.
     */
    fun build(): CustomCraftRecipe {
        require(!RecipeRegistry.hasCraftRecipe(recipe.key)) { "Recipe ${recipe.key} is already registered" }
        check(Bukkit.addRecipe(recipe)) { "Could not register recipe ${recipe.key}" }

        val customRecipe = CustomCraftRecipe(
            item = item,
            recipe = recipe,
            craftActions = craftActions.toList()
        )

        RecipeRegistry.register(customRecipe)
        return customRecipe
    }
}
