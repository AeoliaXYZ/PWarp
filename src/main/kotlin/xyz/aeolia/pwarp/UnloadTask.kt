package xyz.aeolia.pwarp

import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class UnloadTask(val uuid: UUID) : BukkitRunnable() {
  override fun run() {
    WarpPlayer.unloadPlayer(uuid)
  }
}