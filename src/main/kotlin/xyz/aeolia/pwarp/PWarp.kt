package xyz.aeolia.pwarp

import org.bukkit.plugin.java.JavaPlugin

class PWarp : JavaPlugin() {

  override fun onEnable() {
    INSTANCE = this
    UnloadTask().runTaskTimer(this, 1200L, 1200L)
    getCommand("pwarp")?.setExecutor(PWarpCommandExecutor())
    getCommand("pwarp")?.tabCompleter = PWarpCommandExecutor()
  }

  override fun onDisable() {
    logger.info("Saving warps...")
    WarpPlayer.players.values.forEach { wPlayer ->
      wPlayer.warps.forEach { warp ->
        warp.save()
      }
    }
    logger.info("ttyl")
  }

  companion object {
    lateinit var INSTANCE: JavaPlugin
  }
}
