package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.RequirePermission;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserPermission;

public class CommandCalculateMenu extends Command {
    public static final int CALCULATE_ID = 1007;

    public CommandCalculateMenu() {
        super(CALCULATE_ID);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    @RequirePermission(value = UserPermission.OP)
    public String onReceive(String... args) {
        if (IntegratedManager.server != null)
            IntegratedManager.server.onCalculate();
        return "Menu refreshed";
    }
}
