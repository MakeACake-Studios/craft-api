package org.makeacake.craft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.PlaceBlockAction
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.util.CooldownManager
import org.makeacake.craft.util.PermissionCheck
import kotlin.collections.filter

class ItemPlaceListener : Listener {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val item = event.itemInHand
        val customItem = ItemRegistry.byItemStack(item) ?: return

        val actions = customItem.actions
            .filter { it.type == ActionType.BLOCK_PLACE }
            .filterIsInstance<PlaceBlockAction>()

        if (actions.isEmpty()) return

        if (!PermissionCheck.canUse(player, customItem)) {
            player.sendMessage("У вас нет прав для установки этого предмета!")
            event.isCancelled = true
            return
        }

        val cooldownToApply = actions.mapNotNull { it.cooldownSeconds }.maxOrNull()

        if (!CooldownManager.tryUse(player.uniqueId, customItem.key, ActionType.BLOCK_PLACE, cooldownToApply)) {
            val time = CooldownManager.remaining(player.uniqueId, customItem.key, ActionType.BLOCK_PLACE)
            player.sendMessage("Подожди $time секунд перед повторным использованием.")
            event.isCancelled = true
            return
        }

        actions.forEach {
            it.handler(player, item, event.block, event)
        }
    }
}
