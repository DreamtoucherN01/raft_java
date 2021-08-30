package com.blake.net;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NetSender {

    /*
        维护所有的peer信息待发送
     */
    private ConcurrentHashMap<String, NetHandler> handler = new ConcurrentHashMap();

    public void sendMessage(String peerId, String msg) throws Exception {

        NetHandler peerInfo = handler.get(peerId);
        peerInfo.send(msg);
    }

    public void sendMessgage2All(String msg) throws Exception {

        handler.forEach((k,v) -> v.send(msg));
    }

}
