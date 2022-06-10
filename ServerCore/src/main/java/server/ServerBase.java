package server;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.Side;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static cn.sunnysky.IntegratedManager.logger;
public class ServerBase {
    private static final int port = 40000;
    private ServerSocket serverSocket;
    private IntegratedManager manager;

    public ServerBase() throws IOException {
        serverSocket = new ServerSocket(port);
        manager = new IntegratedManager(Side.SERVER);
        logger.log("Server Started");
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
    public void server() throws IOException
    {
        boolean flag = false;
        while(!flag)
        {
            Socket socket = null;
            try {
                socket = serverSocket.accept();//Waiting for the client
                logger.log("Client connected, address: "+socket.getInetAddress()+" Port:"+socket.getPort());

                PrintWriter writer = this.getWriter(socket);
                BufferedReader reader = this.getReader(socket);
                String msg = null;
                while ((msg = reader.readLine())!=null)
                {
                    if(msg.contentEquals("CMD:DEAC")){
                        System.out.println("Server Shutdown");
                        writer.println("CMD:DEAC");
                        flag = true;
                        break;
                    }
                    manager.resolveCmd(msg,writer);
                    logger.log(socket.getInetAddress()+" "+socket.getPort()+" Sent: "+msg);
                    writer.println("Server recived: " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        serverSocket.close();
    }
    public static void main(String[] args) throws IOException {
        new ServerBase().server();
    }
}
