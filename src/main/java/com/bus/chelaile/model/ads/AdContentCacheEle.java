package com.bus.chelaile.model.ads;




import java.util.List;

import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.model.rule.UserClickRate;






public class AdContentCacheEle {
    private AdContent ads;
    private Rule rule;
    private List<Rule> rules;
    private UserClickRate userClickRate;
	public AdContent getAds() {
		return ads;
	}
	public void setAds(AdContent ads) {
		this.ads = ads;
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	@Override
	public String toString() {
		return "AdContentCacheEle [ads=" + ads + ", rule=" + rule + ", rules=" + rules + ", userClickRate="
				+ userClickRate + "]";
	}
	public UserClickRate getUserClickRate() {
		return userClickRate;
	}
	public void setUserClickRate(UserClickRate userClickRate) {
		this.userClickRate = userClickRate;
	}
	
    
    
    
}
