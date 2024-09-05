package org.oreo.antinerdpole

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.antinerdpole.listeners.GlompedPlayersListener
import org.oreo.antinerdpole.listeners.NerdPoleListener

class AntiNerdpole : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(NerdPoleListener(this), this)
        server.pluginManager.registerEvents(GlompedPlayersListener(), this)
        saveDefaultConfig()
    }

    companion object {
        val playersPunished: MutableList<Player> = mutableListOf()
    }
}

