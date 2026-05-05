package org.makeacake.craft.util

import org.makeacake.craft.action.ActionType
import org.makeacake.craft.item.ItemKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

/**
 * Менеджер кулдаунов для кастомных предметов.
 *
 * @author nalart11
 * @since 0.7b
 */
object CooldownManager {

    private val cooldowns = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    private const val DEFAULT_COOLDOWN = 5
    fun tryUse(player: UUID, key: ItemKey, actionType: ActionType, cooldownSeconds: Int?): Boolean {
        val now = System.currentTimeMillis()
        val map = cooldowns.computeIfAbsent(player) { ConcurrentHashMap() }

        val mapKey = "${key.id}_${actionType.name}"

        val expiresAt = map[mapKey] ?: 0L
        if (expiresAt > now) return false

        map[mapKey] = now + (cooldownSeconds?.times(1000L) ?: DEFAULT_COOLDOWN.times(1000L))
        return true
    }

    fun remaining(player: UUID, key: ItemKey, actionType: ActionType): Int {
        val mapKey = "${key.id}_${actionType.name}"
        val expiresAt = cooldowns[player]?.get(mapKey) ?: return 0
        val diff = expiresAt - System.currentTimeMillis()
        return if (diff <= 0) 0 else ceil(diff / 1000.0).toInt()
    }

    fun clear(player: UUID) {
        cooldowns.remove(player)
    }
}
