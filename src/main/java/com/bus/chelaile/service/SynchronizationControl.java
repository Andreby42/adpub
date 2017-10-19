package com.bus.chelaile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 同步控制
 * @author zzz
 *
 */
public class SynchronizationControl {
	//	reload同步锁
	private static boolean reloadSynLock = false;
	
//	reload同步锁
	private static boolean reloadUCLock = false;
	
	 protected static final Logger logger = LoggerFactory.getLogger(SynchronizationControl.class);
	
	/**
	 * true 正在reload
	 * @return
	 */
	public static boolean isReload(){
		if( reloadSynLock ){
			logger.info("reload is runnging");
		}
		return reloadSynLock;
	}
	
	/**
	 * 设置reload状态
	 * @param reloadState
	 */
	public static void setReloadSynLockState(boolean reloadState){
		reloadSynLock = reloadState;
		if( reloadSynLock ){
			try{
				Thread.sleep(3 * 1000);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
		}
	}
	
	/**
	 * true 正在reloadUC
	 * @return
	 */
	public static boolean isReloadUC(){
		if( reloadUCLock ){
			logger.info("reload is runnging");
		}
		return reloadUCLock;
	}
	
	/**
	 * 设置reloadUC状态
	 * @param reloadState
	 */
	public static void setReloadUCLockState(boolean reloadState){
		reloadUCLock = reloadState;
		if( reloadUCLock ){
			try{
				Thread.sleep(1 * 1000);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
		}
	}

}
