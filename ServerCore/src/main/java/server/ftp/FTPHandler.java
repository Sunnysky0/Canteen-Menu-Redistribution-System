package server.ftp;

import cn.sunnysky.IntegratedManager;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.ipfilter.SessionFilter;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.mina.filter.firewall.Subnet;

import java.io.File;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


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

        ListenerFactory listenerFactory = new ListenerFactory();
        //设置监听端口
        listenerFactory.setPort(40021);

        DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();

        dccFactory.setPassiveIpCheck(true);

        dccFactory.setPassivePorts("40022-40025");

        DataConnectionConfiguration dcc = dccFactory.createDataConnectionConfiguration();

        listenerFactory.setDataConnectionConfiguration(dcc);


        this.factory.addListener("default", listenerFactory.createListener());

        this.server = factory.createServer();

        this.server.start();

    }

    public static void main(String[] args) throws FtpException {

        // File output = DataModelManager.copyResource("/assets/food_data_s1.fson");

        File output= new File("PUBLIC_DATA");

        new FTPHandler().startOrRestartFtpServerAt(
                output.toURI()
        );
    }
}
