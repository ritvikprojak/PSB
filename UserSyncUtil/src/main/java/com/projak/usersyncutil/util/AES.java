package com.projak.usersyncutil.util;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	public static String encryptMessage(String message, String secretKeyText) throws Exception {
		System.out.println("AES.encryptMessage() "+secretKeyText);
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyText);
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");

        Cipher encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = {0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8};
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptionCipher.doFinal(messageBytes);
        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        return encryptedMessage;
    }
	
	public static String decryptMessage(String encryptedMessage, String secretKeyText) throws Exception {
		System.out.println("AES.decryptMessage() "+secretKeyText);
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyText);
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");

        Cipher decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = {0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8};
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedMessageBytes = decryptionCipher.doFinal(encryptedMessageBytes);
        String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

        return decryptedMessage;
    }
	
	public static byte[] generateAESKey(int keySize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keySize / 8];
        secureRandom.nextBytes(key);
        System.out.println("Skey: "+key);
        return key;
    }

    public static String encodeKey(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static void main(String[] args) throws Exception {
    	
    	 // Generate a 256-bit (32-byte) AES secret key
        byte[] secretKey = generateAESKey(128);

        // Encode the secret key as a base64 string
        String secretKeyText = encodeKey(secretKey);
        
        System.out.println("Secret Key: " + secretKeyText);

        
        String message = "Some message to encrypt";
        // secretKeyText = "sOmESeCrEtKeY";

        String encryptedMessage = encryptMessage(message, secretKeyText);
        System.out.println("Encrypted Message: " + encryptedMessage);
        
        
       //  secretKeyText = "sOmESeCrEtKeY";

        String decryptedMessage = decryptMessage(encryptedMessage, secretKeyText);
        System.out.println("Decrypted Message: " + decryptedMessage);
        
    }
    
}
