package com.bus.chelaile.util;

import java.util.Calendar;
import java.util.UUID;





public class IdGenerateUtil {
	
	public static int generateId(){
		// Long ld= IdWorker.getId();
		// return ld.intValue();
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		int amplificationRatio = Integer.MAX_VALUE / (24 * 60 * 60 * 1000);
		int uuidLastThree = 10000;
		while (uuidLastThree >= (4096 / amplificationRatio * amplificationRatio)) {
			String uuid = getUUID();
			String uuidLastThreeStr = uuid.substring(uuid.length() - 3);
			uuidLastThree = Integer.parseInt(uuidLastThreeStr, 16);
		}
		
		return (int)(now - c.getTimeInMillis()) * amplificationRatio + uuidLastThree % amplificationRatio;
	}
	
	  public static synchronized String getUUID()
	  {
	    return UUID.randomUUID().toString().replaceAll("-", "");
	  }

	
	public static void main(String[] args) {
		System.out.println(IdGenerateUtil.generateId());
	}
}
