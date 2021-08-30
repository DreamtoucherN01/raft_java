package com.blake.http;

import com.blake.conf.QueryHelper;
import com.blake.raft.Raft;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class RestVoteHandler implements HttpHandler {

    Logger logger = Logger.getLogger(RestVoteHandler.class.getName());

    Raft raft;

    @Override
    public void handle(HttpExchange he) throws IOException {

        String requestMethod = he.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {

            Headers responseHeaders = he.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");

            he.sendResponseHeaders(200, 0);
            // parse request
            OutputStream responseBody = he.getResponseBody();
            Headers requestHeaders = he.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            StringBuilder sb = new StringBuilder();
            while (iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\r\n";
                sb.append(s);
            }
            logger.info(sb.toString());
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            Map<String, Object> parameters = new HashMap<>();
            QueryHelper.parseQuery(query, parameters);
            Object key = parameters.getOrDefault("key", "");
            Object value = parameters.getOrDefault("value", "");
            raft.getStorage().store(String.valueOf(key), String.valueOf(value));
            logger.info(key + " ; value : " + value);
            // send response
            String response = "OK";
            responseBody.write(response.getBytes());
            responseBody.close();
        }
    }

    public void setRaft(Raft raft) {
        this.raft = raft;
    }
}
