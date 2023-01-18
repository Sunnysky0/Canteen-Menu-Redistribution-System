package cn.sunnysky.network;

import android.accounts.NetworkErrorException;
import android.content.Context;
import client.ClientBase;
import cn.sunnysky.IntegratedManager;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.api.LogType;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.command.CommandManager;
import cn.sunnysky.command.impl.*;
import cn.sunnysky.security.SecurityManager;
import cn.sunnysky.util.AndroidClassUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;

public class NetworkHandler {

    private ClientBase client;
    private Context ctx;

    public ClientBase getClient() {
        return client;
    }

    public NetworkHandler(Context ctx) throws NetworkErrorException {
        this.ctx = ctx;
        if (!connect()){
            IntegratedManager.logger.log("Unable to connect server!",LogType.ERROR);
            throw new NetworkErrorException();
        }
    }

    private boolean connect(){
        final Boolean[] flag = {null};

        CommandManager.setClassUtil(new AndroidClassUtil(ctx));

        StudentClientApplication.join(
                () -> {
                    try {
                        this.client = StudentClientApplication.INSTANCE.createClient();

                        flag[0] = true;

                    } catch (IOException e) {
                        flag[0] = false;
                        e.printStackTrace();
                    }
                }
        );

        long limit = System.currentTimeMillis() + 3000;

        while(flag[0] == null && System.currentTimeMillis() < limit)
            continue;

        if (flag[0] == null)
            flag[0] = false;

        return flag[0];
    }

    public String login(String userName,String pwd) throws IOException {
        if (client == null) client = StudentClientApplication.INSTANCE.createClient();
        try {
            return client.sendCmd(CommandLogin.LOGIN_ID,userName,pwd);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public String register(String userName, String pwd){
        assert client != null;
        try {
            return client.sendCmd(CommandRegister.REGISTER_ID,userName,pwd);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public String uploadMenu(String... menu){
        assert client != null;
        try {
            return client.sendCmd(CommandUpload.UPLOAD_ID,menu);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public String getRecommendedMenu(){
        assert client != null;
        try {
            return client.sendCmd(CommandGetMenu.MENU_ID);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public String sendReq(String... req){
        assert client != null;
        try {
            return client.sendCmd(CommandRequest.REQ_ID,req);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public String sendRating(String... rating){
        assert client != null;
        try {
            return client.sendCmd(CommandNewRating.RATING_ID,rating);
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();

            return "ERR: Network failure";
        }
    }

    public void disconnect(){
        try {
            client.sendCmd(CommandDisconnect.DISCONNECT_ID);
            client = null;
        } catch (IOException e) {
            IntegratedManager.logger.log("Network error", LogType.ERROR);
            e.printStackTrace();
        }
    }

    public boolean synchronize(String downloadPath) throws IOException, URISyntaxException {
        File dir = new File(downloadPath);
        if (!dir.exists())
            dir.mkdirs();

        File indicator = new File(downloadPath,"idc.pom");
        if (! indicator.exists() )
            transferRemoteFile(".//idc.pom",downloadPath + "/idc.pom");

        final Map<String,String> data =
                ((DefaultFileManager) IntegratedManager.fileManager).readSerializedDataFromFile(indicator.toURI(), null);

        boolean flag = false;

        if (data == null) {
            IntegratedManager.logger.log("Null data");
            return false;
        }

        for (String k : data.keySet()){
            File f = new File(downloadPath,k);

            if ( f.exists() )
                f.delete();

            flag = transferRemoteFile(".//" + k,downloadPath + "/" + k);

            if (!flag)
                IntegratedManager.logger.log("Download Error",LogType.ERROR);
        }

        return flag;
    }

    public boolean transferRemoteFile(String remoteFilePath, String localFilePath) throws IOException { return client.getClientFtpHandler().download(remoteFilePath,localFilePath); }
}
