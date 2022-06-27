package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

public class CommandDisconnect extends Command {
    public static final int DISCONNECT_ID = 999;

    public CommandDisconnect() {
        super(DISCONNECT_ID);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        writer.println("CMD:" + DISCONNECT_ID);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {
        return "DEACTIVATE";
    }
}
