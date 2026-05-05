package org.makeacake.craft.util

import org.bukkit.entity.Player
import org.makeacake.craft.CraftAPI
import org.makeacake.craft.item.CustomItem

/**
 * Проверка прав для кастомных предметов.
 *
 * permission: plugin.item.<item_id>
 *
 * @author nalart11
 * @since 0.7b
 */
object PermissionCheck {

    fun canUse(player: Player, item: CustomItem): Boolean {
        val prefix = CraftAPI.plugin.name.lowercase()
        val permission = "$prefix.item.${item.key.id}"
        return player.hasPermission(permission)
    }
}
