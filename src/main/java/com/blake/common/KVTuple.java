package com.blake.common;

public class KVTuple {

    private Object key;
    private Object value;

    public KVTuple() {
        this.key = "default";
        this.value = "0";
    }

    public KVTuple(String info) {

        Object [] objects = info.split("=");
        this.key = objects[0];
        this.value = objects[1];
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String formatKvTuple() {
        return key + "=" + value;
    }

    @Override
    public String toString() {
        return "KVTuple{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
