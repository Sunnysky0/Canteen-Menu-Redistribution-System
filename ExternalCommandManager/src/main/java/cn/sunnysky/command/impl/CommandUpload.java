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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.sunnysky.IntegratedManager.logger;

public class CommandUpload extends Command {
    public static final int UPLOAD_ID = 1003;

    public CommandUpload() {
        super(UPLOAD_ID);
    }


    @SuppressWarnings("NewApi")
    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer, String... args) {
        StringBuilder s = new StringBuilder("CMD:" + UPLOAD_ID + ";" + "ARGS:");

        s.append(Objects.requireNonNullElse(IntegratedManager.temporaryUserActivationCode, "dummy"));

        for (String arg : args) s.append(",").append(arg);

        if (IntegratedManager.temporaryUserActivationCode != null)
            s.append(";AUTH:").append(IntegratedManager.temporaryUserActivationCode);

        writer.println(s);
    }

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

            mapping.put(arg,"selected");
        }

        final UserManager.User user = IntegratedManager.getUserManager().getUserByTUAC(args[0]);

        IntegratedManager.fileManager.createNewFileInstance(user.userName);

        IntegratedManager.fileManager.writeSerializedData(mapping,user.userName);

        return "Menu has uploaded successfully";
    }
}
