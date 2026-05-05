package org.makeacake.craft.recipe

import org.bukkit.inventory.Recipe
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.CustomItem

/**
 * Кастомный рецепт предмета.
 *
 * @property item Предмет, создаваемый рецептом
 * @property recipe Bukkit-рецепт
 * @property craftActions Действия при крафте
 *
 * @author nalart11
 * @since 0.7b
 */
class CustomRecipe(
    val item: CustomItem,
    val recipe: Recipe,
    val craftActions: List<CraftAction>
)
