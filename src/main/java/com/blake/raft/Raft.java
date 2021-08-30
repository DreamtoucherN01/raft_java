package com.blake.raft;

import com.blake.conf.Configuration;
import com.blake.conf.ConfigurationArgs;
import com.blake.storage.Storage;
import com.blake.worker.NetWorker;
import com.blake.worker.NodeWorker;
import com.blake.worker.StatWorker;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.*;

public class Raft {

    ExecutorService tpe = newFixedThreadPool(3);

    private Configuration configuration;
    private Storage storage;

    public static void main(String args[]) {

        Raft raft = new Raft();
        raft.runRaft(args);
    }

    public void runRaft(String args[]) {

        // 解析配置文件，先args，后配置文件。以args为主
        ConfigurationArgs ca = new ConfigurationArgs();
        ca.parseArgs(args);
        configuration = Configuration.getConfiguration();
        configuration.setConfigurationArgs(ca);
        configuration.generateConfiguration();
        RaftStat.getInstance().setConf(configuration);
        storage = Storage.getStorage(configuration);

        // 运行worker
        NodeWorker node = new NodeWorker(this);
        NetWorker netWorker = new NetWorker(this);
        StatWorker statWorker = new StatWorker(this);
        tpe.submit(node);
        tpe.submit(netWorker);
        tpe.submit(statWorker);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Storage getStorage() {
        return storage;
    }
}
