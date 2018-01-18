package com.bus.chelaile.model.rule;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.flowNew.model.pojo.JsonRootBean;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkContentGetRequest;
import com.taobao.api.response.TbkContentGetResponse;

public class Taobaoke {

	public static void main(String[] args) {
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "24761264", "83cde4a2b9bcd9e10bcb53dd09f45607");
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "24766034", "268a3593256c34e54e4f3131e6cb60d1");
//		TbkContentGetRequest req = new TbkContentGetRequest();
//		req.setAdzoneId(190990271L);
//		req.setType(1L);
//		req.setBeforeTimestamp(System.currentTimeMillis());
//		req.setCount(20L);
//		req.setCid(2L);
//		req.setImageWidth(300L);
//		req.setImageHeight(300L);
//		TbkContentGetResponse rsp = null;
//		try {
//			rsp = client.execute(req);
//		} catch (ApiException e) {
//			e.printStackTrace();
//		}
//		System.out.println(rsp.getBody());
//		
//		
//		JsonRootBean root = JSON.parseObject(rsp.getBody(), JsonRootBean.class);
//		System.out.println(root.getTbk_content_get_response().getRequest_id());
		
		System.out.println(("3.90".compareTo("4.4") < 0));
		System.out.println(("4.4".compareTo("4.4") < 0));
		System.out.println(("4.41".compareTo("4.4") < 0));
		System.out.println(("10.3".compareTo("4.4") < 0));
	}
}
