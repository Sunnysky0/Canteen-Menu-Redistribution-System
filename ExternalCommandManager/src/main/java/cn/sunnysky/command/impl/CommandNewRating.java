package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.RequirePermission;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserManager;
import cn.sunnysky.user.UserPermission;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.sunnysky.IntegratedManager.logger;

public class CommandNewRating extends Command {

    public static final int RATING_ID = 1006;

    public CommandNewRating() {
        super(RATING_ID);
    }


    @SuppressWarnings("NewApi")
    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        StringBuilder s = new StringBuilder("CMD:" + RATING_ID + ";" + "ARGS:");

        s.append(Objects.requireNonNullElse(IntegratedManager.getTemporaryUserActivationCode(), "dummy"));

        for (String arg : args) s.append(",").append(arg);

        if (IntegratedManager.getTemporaryUserActivationCode() != null)
            s.append(";AUTH:").append(IntegratedManager.getTemporaryUserActivationCode());

        writer.println(s);
    }

    @SuppressWarnings("NewApi")
    @Override
    @SideOnly(value = Side.SERVER)
    @RequirePermission(value = UserPermission.STUDENT)
    public String onReceive(String... args) {
        Map<String,String> mapping = new HashMap<>();
        int i = 0;
        for (String arg : args) {
            if (i == 0) {
                i ++;
                continue;
            } else if (arg.equalsIgnoreCase("dummy"))
                continue;

            logger.log(arg);

            final String[] strings = arg.split("-");
            mapping.put(strings[0], strings[1]);
        }

        final UserManager.User user = IntegratedManager.getUserManager().getUserByTUAC(args[0]);

        final String fileName = user.userName + "-Rating-" + Date.from(Instant.ofEpochSecond(System.currentTimeMillis()));
        IntegratedManager.fileManager.createNewFileInstance(fileName);

        IntegratedManager.fileManager.writeSerializedData(mapping,fileName);

        return "Rating received";
    }
}
