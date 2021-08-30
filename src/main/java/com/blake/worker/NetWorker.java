package com.blake.worker;

import com.blake.common.MessageFormat;
import com.blake.common.Peer;
import com.blake.conf.ConfigurationName;
import com.blake.net.HttpHandler;
import com.blake.net.TCPHandler;
import com.blake.queue.Messages;
import com.blake.raft.Raft;
import com.blake.raft.RaftStat;
import com.blake.util.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class NetWorker implements Runnable {

    static Logger logger = Logger.getLogger(NetWorker.class.getName());

    ExecutorService tpe = newFixedThreadPool(2);

    Raft raft;

    public NetWorker(Raft raft) {
        this.raft = raft;
    }

    @Override
    public void run() {

        HttpServer server = new HttpServer(raft);
        TcpServer tcpServer = new TcpServer(raft);
        tpe.submit(tcpServer);
        if(raft.getConfiguration().getValue(ConfigurationName.needHttp).equals("1")) {
            tpe.submit(server);
        }
    }

    // 发送信息到其它peer
    public static void send(Peer p, MessageFormat message) {

        Messages.getMessagesInstance().sendHeartBeat(message, p);
    }

    class HttpServer implements Runnable {

        Raft raft;
        HttpHandler handler;

        public HttpServer (Raft raft){
            this.raft = raft;
            this.handler = new HttpHandler(raft);
        }

        @Override
        public void run() {

            Object httpPort = raft.getConfiguration().getValue(ConfigurationName.httpPort);
            logger.info("http_port info : " + httpPort);
            int port = Integer.valueOf(String.valueOf(httpPort));
            try {
                this.handler.runHttpServer(port);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
    }

    class TcpServer implements Runnable {


        Raft raft;
        TCPHandler tcpHandler;

        public TcpServer(Raft raft) {
            this.raft = raft;
            Peer peer = (Peer) raft.getConfiguration().getValue(ConfigurationName.idName);
            logger.info("cur info : " + peer.getIp() + ",  " + peer.getPort());
            this.tcpHandler = new TCPHandler(peer.getIp(), peer.getPort());
        }

        @Override
        public void run() {

            this.tcpHandler.runTcpServer();
        }


    }
}
