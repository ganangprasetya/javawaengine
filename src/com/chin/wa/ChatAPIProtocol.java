/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chin.wa;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author henry
 */
public class ChatAPIProtocol {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String response = "";
    private static Logger logger = Logger.getLogger(com.chin.wa.ChatAPIProtocol.class);
    String body="";
    String phone="";
    int messageType = 1;
    public void sendMessage(int id,String phone,String body, int messageType) throws SQLException{
        String urlResponse = null;
        String decodedString;
        String urlPath = "";
        String sender = "";
        String urlParameters = "";
//        if(id % 2 == 1){
        String[] account = getAccount(id).split("=");
        if(messageType == 2){
            urlPath = account[0]+"sendFile?token="+account[1];
            urlParameters =  "phone=" + phone + "&" + "body=" + body + "&" + "filename=" + "default.jpg";
        }else{
            urlPath = account[0]+"message?token="+account[1];
            urlParameters =  "phone=" + phone + "&" + "body=" + body;
        }
        sender = account[2];
//        }else{
//            urlPath = "https://eu9.chat-api.com/instance7914/message?token=r8goefk0lqq0bfp5";
//            sender = "6285881172081";
//        }
        try {
            URL url = new URL(urlPath);
            URLConnection con = url.openConnection();
            logger.info(urlPath+urlParameters);
            //send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((decodedString = in.readLine()) != null) {
                urlResponse = urlResponse + decodedString;
            }
            if (urlResponse.startsWith("null")) urlResponse = urlResponse.replaceFirst("null", "");
            logger.info(urlResponse);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(urlResponse);
            String statusDesc = (String) jsonObject.get("message");
            if(statusDesc.startsWith("Sent")){
                updateStatus(id, 1, statusDesc, sender);
            }else{
                updateStatus(id, 2, statusDesc, sender);
            }

        } catch (Exception e) {
        }
    }
    private void updateStatus(int id,int status, String statusDesc, String sender) throws SQLException{
        conn = ConnectionManager.getInstance().getConnection("core");
        String strSQL = "";
        if(status == 1){
            strSQL = "UPDATE messages SET sender=?,status=?,statusDescription=?,sentdate=NOW() WHERE id=?";
        }else{
            strSQL = "UPDATE messages SET sender=?,status=?,statusDescription=? WHERE id=?";
        }
        try {
            ps = conn.prepareStatement(strSQL);
            ps.setString(1, sender);
            ps.setInt(2, status);
            ps.setString(3, statusDesc);
            ps.setInt(4, id);
            if(ps.executeUpdate() > 0){
                logger.info(statusDesc);
            } else {
                logger.error("UPDATING FAILED");
            }
        ps.close();
        conn.close();
        } catch (Exception e) {
        }
    }
//    private int getActiveAccount() throws SQLException{
//        Connection conn3 = null;
//        PreparedStatement ps3 = null;
//        ResultSet rs3 = null;
//        int active = 0;
//        String sql = "";
//        conn3 = ConnectionManager.getInstance().getConnection("core");
////        if(id % 2 == 1){
//        sql = "SELECT count(*) FROM ACCOUNT WHERE STATUS=1";
////        }else{
////            sql = "SELECT * FROM ACCOUNT WHERE ORIGINAL='6285881172081' AND STATUS=1  LIMIT 1";
////        }
//        ps3 = conn3.prepareStatement(sql);
//        rs3 = ps3.executeQuery();
//        while (rs3.next()) {
//            active = rs3.getInt("count(*)");
//        }
//        ps3.close();
//        rs3.close();
//        conn3.close();
//        return active;
//    }
    private String getAccount(int id) throws SQLException{
        Connection conn2 = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        String sql = "";
        String account_url = "";
        String account_token = "";
        String account_original = "";
        conn2 = ConnectionManager.getInstance().getConnection("core");
//        if(id % 2 == 1){
        sql = "SELECT * FROM ACCOUNT WHERE STATUS=1  LIMIT 1";
//        }else{
//            sql = "SELECT * FROM ACCOUNT WHERE ORIGINAL='6285881172081' AND STATUS=1  LIMIT 1";
//        }
        ps2 = conn2.prepareStatement(sql);
        rs2 = ps2.executeQuery();
        while (rs2.next()) {
            account_url = rs2.getString("url");
            account_token = rs2.getString("token");
            account_original = rs2.getString("original");
        }
        ps2.close();
        rs2.close();
        conn2.close();
        return account_url+"="+account_token+"="+account_original;
    }
    
}
