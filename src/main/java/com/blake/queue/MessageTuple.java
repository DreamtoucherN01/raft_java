package com.blake.queue;

import com.blake.common.MessageFormat;
import com.blake.common.Peer;

public class MessageTuple {

    Peer peer;
    MessageFormat messageFormat;

    public MessageTuple(Peer peer, MessageFormat messageFormat) {
        this.peer = peer;
        this.messageFormat = messageFormat;
    }

    public Peer getPeer() {
        return peer;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    @Override
    public String toString() {
        return "MessageTuple{" +
                "peer=" + peer +
                ", messageFormat=" + messageFormat +
                '}';
    }
}
