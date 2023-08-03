package com.thgplugins.economy.model;

import com.thgplugins.economy.controller.UserController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Getter
    private long id;

    @Getter
    private String name;

    @Getter
    private double balance;

    @SneakyThrows
    public User(ResultSet rs){
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.balance = rs.getDouble("balance");
    }

    public void addBalance(double value){
        this.setBalance(this.balance + value);
    }

    public void setBalance(double balance){
        this.balance = Math.max(0, balance);
        this.update();
    }

    public void withdrawBalance(double value){
        this.setBalance(this.balance - value);
    }

    private CompletableFuture<Void> update(){
        return UserController.getInstance().insertOrUpdate(this.name, this.balance);
    }

}
