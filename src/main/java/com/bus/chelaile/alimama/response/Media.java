package com.bus.chelaile.alimama.response;


/**
 * Created by Administrator on 2016/10/8.
 */
public class Media {
    Integer type;
    Integer w;
    Integer h;
    Integer event;
    String img_url;
    String title;
    String h5_snippet;
    String h5_url;
    String click_url;
    String download_url;
    String ad_words;
    String price;
    String promoprice;
    String sell;
    String deep_link_url;

    public void print() {
        System.out.println("|----|----|----Type: " + type);
        System.out.println("|----|----|----Width: " + w);
        System.out.println("|----|----|----Height: " + h);
        System.out.println("|----|----|----Event: " + event);
        System.out.println("|----|----|----Image URL: " + img_url);
        System.out.println("|----|----|----Title: " + title);
        System.out.println("|----|----|----H5 Snippet: " + h5_snippet);
        System.out.println("|----|----|----H5 URL: " + h5_url);
        System.out.println("|----|----|----Click URL: " + click_url);
        System.out.println("|----|----|----Download URL: " + download_url);
        System.out.println("|----|----|----Ad Words: " + ad_words);
        System.out.println("|----|----|----Price: " + price);
        System.out.println("|----|----|----Promotion Price: " + promoprice);
        System.out.println("|----|----|----Sell: " + sell);
        System.out.println("|----|----|----Deep Link URL: " + deep_link_url);
    }

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public Integer getH() {
		return h;
	}

	public void setH(Integer h) {
		this.h = h;
	}

	public Integer getEvent() {
		return event;
	}

	public void setEvent(Integer event) {
		this.event = event;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getH5_snippet() {
		return h5_snippet;
	}

	public void setH5_snippet(String h5_snippet) {
		this.h5_snippet = h5_snippet;
	}

	public String getH5_url() {
		return h5_url;
	}

	public void setH5_url(String h5_url) {
		this.h5_url = h5_url;
	}

	public String getClick_url() {
		return click_url;
	}

	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getAd_words() {
		return ad_words;
	}

	public void setAd_words(String ad_words) {
		this.ad_words = ad_words;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPromoprice() {
		return promoprice;
	}

	public void setPromoprice(String promoprice) {
		this.promoprice = promoprice;
	}

	public String getSell() {
		return sell;
	}

	public void setSell(String sell) {
		this.sell = sell;
	}

	public String getDeep_link_url() {
		return deep_link_url;
	}

	public void setDeep_link_url(String deep_link_url) {
		this.deep_link_url = deep_link_url;
	}
    
}