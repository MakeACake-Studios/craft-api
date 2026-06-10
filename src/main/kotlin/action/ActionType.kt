package org.makeacake.craft.action

import org.bukkit.block.Block
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * Enumeration of supported interaction types that can trigger an [ItemAction].
 *
 * @since 1.0.0
 * @author nalart11
 */
enum class ActionType {
    RIGHT_CLICK,
    LEFT_CLICK,
    CRAFT,
    SHIFT_RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    DROP,
    PICKUP,
    ENTITY_INTERACT,
    BLOCK_PLACE,
    BREW
}

/**
 * The base interface for all item-related actions.
 * Any custom behavior associated with an item must implement this interface.
 *
 * @property type The [ActionType] that triggers this action.
 * @property cooldownSeconds The mandatory delay (in seconds) between action executions.
 * Use `null` for no cooldown.
 *
 * @since 1.0.0
 * @author nalart11
 */
interface ItemAction {
    val type: ActionType
    var cooldownSeconds: Int?
}

/**
 * Represents an action triggered by player click interactions (e.g., right-click or left-click).
 *
 * @param type The specific click type (e.g., [ActionType.RIGHT_CLICK]).
 * @param cooldownSeconds Optional cooldown in seconds.
 * @param handler The logic to execute, providing the [Player], the [ItemStack] used,
 * and the original [PlayerInteractEvent].
 *
 * @since 1.0.0
 * @author nalart11
 */
class ClickAction(
    override val type: ActionType,
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, PlayerInteractEvent) -> Unit
) : ItemAction

/**
 * Represents an action triggered when a player crafts a specific item.
 *
 * @param cooldownSeconds Optional cooldown in seconds.
 * @param handler The logic to execute, providing the [Player], the resulting [ItemStack],
 * and the original [CraftItemEvent].
 *
 * @since 1.0.0
 * @author nalart11
 */
class CraftAction(
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, CraftItemEvent) -> Unit
) : ItemAction {
    override val type: ActionType = ActionType.CRAFT
}

/**
 * Represents an action triggered when a player places a block.
 *
 * @param cooldownSeconds Optional cooldown in seconds.
 * @param handler The logic to execute, providing the [Player], the [ItemStack] in hand,
 * the [Block] being placed, and the original [BlockPlaceEvent].
 *
 * @since 1.0.0
 * @author nalart11
 */
class PlaceBlockAction(
    override var cooldownSeconds: Int? = null,
    val handler: (Player, ItemStack, Block, BlockPlaceEvent) -> Unit
) : ItemAction {
    override val type: ActionType = ActionType.BLOCK_PLACE
}

class BrewAction(
    val handler: (BrewingStand, ItemStack, BrewEvent) -> Unit
) : ItemAction {
    override val type: ActionType = ActionType.BREW
    override var cooldownSeconds: Int? = null
}