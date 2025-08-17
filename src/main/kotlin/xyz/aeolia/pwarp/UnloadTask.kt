package xyz.aeolia.pwarp

import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class UnloadTask : BukkitRunnable() {
  override fun run() {
    for (uuid in WarpPlayer.players.keys) {
      WarpPlayer.unloadPlayer(uuid)
    }
  }
}