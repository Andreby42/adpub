/**
  * Copyright 2018 bejson.com 
  */
package com.bus.chelaile.flowNew.model.pojo;
import java.util.ArrayList;
import java.util.List;

import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.util.New;

/**
 * Auto-generated: 2018-01-16 20:0:46
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Images {

    private List<String> string;
    public void setString(List<String> string) {
         this.string = string;
     }
     public List<String> getString() {
         return string;
     }
	public ArrayList<Thumbnail> createThumbnails() {
		if(string.size() < 1)
			return null;
		ArrayList<Thumbnail> thumbnails = new ArrayList<>();
		for(String s : string) {
			thumbnails.add(new Thumbnail(s));
		}
		return thumbnails;
	}

}