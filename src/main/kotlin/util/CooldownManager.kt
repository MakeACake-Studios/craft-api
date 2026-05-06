package org.makeacake.craft.util

import org.makeacake.craft.action.ActionType
import org.makeacake.craft.item.ItemKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

/**
 * A thread-safe manager for handling action cooldowns on custom items.
 *
 * This utility tracks interaction timestamps for players to prevent spamming
 * of custom item abilities. It supports per-action cooldowns and provides
 * methods to check, retrieve, and clear active timers.
 *
 * @author nalart11
 * @since 1.0.0
 */
object CooldownManager {

    private val cooldowns = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    private const val DEFAULT_COOLDOWN = 5

    /**
     * Attempts to perform an action. If the action is not on cooldown,
     * a new cooldown is set and the method returns true.
     *
     * @param player The unique ID of the player.
     * @param key The unique key of the custom item.
     * @param actionType The type of action being performed.
     * @param cooldownSeconds The duration of the cooldown in seconds. If null, [DEFAULT_COOLDOWN] is used.
     * @return `true` if the action can be performed, `false` if it is currently on cooldown.
     */
    fun tryUse(player: UUID, key: ItemKey, actionType: ActionType, cooldownSeconds: Int?): Boolean {
        val now = System.currentTimeMillis()
        val map = cooldowns.computeIfAbsent(player) { ConcurrentHashMap() }

        val mapKey = "${key.id}_${actionType.name}"

        val expiresAt = map[mapKey] ?: 0L
        if (expiresAt > now) return false

        map[mapKey] = now + (cooldownSeconds?.times(1000L) ?: DEFAULT_COOLDOWN.times(1000L))
        return true
    }

    /**
     * Calculates the remaining time of a cooldown for a specific action.
     *
     * @param player The unique ID of the player.
     * @param key The unique key of the custom item.
     * @param actionType The type of action to check.
     * @return The number of seconds remaining, rounded up. Returns 0 if no cooldown is active.
     */
    fun remaining(player: UUID, key: ItemKey, actionType: ActionType): Int {
        val mapKey = "${key.id}_${actionType.name}"
        val expiresAt = cooldowns[player]?.get(mapKey) ?: return 0
        val diff = expiresAt - System.currentTimeMillis()
        return if (diff <= 0) 0 else ceil(diff / 1000.0).toInt()
    }

    /**
     * Completely removes all active cooldowns for a specific player.
     *
     * @param player The unique ID of the player whose cooldowns should be cleared.
     */
    fun clear(player: UUID) {
        cooldowns.remove(player)
    }
}