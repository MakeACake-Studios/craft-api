package org.makeacake.craft.util

import org.bukkit.NamespacedKey
import org.makeacake.craft.action.ActionType
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CooldownManagerTest {
    private val player = UUID.randomUUID()
    private val key = NamespacedKey("craft_test", "wand")

    @AfterTest
    fun cleanup() = CooldownManager.clearAll()

    @Test
    fun noCooldownUnlessConfigured() {
        assertTrue(CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, null))
        assertTrue(CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, null))
        assertEquals(0, CooldownManager.remaining(player, key, ActionType.RIGHT_CLICK))
    }

    @Test
    fun configuredCooldownIsScopedToActionAndPlayer() {
        assertTrue(CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, 5))
        assertFalse(CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, 5))
        assertTrue(CooldownManager.tryUse(player, key, ActionType.LEFT_CLICK, 5))
        assertTrue(CooldownManager.tryUse(UUID.randomUUID(), key, ActionType.RIGHT_CLICK, 5))
        assertTrue(CooldownManager.remaining(player, key, ActionType.RIGHT_CLICK) > 0)
    }

    @Test
    fun negativeCooldownIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, -1)
        }
    }

    @Test
    fun concurrentUsesAllowOnlyOneAttempt() {
        val workers = Executors.newFixedThreadPool(8)
        try {
            val start = CountDownLatch(1)
            val attempts = (1..8).map {
                workers.submit(Callable {
                    start.await()
                    CooldownManager.tryUse(player, key, ActionType.RIGHT_CLICK, 5)
                })
            }
            start.countDown()
            assertEquals(1, attempts.count { it.get() })
        } finally {
            workers.shutdownNow()
        }
    }
}
