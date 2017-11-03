package com.bus.chelaile.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;



import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.service.StartService;
import com.bus.chelaile.util.config.PropertiesUtils;

public class OSSUtil {
    private static final String OSS_ACCESS_KEYID = "tSD8pS46EqCGjaXt";
    private static final String OSS_SECRET = "zM5UD70G85QRxBPUvQLtBZjW5DlKra";
    
    private static final String OSS_ENDPOINT = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"oss_endpoint","http://oss-cn-hangzhou-internal.aliyuncs.com");
    
  //  private static final String OSS_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    private static final String OSS_PHOTO_BUCKET_NAME = "appadv";
//    private static final String folder = "adv/open/";
//    private static final String RESOURCE_URL_PREFIX = "http://appadv.oss-cn-hangzhou.aliyuncs.com/adv/open/";
    private static final String RESOURCE_URL_PREFIX = "http://appadv.oss-cn-hangzhou.aliyuncs.com/";
    private static final int MD5_CACHE_EXPIRE = 60 * 60 * 24;
    private static final Date URL_EXPIRE_TIME;
    private static  OSSClient ossClient = null; 
    
    protected static final Logger logger = LoggerFactory.getLogger(OSSUtil.class);
    
    static {
        logger.warn("初始化OSSClient开始...");
        init();
        URL_EXPIRE_TIME = genExpireTime();
        logger.warn("初始化OSSClient完成: ossClient==null ? " + (ossClient==null));
    }
    
    private static void init() {
        ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESS_KEYID, OSS_SECRET);
        logger.info("ossClient:"+ossClient.getEndpoint());
    }
    
    private static Date genExpireTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse("2100-01-01");
        } catch (ParseException pe) {
            logger.error("初始化URL_EXPIRE_TIME异常, 重试:" + pe.getMessage(), pe);
            
            Calendar centuryLater = Calendar.getInstance();
            centuryLater.add(Calendar.YEAR, 100);//加一个年
            return centuryLater.getTime();
        }
    }
    
    /**
     * 将图片上传到oss中，并返回相应的 图片在oss中的地址
     * @param key
     * @param file
     * @param contentType
     * @return
     * @throws FileNotFoundException
     */
    public static String putPhoto(String key, File file, String contentType, String folder) throws FileNotFoundException {
    	long time = System.currentTimeMillis();
    	//String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    	try{
    		return putPhoto(OSS_PHOTO_BUCKET_NAME, key, file, contentType, folder);
    	}finally{
    		time = System.currentTimeMillis() - time;
    		logger.debug("OSS_putTime="+time);
    	}
        
    }
    
   
    
    public static String putPhoto(String bucketName, String key, File file, String contentType, String folder) throws FileNotFoundException {
        if (ossClient == null ) {
            throw new IllegalArgumentException("ossClient 初始化失败");
        }else	if( file == null ){
        	throw new IllegalArgumentException("file 为null");
        }else	if( key == null ){
        	throw new IllegalArgumentException("key 为null");
        }
        
        String imgUrl = getImgUrl(key);
        //	已经在缓存中，直接返回
        if( imgUrl != null ){
        	return imgUrl;
        }
        try{
        	  boolean isExist =  ossClient.doesObjectExist(bucketName, folder+key);
              //	已经存在,就直接返回,有可能返回为空
              if( isExist ){
              	 String url = getImgUrl(key);
              	 if( url != null ){
              		 return url;
              	 }
              }
        }catch( Exception e ){
        	logger.info(e.getMessage()+",key="+folder+key,e);
        }
      
        
        FileInputStream inputStream = new FileInputStream(file);
        
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(file.length());
        meta.setContentType(contentType);
        
        // 用于与上传返回的结果进行校验
        String contentMD5 = CypherHelper.genFileMD5(inputStream, file.length());
      
        // 上传Object.
        PutObjectResult result = ossClient.putObject(bucketName, folder+key, inputStream, meta);
        
        if( result != null ){
        	logger.info("PutObjectResult={},folder+key={},bucketName={}",result.getETag(),folder+key,bucketName);
        }else{
        	logger.info("PutObjectResult is null,folder+key={},bucketName={}",folder+key,bucketName);
        }
        
       // ossClient.createBucket(bucketName)
        try {
            inputStream.close();
        } catch (IOException ioEx) {
            logger.error("关闭文件inputStream失败: " + ioEx.getMessage(), ioEx);
        }
        // 打印ETag
        // System.out.println("PutObjectResult: eTag=" + result.getETag());
        
        if (result == null || !StringUtils.equalsIgnoreCase(contentMD5, result.getETag())) {
            logger.error(String.format(
                    "上传文件的MD5校验不成功，key=%s, filename=%s, contentType=%s, md5=%s, eTag=%s", 
                    key, file.getName(), contentType, contentMD5,
                    (result == null) ? "PutObjectResult_is_NULL" : result.getETag()));
            return null;
        }
        
        //URL photoUrl = ossClient.generatePresignedUrl(bucketName, key, URL_EXPIRE_TIME);
        
        //System.out.println("photoUrl: " + photoUrl);
        //logger.error("图片URL:" + photoUrl);
         
        String urlStr = genResouseUrl(bucketName, key, folder);
        // 将图片的MD5值放入到OCS之中， 用于判断图片是否重复。
        cacheImageUrl(urlStr, key);
        return urlStr;
    }
    
    public static void cacheImageUrl(final String url, final String key) {
        String cacheKey = getCacheKey(key);
        CacheUtil.set(cacheKey, MD5_CACHE_EXPIRE, url);
    }
    
    /**
     * 从ocs缓存中获取 图片 name
     * @param key
     * @return
     */
    public static String getImgUrl(String key) {
        String cacheKey = getCacheKey(key);
        Object obj = CacheUtil.get(cacheKey);
        if( obj == null ){
        	return null;
        }
        return (String)obj;
    }
    
    public static String getCacheKey(String cacheKey) {
        return "openPic#" + cacheKey;
    }
    
//    public static String getMD5(String imageUrl) {
//        if (imageUrl == null) {
//            return null;
//        }
//        
//        String formattedUrl = formatUrl(imageUrl);
//        if (formattedUrl == null) {
//            logger.error("无法格式化imageUrl: " + imageUrl);
//            return null;
//        }
//            
//        String key = getMD5CacheKey(formattedUrl);
//        String md5 = (String)CacheUtil.get(key);
//        
//        if (md5 == null) {
//            md5 = getMD5FromOSS(formattedUrl);
//            
//            logger.info("从OSS之中获取图片MD5：imageUrl={}, md5={}, formatUrl={}", 
//                    new Object[]{imageUrl, md5, formattedUrl});    
//        } else {
//            logger.info("从OCS之中获取图片MD5：imageUrl={}, md5={}, formatUrl={}",
//                    new Object[]{imageUrl, md5, formattedUrl});
//        }
//        
//        return md5;
//    }
    
    public static String formatUrl(String imageUrl) {
        try {
            if (imageUrl.contains("?")) {
                //去除URL之中的所有的参数
                int idx = imageUrl.indexOf("?");
                imageUrl = imageUrl.substring(0, idx);
            }
            
            if (imageUrl != null && imageUrl.contains("@")) {
                int idx = imageUrl.indexOf("@");  //删除图片处理的格式
                imageUrl = imageUrl.substring(0, idx);
            }
            
            if (StringUtils.startsWith(imageUrl, "https://")) {
                imageUrl = "http://" + imageUrl.substring(8);
            } else if (!StringUtils.startsWith(imageUrl, "http://")) {
                imageUrl = "http://" + imageUrl;
            }
            
            // 删除最末尾的'/'字符
            int len = imageUrl.length();
            char c = imageUrl.charAt(len - 1);
            while (c == '/') {
                len--;
                c = imageUrl.charAt(len - 1);
            }
            if (len < imageUrl.length()) {
                imageUrl = imageUrl.substring(0, len);
            }
            
            return imageUrl;
        } catch(Exception ex) {
            logger.error("Format imageUrl exception: " + ex.getMessage(), ex);
            return null;
        }
    }
    
//    private static String getMD5FromOSS(String formattedUrl) {
//        String md5 = null;
//        // 根据URL分析该对象的bucket和objectName
//        String bucketName = OSS_PHOTO_BUCKET_NAME;
//        if (formattedUrl.contains("image.chelaile.net.cn")) {
//            bucketName = "rdtest"; // 路测照片
//        }
//
//        int idx = formattedUrl.lastIndexOf('/');
//        String objKey = formattedUrl.substring(idx + 1);
//
//        try {
//            OSSObject ossObj = ossClient.getObject(bucketName, objKey);
//            if (ossObj != null) {
//                ObjectMetadata metadata = ossObj.getObjectMetadata();
//                md5 = metadata.getETag();
//                
//            } else {
//                logger.error("[NO_OSS_OBJ] bucketName={}, key={}, formattedUrl={}", new Object[] {
//                        bucketName, objKey, formattedUrl });
//            }
//            
//            if (md5 != null) {
//                // 将MD5值放入到OCS之中。
//                cacheImageMD5(formattedUrl, md5);
//            }
//            
//            return md5;
//        } catch (Exception ex) {
//            logger.error(
//                    String.format("无法找到OSSObject: %s#%s, errMsg=%s", bucketName, objKey,
//                            ex.getMessage()), ex);
//        }
//        return null;
//    }
    
    public static void main(String [] args) throws FileNotFoundException {
     //   String url = "http://image.chelaile.net.cn/3b4d003eb7264929984b07d1c306572d";
//        String md5 = getMD5(url);
//        System.out.println("MD5: " + md5);
    //    getObject("rdtest", "3b4d003eb7264929984b07d1c306572d");
    	ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		StartService st = context.getBean(StartService.class);
    	
    	File file = new File("D:/temp/shoujijingdongxinrenmianfeiling188yuanyouhuiquan.jpg");
    	
//    	 ObjectListing listing = ossClient.listObjects(OSS_PHOTO_BUCKET_NAME, "adv/open/shoujijing");
    	
//    	List<OSSObjectSummary> list = listPhotoObjects();
//    	
//    	for( OSSObjectSummary oj : list ){
//    		Date dt = oj.getLastModified();
//    		System.out.println(dt.getMonth());
//    	}
//    	
    	String folder = "adv/open/";
    	System.out.println(putPhoto("1shoujijingdongxinrenmianfeiling188yuanyouhuiquan.jpg", file, "image/jpeg", folder));
    }
    
    @SuppressWarnings("unchecked")
    public static List<OSSObjectSummary> listPhotoObjects() {
        if (ossClient == null) {
            return Collections.emptyList();
        }

        ObjectListing listing = ossClient.listObjects(OSS_PHOTO_BUCKET_NAME);

        return (listing == null) ? 
                Collections.EMPTY_LIST : 
                listing.getObjectSummaries();
    }
    
    private static String genResouseUrl(final String bucketName, final String key, final String folder) {
      //  String urlPattern = PropertiesReaderWrapper.read("oss." + bucketName + ".image.url");
      //  if (StringUtils.isEmpty(urlPattern)) {
            return RESOURCE_URL_PREFIX + folder + key; 
      //  }
      //  return String.format(urlPattern, key);
    }

    public static String generateUrl(String key) {
        return generateUrl(key, URL_EXPIRE_TIME);
    }
    
    public static String generateUrl(String key, Date expireTime) {
        URL url = ossClient.generatePresignedUrl(OSS_PHOTO_BUCKET_NAME, key, URL_EXPIRE_TIME);
        
        return (url == null) ? null : url.toExternalForm();
    }
    
    public static void getObject(String bucketName, String key) {
        ossClient.getObject(bucketName , key);
    }
}
