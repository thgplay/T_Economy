package com.thgplugins.economy.listener;

import com.thgplugins.economy.controller.UserController;
import com.thgplugins.economy.repository.UserConstants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserListener implements Listener {

    public UserController controller;

    public UserListener(){
        this.controller = UserController.getInstance();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        this.controller.insertOrUpdate(e.getPlayer().getName(), UserConstants.DEFAULT_VALUE);
    }

}
