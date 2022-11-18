package client.ftp;

import client.ClientBase;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.concurrent.Executors;

import static cn.sunnysky.IntegratedManager.logger;

public class FTPHandler {
    private FTPClient ftpClient;


    public FTPHandler() throws IOException {
        ftpLogin();

    }

    private void ftpLogin() throws IOException {
        int reply;

        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(3000);
        ftpClient.setControlEncoding("GBK");
        try {
            ftpClient.connect(ClientBase.HOST, ClientBase.FTP_PORT);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.log("Connection failed due to negative reply code ");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // FTP登陆
        try {
            ftpClient.login("anonymous", "");
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.log("Login failed due to negative reply code ");
            }
            ftpClient.enterLocalPassiveMode();// 设置被动模式
            String sysType = ftpClient.getSystemType();
            FTPClientConfig config = new FTPClientConfig(sysType.split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean download(String remote, String local) throws IOException {
        if (!ftpClient.isConnected())
            ftpLogin();

        // 设置被动模式
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("GBK");
        boolean result;


        // 检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
        if (files.length != 1) {
            logger.log("Remote file does not exist");
            return false;
        }
        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        // 本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();
            // 判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize || f.hashCode() == files[0].hashCode()) {
                logger.log("Stopping download because local file is at least as large as the remote one");
                return true;
            }

            // 进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
            }
            in.close();
            out.close();
        } else {
            logger.log("Retrieving remote file into " + f.getPath());

            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            // long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
            }
            in.close();
            out.close();
        }
        result = ftpClient.completePendingCommand();
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @param localPath 下载后保存到本地的路径
     * @author 上善若水
     * @return
     */
    @Deprecated
    public boolean downloadFile(String url, int port,String username, String password, String remotePath,String fileName,String localPath) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录

            logger.log("Searching files in remote directory");
            for(FTPFile ff:ftp.listFiles()){
                if(ff.getName().equals(fileName)){
                    File localFile = new File(localPath+"/"+ff.getName());
                    localFile.createNewFile();

                    OutputStream is = new FileOutputStream(localFile);

                    logger.log("Retrieving remote file into " + localFile.getPath());
                    ftp.retrieveFile(ff.getName(), is);

                    is.flush();
                    is.close();
                }
            }

            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return success;
    }

    public boolean transferRemoteFile(String fileName,String localPath){
        return downloadFile(
                ClientBase.HOST,ClientBase.FTP_PORT,
                "anonymous","",
                ".//",
                fileName,
                localPath);
    }

    private static void download(){
        try {
            new FTPHandler().download(".\\food_data_s1.fson",
                    "F:\\Repos\\Cantenn Menu Redistribution System\\Canteen-Menu-Redistribution-System\\DATA_OF_CLIENT\\food_data_s1.fson");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        Executors.newCachedThreadPool().execute(FTPHandler::download);
    }
}
