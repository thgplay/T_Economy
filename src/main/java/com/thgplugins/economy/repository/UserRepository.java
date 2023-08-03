package com.thgplugins.economy.repository;

import com.thgplugins.economy.EconomyPlugin;
import com.thgplugins.economy.interfaces.IRepository;
import com.thgplugins.economy.model.User;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Language;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserRepository implements IRepository {

    private AsyncSQLAPI SQL;

    @Override
    public void init() {
        SQL = EconomyPlugin.getSQL();
        createTableIfNotExists();
    }

    @Override
    public void createTableIfNotExists() {

        @Language("MySQL")
        var query = """
                CREATE TABLE IF NOT EXISTS %s
                (
                    id      BIGINT          AUTO_INCREMENT,
                    name    VARCHAR(16)      NOT NULL,
                    balance DOUBLE DEFAULT 0 NOT NULL,
                    CONSTRAINT user_pk
                        PRIMARY KEY (id),
                    CONSTRAINT user_pk2
                        UNIQUE (name)
                )
                    CHARSET = utf8;
                """;

        SQL.executeUpdate(String.format(query, getTable()));
    }

    public CompletableFuture<Optional<User>> fetch(Player player){
        return fetch(player.getName());
    }

    public CompletableFuture<Optional<User>> fetch(String name){

        var future = new CompletableFuture<Optional<User>>();

        @Language("MySQL")
        var query = "SELECT * FROM %s WHERE name LIKE('%%" + name + "%%')";

        SQL.executeQuery(String.format(query, getTable()), rs -> {
            try (rs){

                if (rs.next()){
                    future.complete(Optional.of(new User(rs)));
                } else future.complete(Optional.empty());

            }catch (Exception ex){
                future.completeExceptionally(ex);
            }
        }).exceptionally(SQL::handleExceptionally);

        return future;
    }

    public CompletableFuture<Void> insertOrUpdate(String name, double balance){

        var future = new CompletableFuture<Void>();

        @Language("MySQL")
        var query = """
            INSERT INTO %s (name, balance)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE balance = ?;
        """;

        SQL.executeUpdate(String.format(query, getTable()), name, balance, balance)
                .thenRun(() -> future.complete(null))
                .exceptionally(SQL::handleExceptionally);

        return future;
    }



    public String getTable(){
        return String.format("`%s`.`%s`", SQL.getDatabase(), "user");
    }
}
