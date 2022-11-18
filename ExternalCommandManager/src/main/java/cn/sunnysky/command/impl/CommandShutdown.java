package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.command.Command;

public class CommandShutdown extends Command {
    public static final int SHUTDOWN_ID = 1010;

    public CommandShutdown() {
        super(SHUTDOWN_ID);
    }

    @Override
    public String onReceive(String... args) {
        if (IntegratedManager.server != null)
            IntegratedManager.server.shutdown();
        return "Server shutting down";
    }
}
