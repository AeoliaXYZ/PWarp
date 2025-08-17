package xyz.aeolia.pwarp

import org.bukkit.entity.Player
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


    /*
    Note to self. Please do not run this blocking. Please. Please. I beg you.
     */
    fun maxWarps(player: Player): Int {
      var maxWarps = 1
      if (player.hasPermission("pwarp.admin")) return 0

      player.effectivePermissions.forEach { permission ->
        val node = permission.permission
        if (node.startsWith("pwarp.max.")) {
          val value = node.removePrefix("pwarp.max.")
          val nodeValue = try {
            Integer.parseInt(value, 10)
          } catch (_: NumberFormatException) {
            return@forEach
          }
          if (nodeValue > maxWarps) maxWarps = nodeValue
        }
      }
      return maxWarps
    }
  }
}