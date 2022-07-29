package client;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.command.impl.CommandDemo;
import cn.sunnysky.command.impl.CommandLogin;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static cn.sunnysky.IntegratedManager.logger;

public class ClientBase {
    private static final String host = "120.48.47.150";
    private static final int port = 40000;
    private Socket socket;
    private IntegratedManager manager;

    public ClientBase() throws IOException {
        socket = new Socket(host,port);
        manager = new IntegratedManager(Side.CLIENT);
        logger.log("Client started");
    }

    public PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketoutput = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(socketoutput,true);
        return printWriter;
    }

    public BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketinput = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketinput));

        return bufferedReader;
    }

    public void talk() throws IOException {
        try {
            BufferedReader reader = this.getReader(socket);
            PrintWriter wirter = this.getWriter(socket);

            String msg = null;
            String rsp = null;
            Scanner in = new Scanner(System.in);
            while(!(msg = in.nextLine()).equals(" "))
            {
                String[] temp = msg.split(":");

                String[] args;
                if (temp.length > 1) args = temp[1].split(",");
                else args = new String[0];

                manager.sendCmd(Integer.parseInt(temp[0]),wirter,args);
                rsp = reader.readLine();
                assert rsp != null;
                if( rsp.contentEquals("DEACTIVATE")){
                    logger.log("Client Shutdown");
                    break;
                } else if (Integer.parseInt(temp[0]) == CommandLogin.LOGIN_ID)
                    IntegratedManager.temporaryUserActivationCode = rsp;
                logger.log(rsp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
    public static void main(String[] args) throws IOException {
        new ClientBase().talk();
    }
}

