package xyz.aeolia.pwarp

import org.bukkit.scheduler.BukkitRunnable

class UnloadTask : BukkitRunnable() {
  override fun run() {
    for (uuid in WarpPlayer.players.keys.toList()) {
      WarpPlayer.unloadPlayer(uuid)
    }
  }
}