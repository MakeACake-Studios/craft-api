package org.makeacake.craft.action

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

enum class ActionType {
    RIGHT_CLICK, LEFT_CLICK, CRAFT, SHIFT_RIGHT_CLICK, SHIFT_LEFT_CLICK, DROP, ENTITY_INTERACT, BLOCK_PLACE
}

interface ItemAction {
    val type: ActionType
    var cooldownSeconds: Int?
}

class ClickAction(
    override val type: ActionType,
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, PlayerInteractEvent) -> Unit
) : ItemAction

class CraftAction(
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, CraftItemEvent) -> Unit
) : ItemAction {
    override val type: ActionType = ActionType.CRAFT
}

class PlaceBlockAction(
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, Block, BlockPlaceEvent) -> Unit
) : ItemAction {
    override val type: ActionType = ActionType.BLOCK_PLACE
}
