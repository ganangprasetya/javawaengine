/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chin.wa;

import com.chin.wa.Base64;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.log4j.Logger;

/**
 *
 * @author Chin
 */
public class PasswordEncryption {

    private Logger logger = Logger.getLogger(com.chin.wa.PasswordEncryption.class);
    private String mySecret = "@l1nLi~?%*$-/SoIBIJi6NQmkyD;FsR6}Egk:$!AQYnRNNxu5U%[Q2tv]E?)11V%";

    public String encrypt(String password) {
        String encrypedPassword = "";
        // only the first 8 Bytes of the constructor argument are used
        // as material for generating the keySpec

        try {
            DESKeySpec keySpec = new DESKeySpec(mySecret.getBytes("UTF8"));
            SecretKeyFactory keyFactory;
            keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            // ENCODE plainTextPassword String
            byte[] cleartext = password.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypedPassword = Base64.encodeToString(cipher.doFinal(cleartext), false);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid Key in PasswordEncryption.encrypt method:", ex);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Unsupported Encoding in PasswordEncryption.encrypt method:", ex);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("No Such Algorithm in PasswordEncryption.encrypt method:", ex);
        } catch (InvalidKeySpecException ex) {
            logger.error("Invalid Key Spec in PasswordEncryption.encrypt method:", ex);
        } catch (NoSuchPaddingException ex) {
            logger.error("No Such Padding in PasswordEncryption.encrypt method:", ex);
        } catch (IllegalBlockSizeException ex) {
            logger.error("Illegal Block Size in PasswordEncryption.encrypt method:", ex);
        } catch (BadPaddingException ex) {
            logger.error("Bad Padding in PasswordEncryption.encrypt method:", ex);
        }

        return encrypedPassword;
    }

    public String decrypt(String encryptedPassword) {
        String password = "";
        // only the first 8 Bytes of the constructor argument are used
        // as material for generating the keySpec

        try {
            DESKeySpec keySpec = new DESKeySpec(mySecret.getBytes("UTF8"));
            SecretKeyFactory keyFactory;
            keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            // DECODE encryptedPwd String
            byte[] encrypedPwdBytes = Base64.decodeFast(encryptedPassword);
            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
            password = new String(plainTextPwdBytes);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid Key in PasswordEncryption.decrypt method:", ex);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Unsupported Encoding in PasswordEncryption.decrypt method:", ex);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("No Such Algorithm in PasswordEncryption.decrypt method:", ex);
        } catch (InvalidKeySpecException ex) {
            logger.error("Invalid Key Spec in PasswordEncryption.decrypt method:", ex);
        } catch (NoSuchPaddingException ex) {
            logger.error("No Such Padding in PasswordEncryption.decrypt method:", ex);
        } catch (IllegalBlockSizeException ex) {
            logger.error("Illegal Block Size in PasswordEncryption.decrypt method:", ex);
        } catch (BadPaddingException ex) {
            logger.error("Bad Padding in PasswordEncryption.decrypt method:", ex);
        } catch (IOException ex) {
            logger.error("IO Exception in PasswordEncryption.decrypt method:", ex);
        }

        return password;
    }
}
