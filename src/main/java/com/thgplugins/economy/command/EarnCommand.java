package com.thgplugins.economy.command;

import com.google.common.collect.Maps;
import com.thgplugins.economy.controller.UserController;
import com.thgplugins.economy.repository.UserConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class EarnCommand extends AbstractCommand{

    private final Map<UUID, Instant> delay = Maps.newHashMap();

    public EarnCommand(@NotNull Plugin plugin) {
        super(plugin, "earn");
        setPermission(UserConstants.EARN_PERMISSION);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return true;

        var opt = Optional.ofNullable(this.delay.get(player.getUniqueId()));

        if (opt.isPresent()){
            var cd = opt.get();
            var now = Instant.now();
            if (cd.isAfter(now)){
                sender.sendMessage(formatTime(now, cd));
                return true;
            }
            this.delay.remove(player.getUniqueId());
        }


        UserController.getInstance().fetch(player).thenAccept(optt -> optt.ifPresentOrElse(user -> {
            var randomNumber = (int)(Math.random() * 5) + 1;
            user.addBalance(randomNumber);
            sender.sendMessage(String.format("§e§lBALANCE §6➜ §fYou own §a$%s§f.", NumberFormat.getInstance().format(user.getBalance())));
            this.delay.put(player.getUniqueId(), Instant.now().plusSeconds(5 * 60));
        }, () -> {
            sender.sendMessage("§e§lBALANCE §c✘ §fPlayer is not found.");
        }));



        return false;
    }

    private String formatTime(Instant time1, Instant time2){
        var duration = Duration.between(time1, time2);

        var sb = new StringBuilder("§e§lBALANCE §c✘ §fYou need to wait ");
        var seconds = duration.toSeconds() % 60;

        if (duration.toMinutes() > 0)
            sb.append(duration.toMinutes()).append(" minutes");

        if (duration.toMinutes() > 0 && seconds > 0)
            sb.append(" and ");

        if (seconds > 0)
            sb.append(seconds).append(" seconds");

        sb.append(" to run this command again.");
        return sb.toString();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        return Collections.emptyList();
    }
}
