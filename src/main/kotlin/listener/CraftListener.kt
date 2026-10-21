package org.makeacake.craft.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.recipe.RecipeRegistry
import org.makeacake.craft.util.CooldownManager

/**
 * A Bukkit listener responsible for handling crafting events related to custom items.
 *
 * This listener acts as a bridge between Bukkit's event system and the custom item framework.
 * It intercepts crafting attempts, checks if the resulting item is a custom item,
 * and delegates the execution to the appropriate [CraftAction] handlers.
 *
 * @author nalart11
 * @since 1.0.0
 */
class CraftListener : Listener {

    /**
     * Handles the [CraftItemEvent] triggered when a player crafts an item.
     *
     * The method verifies if the crafted result exists in the [ItemRegistry].
     * If a match is found, it extracts all actions of type [ActionType.CRAFT]
     * (specifically [CraftAction]) and executes their associated logic.
     *
     * @param event The Bukkit event containing details about the crafting action.
     */
    @EventHandler(ignoreCancelled = true)
    fun onCraft(event: CraftItemEvent) {
        val player = event.whoClicked as? Player ?: return
        val resultItem = event.currentItem ?: event.recipe.result
        val recipe = RecipeRegistry.byRecipe(event.recipe)
        val customItem = recipe?.item ?: ItemRegistry.byItemStack(resultItem) ?: return
        val actions = recipe?.craftActions.orEmpty() + customItem.actions
            .filter { it.type == ActionType.CRAFT }
            .filterIsInstance<CraftAction>()
        if (actions.isEmpty()) return

        val cooldown = actions.mapNotNull { it.cooldownSeconds }.maxOrNull()
        if (!CooldownManager.tryUse(player.uniqueId, customItem.key, ActionType.CRAFT, cooldown)) {
            event.isCancelled = true
            return
        }

        actions.forEach { it.handler(player, resultItem, event) }
    }
}
