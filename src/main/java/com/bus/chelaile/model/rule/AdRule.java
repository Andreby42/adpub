package com.bus.chelaile.model.rule;

import java.util.Date;




public class AdRule {
    private int ruleId;
    private int advId;
    private String ruleName;
    private Date startDate;
    private Date endDate;
    private String rule;
	public int getRuleId() {
		return ruleId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}
	public int getAdvId() {
		return advId;
	}
	public void setAdvId(int advId) {
		this.advId = advId;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
    
    
}
