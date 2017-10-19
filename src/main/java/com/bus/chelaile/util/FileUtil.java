package com.bus.chelaile.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    protected static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static List<String> getFileContent(String fileName) {
        long st = System.currentTimeMillis();
        
        File checkFile = new File(fileName);
        if( !checkFile.exists() ){
        	logger.info("fileName={} is not exist",fileName);
        	downloadFileFromRome(fileName);
        }
        
        
        List<String> contentList = new ArrayList<String>();
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            while (line != null) {
                line = StringUtils.trimToEmpty(line);
                if (!line.startsWith("#")) {
                    contentList.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException ioe) {
            String errMsg = "Read file '" + fileName + "' exception, errMsg=" + ioe.getMessage();
            logger.error(errMsg, ioe);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                // Ignore this exception
            }
        }
        logger.info("fineName costs {} ms", (System.currentTimeMillis() - st));
        return contentList;
    }

    public static void downloadFileFromRome(String remoteFileName) {
        String user = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"adv.udid.file.user", "root");
        String pass = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"adv.udid.file.password", "zBgSH6DjnM8yEIv");
        String host = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"adv.udid.file.host", "10.171.225.150"); //WHTEST1, 外网IP：121.41.115.59

        Connection con = new Connection(host);
        try {
        	String todayStr = null; 
        	try{
        		todayStr = remoteFileName.split("/")[4];
        	}catch(Exception e) {
        		e.printStackTrace();
        		todayStr = DateUtil.getTodayStr("yyyyMM");
        	}
            String filePre = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"adv.udid.file.prefix",
                    "/data/outman/rule/") + todayStr;
            File file = new File(filePre);
            if (!file.exists()) {
                boolean createFlag = file.mkdirs();
                logger.info("create file {} : {}", filePre, createFlag);
            }
            if (file.exists()) {
            	logger.info("scp file path: {}", filePre);
                con.connect();
                boolean isAuthed = con.authenticateWithPassword(user, pass);
                if (!isAuthed) {
                    logger.info("host:{}, pass:{} auth failed", host, pass);
                }
                SCPClient scpClient = con.createSCPClient();
                scpClient.get(remoteFileName, filePre + "/");  //从远程获取文件
            }
        } catch (IOException e) {
            logger.error("downloadFileFromRome has exception", e);
        } finally {
            con.close();
        }
    }

    /**
     * 写指定内容到文件中
     *
     * @param dirPath  目录名
     * @param fileName 文件名
     * @param content  内容
     * @return 是否写成功 true成功, false失败
     */
    public static boolean writeContentToFile(String dirPath, String fileName, String content) {
        long st = System.currentTimeMillis();

        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean mkdirsFlag = dir.mkdirs();
            logger.info("create dir {} : {}", dir, mkdirsFlag);
        }

        File file = new File(dir + "/" + fileName);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {

            writer.write(content);
            writer.close();

            logger.info("writeContentToFile {} cost {} ms", fileName, System.currentTimeMillis() - st);

            return true;
        } catch (IOException e) {
            logger.error("writeContentToFile {} has exception {}", fileName, e);
            return false;
        }
    }

    /**
     * 从指定文件读取内容
     * @param fileName 文件名
     * @return 文件内容
     */
    public static String readFile(String fileName) {
        long st = System.currentTimeMillis();

        File file = new File(fileName);
        if (!file.exists()) {
            logger.error("file {} not exist, can't read", fileName);
            return "";
        }

        String content = "";
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            while (line != null) {
                content += line.trim();
                line = reader.readLine();
            }
        } catch (IOException ioe) {
            String errMsg = "Read file '" + fileName + "' exception, errMsg=" + ioe.getMessage();
            logger.error(errMsg, ioe);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                // Ignore this exception
            }
        }

        logger.info("readFile {} cost {} ms", fileName, System.currentTimeMillis() - st);

        return content;
    }

    /**
     * 上传文件到远程服务器
     *
     * @param localPath  本地目录地址
     * @param fileName   文件名
     * @param remotePath 远程服务器目录
     * @return 上传标识 true成功, false失败
     */
    public static boolean uploadFileToRemote(String localPath, String fileName, String remotePath) {

        long st = System.currentTimeMillis();

        String user = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"push.adv.udid.file.user", "root");
        String pass = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"push.adv.udid.file.password", "MRyi84fGrNgsxnv");
        String host = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"push.adv.udid.file.host", "10.47.48.208");

        Connection con = new Connection(host);
        try {
            File file = new File(localPath + fileName);
            if (!file.exists()) {
                boolean createFlag = file.createNewFile();
                logger.info("create file {} : {}", localPath, createFlag);
            }
            if (file.exists()) {
                con.connect();
                boolean isAuthed = con.authenticateWithPassword(user, pass);
                if (!isAuthed) {
                    logger.info("host:{}, pass:{} auth failed", host, pass);
                }
                Thread.sleep(300);
                SCPClient scpClient = con.createSCPClient();
                scpClient.put(localPath + fileName, remotePath);

                file.delete();
            }
        } catch (IOException e) {
            logger.error("uploadFileToRemote has exception", e);
            return false;
        } catch (InterruptedException e) {
            logger.error("uploadFileToRemote has InterruptedException", e);
            return false;
        } finally {
            con.close();
        }

        logger.info("uploadFileToRemote {} cost {} ms", fileName, System.currentTimeMillis() - st);
        return true;
    }
    
//    /**
//     * 得到一个文件夹下所有文件
//     * @param paths
//     * @return
//     */
//    public List<String> getFiles(String paths){
//    	
//    }
}


