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
import kotlin.collections.filter

class ItemUseListener : Listener {

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
            .filter { it.type == actionType }
            .filterIsInstance<ClickAction>()

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
