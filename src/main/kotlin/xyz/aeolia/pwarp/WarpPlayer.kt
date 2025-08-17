package xyz.aeolia.pwarp

import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class WarpPlayer(
  val uuid: UUID
) {
  private val _warps = mutableListOf<Warp>()
  val warps: List<Warp>
    get() = _warps

  fun addWarp(warp: Warp) {
    _warps.add(warp)
  }

  fun removeWarp(warp: Warp) {
    _warps.remove(warp)
  }

  companion object {
    val players = mutableMapOf<UUID, WarpPlayer>()
    val plugin by lazy {
      PWarp.INSTANCE
    }

    fun loadPlayer(uuid: UUID): WarpPlayer {
      if (players[uuid] != null) return players[uuid]!!
      val player = fromFile(uuid)
      return player.also { players[uuid] = it }
    }

    fun unloadPlayer(uuid: UUID) {
      if (players[uuid] == null) return
      players[uuid]!!.warps.forEach { warp ->
        warp.save()
      }
      players.remove(uuid)
    }

    private fun fromFile(uuid: UUID): WarpPlayer {
      val wPlayer = WarpPlayer(uuid)
      val warpFolder = File(plugin.dataFolder.path + File.separatorChar + "warps", uuid.toString())
      if (!warpFolder.exists()) {
        return wPlayer
      }
      val list = (warpFolder.listFiles() ?: return wPlayer).filter {
        it.isFile && it.name.endsWith(".json")
      }
      list.forEach { file ->
        val warp = try {
          Json.decodeFromString<Warp>(file.readText())
        } catch (e: Exception) {
          e.printStackTrace()
          return@forEach
        }
        wPlayer.addWarp(warp)
      }
      return wPlayer
    }
  }
}