package com.blake.worker;

import com.blake.common.KVTuple;
import com.blake.common.MessageFormat;
import com.blake.common.MessageType;
import com.blake.common.Peer;
import com.blake.conf.Configuration;
import com.blake.conf.ConfigurationName;
import com.blake.queue.Messages;
import com.blake.raft.Raft;
import com.blake.raft.RaftStat;
import com.blake.util.StringUtils;
import com.blake.util.ThreadUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/*
    主要工作，timeout时候发起candidate请求
 */
public class NodeWorker implements Runnable {

    static Logger logger = Logger.getLogger(NodeWorker.class.getName());

    Raft raft;

    private HeartBeater heartBeater;
    private ElectionEmiter electionEmiter;
    private Thread heartBeaterThread;
    private Thread electionEmiterThread;

    public NodeWorker(Raft raft) {

        this.raft = raft;
        this.heartBeater = new HeartBeater(raft);
        this.electionEmiter = new ElectionEmiter(raft);
        heartBeaterThread = new Thread(this.heartBeater);
        heartBeaterThread.start();
        electionEmiterThread = new Thread(this.electionEmiter);
        electionEmiterThread.start();
    }

    @Override
    public void run() {


        while (true) {

            // 1. 先判断是不是leader，不是leader停止发心跳
            if(!RaftStat.getInstance().isLeader())  {

                if(RaftStat.getInstance().isHasLeader()) {
                    heartBeater.stop();
                } else {
                    int term = RaftStat.getInstance().getTerm();
                    int vote = RaftStat.getInstance().getVote();
                    elect(term, vote);
                }

            } else {
                heartBeater.start();
            }
            // 2. 是否能拉到心跳信息，没有的话，就开启选举
            MessageFormat ms = Messages.getMessagesInstance().handleHeartBeatQueue();
            if(ms != null) {

                electionEmiterThread.interrupt();
            }
        }

    }

    public static void elect(int term, int vote) {
        // logger.info(" begin election loop ");
        // int term = RaftStat.getInstance().increTermAndGet();

        // logger.info(" end election loop ");
    }

    public static class ElectionEmiter implements Runnable {

        Logger logger = Logger.getLogger(ElectionEmiter.class.getName());

        Raft raft;

        int electionTimeout;

        public ElectionEmiter(Raft raft) {
            this.raft = raft;
            this.electionTimeout = StringUtils.parseIntFromObject(Configuration.getConfiguration().getValue(ConfigurationName.electionTickName));
        }

        @Override
        public void run() {

            while (true) {

                try {
                    Thread.sleep(electionTimeout + StringUtils.randomNext(electionTimeout));
                    int term = RaftStat.getInstance().getTerm();
                    int vote = RaftStat.getInstance().getVote();
                    NodeWorker.elect(term+1, vote);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.info(" meet interrupt, begin another sleep ");
                }
            }
        }

    }

    public static class HeartBeater implements Runnable {

        Logger logger = Logger.getLogger(HeartBeater.class.getName());

        Raft raft;

        private boolean run = false;

        public HeartBeater(Raft raft) {
            this.raft = raft;
        }

        @Override
        public void run() {

            Peer curPeer = (Peer) raft.getConfiguration().getValue(ConfigurationName.idName);
            logger.info("cur peer: " + curPeer);
            int heartBeatTickTime = StringUtils.parseIntFromObject(
                    raft.getConfiguration().getValue(ConfigurationName.heartbeatTickName));
            logger.info("heartBeatTickTime: " + heartBeatTickTime);
            HashSet<Peer> peers = (HashSet<Peer>)raft.getConfiguration().getValue(ConfigurationName.peersName);
            List<Peer> others = new ArrayList<>();
            for(Peer p: peers) {

                if(!p.equals(curPeer)) {
                    others.add(p);
                }
            }
            logger.info("cur machine other peers : " + others.toString());
            while (true) {

                ThreadUtils.sleepMillis(heartBeatTickTime + StringUtils.randomNext(heartBeatTickTime));
                if(!run) {
                    // logger.info(" not run heartbeat, please check if is leader ");
                    continue;
                }
                for(Peer p: others) {

                    MessageFormat m = new MessageFormat(MessageType.HEARTBEAT.getInfo(), new KVTuple(),
                            RaftStat.getInstance().getTerm(), RaftStat.getInstance().getVote());
                    NetWorker.send(p, m);
                }
            }
        }

        public void stop() {
            this.run = false;
        }

        public void start() {
            this.run = true;
        }
    }
}
