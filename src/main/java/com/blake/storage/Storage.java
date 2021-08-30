package com.blake.storage;

import com.blake.common.VoteInfo;
import com.blake.conf.Configuration;
import com.blake.conf.ConfigurationName;
import com.blake.raft.RaftStat;
import com.blake.storage.disk.Disk;
import com.blake.storage.memory.Memory;
import com.blake.util.FileUtils;
import org.json.JSONObject;

import java.util.logging.Logger;

public class Storage {

    private final Logger logger = Logger.getLogger(Storage.class.getName());

    private static Storage storage;
    private final Memory memory;
    private final Disk disk;

    public static Storage getStorage(Configuration configuration) {

        if (storage == null) {

            synchronized (Storage.class) {

                if(storage == null) {

                    storage = new Storage(configuration);
                }
            }
        }
        return storage;
    }

    private Storage(Configuration configuration){

        memory = new Memory(configuration);
        disk = new Disk(configuration);
        Object logPath = configuration.getValue(ConfigurationName.logPathName);
        FileUtils.makePathIfNotExist(String.valueOf(logPath));
        try {
            FileUtils.createFile(String.valueOf(logPath), leaderInfo);
            FileUtils.createFile(String.valueOf(logPath), dataInfo);
        } catch (Exception e) {
            logger.info(" file create error : " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void store(String key, String value) {

        JSONObject object = new JSONObject();
        object.put("key", key);
        object.put("value", value);
        VoteInfo voteInfo = new VoteInfo(RaftStat.getInstance().getTerm(), RaftStat.getInstance().getVote(), object);
        this.memory.set(key, voteInfo.toString());
        // if (this.memory.needFlush()) {
        //     this.disk.flush(key, value);
        // }
    }



    public String get(String key) {

        if(this.memory.contain(key)) {

            return this.memory.get(key);
        } else {

            String diskReadValue = this.disk.read(key);
            if(diskReadValue != null) {
                this.memory.set(key, diskReadValue);
            }
            return diskReadValue;
        }
    }

    private static final String leaderInfo = "leader_info";
    private static final String dataInfo = "data_info";

}
