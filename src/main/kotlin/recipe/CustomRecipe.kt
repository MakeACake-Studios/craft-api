package org.makeacake.craft.recipe

import org.bukkit.inventory.Recipe
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.CustomItem

/**
 * Represents a custom crafting recipe for a specific custom item.
 *
 * This class securely binds a standard Bukkit[Recipe] to its server-side
 * [CustomItem] representation, while maintaining a dedicated list of actions
 * to execute when the item is successfully crafted.
 *
 * @property item The [CustomItem] that serves as the final result of this recipe.
 * @property recipe The underlying Bukkit [Recipe] instance (e.g., ShapedRecipe or ShapelessRecipe).
 * @property craftActions A collection of [CraftAction]s triggered upon completion of the craft.
 *
 * @author nalart11
 * @since 1.0.0
 */
class CustomRecipe(
    val item: CustomItem,
    val recipe: Recipe,
    val craftActions: List<CraftAction>
)