package org.makeacake.craft.util

import org.bukkit.entity.Player
import org.makeacake.craft.CraftAPI
import org.makeacake.craft.item.CustomItem

/**
 * A utility class for verifying player permissions related to custom items.
 *
 * This object centralizes the logic for checking whether a player has the
 * necessary authorization to interact with or use specific custom items
 * based on a dynamic permission node pattern.
 *
 * @author nalart11
 * @since 1.0.0
 */
object PermissionCheck {

    /**
     * Determines if a player is allowed to use a specific custom item.
     *
     * The permission node follows the pattern: {plugin_name}.item.{item_id}
     * (e.g., "craftapi.item.magic_wand").
     *
     * @param player The player whose permissions are being checked.
     * @param item The custom item instance for which access is being verified.
     * @return `true` if the player has the required permission node, `false` otherwise.
     */
    fun canUse(player: Player, item: CustomItem): Boolean {
        val prefix = CraftAPI.plugin.name.lowercase()
        val permission = "$prefix.item.${item.key.id}"
        return player.hasPermission(permission)
    }
}