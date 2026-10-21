package org.makeacake.craft

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin
import org.makeacake.craft.listener.BrewListener
import org.makeacake.craft.listener.CraftListener
import org.makeacake.craft.listener.ItemPlaceListener
import org.makeacake.craft.listener.ItemUseListener
import org.makeacake.craft.listener.JoinListener
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.recipe.RecipeRegistry
import org.makeacake.craft.util.CooldownManager

/**
 * The main entry point and bridge for the Craft framework.
 *
 * This singleton stores the parent [JavaPlugin] instance and provides
 * a centralized way to initialize all core components, including
 * event listeners and registry systems.
 *
 * @author nalart11
 * @since 1.0.0
 */
object CraftAPI {

    private var activePlugin: JavaPlugin? = null
    val plugin: JavaPlugin
        get() = checkNotNull(activePlugin) { "CraftAPI.init must be called first" }
    private val listeners = mutableListOf<Listener>()

    /**
     * Initializes the framework for the specified plugin.
     *
     * This method stores the plugin instance for global access and
     * registers all necessary event listeners (Craft, Use, Join, Place)
     * within the Bukkit PluginManager.
     *
     * @param plugin The JavaPlugin instance that is using this library.
     */
    fun init(plugin: JavaPlugin) {
        if (activePlugin === plugin) return
        check(activePlugin == null) { "CraftAPI is already initialized for another plugin" }
        activePlugin = plugin

        val pm = Bukkit.getPluginManager()
        listeners += listOf(
            CraftListener(), ItemUseListener(), JoinListener(), ItemPlaceListener(),
            BrewListener(), PluginLifecycleListener(plugin)
        )
        listeners.forEach { pm.registerEvents(it, plugin) }
    }

    /** Removes this library's listeners, recipes, items, and cooldowns. Also called on plugin disable. */
    fun shutdown() {
        if (activePlugin == null) return
        listeners.forEach(HandlerList::unregisterAll)
        listeners.clear()
        RecipeRegistry.clear()
        ItemRegistry.clear()
        CooldownManager.clearAll()
        activePlugin = null
    }

    private class PluginLifecycleListener(private val owner: JavaPlugin) : Listener {
        @EventHandler
        fun onDisable(event: PluginDisableEvent) {
            if (event.plugin === owner) shutdown()
        }
    }
}
