package org.oreo.antinerdpole.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.oreo.antinerdpole.AntiNerdpole

class GlompedPlayersListener : Listener {

    @EventHandler
    fun glompedMove(e:PlayerMoveEvent){
        val player = e.player

        if (AntiNerdpole.playersPunished.contains(player)){
            if (e.from.y > e.to.y){
                return
            }
            e.isCancelled = true
        }
    }

    @EventHandler
    fun glompedInteract(e:PlayerInteractEvent){
        val player = e.player

        if (AntiNerdpole.playersPunished.contains(player)){
            e.isCancelled = true
        }
    }
}