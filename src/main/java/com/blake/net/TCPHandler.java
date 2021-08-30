package com.blake.net;

import com.blake.common.MessageFormat;
import com.blake.common.Peer;
import com.blake.queue.MessageTuple;
import com.blake.queue.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class TCPHandler implements NetHandler{

    private static final int MAX_THREADS=10;

    public String ip;
    public int port;

    private boolean isFinished;
    private ServerSocket serverSocket;
    ExecutorService serverTpe = newFixedThreadPool(11);
    ExecutorService clientTpe = newFixedThreadPool(10);
    Logger logger = Logger.getLogger(TCPHandler.class.getName());


    public TCPHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void runTcpServer() {

        logger.info("run TCP server");
        isFinished = false;
        // 启动client线程，发送信息
        logger.info(" run tcp client server list ");
        for (int i=0; i<MAX_THREADS; i++) {
            ClientThread clientThread = new ClientThread(isFinished);
            clientTpe.submit(clientThread);
        }
        // 启动server线程，接收信息
        logger.info(" run tce server server list ");
        try {
            //创建服务器套接字，绑定到指定的端口
            serverSocket = new ServerSocket(port);
            //等待客户端连接
            while (!isFinished) {

                try {
                    //接受连接
                    Socket socket = serverSocket.accept();
                    //创建线程处理连接
                    SocketThread socketThread = new SocketThread(socket);
                    serverTpe.submit(socketThread);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public void close() {

        this.isFinished = true;
        this.serverTpe.shutdown();
        this.clientTpe.shutdown();
    }

    @Override
    public void send(String msg) {


    }

    @Override
    public void receive() {

    }

    @Override
    public String toString() {
        return "TCPHandler{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    private class ClientThread implements Runnable {

        boolean isFinished;

        ExecutorService tpe = newFixedThreadPool(10);

        public ClientThread(boolean isFinished) {
            this.isFinished = isFinished;
        }

        @Override
        public void run() {


            while (true) {

                logger.info(" client fetch and deal ");
                MessageTuple mt = Messages.getMessagesInstance().getMessageTuple();
                if(mt != null) {
                    send(mt);
                }
            }
        }

        public void send(MessageTuple messages) {


            Peer peer = messages.getPeer();
            BufferedReader in = null;
            PrintWriter out = null;
            try{
                Socket s = new Socket(peer.getIp(), peer.getPort());
                out = new PrintWriter(new BufferedWriter
                        (new OutputStreamWriter(s.getOutputStream())), true);
                out.write(messages.getMessageFormat().toMessage());
                out.flush();
            } catch(IOException e){
                logger.info("send socket connect exception: " + e.getMessage());
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.info( "send output stream exception: " + e.getMessage());
                }
            }
        }
    }

    private class SocketThread implements Runnable {

        private Socket socket;
        private InputStream in;
        private OutputStream out;

        SocketThread(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            if (in == null) {
                return;
            }
            try {
                int available = in.available();
                if (available > 0) {
                    byte[] buffer = new byte[available];
                    int size = in.read(buffer);
                    if (size > 0) {
                        String data = new String(buffer,0,size);
                        logger.info("TCPClient say :" + data);
                        MessageFormat ms = MessageFormat.fromString(data);
                        Messages.getMessagesInstance().handleMessage(ms);
                        // 返回结果给TcpClient
                        String response = "OK";
                        out.write(response.getBytes());
                        out.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void close() {

            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
