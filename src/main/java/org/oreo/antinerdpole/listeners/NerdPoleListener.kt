package org.oreo.antinerdpole.listeners

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.oreo.antinerdpole.AntiNerdpole
import org.oreo.antinerdpole.java.GetNodesInfo
import phonon.nodes.Nodes
import java.util.*


class NerdPoleListener(private val plugin: AntiNerdpole, private val nodes: Nodes = Nodes) : Listener {

    val nerdpoleWarningHeight = plugin.config.getInt("nerdpole-warning-height")
    val maxNerdpoleHeight = plugin.config.getInt("nerdpole-max-height")
    val debuffDuration = plugin.config.getInt("nerdpole-debuff-duration")

    @EventHandler
    fun nerdPoleListener(e:BlockPlaceEvent){
        val block = e.blockPlaced
        val player = e.player
        val playerNation = Nodes.getResident(player)?.nation

        if (isPillaring(block,player) || player.gameMode == GameMode.CREATIVE){
            return
        }


        val territory = nodes.getTerritoryFromChunk(block.chunk)

        if  (!GetNodesInfo.isWarOn()){
            if (territory != null) {
                if (territory.town?.nation == playerNation){
                    return
                } else {
                    if (playerNation != null) {
                        for (nation in playerNation.allies){
                            if (territory.town?.nation == nation){
                                return
                            }
                        }
                    }
                }
            }
        }

        if (isPillaringTooHigh(block,player)){

            e.isCancelled = true

            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f, 0.5f)
            setPlayerPitch(player,0f)
            sendTitle(player, "§cNerdpole limit reached", "§eGet glomped bitch",
                10, 65, 20)

            player.addPotionEffect(PotionEffect(PotionEffectType.POISON, debuffDuration * 20,1))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, debuffDuration * 20,3))
            player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, debuffDuration * 20, 87))

            AntiNerdpole.playersPunished.add(player)

            object : BukkitRunnable() {
                override fun run() {
                    AntiNerdpole.playersPunished.remove(player)
                }
            }.runTaskLater(plugin, (debuffDuration * 20).toLong())
        }

    }

    private fun isPillaring(block: Block , player: Player) : Boolean{
        return !(player.location.blockY - 1 == block.y && (player.location.blockX == block.x || player.location.blockZ == block.z))
    }

    private fun isPillaringTooHigh(block: Block, player: Player) : Boolean{

        val world = block.world

        val north = world.getHighestBlockAt(block.location.clone().add(0.0,0.0,-1.0)).y
        val south = world.getHighestBlockAt(block.location.clone().add(0.0,0.0,1.0)).y
        val west = world.getHighestBlockAt(block.location.clone().add(-1.0,0.0,0.0)).y
        val east = world.getHighestBlockAt(block.location.clone().add(1.0,0.0,0.0)).y

        val northWest = world.getHighestBlockAt(block.location.clone().add(-1.0,0.0,-1.0)).y
        val northEast = world.getHighestBlockAt(block.location.clone().add(1.0,0.0,-1.0)).y
        val southWest = world.getHighestBlockAt(block.location.clone().add(-1.0,0.0,1.0)).y
        val southEast = world.getHighestBlockAt(block.location.clone().add(1.0,0.0,1.0)).y

        val numbers = arrayOf(north, south, west, east, northWest, northEast, southWest, southEast)
        val average = numbers.average()

        if (block.y - average > maxNerdpoleHeight){
            return true
        } else if ((block.y - average).toInt() == maxNerdpoleHeight - nerdpoleWarningHeight){
            sendActionBar(player,"Nearing nerdpole limit")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f)
        }

        return false
    }

    private fun sendActionBar(player: Player, message: String) {
        val formattedMessage = ChatColor.RED.toString() + "" + ChatColor.BOLD + message.uppercase(Locale.getDefault())
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(formattedMessage))
    }

    private fun sendTitle(player: Player, title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
    }

    fun setPlayerPitch(player: Player, pitch: Float) {
        // Ensure the pitch is within the valid range (-90 to 90)
        val clampedPitch = pitch.coerceIn(-90.0f, 90.0f)

        val location = player.location
        location.pitch = clampedPitch
        player.teleport(location) // Teleport the player to the new location with the updated pitch
    }
}