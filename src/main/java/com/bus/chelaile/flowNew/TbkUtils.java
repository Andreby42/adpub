package com.bus.chelaile.flowNew;

import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flowNew.model.pojo.JsonRootBean;
import com.bus.chelaile.flowNew.model.pojo.Map_data;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkContentGetRequest;
import com.taobao.api.response.TbkContentGetResponse;

public class TbkUtils {

	private static final String TBK_URL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "tbk.ruanwen.url",
			"http://gw.api.taobao.com/router/rest");
	
	// TODO 缺少异常判断
	public static FlowContent getTbkContent() {

		try {
//			TaobaoClient client = new DefaultTaobaoClient(TBK_URL, "24766034",
//					"268a3593256c34e54e4f3131e6cb60d1");
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

			FlowContent flow = new FlowContent();
			// TODO 此处应该做缓存
			List<Map_data> map_data = root.getTbk_content_get_response().getResult().getData().getContents()
					.getMap_data();
			int size = map_data.size();
			Random r = new Random();
			Map_data mapData = map_data.get(r.nextInt(size));

			flow.setId(mapData.getAuthor_id());
			flow.setUrl("https:" + mapData.getClink()); // 商品跳转链接
			flow.setTitle(mapData.getTitle());
			flow.setTime(Long.parseLong(mapData.getPublish_time()));
			flow.setImgs(mapData.getImages().createThumbnails());
			flow.setDesc(mapData.getAuthor_nick());
			// flow.createFlowFromTbk(root.getTbk_content_get_response().getResult().getData().getContents().getMap_data().get(0));
			return flow;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		getTbkContent();
	}
}
