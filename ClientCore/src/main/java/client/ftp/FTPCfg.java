package client.ftp;

import client.ClientBase;

public class FTPCfg {
    public FTPCfg() {
    }

    public final int port = 40021;
    public int bufferSize = 1024 * 4;
    public String address = ClientBase.HOST;
    public String user = "anonymous";
    public String pass = "";
}
