/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chin.wa;

import java.io.FileInputStream;
import com.chin.wa.State;
import com.chin.wa.PasswordEncryption;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Chin
 */
public class Configuration {

    private Logger logger = Logger.getLogger(com.chin.wa.Configuration.class);
    private static Configuration instance;

    private static int reload = 0;

    private int port = 18966;

    private String driverName = null;
    private String user = null;
    private String password = null;
    private String uri = null;
    private int poolMinSize = 0;
    private int poolMaxSize = 0;

    private boolean useProxy = false;
    private String proxyIp = "";
    private int proxyPort = 0;
    private String proxyUser = "";
    private String proxyPassword = "";
    
    private long monitoringFirstDelay = 120;
    private long monitoringSchedule = 10;
    private String monitoringFile = null;
    private String monitoringLocation = null;

    private Configuration() {
        Properties p = new Properties();
        try {
            FileInputStream fs = null;
            try {
                if (System.getProperty("cclient.configdir") != null) {
                    fs = new FileInputStream(System.getProperty("csms.configdir").concat(State.getConfigFile()));
                    if (fs!=null) {
                        logger.info("Load config from: ".concat(System.getProperty("cclient.configdir").concat(State.getConfigFile())));
                    }
                } else if (System.getProperty("cclient.configfile") != null) {
                    fs = new FileInputStream(System.getProperty("cclient.configfile"));
                    if (fs!=null) {
                        logger.info("Load config from: ".concat(System.getProperty("cclient.configfile")));
                    }
                } else {
                    fs = new FileInputStream(State.getConfigFile());
                    if (fs!=null) {
                        logger.info("Load config from: ".concat(State.getConfigFile()));
                    }
                }
                p.load(fs);
            }  catch (FileNotFoundException ex) {
                InputStream in = getClass().getClassLoader().getResourceAsStream(State.getConfigFile());
                p.load(in);
                if (in!=null) {
                    logger.info("Load config from resource: ".concat(State.getConfigFile()));
                    in.close();
                }
            } finally {
                if (fs!=null) {
                    fs.close();
                }
            }
            if (p!=null) {
                this.driverName = p.getProperty("DriverName");
                PasswordEncryption pe = new PasswordEncryption();
                this.user = pe.decrypt(p.getProperty("User"));
                this.password = pe.decrypt(p.getProperty("Password"));
                pe = null;
                this.uri = p.getProperty("URI");
                this.poolMinSize = Integer.parseInt(p.getProperty("PoolMinSize"));
                this.poolMaxSize = Integer.parseInt(p.getProperty("PoolMaxSize"));

                try {
                    this.port = Integer.parseInt(p.getProperty("port"));
                } catch (Exception ex) {}
                
                if (p.getProperty("useProxy").equalsIgnoreCase("true")) {
                    this.useProxy = true;
                } else {
                    this.useProxy = false;
                }
                this.proxyIp = p.getProperty("proxyIp");
                try {
                    this.proxyPort = Integer.parseInt(p.getProperty("proxyPort"));
                } catch (Exception ex) { }
                this.proxyUser = p.getProperty("proxyUser");
                this.proxyPassword = p.getProperty("proxyPassword");
                
                try {
                    this.monitoringFirstDelay = Long.parseLong(p.getProperty("monitoring.firstDelay"));
                } catch (Exception ex) { }
                try {
                    this.monitoringSchedule = Long.parseLong(p.getProperty("monitoring.schedule"));
                } catch (Exception ex) { }
                monitoringFile = p.getProperty("monitoring.file");
                monitoringLocation = p.getProperty("monitoring.location");
            } else {
                logger.warn("Error in reading Property file: ");
//                throw new Exception();
            }
        } catch (FileNotFoundException ex) {
            logger.error("File Not Found in Configuration.Init method:", ex);
        } catch (IOException ex) {
            logger.error("IO Exception in Configuration.Init method:", ex);
        }
    }

    private static void reloadSetting() {
        
    }

    public static Configuration getInstance() {
        if (instance==null) {
            synchronized(Configuration.class) {
                if (instance==null) {
                    instance = new Configuration();
                } 
            }
        }
        if (getReload()==1) {
            setReload(0);
            reloadSetting();
        }
        return instance;
    }

//    public static ArrayList<Application> getMonitoringList() {
//        return monitoringList;
//    }

    public static int getReload() {
        return reload;
    }

    public static void setReload(int needReload) {
        reload = needReload;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURI() {
        return this.uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public int getPoolMinSize() {
        return this.poolMinSize;
    }

    public void setPoolMinSize(int poolMinSize) {
        this.poolMinSize = poolMinSize;
    }

    public int getPoolMaxSize() {
        return this.poolMaxSize;
    }

    public void setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }

    public int getPort() {
        return port;
    }
    
    public boolean isUseProxy() {
        return useProxy;
    }

    public String getProxyUser() {
        return proxyUser;
    }
    
    public String getProxyIp() {
        return proxyIp;
    }
    
    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public long getMonitoringFirstDelay() {
        return this.monitoringFirstDelay;
    }

    public void setMonitoringFirstDelay(long monitoringFirstDelay) {
        this.monitoringFirstDelay = monitoringFirstDelay;
    }

    public long getMonitoringSchedule() {
        return monitoringSchedule;
    }

    public void setMonitoringSchedule(long monitoringSchedule) {
        this.monitoringSchedule = monitoringSchedule;
    }

    public String getMonitoringFile() {
        return monitoringFile;
    }

    public String getMonitoringLocation() {
        return monitoringLocation;
    }

}
