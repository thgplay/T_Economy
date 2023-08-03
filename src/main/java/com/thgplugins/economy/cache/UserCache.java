package com.thgplugins.economy.cache;

import com.thgplugins.economy.model.User;
import lombok.Getter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
public class UserCache {

    private final ExpiringMap<String, User> cache = ExpiringMap.builder()
            .expiration(10L, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    public Optional<User> fetch(String name){
        return Optional.ofNullable(this.cache.get(name.toLowerCase()));
    }

    public void put(User user){
        this.cache.put(user.getName().toLowerCase(), new User());
    }

}
