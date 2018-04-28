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
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ads == null) ? 0 : ads.hashCode());
        result = prime * result + ((rule == null) ? 0 : rule.hashCode());
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
        result = prime * result + ((userClickRate == null) ? 0 : userClickRate.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdContentCacheEle other = (AdContentCacheEle) obj;
        if (ads == null) {
            if (other.ads != null)
                return false;
        } else if (!ads.equals(other.ads))
            return false;
        if (rule == null) {
            if (other.rule != null)
                return false;
        } else if (!rule.equals(other.rule))
            return false;
        if (rules == null) {
            if (other.rules != null)
                return false;
        } else if (!rules.equals(other.rules))
            return false;
        if (userClickRate == null) {
            if (other.userClickRate != null)
                return false;
        } else if (!userClickRate.equals(other.userClickRate))
            return false;
        return true;
    }
}
