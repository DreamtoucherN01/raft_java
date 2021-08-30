package com.blake.conf;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.logging.Logger;

import static java.lang.System.out;

// java -jar target/raft-jar-with-dependencies.jar --http_server --log_path /tmp/test
public class ConfigurationArgs {

    Logger logger = Logger.getLogger(ConfigurationArgs.class.getName());

    @Option(name = "-http_server", aliases = "--hs", usage = "是否启动http server，1表示启动，0表示不启动")
    private boolean http_server;

    @Option(name = "-log_path", aliases = "--lg", usage = "日志数据文件地址")
    private String log_path;

    @Option(name = "-http_port", aliases = "--hp", usage = "http 服务端口配置")
    private int httpPort;

    @Option(name = "-peer_connect", aliases = "--pn", usage = "服务器内部访问当前机器ip端口设置")
    private String ipPort;

    public void parseArgs(String args[]) {

        final CmdLineParser parser = new CmdLineParser(this);
        if (args.length < 1) {
            parser.printUsage(out);
            logger.info("args is empty, using local config");
        }
        try {
            parser.parseArgument(args);
        } catch (CmdLineException  clEx) {
            logger.info("ERROR: Unable to parse command-line options: " + clEx);
            System.exit(-1);
        }
        logger.info(this.toString());
    }

    public boolean isHttp_server() {
        return http_server;
    }

    public String getLog_path() {
        return log_path;
    }

    public String getIpPort() {
        return ipPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    @Override
    public String toString() {
        return "ConfigurationArgs{" +
                "http_server=" + http_server +
                ", log_path='" + log_path + '\'' +
                ", httpPort=" + httpPort +
                ", ipPort='" + ipPort + '\'' +
                '}';
    }
}
