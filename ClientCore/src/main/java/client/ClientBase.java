package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientBase {
    private static final String host = "localhost";
    private static final int port = 40000;
    private Socket socket;

    public ClientBase() throws IOException {
        socket = new Socket(host,port);
        System.out.println("Client started");
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
                wirter.println(msg);
                rsp = reader.readLine();
                if(rsp.contentEquals("CMD:DEAC")){
                    System.out.println("Client Shutdown");
                    break;
                }
                System.out.println(rsp);
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

