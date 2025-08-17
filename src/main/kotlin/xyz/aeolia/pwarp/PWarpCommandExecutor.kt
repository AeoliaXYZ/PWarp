package xyz.aeolia.pwarp

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.manager.UserMapManager

class PWarpCommandExecutor : TabExecutor {
  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): List<String> {
    val commands = mutableListOf<String>()
    val completions = mutableListOf<String>()
    if (args.size == 1)
      Bukkit.getOnlinePlayers().forEach {
        commands.add(it.name)
        StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
      }
    else if (args.size == 2) {
      val uuid = UserMapManager.getUuidFromName(args[0]) ?: return completions
      val wPlayer = WarpPlayer.loadPlayer(uuid)
      wPlayer.warps.forEach {
        commands.add(it.name)
      }
      StringUtil.copyPartialMatches<MutableList<String>>(args[1], commands, completions)
    }
    completions.sort()
    return completions
  }

  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    TODO("Not yet implemented")
  }
}