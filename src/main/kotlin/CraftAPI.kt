package org.makeacake.craft

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.makeacake.craft.listener.CraftListener
import org.makeacake.craft.listener.ItemPlaceListener
import org.makeacake.craft.listener.ItemUseListener
import org.makeacake.craft.listener.JoinListener

object CraftAPI {
    lateinit var plugin: JavaPlugin
        private set

    /**
     * Инициализирует библиотеку для конкретного плагина.
     * Регистрирует все необходимые слушатели событий.
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