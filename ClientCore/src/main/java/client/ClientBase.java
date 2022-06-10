package client;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.Side;
import cn.sunnysky.command.impl.CommandDemo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static cn.sunnysky.IntegratedManager.logger;

public class ClientBase {
    private static final String host = "localhost";
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
                if(msg.contentEquals("1000")) manager.sendCmd(CommandDemo.DEMO_ID,wirter);
                else wirter.println(msg);
                rsp = reader.readLine();
                assert rsp != null;
                if( rsp.contentEquals("CMD:DEAC")){
                    logger.log("Client Shutdown");
                    break;
                }
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

