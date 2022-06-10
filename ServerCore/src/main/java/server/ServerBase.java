package server;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.Side;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.sunnysky.IntegratedManager.logger;
public class ServerBase implements Runnable{
    private static final int port = 40000;
    /**
     * True when running
     */
    private static boolean statusFlag = true;
    private Socket socket;
    private IntegratedManager manager;

    public ServerBase(Socket socket){
        this.socket = socket;
        manager = new IntegratedManager(Side.SERVER);
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

    @Override
    public void run() {
        boolean flag = false;
        while(!flag)
        {
            try {

                PrintWriter writer = this.getWriter(socket);
                BufferedReader reader = this.getReader(socket);
                String msg = null;
                while ((msg = reader.readLine())!=null)
                {
                    if(msg.contentEquals("CMD:DEAC")){
                        logger.log("Client disconnected, address: "+socket.getInetAddress()+" Port:"+socket.getPort());
                        writer.println("CMD:DEAC");
                        flag = true;
                        break;
                    }
                    manager.resolveCmd(msg,writer);
                }
            //} catch (SocketException e){

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(31);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.log("Server started");

            while (statusFlag){
                Socket socket = serverSocket.accept();
                logger.log("Client connected, address: "+socket.getInetAddress()+" Port:"+socket.getPort());
                executorService.execute(new ServerBase(socket));
            }
            serverSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
