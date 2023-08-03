package com.thgplugins.economy.command;

import com.google.common.collect.Lists;
import com.thgplugins.economy.controller.UserController;
import com.thgplugins.economy.repository.UserConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class BalCommand extends AbstractCommand{

    public BalCommand(@NotNull Plugin plugin) {
        super(plugin, "bal");
        setAliases("balance");
        setPermission(UserConstants.BAL_PERMISSION);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0){
            if (!(sender instanceof Player player)){
                sender.sendMessage("§e§lBALANCE §c✘ §fUse: /bal <player>.");
                return true;
            }

            UserController.getInstance().fetch(player.getName()).thenAccept(opt
                    -> opt.ifPresentOrElse(user
                            -> sender.sendMessage(String.format("§e§lBALANCE §6➜ §fYour balance: §a$%s§f.", NumberFormat.getInstance().format(user.getBalance()))),
                    () -> sender.sendMessage("§e§lBALANCE §c✘ §fPlayer is not found.")));
        } else {

            var target = args[0];

            UserController.getInstance().fetch(target).thenAccept(opt
                    -> opt.ifPresentOrElse(user
                            -> sender.sendMessage(String.format("§e§lBALANCE §6➜ §fBalance from §e%s§f: §a$%s§f.", user.getName(), NumberFormat.getInstance().format(user.getBalance()))),
                    () -> sender.sendMessage("§e§lBALANCE §c✘ §fPlayer is not found.")));


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        if (args.length == 1) {
            var playersNameList = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
            return StringUtil.copyPartialMatches(args[0], playersNameList, Lists.newArrayList());
        }
        return Collections.emptyList();
    }
}
