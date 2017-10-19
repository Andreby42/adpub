package com.bus.chelaile.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;


/**
 * 
 * @author liujh
 *
 */
public class CypherHelper {
    private static final String[] strDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
    
    public static final String PASSWORD_HASH = "password_hash";
    public static final String SALT = "salt";
    
    public static Map<String, String> encryptSHA256(final String password) {
        Map<String, String> resultMap = new HashMap<String, String>();
        final String salt = getSalt();
        
        String newPassword = password + salt;
        String hashStr = getSHA256Code(newPassword); 
        
        resultMap.put(PASSWORD_HASH, hashStr);
        resultMap.put(SALT, salt);
        
        return resultMap;
    }
    
    public static boolean checkPassword(final String password, final String passwordHash, final String salt) {
        String newPassword = password + salt;
        String hashStr = getSHA256Code(newPassword);
        
        return StringUtils.equals(hashStr, passwordHash);
    }
    
    private static String getSHA256Code(String plainText) {
        String hashStr = null;
        try {
            hashStr = new String(plainText);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            hashStr = byteToString(md.digest(plainText.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return hashStr;
    }
    
    public static String genFileMD5(File file) throws FileNotFoundException {
        if (file == null) {
            return null;
        }
        
        return genFileMD5(new FileInputStream(file), file.length());
    }
    
    public static String genMD5(String md5s) throws Exception {
        try {
            byte[] bytes = md5s.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return byteToString(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } 
        
    }
    
    public static String genFileMD5(FileInputStream in, long fileLen) {
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    fileLen);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            return byteToString(md5.digest());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    
    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    
    private static String getSalt() {
    	
        String salt = UUID.randomUUID().toString().replaceAll("-", "") + "******";
        
        if (salt != null && salt.length() > 32) {
            salt = salt.substring(0, 32);
        }
        return salt;
    }
    
    public static void main(String[] args) throws Exception {
//        for(int i=0; i<10; i++) {
//           // System.out.println(UUIDGenarator.getUUID());
//            System.out.println(getSalt());
//        }
        String str = "2DtLS2TMxSEWCqmj";
        System.out.println(genMD5(str));
    }
}
