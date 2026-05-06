package org.makeacake.craft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.PlaceBlockAction
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.util.CooldownManager
import org.makeacake.craft.util.PermissionCheck

/**
 * A Bukkit listener responsible for handling block placement events involving custom items.
 *
 * This listener intercepts the placement of blocks, verifies if the item being placed is a registered [org.makeacake.craft.item.CustomItem],
 * validates player permissions, manages action cooldowns, and executes associated [PlaceBlockAction]s.
 *
 * @author nalart11
 * @since 1.0.0
 */
class ItemPlaceListener : Listener {

    /**
     * Handles the [BlockPlaceEvent] when a player attempts to place a custom block.
     *
     * This method performs permission checks, cooldown validation, and executes the custom logic associated
     * with the [PlaceBlockAction]. If the player lacks permission or the action is on cooldown,
     * the placement event is canceled.
     *
     * @param event The block placement event containing details about the player, item in hand, and placed block.
     */
    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val item = event.itemInHand
        val customItem = ItemRegistry.byItemStack(item) ?: return

        val actions = customItem.actions.filterIsInstance<PlaceBlockAction>()

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