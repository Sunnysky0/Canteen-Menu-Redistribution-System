package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.RequirePermission;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserPermission;

public class CommandDirectControl extends Command {
    public static final int CONTROL_ID = 1008;

    public CommandDirectControl() {
        super(CONTROL_ID);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    @RequirePermission(value = UserPermission.OP)
    public String onReceive(String... args) {
        if(IntegratedManager.server != null)
            for (String cmd : args)
                IntegratedManager.server.onCommonCommand(cmd);
        return "Command executed";
    }
}
