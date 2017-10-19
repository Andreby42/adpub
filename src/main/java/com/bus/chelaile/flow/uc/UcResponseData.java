package com.bus.chelaile.flow.uc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;

public class UcResponseData{
	
	private static final Logger logger = LoggerFactory.getLogger(UcResponseData.class);
	
	public ArrayList<ItemsTable> items;	//item列表. 列表页按items数组的顺序依次展现各个item
	public JSONObject articles;
	public JSONObject specials;
	
	int is_clean_cache;
	public JSONObject pull_down_hint;
	public int status;
	public String message;
//	public Map<String, Map<String, String>> result;
	
	public ArrayList<ItemsTable> getItems() {
		return items;
	}
	public void setItems(ArrayList<ItemsTable> items) {
		this.items = items;
	}
	public int getIs_clean_cache() {
		return is_clean_cache;
	}
	public void setIs_clean_cache(int is_clean_cache) {
		this.is_clean_cache = is_clean_cache;
	}
	public JSONObject getPull_down_hint() {
		return pull_down_hint;
	}
	public void setPull_down_hint(JSONObject pull_down_hint) {
		this.pull_down_hint = pull_down_hint;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
//	public Map<String, Map<String, String>> getResult() {
//		return result;
//	}
//	public void setResult(Map<String, Map<String, String>> result) {
//		this.result = result;
//	}
	public JSONObject getArticles() {
		return articles;
	}
	public void setArticles(JSONObject articles) {
		this.articles = articles;
	}
	public JSONObject getSpecials() {
		return specials;
	}
	public void setSpecials(JSONObject specials) {
		this.specials = specials;
	}
	
	
	public Item getArticleByTableId(String id) {	//set articleList
		Item item = null;
		if(this.articles != null && articles.containsKey(id)) {
			item = JSON.parseObject(articles.get(id).toString(), Item.class);
			if(item != null && item.getItem_type() != 0 && item.getItem_type() != 1 ) {	//只取 type为0和1的
				logger.error("发现item_type不等于0和1的: {}", item.toString());
				return null;
			}
		}
		return item;
	}
	
	public List<Item> getSpecialListByTableId(String id) {	//set specialList
		List<Item> itemList = New.arrayList();
		if(specials != null && specials.containsKey(id)) {
			Special spe = JSON.parseObject(specials.get(id).toString(), Special.class);
			
			if(spe.getItems() != null && spe.getItems().size() > 0) {
				Iterator<Item> it = spe.getItems().iterator();
				while (it.hasNext()) {
					Item item = it.next();
					if(item.getItem_type() != 0 && item.getItem_type() != 1 ) {	//只取 type为0的
						logger.error("发现item_type不等于0和1的: {}", item.toString());
						it.remove();
					}
				}
			}
			
			return spe.getItems();
		}
		return itemList;
	}
}