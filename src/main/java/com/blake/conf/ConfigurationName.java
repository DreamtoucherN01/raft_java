package com.blake.conf;

import com.blake.common.Peer;

public enum  ConfigurationName {

    idName("id", 1),
    peersName("peers", 2),
    electionTickName("election_tick", 3),
    heartbeatTickName("heartbeat_tick", 4),
    logPathName("log_path", 5),
    httpPort("http_port", 6),
    needHttp("need_http", 7),
    unknownName("unknown", 1000);

    String name;
    int sequence;

    ConfigurationName(String name, int sequence) {

        this.name = name;
        this.sequence = sequence;
    }

    public static ConfigurationName fromConfigurationName(String name) {

        for(ConfigurationName c:ConfigurationName.values()) {

            if(c.name.equals(name)) {

                return c;
            }
        }
        return ConfigurationName.unknownName;
    }

}
