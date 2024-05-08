package com.projak.psb.search.plugin.utils;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DMSENCDECUtil {
    public static void main(String[] args) {
        String message = "Hello, World!";
        String secretKeyText = generateSecret();
        System.out.println("secretKeyTecxt: "+secretKeyText);
        String encryptedMessage = encrypt(message, secretKeyText);
        System.out.println("Encrypted Message: " + encryptedMessage);

        String decryptedMessage = decrypt(encryptedMessage, secretKeyText);
        System.out.println("Decrypted Message: " + decryptedMessage);
    }

    public static String encrypt(String message, String secretKeyText) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyText);
            SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
            Cipher encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = {0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8};
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] encryptedMessageBytes = encryptionCipher.doFinal(message.getBytes());
            String encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
            return encryptedMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedMessage, String secretKeyText) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyText);
            SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
            Cipher decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = {0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8};
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedMessageBytes = decryptionCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            String decryptedMessage = new String(decryptedMessageBytes);
            return decryptedMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return secretKeyString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
