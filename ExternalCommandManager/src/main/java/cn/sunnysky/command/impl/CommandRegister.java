package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserPermission;
import cn.sunnysky.user.security.SecurityManager;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

public class CommandRegister extends Command {
    public static final int REGISTER_ID = 1002;

    public CommandRegister() {
        super(REGISTER_ID);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        String msg = "CMD:" + REGISTER_ID + ";ARGS:" + args[0] + "," + SecurityManager.hashNTLM(args[1]);
        writer.println(msg);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {
        if(IntegratedManager.getUserManager().createNewUser(args[0], UserPermission.OP,args[1]))
            return "Successfully registered!";
        return "ERR: Some problems have occurred";
    }
}
