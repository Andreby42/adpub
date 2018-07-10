package com.bus.chelaile.model.rule;



import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.rule.version.VersionEntity;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.FileUtil;
import com.bus.chelaile.util.LocationKDTree;
import com.bus.chelaile.util.New;



import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RuleEngine {
    protected static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);
    private static ObjectMapper objMapper = new ObjectMapper();
    private static final Set<String> EMPTY_STR_SET = new HashSet<>(0);
    
    public static Rule parseRule(AdRule adRule) {
        try {
            JsonNode rulesInfo = objMapper.readTree(adRule.getRule());
            if (rulesInfo != null) {
                    JsonNode rInfo = rulesInfo;
                    logger.debug(AdvUtil.getTimeStr() + " - rInfo: " + rInfo);
                    Rule rule = parseRule(rInfo);
                    if (rule != null) {
//                        if (rule.hasCities() || rule.hasPlatforms() || rule.hasChannels() || rule.hasStations()
//                                || rule.hasUserIds() || rule.hasVersions() || rule.hasGpsList() || rule.hasNetStatus()) {
                        rule.setRuleId(Integer.toString(adRule.getRuleId()));
                        rule.setStartDate(adRule.getStartDate());
                        rule.setEndDate(adRule.getEndDate());
//                        } else {
//                            logger.info("adRuleId: {} has no rule", adRule.getAdvId());
//                            return null;
//                        }
                    }
                    return rule;
            } else {
                logger.info(String.format("%s - RuleInfo is null, adRule=%s", AdvUtil.getTimeStr(), adRule));
            }
            
            return null;
        } catch (JsonProcessingException e) {
            logger.error(String.format("Parse json exception: adRule=%s", adRule), e);
        } catch (IOException e) {
            logger.error("获取ResponseBody异常: siteId=nj_zsgj", e);
        }
        
        return null;
    }
    
    @Deprecated
    /**
     * 该方法用于解析一个RULE的数组， 数组是JSON的形式。目前，已经不再使用该方法。
     * @param adRule
     * @return
     */
    public static List<Rule> parseRuleList(AdRule adRule) {
        ObjectMapper objMapper = new ObjectMapper();
        List<Rule> ruleList = new ArrayList<Rule>();
        try {
            //node = objMapper.readTree("{" + adRule.getRule() + "}");
            //JsonNode rulesInfo = node.get("RuleList");
            JsonNode rulesInfo = objMapper.readTree(adRule.getRule());
            if (rulesInfo != null) {
                for (int i=0; i<rulesInfo.size(); i++) {
                    JsonNode rInfo = rulesInfo.get(i);
                    Rule rule = parseRule(rInfo);
                    rule.setRuleId(Integer.toString(adRule.getRuleId()));
                    if (rule != null) {
                        ruleList.add(rule);
                    }
                }
            } else {
                logger.info("RuleList is null");
                
            }
            
            return ruleList;
        } catch (JsonProcessingException e) {
            logger.error(String.format("Parse json exception: adRule=%s", adRule), e);
        } catch (IOException e) {
            logger.error("获取ResponseBody异常: siteId=nj_zsgj", e);
        }
        
        return Collections.emptyList();
    }

    /**
     * 判断list的大小是否是0，当且仅当list不是null并且size是0，才会true
     * @param list
     * @return
     */
    private static boolean isZeroSizeList(List<String> list) {
        if (list != null && list.size() == 0) {
            return true;
        }
        return false;
    }
    
    private static boolean isZeroSizeMap(Map<String, String> map) {
        if (map != null && map.size() == 0) {
            return true;
        }
        return false;
    }
    
    public static Rule parseRule(JsonNode rInfo) {
        /**
         * 规则中的内容，list类型 不是null就是size大于0； int类型不是0就是大于0，否则返回null
         */
        try {
            Rule rule = new Rule();
            
            //	uv数量
            rule.setUvLimit(getChildAsInt(rInfo, "uvLimit"));
            rule.setAutoBlackList(getChildAsInt(rInfo, "autoBlackList"));
            
            //	解析生成线路详情的规则
            rule.setAdTimeCounts(parseAdTimeCountsList(rInfo));
            //	线路详情单个用户放到缓存中的有效时间
            rule.setCacheTime(parseCacheTime(rInfo));
            
            Map<String, String> citys = parseMap(rInfo, "cities");
            if (isZeroSizeMap(citys)) {
                return null;
            }
            rule.setCities(citys);
            List<String> stations = parseList(rInfo, "stations");
            if (isZeroSizeList(stations)) {
                return null;
            }
            rule.setStations(stations);
            List<String> channels = parseList(rInfo, "channels");
            if (isZeroSizeList(channels)) {
                return null;
            }
            rule.setChannels(channels);
            List<String> users = parseList(rInfo, "userIds");
            if (isZeroSizeList(users)) {
                return null;
            }
            rule.setUserIds(users);
            List<String> platforms = parseList(rInfo, "platforms");
            if (isZeroSizeList(platforms)) {
                return null;
            }
            rule.setPlatforms(platforms);
            List<String> netStatus = parseList(rInfo, "netStatus");
            if (isZeroSizeList(netStatus)) {
                return null;
            }
            rule.setNetStatus(netStatus);
            String udidFile = getChildAsText(rInfo, "udidFile");
            if (StringUtils.isNotBlank(udidFile)) {
                List<String> rowList = FileUtil.getFileContent(udidFile);
                if (rowList != null) {
                    rule.addUdids(rowList);
                }
            }
            
            // 解析lineStns配置
            Map<String, Set<String>> lineStnMap = parseListStns(rInfo);
            rule.setLineStationsMap(lineStnMap);
            printLineStnMap(lineStnMap);
            
            String lineStnFile = getChildAsText(rInfo, "lineStnFile");
            if (StringUtils.isNotBlank(lineStnFile)) {
                List<String> rowList = FileUtil.getFileContent(lineStnFile);
                Map<String, Set<String>> stnMap = rule.getLineStationsMap();
                if (stnMap == null) {
                    stnMap = new ConcurrentHashMap<>();
                    rule.setLineStationsMap(stnMap);
                }
                parseLineStnList(lineStnMap, rowList);
            }
            rule.setSendType(getChildAsInt(rInfo, "sendType"));
            rule.setUserType(getChildAsInt(rInfo, "userType"));
            rule.setClickCount(getChildAsInt(rInfo, "clickCount"));
            rule.setTotalClickPV(getChildAsInt(rInfo, "totalClickPV"));
            rule.setPclickCount(getChildAsInt(rInfo, "pclickCount"));
            rule.setDays(getChildAsInt(rInfo, "days"));
            rule.setPerDayCount(getChildAsInt(rInfo, "perDayCount"));
            rule.setTotalCount(getChildAsInt(rInfo, "totalCount"));
            rule.setRightPushNum(getChildAsInt(rInfo, "rightPushNum")+"");
            rule.setBlackList(getChildAsText(rInfo, "blackList"));
            rule.setUdidPattern(getChildAsText(rInfo, "udidPattern"));
            rule.setScreenHeight(getChildAsInt(rInfo, "screenHeight"));
            rule.setCanPubMIUI(getChildAsInt(rInfo, "canPubMIUI")); // 开屏是否开启MIUI
            rule.setStartMode(getChildAsInt(rInfo, "startMode")); // 冷热启动模式控制
            rule.setProjectClick(getChildAsInt(rInfo, "projectClick")); // 项目 次数控制
            rule.setProjectTotalClick(getChildAsInt(rInfo, "projectTotalClick")); // 
            rule.setProjectTotalSend(getChildAsInt(rInfo, "projectTotalSend")); // 
            rule.setProjectDaySend(getChildAsInt(rInfo, "projectDaySend")); //
            rule.setProjectDayClick(getChildAsInt(rInfo, "projectDayClick")); //
            List<Position> positions = parseGpsList(rInfo);
            if (positions != null && positions.size() > 0) {
            	rule.setGpsList(positions);
            	rule.setKdTree(new LocationKDTree(positions));
            	//  return null;
            }
            
            Map<VersionEntity, String> versionCmps = parseVersionList(rInfo, "versions");
            if (versionCmps != null && versionCmps.size() > 0) {
            	rule.setVersions(versionCmps);
            }
            // android的最低版本和ios的最低版本
            VersionEntity vsAndroid = parseNoLessVersion(rInfo, "noLessThanVersionsAndroid");
            VersionEntity vsIos = parseNoLessVersion(rInfo, "noLessThanVersionsAndroid");
            rule.setNoLessThanVersionsAndroid(vsAndroid);
            rule.setNoLessThanVersionsIos(vsIos);
            
            rule.setChatOrRide(getChildAsInt(rInfo, "chatOrRide"));
            rule.setIsClickEndPush(getChildAsInt(rInfo, "isClickEndPush"));
            
            //开屏最小时间间隔 和 feed流广告最小间隔
            String minIntervalTimeStr = getChildAsText(rInfo, "minIntervalTime");
            if(StringUtils.isNoneBlank(minIntervalTimeStr)) {
            	rule.setMinIntervalTime(Integer.parseInt(minIntervalTimeStr) * 60 * 1000);
            }
            String minIntervalPagesStr = getChildAsText(rInfo, "minIntervalPages");
            if(StringUtils.isNoneBlank(minIntervalPagesStr)) {
            	rule.setMinIntervalPages(Integer.parseInt(minIntervalPagesStr));
            }
            
            return rule;
        } catch (Exception e) {
            logger.error("parseRule exception", e);
        }
        return null;
    }
    
    private static Long parseCacheTime(JsonNode rInfo){
    	JsonNode jsonNode = rInfo.get("cacheTime");
    	if( jsonNode == null ){
    		return 0L;
    	}
    	return jsonNode.getLongValue();
    }
    
    private static List<AdTimeCounts> parseAdTimeCountsList(JsonNode rInfo){
//    	 logger.info(rInfo.toString());
    	 JsonNode jsonNode = rInfo.get("adTimeCounts");
    	 if( jsonNode == null ){
    		 return null;
    	 }
    	 List<AdTimeCounts> list = new ArrayList<AdTimeCounts>();
         Iterator<JsonNode> keys = jsonNode.getElements();     
         while(keys.hasNext()){
         	JsonNode jn = keys.next();
         	JsonNode jtime = jn.get("time");
         	JsonNode jcount = jn.get("count");
         	AdTimeCounts ac = new AdTimeCounts();
         	if( jtime != null ){
         		String time = jtime.getTextValue();
             	if( time != null ){
             		ac.setTime(time);
             	}
         	}
         	if( jcount != null ){
         		long count = jcount.getLongValue();
             	ac.setCount(count);
         	}
         	
         	list.add(ac);
         }
         return list;
         
    }
    
    private static void printLineStnMap(Map<String, Set<String>> lineStnMap) {
        if (lineStnMap == null || lineStnMap.isEmpty()) {
//            logger.info("=====> LineStnMap is EMPTY.....");
            return;
        }
        
        try {
            StringBuffer sb = new StringBuffer("===========> lineStnMap={");
            for (Entry<String, Set<String>> entry : lineStnMap.entrySet()) {
                sb.append(entry.getKey()).append(" ==> [");
                if (entry.getValue() != null) {
                    for (String stn : entry.getValue()) {
                        sb.append(stn).append(",");
                    }
                }
                sb.append("],    ");
            }
            sb.append("}");
            
//            logger.info(sb.toString());
        } catch (Exception e) {
            
        }
    }
    
    /**
     * lineStn配置的格式如下：
     * 
     * @param rInfo
     */
    private static Map<String, Set<String>> parseListStns(JsonNode rInfo) {
        List<String> lineStnList = parseList(rInfo, "lineStns");
        if (lineStnList == null || lineStnList.size() == 0) {
            return null;
        }
        
        Map<String, Set<String>> lineStnMap = new ConcurrentHashMap<>();
        parseLineStnList(lineStnMap, lineStnList);
        
        return lineStnMap;
    }
    
    private static void parseLineStnList(Map<String, Set<String>> lineStnMap, List<String> lineStnList) {
        if (lineStnMap == null || lineStnList == null) {
            return;
        }
        
        for (String s : lineStnList) {
            try {
                if (!s.contains(";")) {
                    s = s.trim();
                    lineStnMap.put(s, EMPTY_STR_SET);
                    continue;
                }
                
                String[] parts = s.split(";");
                if (parts.length < 2) {
                    lineStnMap.put(parts[0], EMPTY_STR_SET);
                    continue;
                }
                
                Set<String> stnSet = lineStnMap.get(parts[0]);
                if (stnSet == null ) { //注意：此处必须对EMPTY_STR_SET的情况进行特殊处理。
                    stnSet = new HashSet<>();
                    lineStnMap.put(parts[0], stnSet);
                } else if (stnSet == EMPTY_STR_SET) {
                    // 已经包括该线路的所有的站点了。
                    continue;
                }
                
                String stn = null;
                for(int i=1; i < parts.length; i++) {
                    if (parts[i] == null) {
                        continue;
                    }
                    stn = parts[i].trim();
                    stnSet.add(stn);
                }
                
            } catch(Exception ex) {
                logger.error("解析lineStn的时候出现异常, lineStn={}, errMsg={}", s, ex.getMessage());
            }
        }
    }
    
    private static Map<VersionEntity, String> parseVersionList(JsonNode rInfo, String propName) {
        Map<VersionEntity, String> vMap = New.hashMap();
        JsonNode verNodes = rInfo.get(propName);
        if (verNodes != null) {
            for (int i=0; i<verNodes.size(); i++) {
                String verStr = verNodes.get(i).getTextValue();
                VersionEntity ver = VersionEntity.parseVersionStr(verStr);
                if (ver != null) {
                	vMap.put(ver, null);
                }
            }
        } else {
            return null;
        }
        return vMap;
    }
    
    private static VersionEntity parseNoLessVersion(JsonNode rInfo, String propName) {
        VersionEntity vs = null;
        String verStr = getChildAsText(rInfo, propName);
        VersionEntity ver = VersionEntity.parseVersionStr(verStr);
        if (ver != null) {
            return ver;
        }
        return vs;
    }
    
    private static List<Position> parseGpsList(JsonNode rInfo) {
        List<Position> posList = new ArrayList<Position>();
        JsonNode node = rInfo.get("gpsList");
        if (node != null) {
            for (int i=0; i<node.size(); i++) {
                JsonNode posNode = node.get(i);
                Position pos = new Position();
                pos.setLng(getChildAsDouble(posNode, "lng"));
                pos.setLat(getChildAsDouble(posNode, "lat"));
                pos.setDist(getChildAsInt(posNode, "dest"));
                pos.setName(""+i);
                posList.add(pos);
            }
        } else {
            return null;
        }
        return posList;
    }
    
    private static List<String> parseList(JsonNode rInfo, String propName) {
        List<String> retList = new ArrayList<String>();
        JsonNode node = rInfo.get(propName);
        if (node != null) {
            for (int i=0; i<node.size(); i++) {
                String value = node.get(i).getTextValue();
                retList.add(value);
            }
        } else {
            return null;
        }
        return retList;
    }
    
    private static Map<String, String> parseMap(JsonNode rInfo, String propName) {
        Map<String, String> retMap = new HashMap<String, String>();
        JsonNode node = rInfo.get(propName);
        if (node != null) {
            for (int i=0; i<node.size(); i++) {
                String value = node.get(i).getTextValue();
                retMap.put(value, null);
            }
        } else {
            return null;
        }
        return retMap;
    }


    public static void main(String[] args) throws IOException {
        String str = "{\"cities\":[],\"days\":\"9\",\"perDayCount\":10}";
        str = "{\"cities\":[\"067\",\"027\"],\"stations\":[\"大屯东\",\"炎黄艺术馆\"],\"platforms\":[\"android\"],\"netStatus\":[\"4G\",\"WIFI\"],\"versions\":[\"2.0.2\",\"2.1.0\",\"3.21.0\",\"3.22.0\"],\"userType\":0,\"cacheTime\":60,\"rightPushNum\":3,\"autoBlackList\":0,\"chatOrRide\":0,\"days\":3,\"perDayCount\":5,\"totalCount\":10,\"adTimeCounts\":[{\"count\":100000,\"time\":\"07:00-08:00\"},{\"count\":100000,\"time\":\"08:00-09:00\"}]}";
        JsonNode rulesInfo = objMapper.readTree(str);
        System.out.println(rulesInfo.toString());
        Rule rule = parseRule(rulesInfo);
        System.out.println("rule:" + rule.getCities());
        System.out.println("rule:" + rule.getVersions());
        System.out.println("rule:" + rule.getAdTimeCounts().size());
        if (rulesInfo != null) {
            JsonNode jsonNode = rulesInfo;
            jsonNode  = jsonNode.get("cacheTime");
            jsonNode = jsonNode.get("adTimeCounts");
            
            
            
            Iterator<JsonNode> keys = jsonNode.getElements();     
            while(keys.hasNext()){
            	JsonNode jn = keys.next();
            	JsonNode jt = jn.get("time");
            	System.out.println(jt.getTextValue());
//                String fieldName = keys.next();    
//                System.out.println(fieldName + ": " + jsonNode.path(fieldName).toString());    
            }  
            List<String> list = parseList(rulesInfo, "netStatus");
            System.out.println(list.size());
        //    int i = getChildAsInt(jsonNode, "days");
         //   System.out.println(i);
        }

    }
    
//    private static Date parseDate(JsonNode rInfo, String propName) {
//        String dateStr = getChildAsText(rInfo, propName);
//        if (dateStr == null  || dateStr.isEmpty()) {
//            return null;
//        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = format.parse(dateStr);
//            return date;
//        } catch (ParseException e) {
//            logger.error(String.format("Parse date exception: propName=%s, errMsg=%s", propName, e.getMessage()), e);
//            return null;
//        }
//        
//    }
    private static String getChildAsText(JsonNode node, String propName) {
        JsonNode child = node.get(propName);
        if (child == null) {
//            logger.warn(String.format("BusInfo Node does not contain attribute: %s, node=%s", propName, node));
            return "";
        }
        return child.getTextValue();
    }
    
    private static double getChildAsDouble(JsonNode node, String propName) {
        JsonNode child = node.get(propName);
        if (child == null) {
//            logger.warn(String.format("BusInfo Node does not contain attribute: %s, node=%s", propName, node));
            return -1.0;
        }
        return child.getDoubleValue();
    }
    
    private static int getChildAsInt(JsonNode node, String propName) {
        JsonNode child = node.get(propName);
        if (child == null) {
           // logger.warn(String.format("BusInfo Node does not contain attribute: %s, node=%s", propName, node));
            return 0;
        }
        return child.getIntValue();
    }
    
//    public boolean matchRule(Rule rule, RuleParam param) {
//        return rule.isMatch(param);
//    }
    
    public static Rule getEmptyRule() {
        Rule rule = new Rule();
        
        rule.setCities(new HashMap<String, String>());
        rule.setStations(new ArrayList<String>());
        rule.setGpsList(new ArrayList<Position>());
        rule.setChannels(new ArrayList<String>());
        rule.setPlatforms(new ArrayList<String>());
        rule.setVersions(new HashMap<VersionEntity, String>());
        rule.setUserIds(new ArrayList<String>());
        rule.setLines(new ArrayList<String>());
        
        rule.setUserType(0);
        return rule;
    }

}
