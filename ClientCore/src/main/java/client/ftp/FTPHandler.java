package client.ftp;

import client.ClientBase;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import static cn.sunnysky.IntegratedManager.logger;

public class FTPHandler {
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
        new FTPHandler().transferRemoteFile("food_data_s1.fson",
                "F:\\Repos\\Cantenn Menu Redistribution System\\Canteen-Menu-Redistribution-System\\DATA_OF_CLIENT");

    }

    public static void main(String[] args){
        Executors.newCachedThreadPool().execute(FTPHandler::download);
    }
}
