package cn.sunnysky.network;

import android.accounts.NetworkErrorException;
import client.ClientBase;
import cn.sunnysky.IntegratedManager;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.api.LogType;
import cn.sunnysky.command.impl.CommandDisconnect;
import cn.sunnysky.command.impl.CommandLogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class NetworkHandler {

    private ClientBase client;

    public NetworkHandler() throws NetworkErrorException {
        if (!initialize()){
            IntegratedManager.logger.log("Unable to start connect server!",LogType.ERROR);
            throw new NetworkErrorException();
        }
    }

    private boolean initialize(){
        final Boolean[] flag = {null};

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

    public String login(String userName,String encryptedPwd){
        assert client != null;
        try {
            return client.sendCmd(CommandLogin.LOGIN_ID,userName,encryptedPwd);
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

    public boolean transferRemoteFile(String remoteFilePath, String localFilePath) throws IOException { return client.getClientFtpHandler().download(remoteFilePath,localFilePath); }
}
