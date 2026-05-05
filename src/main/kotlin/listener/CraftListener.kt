package org.makeacake.craft.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.CraftAction
import org.makeacake.craft.item.ItemRegistry
import kotlin.collections.filter

class CraftListener : Listener {

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val player = event.whoClicked as? Player ?: return
        val resultItem = event.recipe.result

        val customItem = ItemRegistry.byItemStack(resultItem) ?: return

        customItem.actions
            .filter { it.type == ActionType.CRAFT }
            .filterIsInstance<CraftAction>()
            .forEach {
                it.handler(player, resultItem, event)
            }
    }
}
