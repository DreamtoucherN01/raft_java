package com.blake.worker;

import com.blake.common.MessageFormat;
import com.blake.common.MessageType;
import com.blake.conf.ConfigurationName;
import com.blake.queue.Messages;
import com.blake.raft.Raft;
import com.blake.util.StringUtils;
import com.blake.util.ThreadUtils;

import java.util.logging.Logger;

public class StatWorker implements Runnable {

    Logger logger = Logger.getLogger(StatWorker.class.getName());

    Raft raft;

    int tickTime;

    public StatWorker(Raft raft) {
        this.raft = raft;
        tickTime = StringUtils.parseIntFromObject(raft.getConfiguration().getValue(ConfigurationName.electionTickName));
    }

    @Override
    public void run() {

        while (true) {

            MessageFormat ms = Messages.getMessagesInstance().getMessageFormat();
            MessageType mt = MessageType.fromString(ms.getMessageType());
            if(ms == null || mt == null) {
                logger.info(" ms or mt is null ");
                ThreadUtils.sleepMillis(tickTime + StringUtils.randomNext(tickTime));
                continue;
            }

            switch (mt) {
                case CANDIDATE:
                    System.out.println("1");
                    return;
                case CLIENT:
                    return;
                case HEARTBEAT:
                    Messages.getMessagesInstance().sendHeartBeatQueue2Deal(ms);
                    return;
                case DATA_TRANSFER:
                    return;
                default:
                    logger.info(" ms or mt is null ");
                    ThreadUtils.sleepMillis(tickTime + StringUtils.randomNext(tickTime));
                    continue;
            }
        }
    }
}
