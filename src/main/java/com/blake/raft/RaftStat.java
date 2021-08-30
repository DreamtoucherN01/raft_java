package com.blake.raft;

import com.blake.conf.Configuration;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RaftStat {

    static RaftStat raftStat;
    private Configuration conf;
    private Raft raft;

    private int term = 0;
    final ReentrantReadWriteLock termRwl = new ReentrantReadWriteLock();
    private int lastTerm = 0;
    final ReentrantReadWriteLock lastTermRwl = new ReentrantReadWriteLock();
    private int vote = 0;
    final ReentrantReadWriteLock voteRwl = new ReentrantReadWriteLock();

    private boolean isLeader = false;
    final ReentrantReadWriteLock leaderRwl = new ReentrantReadWriteLock();
    boolean httpServerRun = false;
    final ReentrantReadWriteLock httpRwl = new ReentrantReadWriteLock();
    private boolean hasLeader = false;
    final ReentrantReadWriteLock hasLeaderRwl = new ReentrantReadWriteLock();

    private RaftStat() {}

    public static RaftStat getInstance() {

        if (raftStat == null) {

            synchronized (RaftStat.class) {

                if(raftStat == null) {

                    raftStat = new RaftStat();
                }
            }
        }
        return raftStat;
    }

    public int getTerm() {

        termRwl.readLock().lock();
        int ret = term;
        termRwl.readLock().unlock();
        return ret;
    }

    public int increTermAndGet() {
        termRwl.writeLock().lock();
        term = term + 1;
        int ret = term;
        termRwl.writeLock().unlock();
        return ret;
    }

    public int getVote() {
        voteRwl.readLock().lock();
        int ret = vote;
        voteRwl.readLock().unlock();
        return ret;
    }

    public int increVoteAndGet() {

        voteRwl.writeLock().lock();
        vote = vote + 1;
        int ret = vote;
        voteRwl.writeLock().unlock();
        return ret;
    }

    public boolean isLeader() {
        leaderRwl.readLock().lock();
        boolean ret = isLeader;
        leaderRwl.readLock().unlock();
        return ret;
    }

    public void setIsLeader(boolean leader) {
        leaderRwl.writeLock().lock();
        isLeader = leader;
        leaderRwl.writeLock().unlock();
    }

    public boolean isHttpServerRun() {

        httpRwl.readLock().lock();
        boolean ret = httpServerRun;
        httpRwl.readLock().unlock();
        return ret;
    }

    public void setIsHttpServerRun(boolean run) {
        httpRwl.writeLock().lock();
        httpServerRun = run;
        httpRwl.writeLock().unlock();
    }

    public boolean isHasLeader() {

        hasLeaderRwl.readLock().lock();
        boolean ret = hasLeader;
        hasLeaderRwl.readLock().unlock();
        return ret;
    }

    public void setIsHasLeader(boolean hasLeader) {

        hasLeaderRwl.writeLock().lock();
        this.hasLeader = hasLeader;
        hasLeaderRwl.writeLock().unlock();
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }
}

