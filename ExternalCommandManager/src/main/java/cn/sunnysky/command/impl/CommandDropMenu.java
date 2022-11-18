package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.command.Command;

public class CommandDropMenu extends Command {
    public static final int DROP_MENU_ID = 1009;

    public CommandDropMenu() {
        super(DROP_MENU_ID);
    }

    @Override
    public String onReceive(String... args) {
        if (IntegratedManager.server != null)
            IntegratedManager.server.dropMenu();
        return "Menu dropped";
    }
}
