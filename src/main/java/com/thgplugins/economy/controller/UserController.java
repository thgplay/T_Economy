package com.thgplugins.economy.controller;

import com.thgplugins.economy.cache.UserCache;
import com.thgplugins.economy.interfaces.IController;
import com.thgplugins.economy.model.User;
import com.thgplugins.economy.repository.UserRepository;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserController implements IController {

    @Getter
    private static UserController instance;

    private UserRepository repository;

    private UserCache cache;

    @Override
    public void init() {
        instance = this;
        this.cache = new UserCache();
    }

    @Override
    public void loadRepositories() {
        this.repository = new UserRepository();
        this.repository.init();
    }

    @Override
    public void unload() {
        this.repository.close();
    }

    @NotNull
    public CompletableFuture<Optional<User>> fetch(Player player){
        return fetch(player.getName());
    }

    @NotNull
    public CompletableFuture<Optional<User>> fetch(String name){
        var future = new CompletableFuture<Optional<User>>();
        this.cache.fetch(name).ifPresentOrElse(user -> future.complete(Optional.of(user)), () -> this.repository.fetch(name).thenAccept(future::complete));
        return future;
    }

    @NotNull
    public CompletableFuture<Void> insertOrUpdate(String name, double balance){
        return this.repository.insertOrUpdate(name, balance);
    }


}
