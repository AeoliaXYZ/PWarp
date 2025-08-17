package xyz.aeolia.pwarp

import java.io.File
import java.util.*

class WarpPlayer private constructor(
  @Suppress("unused")
  val uuid: UUID
) {
  private val _warps = mutableListOf<Warp>()
  val warps: List<Warp>
    get() {
      updateLastAccess()
      return _warps
    }

  fun updateLastAccess() {
    lastAccess = System.currentTimeMillis()
  }

  var lastAccess: Long = System.currentTimeMillis()

  fun addWarp(warp: Warp) {
    updateLastAccess()
    _warps.add(warp)
  }

  fun removeWarp(warp: Warp) {
    updateLastAccess()
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

    fun unloadPlayer(uuid: UUID, checkLastAccess: Boolean = true) {
      if (players[uuid] == null) return
      players[uuid]!!.warps.forEach { warp ->
        warp.save()
      }
      if (checkLastAccess) {
        if ((System.currentTimeMillis() - players[uuid]!!.lastAccess) < (180 * 1000)) return
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
        val warp = Warp.load(uuid, file.nameWithoutExtension) ?: return wPlayer
        wPlayer.addWarp(warp)
      }
      return wPlayer
    }
  }
}