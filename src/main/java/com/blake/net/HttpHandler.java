package com.blake.net;

import com.blake.http.RestGetHandler;
import com.blake.http.RestVoteGetHandler;
import com.blake.http.RestVoteHandler;
import com.blake.raft.Raft;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HttpHandler{

    Logger logger = Logger.getLogger(HttpHandler.class.getName());

    private Raft raft;

    public HttpHandler(Raft raft) {

        this.raft = raft;
    }

    public void runHttpServer(int port) throws Exception {

        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(port), 100);

        RestGetHandler handler = new RestGetHandler();
        handler.setRaft(raft);
        RestVoteHandler restVoteHandler = new RestVoteHandler();
        restVoteHandler.setRaft(raft);
        RestVoteGetHandler restVoteGetHandler = new RestVoteGetHandler();
        restVoteGetHandler.setRaft(raft);

        httpserver.createContext("/get_config", handler);
        httpserver.createContext("/set_info", restVoteHandler);
        httpserver.createContext("/get_info", restVoteGetHandler);

        httpserver.setExecutor(null);
        httpserver.start();
        logger.info("http server started");

    }


}
