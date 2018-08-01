/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chin.wa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author henry
 */
public class TwoWayChatProtocol extends Thread{
    private static Logger logger = Logger.getLogger(com.chin.wa.TwoWayChatProtocol.class);
    public void run(){
        hitMo();
    }
    public void hitMo(){
        String phone = "";
        String body = "";
        String thirdid = "";
        String urlPath = "";
        while(true){    
            try {
                ConnectionManager cm = ConnectionManager.getInstance();
                Connection conn = null;
                conn = cm.getConnection("core");
                String strSQL;
                PreparedStatement ps = null;
                ResultSet rs = null;
                strSQL = "select thirdid from messages_mo where status='0' order by id desc limit 1";
                ps = conn.prepareStatement(strSQL);
                rs = ps.executeQuery();
                while (rs.next()) {
                    thirdid = rs.getString("thirdid");
                    urlPath = "https://eu10.chat-api.com/instance7868/messages?token=7b31pkn2p00jdcqj&lastMessageNumber="+thirdid;

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
                    JSONArray lang= (JSONArray) jsonObject.get("messages");
                    Iterator i = lang.iterator();
                    // take each value from the json array separately
                    while (i.hasNext()) {
                        JSONObject innerObj = (JSONObject) i.next();
                        System.out.println("language "+ innerObj.get("id"));
                    }


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
