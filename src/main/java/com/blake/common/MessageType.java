package com.blake.common;

public enum MessageType {

    CLIENT(1, "client"),
    CANDIDATE(2, "vote"),
    HEARTBEAT(3, "hb"),
    DATA_TRANSFER(4, "dt");

    int sequence;
    String info;

    MessageType(int sequence, String info) {

        this.info = info;
        this.sequence = sequence;
    }

    public static MessageType fromSequence(int sequence) {

        for(MessageType mt:MessageType.values()) {
            if(mt.sequence == sequence) {
                return mt;
            }
        }
        return null;
    }

    public static MessageType fromString(String info) {

        if(info == null) {
            return null;
        }
        for(MessageType mt:MessageType.values()) {
            if(mt.info.equals(info)) {
                return mt;
            }
        }
        return null;
    }

    public int getSequence() {
        return sequence;
    }

    public String getInfo() {
        return info;
    }
}
