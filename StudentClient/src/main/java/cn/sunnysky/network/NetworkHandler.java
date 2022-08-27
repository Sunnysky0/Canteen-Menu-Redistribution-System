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
                        this.client = new ClientBase();

                        flag[0] = true;

                    } catch (IOException e) {
                        flag[0] = false;
                        e.printStackTrace();
                    }
                }
        );


        while(flag[0] == null)
            IntegratedManager.logger.log("Connecting to server");

        return flag[0];
    }

    public String login(String userName,String pwd){
        assert client != null;
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
        File indicator = new File(downloadPath,"idc.pom");
        if (! indicator.exists() )
            transferRemoteFile(".//idc.pom",downloadPath + "/idc.pom");

        final Map<String,String> data =
                ((DefaultFileManager) IntegratedManager.fileManager).readSerializedDataFromFile(indicator.toURI(), null);

        boolean flag = false;

        if (data == null)
            return false;

        for (String k : data.keySet()){
            File f = new File(downloadPath,k);

            if ( !f.exists() )
                flag = transferRemoteFile(".//" + k,downloadPath + "/" + k);
            else if ( !SecurityManager.md5HashCode(new FileInputStream(f)).contentEquals(data.get(k)) ){
                f.delete();
                flag = transferRemoteFile(".//" + k,downloadPath + "/" + k);
            }
        }

        return flag;
    }

    public boolean transferRemoteFile(String remoteFilePath, String localFilePath) throws IOException { return client.getClientFtpHandler().download(remoteFilePath,localFilePath); }
}
