package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

import static cn.sunnysky.IntegratedManager.logger;

public class CommandDisconnect extends Command {
    public static final int DISCONNECT_ID = 999;

    public CommandDisconnect() {
        super(DISCONNECT_ID);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        String s = "CMD:" + DISCONNECT_ID;
        if (IntegratedManager.getTemporaryUserActivationCode() != null)
            s += ";ARGS:" + IntegratedManager.getTemporaryUserActivationCode();
        writer.println(s);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {
        if(args != null && args.length >= 1){
            IntegratedManager.getUserManager().logout(args[0]);
            logger.log("An user logged out");
        }
        return "DEACTIVATE";
    }
}
