package com.blake.storage.memory;

import com.blake.conf.Configuration;

import java.util.concurrent.ConcurrentHashMap;

public class Memory {

    private ConcurrentHashMap<String, String> mStorage = new ConcurrentHashMap<>();

    public Memory(Configuration configuration) {


    }

    public void set(String key, String value) {
        this.mStorage.put(key, value);
    }

    public String get(String key) {
        return this.mStorage.getOrDefault(key, "-1");
    }

    public boolean contain(String key) {
        return this.mStorage.containsKey(key);
    }

    public boolean needFlush() {
        return true;
    }
}
