package server;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.IServer;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.command.CommandManager;
import cn.sunnysky.user.UserManager;
import server.interaction.MenuCalculator;
import server.ftp.FTPHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.sunnysky.IntegratedManager.*;
import static cn.sunnysky.command.CommandManager.*;

public class ServerBase implements Runnable, IServer {
    private static final int port = 40000;
    /**
     * True when running
     */
    private static boolean statusFlag = true;
    private Socket socketInServer;
    private static final IntegratedManager manager;

    static {
        setFileManager(new DefaultFileManager("DATA_OF_SERVER"));
        manager =
                new IntegratedManager(
                        Side.SERVER,
                        new UserManager());
    }


    public ServerBase(Socket socket){
        this.socketInServer = socket;
        IntegratedManager.setServer(this);
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
                assert socketInServer != null;
                PrintWriter writer = this.getWriter(socketInServer);
                BufferedReader reader = this.getReader(socketInServer);
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
                    if(socketInServer !=null)
                        socketInServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processCmd(){
        String msg;
        Scanner in = new Scanner(System.in);
        while(in.hasNextLine() && !(msg = in.nextLine()).equals(" "))
        {
            if (msg.equalsIgnoreCase("stop")){
                logger.log("Server shutdown");
                statusFlag = false;

                executorService.shutdown();

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            } else if (msg.equalsIgnoreCase("calculate")){
                MenuCalculator.loadAndCalculate("user_index",".//PUBLIC_DATA//food_data_s1.fson");
            } else if (msg.equalsIgnoreCase("commands"))
                logger.log(getCommands().toString());
            else if (msg.equalsIgnoreCase("menu"))
                logger.log(recommendedMenu.toString());
            else
                logger.log("Command " + msg + " not found");

        }

    }


    static Socket socket;
    static ServerSocket serverSocket;
    static boolean isAccepting = false;
    static void accept(){
        isAccepting = true;

        try {
            socket = serverSocket.accept();
        }
        catch (SocketException e){
            logger.log("Unable to establish connection");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        isAccepting = false;
    }

    private static FTPHandler ftpHandler = new FTPHandler();
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    public static void main(String[] args) {


        try {

            serverSocket = new ServerSocket(port);
            logger.log("Server started");

            ftpHandler.startOrRestartFtpServerAt(new File(".\\PUBLIC_DATA").toURI());

            executorService.execute(ServerBase::processCmd);

            while (statusFlag){
                if (serverSocket != null && !serverSocket.isClosed()) {

                    executorService.execute(ServerBase::accept);

                    isAccepting = true;
                    while (isAccepting && !executorService.isShutdown())
                        continue;

                    if (socket == null || executorService.isShutdown()){
                        logger.log("Network failure");
                        continue;
                    }

                    logger.log("Client connected, address: " + socket.getInetAddress() + " Port:" + socket.getPort());
                    executorService.execute(new ServerBase(socket));
                }

            }

            ftpHandler.closeFtpServer();

            System.exit(0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCommonCommand(String msg) {
        if (msg.equalsIgnoreCase("stop")){
            shutdown();
        } else if (msg.equalsIgnoreCase("calculate")){
            MenuCalculator.loadAndCalculate("user_index",".//PUBLIC_DATA//food_data_s1.fson");
        } else if (msg.equalsIgnoreCase("commands"))
            logger.log(getCommands().toString());
        else if (msg.equalsIgnoreCase("menu"))
            logger.log(recommendedMenu.toString());
        else
            logger.log("Command " + msg + " not found");
    }

    @Override
    public void onCalculate() {
        MenuCalculator.loadAndCalculate("user_index",".//PUBLIC_DATA//food_data_s1.fson");
    }

    @Override
    public void dropMenu() {
        fileManager.createNewFileInstance("RecommendedMenu");

        recommendedMenu = null;
    }

    @Override
    public void shutdown() {
        logger.log("Server shutdown");
        statusFlag = false;

        executorService.shutdown();

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
