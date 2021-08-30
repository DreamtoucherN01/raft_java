package com.blake.http;


import com.blake.raft.Raft;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class RestGetHandler implements HttpHandler {

    Logger logger = Logger.getLogger(RestGetHandler.class.getName());

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

            while (iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\r\n";
                responseBody.write(s.getBytes());
            }
            //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            logger.info(query);
            String ret = raft.getConfiguration().getConfigStr();
            // send response
            String response = "";
            response += ret + "\r\n";
            responseBody.write(response.getBytes());
            responseBody.close();
        }
    }

    public void setRaft(Raft raft) {
        this.raft = raft;
    }
}
