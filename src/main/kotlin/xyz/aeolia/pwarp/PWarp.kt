package xyz.aeolia.pwarp

import org.bukkit.plugin.java.JavaPlugin

class PWarp : JavaPlugin() {

  override fun onEnable() {
    INSTANCE = this
  }

  override fun onDisable() {
    // Plugin shutdown logic
  }

  companion object {
    lateinit var INSTANCE: JavaPlugin
  }
}
