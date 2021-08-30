package com.blake.queue;

import com.blake.common.MessageFormat;
import com.blake.common.Peer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Messages {

    static Messages messages;

    static Logger logger = Logger.getLogger(Messages.class.getName());

    private LinkedBlockingQueue<MessageTuple> fifoQueue = new LinkedBlockingQueue();

    private LinkedBlockingQueue<MessageFormat> receiveQueue = new LinkedBlockingQueue();

    private LinkedBlockingQueue<MessageFormat> heartBeatControlQueue = new LinkedBlockingQueue<>();

    public static Messages getMessagesInstance() {

        if(messages == null) {

            synchronized (Messages.class) {

                if(messages == null) {

                    messages = new Messages();
                }
            }
        }
        return messages;
    }

    public boolean sendHeartBeat(MessageFormat messageFormat, Peer peer) {

        MessageTuple messageTuple = new MessageTuple(peer, messageFormat);
        if(fifoQueue.size() % 10 == 0) {
            logger.info(" queue size : " + this.fifoQueue.size());
        }
        return fifoQueue.offer(messageTuple);
    }

    public MessageTuple getMessageTuple() {

        try {
            MessageTuple messageTuple = fifoQueue.take();
            logger.info(messageTuple.toString());
            return messageTuple;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return null;
    }

    // 接收到peer发来的信息，交给raft处理message
    public boolean handleMessage(MessageFormat ms) {
        return receiveQueue.offer(ms);
    }

    // 拉取peer 发来的message，进行选举or心跳处理
    public MessageFormat getMessageFormat() {

        try {
            MessageFormat ms = receiveQueue.take();
            logger.info(ms.toString());
            return ms;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return null;
    }

    // 处理heartBeat的队列
    public boolean sendHeartBeatQueue2Deal(MessageFormat ms) {

        return this.heartBeatControlQueue.offer(ms);
    }

    public MessageFormat handleHeartBeatQueue() {

        try {
            MessageFormat ms = heartBeatControlQueue.take();
            logger.info(ms.toString());
            return ms;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return null;
    }


}
