package com.bus.chelaile.common;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/11/30 0030.
 */
public class ShortUrlUtil {
    protected static final Logger logger = LoggerFactory.getLogger(ShortUrlUtil.class);
    private static char hexChar[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8' , '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getShortUrl(String redirectUrl) {
        // fomatUrl
        logger.debug("getShortUrl re: {} , ", redirectUrl);
        redirectUrl = formatUrl(redirectUrl);
        logger.debug("getShortUrl formatUrl: {}", redirectUrl);
        String shortUrlPre =  PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.short.url.pre","http://short.chelaile.net.cn:7000/");
        //String shortUrlPre = PropertiesReaderWrapper.read("adv.short.url.pre", "http://short.chelaile.net.cn:7000/");
        for (int i = 0; i < 4; i++) {
            // 做短链接
            String shortUrl = shortUrl(redirectUrl, i);
            // 存储shortUrl 跟 redirectUrl 到 OCS
            logger.debug("getShortUrl i: {} ,  shortUrl: {}", i, shortUrl);
            String urlFromCache = AdvCache.getUrlFromCacheByShortUrl(shortUrl);
            if (null == urlFromCache) {
                logger.debug("getShortUrl urlForm cache  is null ");
                AdvCache.setUrlToCache(shortUrl, redirectUrl);
                return shortUrlPre + shortUrl;
            } else {
                logger.debug("getShortUrl urlForm cache  is not null {}", urlFromCache);
                if (!urlFromCache.equals(redirectUrl)) {
                    logger.info("shortUrl:{}, redirectUrl:{}, tryCount: {} failed", shortUrl, redirectUrl, i);
                    continue;
                } else {
                    return shortUrlPre + shortUrl;
                }
            }
        }
        return null;
    }
    private static String formatUrl(String url) {
    	String repStr = "http://";
    	if(url.contains("https://")) {
    		repStr = "https://";
    	}
        String urlA = StringUtils.substringAfter(url, repStr);
        urlA = urlA.replaceAll("//", "/");
        StringBuilder urlBuilder = new StringBuilder(urlA);
        if (urlA.contains("?")) {
            String[] urls = urlA.split("\\?");
            if (urls.length > 1 && StringUtils.isNotBlank(urls[1])) {
                urlBuilder = new StringBuilder(urls[0]).append("?");
                String[] params = urls[1].split("&");
                if (params.length > 0) {
                    Map<String, String> maps = new TreeMap<>();
                    for (String param : params) {
                        if (StringUtils.isNotBlank(param)) {
                            String[] pars = param.split("=");
                            if (pars.length > 1) {
                                if (pars[0].equalsIgnoreCase("link")) {
                                    urlBuilder.append("link=").append(pars[1]);
                                    continue;
                                }
                                maps.put(pars[0], pars[1]);
                            }
                        }
                    }
                    int i = 0;
                    for (String key : maps.keySet()) {
                        if (i == 0) {
                            urlBuilder.append("&");
                        }
                        urlBuilder.append(key).append("=").append(maps.get(key));
                        if (i != maps.size() - 1) {
                            urlBuilder.append("&");
                        }
                        i++;
                    }
                }
            }
        }
        return repStr + urlBuilder.toString();
    }

    private static String shortUrl(String url, int index) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "advertisement" ;
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"
        };
        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = getMd5(key + url);
        String hex = sMD5EncryptResult;

        List<String> urlList = new ArrayList<>();
        for ( int i = 0; i < (index + 1); i++) {
            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
            String outChars = "" ;
            for ( int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long indexJ = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[( int ) indexJ];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            urlList.add(outChars);
        }
        return urlList.get(index);
    }

    private static String getMd5(String s) {
        byte[] b = s.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(b);
            byte[] b2 = md.digest();
            char str[] = new char[b2.length << 1];
            int len = 0;
            for (int i = 0; i < b2.length; i++) {
                byte val = b2[i];
                str[len++] = hexChar[(val >>> 4) & 0xf];
                str[len++] = hexChar[val & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            logger.error("getMd5 exception", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "https://redirect.chelaile.net.cn?link=http%3A%2F%2Ftest.web.chelaile.net.cn%2Ffeed%2F%3FhideTitleBarBg%3D1%26showCloseButton%3D1&hideTitleBarBg=1&showCloseButton=1&adtype=03";
        String str1 = "https://redirect.chelaile.net.cn?link=http%3A%2F%2Ftest1.web.chelaile.net.cn%2Ffeed%2F%3FhideTitleBarBg%3D1%26showCloseButton%3D1&hideTitleBarBg=1&showCloseButton=1&adtype=03";

        str = formatUrl(str);
        System.out.println(formatUrl(str));
        
        System.out.println(formatUrl(str1));
        // TODO 
        
    }
}

