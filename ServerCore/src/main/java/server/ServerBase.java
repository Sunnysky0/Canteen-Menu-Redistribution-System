package server;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.user.UserManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.sunnysky.IntegratedManager.logger;
public class ServerBase implements Runnable{
    private static final int port = 40000;
    /**
     * True when running
     */
    private static boolean statusFlag = true;
    private Socket socketa;
    private IntegratedManager manager;

    public ServerBase(Socket socket){
        this.socketa = socket;
        IntegratedManager.setFileManager(new DefaultFileManager("DATA_OF_SERVER"));
        manager = new IntegratedManager(
                Side.SERVER,
                new UserManager());

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
                assert socketa != null;
                PrintWriter writer = this.getWriter(socketa);
                BufferedReader reader = this.getReader(socketa);
                String msg = null;
                while ((msg = reader.readLine())!=null)
                    manager.resolveCmd(msg, writer);
            } catch (SocketException e){
                logger.log("Client disconnected.");
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(socketa!=null)
                        socketa.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processCmd(){
        String msg;
        Scanner in = new Scanner(System.in);
        while(!(msg = in.nextLine()).equals(" "))
        {
            if (msg.equalsIgnoreCase("stop")){
                logger.log("Server shutdown");
                statusFlag = false;

                break;
            }

        }

    }

    static Socket socket;
    static ServerSocket serverSocket;
    public static void connect() {
        if (serverSocket != null && !serverSocket.isClosed())
            try {
                socket = serverSocket.accept();
                logger.log("Client connected, address: "+ socket.getInetAddress()+" Port:"+socket.getPort());
                executorService.execute(new ServerBase(socket));
            }
            catch (SocketException e){
                logger.log("Unable to establish connection");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

    }

    static ExecutorService executorService = Executors.newCachedThreadPool();
    public static void main(String[] args) {


        try {
            serverSocket = new ServerSocket(port);
            logger.log("Server started");

            //executorService.execute(ServerBase::processCmd);

            while (statusFlag)
                executorService .execute(ServerBase::connect);


            executorService.shutdown();
            serverSocket.close();

            System.exit(0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
