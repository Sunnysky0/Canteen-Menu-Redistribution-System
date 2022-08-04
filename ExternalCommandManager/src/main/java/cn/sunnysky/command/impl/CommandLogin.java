package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.security.SecurityManager;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

public class CommandLogin extends Command {
    public static final int LOGIN_ID = 1001;

    public CommandLogin() {
        super(LOGIN_ID);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        String msg = "CMD:" + LOGIN_ID + ";ARGS:" + args[0] + "," + SecurityManager.hashNTLM(args[1]);
        writer.println(msg);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {
        return IntegratedManager.getUserManager().login(args[0],args[1]);
    }
}
