package server.ftp;

import cn.sunnysky.IntegratedManager;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;

import java.net.URI;


public class FTPHandler {
    private FtpServerFactory factory;
    private BaseUser anonymousUser;
    private FtpServer server;

    public FTPHandler() {
        this.factory = new FtpServerFactory();
        this.anonymousUser = new BaseUser();
        anonymousUser.setName("anonymous");
    }

    public void closeFtpServer(){
        if (this.server != null && !this.server.isStopped())
            this.server.stop();
    }

    public void startOrRestartFtpServerAt(URI location) throws FtpException {

        if (this.server != null && !this.server.isStopped())
            this.server.stop();

        final String path = location.getPath().replaceAll("%20"," ");

        IntegratedManager.logger.log("Starting FTP server at dir: " + path);

        anonymousUser.setHomeDirectory(path);

        factory.getUserManager().save(anonymousUser);

        this.server = factory.createServer();

        this.server.start();
    }

    public static void main(String[] args) throws FtpException {

        // File output = DataModelManager.copyResource("/assets/food_data_s1.fson");

        File output= new File(".\\PUBLIC_DATA");

        new FTPHandler().startOrRestartFtpServerAt(
                output.toURI()
        );
    }
}
