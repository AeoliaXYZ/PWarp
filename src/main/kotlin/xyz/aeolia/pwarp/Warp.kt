  package xyz.aeolia.pwarp

  import kotlinx.serialization.Serializable
  import kotlinx.serialization.Transient
  import kotlinx.serialization.json.Json
  import org.bukkit.Bukkit
  import org.bukkit.Location
  import org.bukkit.entity.Player
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

    constructor(name: String,
      owner: UUID,
      location: Location) : this(
        name,
        owner,
        location.world.name,
        location.x,
        location.y,
        location.z,
        location.yaw,
        location.pitch
    )
    private val file by lazy {
      File(plugin.dataFolder.path + "warps${File.separatorChar}$owner", "$name.json")
    }

    @Transient
    val location = Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)

    fun teleport(player: Player) {
      player.teleport(location)
    }

    fun save() {
      file.parentFile?.mkdirs()
      file.writer().use { writer ->
        writer.write(Json.encodeToString(this))
      }
    }

    fun delete() {
      file.delete()
    }

    companion object {
      val plugin by lazy {
        PWarp.INSTANCE
      }

      fun load(owner: UUID, name: String): Warp? {
        val sep = File.separatorChar
        val file = File(plugin.dataFolder.path + "${sep}warps${sep}$owner${sep}", "$name.json")

        if (!file.exists()) return null

        return try {
          Json.decodeFromString<Warp>(file.readText())
        } catch (e: Exception) {
          e.printStackTrace()
          return null
        }
      }
    }

    override fun toString(): String = "Warp(name: $name, owner: $owner, x: $x, y: $y, z: $z, yaw: $yaw, pitch: $pitch)"
  }