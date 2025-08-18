package xyz.aeolia.pwarp

import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message
import java.util.*
import java.util.concurrent.CompletableFuture

class PWarpCommandExecutor : TabExecutor {
  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): List<String> {
    val commands = mutableListOf<String>()
    val completions = mutableListOf<String>()
    if (args.size == 1) {
      commands.add("create")
      commands.add("delete")
      commands.add("list")
      Bukkit.getOnlinePlayers().forEach {
        commands.add(it.name)
        StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
      }
    } else if (args.size == 2) {
      val uuid = UserMapManager.getUuidFromName(args[0]) ?: return completions
      val wPlayer = WarpPlayer.loadPlayer(uuid)
      wPlayer.warps.forEach {
        commands.add(it.name)
      }
      commands.add("list")
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
    val sender = sender.player() ?: return true
    if (args.size == 1)
      if (args[0] == "list") {
        MessageSender.sendMessage(sender, "Your warps:")
        sendAllWarps(sender, sender.uniqueId)
        return true
      } else {
        MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE)
        return false
      }

    if (args.size != 2) {
      MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE)
      return false
    }

    if (args[0] == "create" || args[0] == "delete")
      return true.also { manageHandler(sender, args) }

    val uuid = UserMapManager.getUuidFromName(args[0]) ?: run {
      MessageSender.sendMessage(sender, Message.Player.NOT_FOUND)
      return true
    }
    val wPlayer = WarpPlayer.loadPlayer(uuid)
    if (wPlayer.warps.isEmpty()) {
      MessageSender.sendMessage(sender, "That player has not set any warps!")
      return true
    }

    if (args[1] == "list") {
      MessageSender.sendMessage(sender, "${args[0]}'s warps:")
      sendAllWarps(sender, uuid)
      return true
    }

    wPlayer.warps.find { it.name == args[1] }?.let {
      MessageSender.sendMessage(sender, "Teleporting...")
      it.teleport(sender)
      return true
    }

    MessageSender.sendMessage(sender, "Warp not found.")
    return true
  }

  fun manageHandler(sender: Player, args: Array<out String>) {
    CompletableFuture.supplyAsync {
      val wPlayer = WarpPlayer.loadPlayer(sender.uniqueId)
      val maxWarps = WarpPlayer.maxWarps(sender)
      val location = sender.location.clone()
      when (args[0]) {
        "create" -> {
          if (wPlayer.warps.any { it.name == args[1] }) {
            return@supplyAsync "You already have a warp by that name!"
          }

          if (wPlayer.warps.size >= maxWarps) {
            return@supplyAsync "You have reached your maximum number of warps."
          }

          val warp = Warp(args[1], sender.uniqueId, location)
          warp.save()
          wPlayer.addWarp(warp)
          return@supplyAsync "Warp created!"
        }

        "delete" -> {
          val found = wPlayer.warps.find { it.name == args[1] } ?: return@supplyAsync "Warp not found!"
          wPlayer.removeWarp(found)
          found.delete()
          return@supplyAsync "Warp deleted."
        }

        else -> {
          PWarp.INSTANCE.logger.warning("Unexpected argument ${args[0]}, please check your build.")
          return@supplyAsync Message.Error.GENERIC
        }
      }
    }.thenAccept { message ->
      Bukkit.getScheduler().runTask(PWarp.INSTANCE, Runnable {
        MessageSender.sendMessage(sender, message)
      })
    }
  }

  fun sendAllWarps(recipient: Audience, uuid: UUID) {
    val wPlayer = WarpPlayer.loadPlayer(uuid)
    wPlayer.warps.forEach { warp ->
      MessageSender.sendMessage(recipient, warp.name, false)
    }
  }
}