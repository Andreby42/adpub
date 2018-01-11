package com.bus.chelaile.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.model.ListIdsCache;
import com.bus.chelaile.flow.model.TabEntity;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.QueueCacheType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class FlowOcs {

	private static final Logger logger = LoggerFactory.getLogger(FlowOcs.class);
	
	private int maxShowTime = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"uc.top-k.maxShowTime", "2"));
	private int curDayCacheExpireTime = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"uc.top-k.curDayCacheExpireTime", "86400")); // 24 hours
	private static final int PAGE_SIZE = 3; // 每页文章数

	
	
	/**
	 * 从缓存中读取访问量最高的信息流
	 */
	@SuppressWarnings("unchecked")
	public List<FlowContent> getTopInfo(AdvParam advParam) {
		// 下拉‘获取更多’的时候，不给算法推荐文章,全部重新拉取
		if (StringUtils.isEmpty(advParam.getStatsAct()) || advParam.getStatsAct().equals("get_more")) {
			return null;
		}

		Platform platform = Platform.from(advParam.getS()); // app的版本判断,ios
															// 5.26.0之前(包含)
															// 不予推荐文章
		if (platform.isIOS(platform.getDisplay()) && advParam.getVc() <= 10310) {
			return null;
		}

		String key = AdvCache.getTop5ContentsKey();
		String value = (String) CacheUtil.getNew(key);
		// logger.info("get topclick items :key={},value={}, udid={}", key,
		// value, advParam.getUdid());
		if (null != value) {
			try {
				List<JSONObject> JSONObjectFromOCS = JSON.parseObject(value, ArrayList.class);
				List<FlowContent> contentsFromOCS = new ArrayList<FlowContent>();
				for (JSONObject json : JSONObjectFromOCS) {
					contentsFromOCS.add(JSON.parseObject(json.toJSONString(), FlowContent.class));
				}

				return contentsFromOCS;
			} catch (Exception e) {
				logger.error("top articles转换出错, topArticles={}", value);
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	
	/**
	 * 过滤掉不需要展示的top列表
	 * 
	 * @param notDisplayIds
	 * @param contentsFromOCS
	 */
	public void filterTopList(List<FlowContent> contentsFromOCS, List<FlowContent> contentsFromUC, List<String> blockIds) {
		// if (contentsFromOCS == null && contentsFromUC == null) {
		// return;
		// }

		if (blockIds == null || blockIds.size() == 0) {
			return;
		}

		try {
			if (contentsFromOCS != null && contentsFromOCS.size() != 0) { // 剔除ocs中的黑名单文章
				Iterator<FlowContent> it = contentsFromOCS.listIterator();
				while (it.hasNext()) {
					FlowContent con = it.next();
					if (blockIds.contains(con.getId())) {
						it.remove();
					}
				}
			}

			if (contentsFromUC != null && contentsFromUC.size() != 0) { // 剔除接口返回的黑名单
				Iterator<FlowContent> it = contentsFromUC.listIterator();
				while (it.hasNext()) {
					FlowContent con = it.next();
					if (blockIds.contains(con.getId())) {
						it.remove();
					}
				}
			}

		} catch (Exception e) {
			logger.error("过滤不需要展示的top列表出错: contentsFromOCS={}, contentsFromUC={}, blockIds ={}",
					JSONObject.toJSON(contentsFromOCS), JSONObject.toJSON(contentsFromUC), blockIds.toString());
			logger.error(e.getMessage(), e);
		}
	}
	
	
	/**
	 * 把给用户展示的新闻放到队列中
	 */
	public void setToQueue(List<FlowContent> contents, AdvParam advParam) {
		String udid = advParam.getUdid();
		// 获取展示过的所有文章id list、和 初始化时间， 1天前的清理掉
		ListIdsCache articleIds = new ListIdsCache();

		String key = AdvCache.getUserContentIds(udid);
		String articleIdsJsr = (String) CacheUtil.getNew(key);
		if (articleIdsJsr != null) {
			try {
				articleIds = JSON.parseObject(articleIdsJsr, ListIdsCache.class);
				if (System.currentTimeMillis() - articleIds.getTime() > curDayCacheExpireTime * 1000) {
					articleIds.setTime(System.currentTimeMillis());
					articleIds.clearIds();
				}
			} catch (Exception e) {
				CacheUtil.deleteNew(key);
				logger.error("用户shown list转换出错, udid={}, articleIdsJsr={}", udid, articleIdsJsr);
				logger.error(e.getMessage(), e);
				return;
			}
		}

		List<String> ids = new ArrayList<String>();
		for (FlowContent content : contents) {
			// 缓存文章
			if (content.getType() != 0)
				continue;

			QueueObject objContent = new QueueObject();
			objContent.setKey(AdvCache.getContenKey(content.getId())); // ARTICLE#
			objContent.setUcContent(content);
			objContent.setTime(Constants.ONE_DAY_TIME);
			objContent.setQueueType(QueueCacheType.ARTICLES);
			Queue.set(objContent);

			ids.add(content.getId());
		}

		if (articleIds != null && ids != null) {
			articleIds.addIds(ids);

			// 缓存用户看过的文章 id
			QueueObject objIds = new QueueObject();
			objIds.setKey(AdvCache.getUserContentIds(advParam.getUdid())); // SHOWN#
			objIds.setTime(Constants.ONE_DAY_TIME);
			objIds.setArticleIds(articleIds);
			objIds.setQueueType(QueueCacheType.DISPLAY_IDS);
			Queue.set(objIds);
		}
	}
	
	
	/**
	 * 得到该用户不需要展示的内容
	 */
	public List<String> getNotDisplayIds(AdvParam advParam) {
		HashSet<String> curUserBlacklist = new HashSet<>(); // 通过计算，不予展示的文章id（点击过的，以及展示超过3次）
		ListIdsCache articleIds = new ListIdsCache(); // 之前展示过的文章的id
		String udid = advParam.getUdid();

		// 用户展示过的文章id，根据这些id生成新的 文章黑名单
		String shownAdsStr = (String) CacheUtil.getNew("SHOWN#" + udid);
		// logger.info("get shown times :key={}, value={}", "SHOWN#" + udid,
		// shownAdsStr);
		try {
			if (shownAdsStr != null) {
				articleIds = JSON.parseObject(shownAdsStr, ListIdsCache.class);
				List<String> shownAds = articleIds.getIdList();

				HashMap<String, Integer> curUserShownTimes = new HashMap<>();
				for (String adId : shownAds) {
					if (!curUserShownTimes.containsKey(adId)) {
						curUserShownTimes.put(adId, 1);
					} else {
						curUserShownTimes.put(adId, curUserShownTimes.get(adId) + 1);
					}
				}
				// 展现次数超过阈值的新闻加入黑名单
				for (Map.Entry<String, Integer> curUserShownEntry : curUserShownTimes.entrySet()) {
					if (curUserShownEntry.getValue() >= maxShowTime) {
						curUserBlacklist.add(curUserShownEntry.getKey());
						// logger.info("shown too many times :udid={}, id={}",
						// udid, curUserShownEntry.getKey());
					}
				}
			}
		} catch (Exception e) {
			CacheUtil.deleteNew("SHOWN#" + udid);
			logger.error("用户shown list转换出错, udid={}, articleIdsJsr={}", udid, shownAdsStr);
			logger.error(e.getMessage(), e);
			return null;
		}

		// 用户之前的黑名单 , 1天前的清理掉
		ListIdsCache blockIds = new ListIdsCache(); // 之前不予展示的id
		try {
			String blockStr = (String) CacheUtil.getNew(AdvCache.getUserBlockContentIds(udid));
			if (blockStr != null) {
				blockIds = JSON.parseObject(blockStr, ListIdsCache.class);
				if (System.currentTimeMillis() - blockIds.getTime() > curDayCacheExpireTime * 1000) {
					blockIds.setTime(System.currentTimeMillis());
					blockIds.clearIds();
				}
			}
		} catch (Exception e) {
			CacheUtil.deleteNew(AdvCache.getUserBlockContentIds(udid));
			logger.error("用户blockIds list转换出错, udid={}, articleIdsJsr={}", udid, shownAdsStr);
			logger.error(e.getMessage(), e);
			return null;
		}
		curUserBlacklist.addAll(blockIds.getIdList());
		blockIds.setIdList(new ArrayList<String>(curUserBlacklist));

		// 缓存用户看过的 文章黑名单 ids
		QueueObject objIds = new QueueObject();
		objIds.setKey(AdvCache.getUserBlockContentIds(udid)); // BLOCK#
		objIds.setTime(Constants.ONE_DAY_TIME);
		objIds.setArticleIds(blockIds);
		objIds.setQueueType(QueueCacheType.DISPLAY_IDS);
		Queue.set(objIds);

		logger.info("get bockIds items :key={},value={}", AdvCache.getUserBlockContentIds(udid), blockIds.getIdList());
		return blockIds.getIdList();
	}
	
	/**
	 * 合并最新的列表和top列表
	 * 
	 * @param contentsFromUC
	 * @param contentsFromOCS
	 */
	public List<FlowContent> merageList(List<FlowContent> contentsFromOCS, List<FlowContent> contentsFromUC,
			HashMap<Integer, FlowContent> contentActivity, HashMap<Integer, FlowContent> contentAdv, String udid, ChannelType channelType) {
		List<FlowContent> content = new ArrayList<FlowContent>();
		if(contentsFromUC == null && contentActivity == null && contentAdv == null) {
			logger.info("空空空空");
			return content;
		}
		
		//增加ocs中推荐的信息流到api获取到的信息罗中
		addOCSFlows(contentsFromOCS, contentsFromUC, content);
//		content.addAll(contentsFromUC);
		
		if(content.size() == 0 && channelType == ChannelType.CUSTOM && contentActivity != null) {
			content.addAll(contentActivity.values());
			return content;
		}

		// content 去重，按照id和title
		cleanRepeated(content);

//		try {
//			//组合活动内容到content中，考虑活动越界情况
//			HashMap<Integer, FlowContent> contentsAdd = New.hashMap();
//			
//			if (content != null && contentActivity != null && contentActivity.size() > 0) {
////				contentActivity.putAll(contentAdv);
//				contentsAdd = contentActivity;
//			} else {
//				contentsAdd = contentAdv;
//			}
//			
//			
//			if (content != null && contentsAdd != null && contentsAdd.size() > 0) {
//				sortFlows(udid, content, contentsAdd);		//排序
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("新增活动和广告到文章列表中失败");
//		}

		return content;
	}


	private void addOCSFlows(List<FlowContent> contentsFromOCS, List<FlowContent> contentsFromUC,
			List<FlowContent> content) {
		boolean hasOCS = false;
		int ocsSize = 0;
		if (contentsFromOCS != null && contentsFromOCS.size() > 0) {
			hasOCS = true;
			ocsSize = contentsFromOCS.size(); // ocs文章数
		}

		if (contentsFromUC != null && contentsFromUC.size() > 0) {
			int location = 0; // 整合后文章位置
			int locationOcs = 0; // 从ocs拿取的文章位置
			if (hasOCS) {
				for (FlowContent contentUc : contentsFromUC) {
					try {
						if (hasOCS) { // 如果存在缓存文章,那么进行插入工作
							if (location % PAGE_SIZE == 0) {
								if (locationOcs < ocsSize) {
									content.add(contentsFromOCS.get(locationOcs));
									locationOcs++;
									location++;
								}
							}
						}
					} catch (Exception e) {
						logger.error("组合文章列表出错: contentsFromOCS={}, contentsFromUC ={}",
								JSONObject.toJSON(contentsFromOCS), JSONObject.toJSON(contentsFromUC));
						logger.error(e.getMessage(), e);
					}
					content.add(contentUc);
					location++;
				}

				// 如果uc的过少，把ocs中剩余的的依次加到后面
				int ocsLast = ocsSize - locationOcs;
				if (ocsLast > 0) {
					for (int i = locationOcs; i < ocsSize; i++) {
						content.add(contentsFromOCS.get(i));
					}
				}
			} else {
				content.addAll(contentsFromUC);
			}
		}
	}


	private void cleanRepeated(List<FlowContent> content) {
		HashSet<String> ids = new HashSet<String>();
		HashSet<String> titles = new HashSet<String>();
		if (content != null && content.size() != 0) {
			Iterator<FlowContent> it = content.listIterator();
			while (it.hasNext()) {
				FlowContent con = it.next();
				if (ids.contains(con.getId()) || titles.contains(con.getTitle())) {
					it.remove();
				}
				ids.add(con.getId());
				titles.add(con.getTitle());
			}
			ids.clear();
			titles.clear();
		}
	}

	
	/**
	 * 将活动和广告加入到信息流中
	 * @param udid
	 * @param content
	 * @param contentsAdd
	 */
	private void sortFlows(String udid, List<FlowContent> content, HashMap<Integer, FlowContent> contentsAdd) {
		Object beginIndexStr = CacheUtil.getNew("flowIndex" + udid);
		int beginIndex = 0;
		if (beginIndexStr != null) {
			beginIndex = (Integer) (beginIndexStr);
		}
		boolean hasOverSize = false;

		for (Entry<Integer, FlowContent> entry : contentsAdd.entrySet()) {
			if (entry.getKey() > beginIndex && entry.getKey() <= content.size() + beginIndex) {
				content.add(entry.getKey() - beginIndex - 1, entry.getValue());
			} else if (entry.getKey() > content.size() + beginIndex) {
				logger.info("信息流的排序超出content的size, udid={}, index={}，contentSize={}", udid, entry.getKey(),
						content.size());
				hasOverSize = true;
			}
		}

		if (hasOverSize) { // 超标,记录content的长度，下回上拉获取信息流，只展示这个位置之后的信息流
			beginIndex += content.size();
			logger.info("排序结束后，新的beginIndex为：udid={}, newBeginIndex={}", udid, beginIndex);
			CacheUtil.setNew("flowIndex" + udid, Constants.ONE_DAY_TIME, beginIndex);
		} else { // 未超标
			if (beginIndex != 0) { // 如果未超标，0值不用管
				CacheUtil.setNew("flowIndex" + udid, Constants.ONE_DAY_TIME, 0);
				logger.info("排序结束后，新的beginIndex为：udid={}, newBeginIndex={}", udid, 0);
			}
		}
	}
	
	
	/**
	 * 记录用户udid和活动id到ocs中，保证每个活动只投放一次
	 * @param tabEntity
	 * @param param
	 * @return
	 */
	public boolean checkTabActivities(TabEntity tabEntity, AdvParam param) {
		String key = AdvCache.getTabActivitesKey(param.getUdid(), tabEntity.getId());
		String value = (String)CacheUtil.getNew(key);
		if(value == null ) {
			CacheUtil.setNew(key, -1, "1");
			return true;
		}
		logger.info("user has send TabActivities already! udid={}, activityId={}, key={},value={}", 
				param.getUdid(), tabEntity.getId(), key, value);
		return false;
	}
	
	public static void main (String[] args) {
		List<String> test = New.arrayList();
		test.add("a");
		
		test.add(1, "b");
		test.add(2, "d");
		
		Map<Integer, String> map = New.hashMap();
		map.put(1, "a");
		Map<Integer, String> map1 = New.hashMap();
		map1.put(1, "b");
		
		map.putAll(map1);
		
		System.out.println(map);
		System.out.println(map1);
		
		System.out.println(test);
	}
}
