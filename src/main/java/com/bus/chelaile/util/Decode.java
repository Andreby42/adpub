package com.bus.chelaile.util;

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Decode {
	private static final String ALGORITHM_AES = "AES/ECB/PKCS5Padding";
	private static final int MAX_KEY_SIZE = 8;

	/**
	 * DES算法，加密
	 *
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, String data) throws Exception {
		return encode(key, data.getBytes());
	}

	/**
	 * DES算法，加密
	 *
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, byte[] data) throws Exception {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			byte[] bytes = cipher.doFinal(data);
			return new String((new Base64()).encode(bytes));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * DES算法，解密
	 *
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static byte[] decode(String key, byte[] data) throws Exception {

            byte[] decrypt = null;
            try{

                Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
                SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
                decrypt = cipher.doFinal(data);
            }catch(Exception e){
                e.printStackTrace();
            }
            return (new String(decrypt).trim()).getBytes();
        }

	/**
	 * 获取编码后的值
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decodeValue(String key, String data) {
		byte[] datas;
		String value = null;
		try {
			// datas = decode(key, (new Base64()).decode(data.getBytes()));
			datas = decode(key, Base64.decodeBase64(data.getBytes()));
			value = new String(datas);
		} catch (Exception e) {
			System.err.println("decode error...");
			value = "";
		}
		return value;
	}

	public static void main(String[] args) throws Exception {
//		String code = "aaa";
//		System.out.println("明：aaa ；密：" + Decode.encode("asdfwef5", code));
//		String encode = Decode.encode("asdfwef5", code);
//		System.out.println("明：aaa ；密：" + Decode.decodeValue("asdfwef5", encode));

		System.out.println(Decode.decodeValue("2DtLS2TMxSEWCqmj", "eiArKPr8QErgSEwGvcgXCg=="));
	}

}
