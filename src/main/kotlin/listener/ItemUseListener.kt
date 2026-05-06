package org.makeacake.craft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.ClickAction
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.util.CooldownManager
import org.makeacake.craft.util.PermissionCheck

/**
 * A Bukkit listener responsible for handling player interactions with custom items.
 *
 * This listener captures general clicks (left-click, right-click, and shift-clicks)
 * performed by players holding a registered [org.makeacake.craft.item.CustomItem]. It dynamically resolves
 * the specific interaction type, checks permissions, applies cooldowns, and triggers
 * the appropriate [ClickAction].
 *
 * @author nalart11
 * @since 1.0.0
 */
class ItemUseListener : Listener {

    /**
     * Handles the [PlayerInteractEvent] when a player clicks while holding an item.
     *
     * The method determines the correct [ActionType] based on the Bukkit [Action]
     * and the player's sneaking state. If the held item matches a registered custom item
     * and has corresponding actions, it verifies player permissions and cooldowns
     * before executing the handlers.
     *
     * @param event The Bukkit interaction event containing details about the click and the item.
     */
    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val customItem = ItemRegistry.byItemStack(item) ?: return

        val actionType = when (event.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK ->
                if (player.isSneaking) ActionType.SHIFT_RIGHT_CLICK else ActionType.RIGHT_CLICK
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK ->
                if (player.isSneaking) ActionType.SHIFT_LEFT_CLICK else ActionType.LEFT_CLICK
            else -> return
        }

        val actions = customItem.actions
            .filterIsInstance<ClickAction>()
            .filter { it.type == actionType }

        if (actions.isEmpty()) return

        if (!PermissionCheck.canUse(player, customItem)) {
            event.isCancelled = true
            return
        }

        val cooldownToApply = actions.mapNotNull { it.cooldownSeconds }.maxOrNull()

        if (!CooldownManager.tryUse(player.uniqueId, customItem.key, actionType, cooldownToApply)) {
            val time = CooldownManager.remaining(player.uniqueId, customItem.key, actionType)
            player.sendMessage("Подожди $time секунд перед повторным использованием.")
            event.isCancelled = true
            return
        }

        actions.forEach { action ->
            action.handler(player, item, event)
        }
    }
}