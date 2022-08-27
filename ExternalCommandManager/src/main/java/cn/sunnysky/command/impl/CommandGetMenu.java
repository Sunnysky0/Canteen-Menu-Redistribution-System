package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.command.Command;

public class CommandGetMenu extends Command {

    public static final int MENU_ID = 1005;

    public CommandGetMenu() {
        super(MENU_ID);
    }

    @Override
    public String onReceive(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String s : IntegratedManager.recommendedMenu)
            builder.append(s).append(",");

        builder.deleteCharAt(builder.lastIndexOf(","));

        return builder.toString();
    }
}
