package xyz.aeolia.pwarp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.utils.UUIDSerializer
import java.io.File
import java.util.*

@Serializable
class Warp(
  val name: String,
  @Serializable(with = UUIDSerializer::class)
  val owner: UUID,
  val world: String,
  val x: Double,
  val y: Double,
  val z: Double,
  val yaw: Float,
  val pitch: Float
) {
  val location by lazy {
    run {
      Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }
  }

  fun teleport(player: Player) {
    player.teleport(location)
  }

  fun save() {
    val ownerUUID = UserMapManager.getUuidFromName(name) ?: return
    val sep = File.separatorChar
    val file = File(plugin.dataFolder.path + "warps$sep$ownerUUID$sep", "$name.json")
    file.parentFile?.mkdirs()
    file.writer().use { writer ->
      writer.write(Json.encodeToString(this))
    }
  }

  companion object {
    val plugin by lazy {
      PWarp.INSTANCE
    }

    fun load(ownerUUID: UUID, name: String): Warp? {
      val sep = File.separatorChar
      val file = File(plugin.dataFolder.path + "${sep}warps${sep}$ownerUUID${sep}", "$name.json")

      if (!file.exists()) return null

      return try {
        Json.decodeFromString<Warp>(file.readText())
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}