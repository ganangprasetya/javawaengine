/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chin.wa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author henry
 */
public class TwoWayChatProtocol extends Thread{
    private static Logger logger = Logger.getLogger(com.chin.wa.TwoWayChatProtocol.class);
    public void run(){
        try {
            hitMo();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(TwoWayChatProtocol.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(TwoWayChatProtocol.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(TwoWayChatProtocol.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(TwoWayChatProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void hitMo() throws MalformedURLException, IOException, ParseException, SQLException, InterruptedException{
        String sender = "";
        String sendto = "";
        String body = "";
        String sendername = "";
        long messageNumber;
        long thirdid;
        String urlPath = "";
        Boolean fromMe;
        String[] urlParameters = getAccount().split("=");
        while (true) {            
            urlPath = urlParameters[0]+"/messages?token="+urlParameters[1]+"&lastMessageNumber="+lastNum();
//            logger.info(urlPath);
            URL url = new URL(urlPath);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
            thirdid = (long) jsonObject.get("lastMessageNumber");
            JSONArray lang= (JSONArray) jsonObject.get("messages");
            Iterator i = lang.iterator();
            // take each value from the json array separately
            while (i.hasNext()) {
                JSONObject innerObj = (JSONObject) i.next();
                String authors = (String) innerObj.get("author");
                String[] author = authors.split("@");
                sendto = author[0];
                sender = urlParameters[2];
                body = (String) innerObj.get("body");
                sendername = (String) innerObj.get("senderName");
                fromMe = (Boolean) innerObj.get("fromMe");
                messageNumber = (long) innerObj.get("messageNumber");
                if(fromMe == false){
                    if(checkMo((int) messageNumber) == 0){
                        StringBuffer message = removeUTFCharacters(body);
                        String messageUnicode = message.toString();
                        insertMo(sender, sendto,messageUnicode,messageNumber,sendername);   
                    }else{
                        logger.info("Select Messages MO");
                    }
                }
            }
            Thread.sleep(2000);
        }
        
    }
    private int lastNum() throws SQLException{
        String strSQL = "";
        int lastNum = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
        strSQL = "SELECT thirdid FROM messages_mo ORDER BY id DESC LIMIT 1";
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
        while (rs.next()) {
            lastNum = rs.getInt("thirdid");
        } 
        rs.close();
        ps.close();
        conn.close();
        return lastNum-10;
    }
    private String getAccount() throws SQLException{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        String account_url = "";
        String account_token = "";
        String account_original = "";
        conn = ConnectionManager.getInstance().getConnection("core");
        sql = "SELECT * FROM ACCOUNT WHERE STATUS=1  LIMIT 1";
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            account_url = rs.getString("url");
            account_token = rs.getString("token");
            account_original = rs.getString("original");
        }
        ps.close();
        rs.close();
        conn.close();
        return account_url+"="+account_token+"="+account_original;
    }
    public static StringBuffer removeUTFCharacters(String data){
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
        String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
        m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf;
    }
    private void insertMo(String sender, String sendto, String body, long thirdid, String sendername) throws SQLException, InterruptedException{
        Connection conn = null;
        logger.info(sendername);
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
        String strSQL = "INSERT INTO messages_mo (sender, sendername, sendto,message,receivedate,thirdid) VALUES (?,?,?,?,NOW(),?)";
        try {
                ps = conn.prepareStatement(strSQL);
                ps.setString(1, sendto);
                ps.setString(2, sendername);
                ps.setString(3, sender);
                ps.setString(4, body);
                ps.setLong(5, thirdid);
                ps.execute();
                logger.info("inserting to messages_mo : "+thirdid+" message : "+body);
                ps.close();
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        updateMo((int) thirdid);
    }
    
    private int checkMo(int thirdid) throws SQLException
    {
        String strSQL = "";
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
        strSQL = "select * from messages_mo where thirdid="+thirdid;
//        ps.setInt(1, (int) thirdid);
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
//        if(rs.next() == false){
//            messageNumber = 0;
//        }
        while (rs.next()) {
            ++count;
//            logger.info(messageNumber);
        } 
        rs.close();
        ps.close();
        conn.close();
//        logger.info(count);
        return count;
    }
    
    
    public void selectMo(int thirdid) throws SQLException, InterruptedException {
        String strSQL = "";
        String message = "";
        String sendto = "";
        int count=0;
        int messageNumber;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
        strSQL = "select * from messages_mo WHERE status='0' and thirdid="+thirdid;
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
        while (rs.next()) {
            ++count;
            message = rs.getString("message");
            sendto = rs.getString("sender");
        }
        compareKeyword(thirdid,message,sendto);
        rs.close();
        ps.close();
        conn.close();
    }
    
    private String responseDefault() throws SQLException{
        String strSQL = "";
        String message = "";
        int messageNumber;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
        strSQL = "select * from keywords WHERE word='DEFAULT'";
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
        while (rs.next()) {
            message = rs.getString("response");
        } 
        rs.close();
        ps.close();
        conn.close();
        return message;
    }
    
    private void compareKeyword(int thirdid,String keyword,String sendto) throws SQLException, InterruptedException{
        String strSQL = "";
        String message = "";
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
//        strSQL = "select * from keywords WHERE name LIKE '%"+keyword.toUpperCase()+"%'";
        strSQL = "select * from keywords WHERE word='"+keyword.toUpperCase()+"'";
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
        while (rs.next()) {
            ++count;
            int take_name = rs.getInt("take_name");
            if(take_name == 1){
                String[] response = rs.getString("response").split("\\.");
//                logger.info(response[0]);
                String name = takeName(thirdid);
//                logger.info(name);
                message = response[0]+" Mr/Mrs "+name+". "+response[1];
            }else{
                message = rs.getString("response");
            }
        }
        if(count > 0 ){
            responseMessage(message,sendto);
        }else{
            responseMessage(responseDefault(),sendto);
        }
        rs.close();
        ps.close();
        conn.close();
    }
    
    private String takeName(int thirdid) throws SQLException{
        String strSQL = "";
        String sendername = "";
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = ConnectionManager.getInstance().getConnection("core");
//        strSQL = "select * from keywords WHERE name LIKE '%"+keyword.toUpperCase()+"%'";
        strSQL = "select sendername from messages_mo WHERE thirdid="+thirdid;
        ps = conn.prepareStatement(strSQL);
        rs = ps.executeQuery();
        while (rs.next()) {
            sendername = rs.getString("sendername");
        }
        rs.close();
        ps.close();
        conn.close();
        return sendername;
    }
    
    private void responseMessage(String message, String sendto) throws SQLException{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        conn = ConnectionManager.getInstance().getConnection("core");
        String strSQL = "INSERT INTO messages (messageid,sendto,message,receivedate) VALUES (?,?,?,NOW())";
        ps = conn.prepareStatement(strSQL);
        ps.setString(1, sendto+"-"+timestamp.getTime());
        ps.setString(2, sendto);
        ps.setString(3, message);
        ps.execute();
        logger.info("Sending Message Response");
        ps.close();
        conn.close();
    }
    
    private void updateMo (int thirdid) throws SQLException, InterruptedException{
        String strSQL = "";
        int messageNumber;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        selectMo(thirdid);
        conn = ConnectionManager.getInstance().getConnection("core");
        strSQL = "UPDATE messages_mo SET status=? WHERE thirdid=?";
        ps = conn.prepareStatement(strSQL);
        ps.setInt(1, 1);
        ps.setInt(2, thirdid);
        if(ps.executeUpdate()> 0){
            logger.info("Successfully responded");
        }
        ps.close();
        conn.close();
    }
    
}
