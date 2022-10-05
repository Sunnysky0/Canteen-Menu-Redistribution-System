package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandRequest extends Command {
    public static final int REQ_ID = 1004;

    public CommandRequest() {
        super(REQ_ID);
    }

    @SuppressWarnings("NewApi")
    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        StringBuilder s = new StringBuilder("CMD:" + REQ_ID + ";" + "ARGS:");

        s.append(Objects.requireNonNullElse(IntegratedManager.getTemporaryUserActivationCode(), "dummy"));

        for (String arg : args) s.append(",").append(arg);

        if (IntegratedManager.getTemporaryUserActivationCode() != null)
            s.append(";AUTH:").append(IntegratedManager.getTemporaryUserActivationCode());

        writer.println(s);
    }

    @SuppressWarnings("NewApi")
    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {

        Map<String,String> mapping = new HashMap<>();
        int i = 0;
        for (String arg : args) {
            if (i == 0) {
                i ++;
                continue;
            } else if (arg.equalsIgnoreCase("dummy"))
                continue;

            mapping.put(arg,"expected");
        }

        final UserManager.User user = IntegratedManager.getUserManager().getUserByTUAC(args[0]);

        final String fileName = user.userName + "-Request-" + Date.from(Instant.ofEpochSecond(System.currentTimeMillis()));
        IntegratedManager.fileManager.createNewFileInstance(fileName);

        IntegratedManager.fileManager.writeSerializedData(mapping,fileName);

        return "Request has been received";
    }
}
