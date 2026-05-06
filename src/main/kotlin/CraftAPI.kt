package org.makeacake.craft

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.makeacake.craft.listener.CraftListener
import org.makeacake.craft.listener.ItemPlaceListener
import org.makeacake.craft.listener.ItemUseListener
import org.makeacake.craft.listener.JoinListener

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

    lateinit var plugin: JavaPlugin
        private set

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
        this.plugin = plugin

        val pm = Bukkit.getPluginManager()
        pm.registerEvents(CraftListener(), plugin)
        pm.registerEvents(ItemUseListener(), plugin)
        pm.registerEvents(JoinListener(), plugin)
        pm.registerEvents(ItemPlaceListener(), plugin)
    }
}