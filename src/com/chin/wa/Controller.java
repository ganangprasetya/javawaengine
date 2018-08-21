/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chin.wa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author henry
 */
public class Controller {
    private static Logger logger = Logger.getLogger(com.chin.wa.Controller.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
//        Configuration cf = Configuration.getInstance();
        ConnectionManager cm = ConnectionManager.getInstance();
        selectData sd = new selectData();
        new Thread(sd).start();
        TwoWayChatProtocol tw = new TwoWayChatProtocol();
        tw.start();
    }
    
}
class selectData extends Thread{
    ChatAPIProtocol cp = new ChatAPIProtocol();
    private static Logger logger = Logger.getLogger(com.chin.wa.Controller.class);
    public void run(){
        while(true){
        logger.info("Select From Table Messages");
            try {
                ConnectionManager cm = ConnectionManager.getInstance();
                Connection conn = null;
                conn = cm.getConnection("core");
                String strSQL;
                PreparedStatement ps = null;
                ResultSet rs = null;
                strSQL = "select * from messages where status='0' order by id desc limit 10";
                ps = conn.prepareStatement(strSQL);
                rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String message = rs.getString("message");
                    String phone = rs.getString("sendto");
                    int messagetype = rs.getInt("messagetype");
                    cp.sendMessage(id,phone, message, messagetype);
                }
                rs.close();
                ps.close();
                conn.close();
                Thread.sleep(800);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        
    }
}
