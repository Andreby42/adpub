package com.bus.chelaile.model.ads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;



import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;




public class Station {
    protected static Logger logger = LoggerFactory.getLogger(Station.class) ;
    
    private String stnName;
    private int index;
    private boolean isUnfold;  //该站点在客户端是否展开
    
    public Station(String name, int index, boolean unfold) {
        this.stnName = name;
        this.index =index;
        this.isUnfold = unfold;
    }
    
    //结构为：站点名，位置，展开；站点名，位置，展开；例如：大屯南,0,1;惠新西街北口,1,0;位置从0开始，首页至多10个站点，所以位置从0-9
    public static List<Station> parseStationList(String stnListStr) {
        if (stnListStr == null) {
            return Collections.emptyList(); 
        }
        List<Station> list = new ArrayList<Station>();
        int maxLen = getMaxStnLen();
        //String stns = StringUtils.trimToNull(stnListStr);
        String stns = stnListStr.replace(" ", "");
        int stationCount = 0;
        if (stns.length() > 0) {
            StringTokenizer stnTokenizer = new StringTokenizer(stns, ";");
            while (stnTokenizer.hasMoreTokens()) {
                String token = stnTokenizer.nextToken();
                try {
                    if (token == null) {
                        continue;
                    }
                    int idx = token.indexOf(',');
                    if (idx <= 0)
                        continue;
                    String name = StringUtils.trim(token.substring(0, idx));

                    int stnIndx = -1;
                    int idx2 = token.indexOf(',', idx + 1);
                    boolean unfold = true;

                    if (idx2 < 0) {
                        stnIndx = Integer.parseInt(StringUtils.trim(token.substring(idx + 1)));
                    } else {
                        stnIndx = Integer.parseInt(StringUtils.trim(token.substring(idx + 1, idx2)));
                        
                        int idx3 = token.indexOf(',', idx2 + 1);
                        if (idx3 < 0) {
                            unfold = "1".equals(token.substring(idx2 + 1));
                        } else {
                            unfold = "1".equals(token.substring(idx2 + 1, idx3));
                        }
                    }

                    Station station = new Station(name, stnIndx, unfold);
                    list.add(station);
                    if ( ++ stationCount > maxLen) {
                        break;
                    }
                } catch (Exception e) {
                    logger.error(
                            String.format("Exception when parse the token: %s, errMsg=%s", token,
                                    e.getMessage()), e);
                    continue;
                }
            }
        }
        
        return list;
    }
    
    public String toString() {
        return stnName + "@" + index + "@"+ isUnfold;
    }
    
    public static int getMaxStnLen() {
    	return  Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.max.station.length","10"));
        //return PropertiesReaderWrapper.readInt("adv.max.station.length", 10);
    }

	public String getStnName() {
		return stnName;
	}

	public void setStnName(String stnName) {
		this.stnName = stnName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isUnfold() {
		return isUnfold;
	}

	public void setUnfold(boolean isUnfold) {
		this.isUnfold = isUnfold;
	}
    
    
}
