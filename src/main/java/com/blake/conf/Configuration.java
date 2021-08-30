package com.blake.conf;

import com.blake.common.Peer;
import com.blake.util.StringUtils;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Configuration {

    Logger logger = Logger.getLogger(Configuration.class.getName());

    private static Configuration configuration;

    private ConfigurationArgs configurationArgs;

    ConcurrentHashMap<ConfigurationName, Object> config = new ConcurrentHashMap<>();

    public static Configuration getConfiguration() {

        if(configuration == null) {

            synchronized (Configuration.class) {

                if(configuration == null) {

                    configuration = new Configuration();
                }
            }
        }
        return configuration;
    }

    public Configuration() {
    }

    public Object getValue(String key) {

        ConfigurationName c = ConfigurationName.fromConfigurationName(key);
        logger.info(c.name);
        return config.getOrDefault(c.name, "");
    }

    public Object getValue(ConfigurationName configurationName) {
        return config.getOrDefault(configurationName, "");
    }

    public void setValue(String key, String value) {

        ConfigurationName c = ConfigurationName.fromConfigurationName(key);
        config.put(c, value);
    }

    public void setConfigurationArgs(ConfigurationArgs configurationArgs) {
        this.configurationArgs = configurationArgs;
    }

    // can be called once
    public void generateConfiguration() {

        // 先读配置文件
        InputStream raftConfInputStream = Configuration.class.getClassLoader()
                .getResourceAsStream("raft.conf");
        Properties properties = new Properties();
        try {
            properties.load(raftConfInputStream);
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        Enumeration<?> p = properties.propertyNames();
        while (p.hasMoreElements()) {
            String e = (String)p.nextElement();
            ConfigurationName c = ConfigurationName.fromConfigurationName(e);
            if(c == ConfigurationName.idName) {
                String ipPort = properties.getProperty(c.name);
                String[] ipPortList = ipPort.split(":");
                Peer peer = new Peer(ipPortList[0], Integer.valueOf(ipPortList[1]));
                config.put(c, peer);
            } else if(c == ConfigurationName.peersName) {

                String ipList = properties.getProperty(c.name);
                String ipListSplit[] = ipList.split(";");
                HashSet<Peer> peers = new HashSet<>();
                for(int i = 0; i < ipListSplit.length; i++) {

                    String ipPort[] = ipListSplit[i].split(":");
                    Peer peer = new Peer(ipPort[0], Integer.valueOf(ipPort[1]));
                    peers.add(peer);
                }
                config.put(c, peers);
            } else {
                config.put(c, properties.getProperty(c.name));
            }
        }
        // 再用args替换
        if(configurationArgs.isHttp_server()) {
            config.put(ConfigurationName.needHttp, "1");
        } else {
            config.put(ConfigurationName.needHttp, "0");
        }
        if(configurationArgs.getLog_path() != null) {
            config.put(ConfigurationName.logPathName, configurationArgs.getLog_path());
        }
        if(configurationArgs.getHttpPort() > 0) {
            config.put(ConfigurationName.httpPort, configurationArgs.getHttpPort());
        }
        if(configurationArgs.getIpPort() != null) {

            String[] ipPort = configurationArgs.getIpPort().split(":");
            config.put(ConfigurationName.idName, new Peer(ipPort[0], StringUtils.parseIntFromObject(ipPort[1])));
        }
        logger.info("config: " + config);
    }

    public String getConfigStr() {

        return config.toString();
    }
}
