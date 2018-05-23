package com.bus.chelaile.model.ads;


import com.alibaba.fastjson.JSON;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * @author liujh
 *
 */
public class AdPushInnerContent extends AdInnerContent {
    private String head;
    private String subhead;
    @Override
    protected void parseJson(String jsonr) {
        AdPushInnerContent adPush = null;
        adPush = JSON.parseObject(jsonr, AdPushInnerContent.class);
        if (adPush != null) {
            this.head = adPush.head;
            this.subhead = adPush.subhead;
        }
    }

    @Override
    public String extractFullPicUrl(String s) {
        return null;
    }
    
	@Override
	public String extractAudiosUrl(String s, int type) {
		return null;
	}

    public static void main(String[] args) {
        AdPushInnerContent adPush = new AdPushInnerContent();
        adPush.setAndParseJson("{\"head\":\"巧虎来了\",\"subhead\":\"亲，你有好久都没看我了哦\"}");
        System.out.println("Head: " + adPush.head);
        System.out.println("SubHead: " + adPush.subhead);
        System.out.println("JsonR: " + adPush.jsonContent);
    }

    @Override
    public void completePicUrl() {
        
    }

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}
    
}
