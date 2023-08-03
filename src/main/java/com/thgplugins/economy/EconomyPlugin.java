package com.thgplugins.economy;

import com.thgplugins.economy.command.BalCommand;
import com.thgplugins.economy.command.EarnCommand;
import com.thgplugins.economy.command.GiveCommand;
import com.thgplugins.economy.command.SetBalCommand;
import com.thgplugins.economy.controller.UserController;
import com.thgplugins.economy.interfaces.IConstructor;
import com.thgplugins.economy.interfaces.IController;
import com.thgplugins.economy.listener.UserListener;
import com.thgplugins.economy.repository.AsyncSQLAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EconomyPlugin extends JavaPlugin implements IConstructor {

    @Getter
    private static EconomyPlugin instance;

    private List<IController> controllerList;

    @Getter
    private static AsyncSQLAPI SQL;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        init();
    }

    @Override
    public void onDisable() {
        this.controllerList.forEach(IController::unload);
        SQL.close();
    }

    @Override
    public void init() {

        /* Setup databases */
        SQL = new AsyncSQLAPI(this);

        initControllers();
        initCommands();
        initListeners();
        initTasks();
    }

    @Override
    public void initControllers() {
        this.controllerList = Stream.of(
                new UserController(),
                new UserController()
                ).collect(Collectors.toCollection(ArrayList::new));
        this.controllerList.forEach(IController::init);
        this.controllerList.forEach(IController::loadRepositories);
    }

    @Override
    public void initCommands() {
        register(
                new BalCommand(this),
                new EarnCommand(this),
                new GiveCommand(this),
                new SetBalCommand(this)
        );
    }

    @Override
    public void initListeners() {
        register(this, new UserListener());
    }

    @Override
    public void initTasks() {

    }
}
