package com.bus.chelaile.flowNew;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flowNew.model.pojo.JsonRootBean;
import com.bus.chelaile.flowNew.model.pojo.Map_data;
import com.bus.chelaile.util.New;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkContentGetRequest;
import com.taobao.api.response.TbkContentGetResponse;

public class TbkUtils {

	// private static final String TBK_URL =
	// PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
	// "tbk.ruanwen.url",
	// "http://gw.api.taobao.com/router/rest");
	private static final Logger logger = LoggerFactory.getLogger(TbkUtils.class);

	// TODO 缺少异常判断
	public static List<FlowContent> getTbkContent() {

		try {
			// TaobaoClient client = new DefaultTaobaoClient(TBK_URL,
			// "24766034",
			// "268a3593256c34e54e4f3131e6cb60d1");
			TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "24766034",
					"268a3593256c34e54e4f3131e6cb60d1");
			TbkContentGetRequest req = new TbkContentGetRequest();
			req.setAdzoneId(190990271L);
			req.setType(1L);
			req.setBeforeTimestamp(System.currentTimeMillis());
			req.setCount(20L);
			req.setCid(2L);
			req.setImageWidth(300L);
			req.setImageHeight(300L);
			TbkContentGetResponse rsp = null;
			try {
				rsp = client.execute(req);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			System.out.println(rsp.getBody());

			JsonRootBean root = JSON.parseObject(rsp.getBody(), JsonRootBean.class);
			System.out.println(root.getTbk_content_get_response().getRequest_id());

			List<FlowContent> flows = New.arrayList();

			List<Map_data> map_data = root.getTbk_content_get_response().getResult().getData().getContents()
					.getMap_data();

			for (int i = 0; i < map_data.size(); i++) {
				Map_data mapData = map_data.get(i);
				FlowContent flow = new FlowContent();
				flow.setId(mapData.getAuthor_id());
				flow.setUrl("https:" + mapData.getClink()); // 商品跳转链接
				flow.setTitle(mapData.getTitle());
				flow.setTime(Long.parseLong(mapData.getPublish_time()));
				flow.setImgs(mapData.getImages().createThumbnails());
				flow.setDesc(mapData.getAuthor_nick());

				flows.add(flow);
			}

			return flows;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 缓存 tbk 软文
	public static void cacheTAK() {
		logger.info("缓存淘宝客软文  ");
		List<FlowContent> flows = getTbkContent();

		if (flows != null && flows.size() > 0) {
			String key = "TBK_RUANWEN_FOR_FLOWS";
			CacheUtil.set(key, Constants.LONGEST_CACHE_TIME, JSONObject.toJSONString(flows));
		}
	}

	public static void main(String[] args) {
		getTbkContent();
	}

	// 取缓存 tbk 软文
	@SuppressWarnings("unchecked")
	public static FlowContent getTbkContentFromCache() {
		String key = "TBK_RUANWEN_FOR_FLOWS";
		String value = (String) CacheUtil.get(key);
		if (null != value && StringUtils.isNotBlank(value)) {
			try {
				List<JSONObject> JSONObjectFromOCS = JSON.parseObject(value, ArrayList.class);
				List<FlowContent> contentsFromOCS = new ArrayList<FlowContent>();
				for (JSONObject json : JSONObjectFromOCS) {
					contentsFromOCS.add(JSON.parseObject(json.toJSONString(), FlowContent.class));
				}

				Random r = new Random();
				return contentsFromOCS.get(r.nextInt(contentsFromOCS.size()));
				
			} catch (Exception e) {
				logger.error("tbk缓存 转换出错, tbk Flows ={}", value);
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
}
