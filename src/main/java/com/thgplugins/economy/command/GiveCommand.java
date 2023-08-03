package com.thgplugins.economy.command;

import com.google.common.collect.Lists;
import com.thgplugins.economy.controller.UserController;
import com.thgplugins.economy.repository.UserConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class GiveCommand extends AbstractCommand{

    public GiveCommand(@NotNull Plugin plugin) {
        super(plugin, "give");
        setPermission(UserConstants.BAL_PERMISSION);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if (args.length <= 1){
            sender.sendMessage("§e§lBALANCE §c✘ §fUse: /give <player> <amount>.");
            return true;
        }

        var target = args[0];
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        }catch (Exception ex){
            sender.sendMessage("§e§lBALANCE §c✘ §fPut a valid number.");
            return true;
        }

        UserController.getInstance().fetch(target).thenAccept(opt -> opt.ifPresentOrElse(user -> {
            user.addBalance(amount);
            sender.sendMessage(String.format("§e§lBALANCE §6➜ §fYou have added §a$%s §fto the §d%s§f.", NumberFormat.getInstance().format(user.getBalance()), user.getName()));
        }, () -> {
            sender.sendMessage("§e§lBALANCE §c✘ §fPlayer is not found.");
        }));



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
