package com.bus.chelaile.model.strategy;

/**
 * Created by tingx on 2016/11/28.
 */
public class AdStrategyParam {
    private String udidPrefixes;
    private String os;
    private String thirdPartyLineDetailWeights;
    private String thirdPartyLineDetailLongtailWeights;
    // 第三方开屏广告
    private String thirdPartyStartScreenWeights;
    private String thirdPartyStartScreenLongtailWeights;
    private String adExclusion;
    private String strategyName;
    private Integer lineDetailLongtailThreshold;
    private int lineDetailNPV;
    private String lineDetail3rdGroups;
    private Integer startScreenLongtailThreshold;
    private int startScreenNPV;
    private String startScreen3rdGroups;

    public String getUdidPrefixes() {
        return udidPrefixes;
    }

    public void setUdidPrefixes(String udidPrefixes) {
        this.udidPrefixes = udidPrefixes;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getThirdPartyLineDetailWeights() {
        return thirdPartyLineDetailWeights;
    }

    public void setThirdPartyLineDetailWeights(String thirdPartyLineDetailWeights) {
        this.thirdPartyLineDetailWeights = thirdPartyLineDetailWeights;
    }

    public String getThirdPartyLineDetailLongtailWeights() {
        return thirdPartyLineDetailLongtailWeights;
    }

    public void setThirdPartyLineDetailLongtailWeights(String thirdPartyLineDetailLongtailWeights) {
        this.thirdPartyLineDetailLongtailWeights = thirdPartyLineDetailLongtailWeights;
    }

    public String getThirdPartyStartScreenWeights() {
        return thirdPartyStartScreenWeights;
    }

    public void setThirdPartyStartScreenWeights(String thirdPartyStartScreenWeights) {
        this.thirdPartyStartScreenWeights = thirdPartyStartScreenWeights;
    }

    public String getThirdPartyStartScreenLongtailWeights() {
        return thirdPartyStartScreenLongtailWeights;
    }

    public void setThirdPartyStartScreenLongtailWeights(String thirdPartyStartScreenLongtailWeights) {
        this.thirdPartyStartScreenLongtailWeights = thirdPartyStartScreenLongtailWeights;
    }

    public String getAdExclusion() {
        return adExclusion;
    }

    public void setAdExclusion(String adExclusion) {
        this.adExclusion = adExclusion;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public Integer getLineDetailLongtailThreshold() {
        return lineDetailLongtailThreshold;
    }

    public void setLineDetailLongtailThreshold(Integer lineDetailLongtailThreshold) {
        this.lineDetailLongtailThreshold = lineDetailLongtailThreshold;
    }

    public int getLineDetailNPV() {
        return lineDetailNPV;
    }

    public void setLineDetailNPV(int lineDetailNPV) {
        this.lineDetailNPV = lineDetailNPV;
    }

    public String getLineDetail3rdGroups() {
        return lineDetail3rdGroups;
    }

    public void setLineDetail3rdGroups(String lineDetail3rdGroups) {
        this.lineDetail3rdGroups = lineDetail3rdGroups;
    }

    public Integer getStartScreenLongtailThreshold() {
        return startScreenLongtailThreshold;
    }

    public void setStartScreenLongtailThreshold(Integer startScreenLongtailThreshold) {
        this.startScreenLongtailThreshold = startScreenLongtailThreshold;
    }

    public int getStartScreenNPV() {
        return startScreenNPV;
    }

    public void setStartScreenNPV(int startScreenNPV) {
        this.startScreenNPV = startScreenNPV;
    }

    public String getStartScreen3rdGroups() {
        return startScreen3rdGroups;
    }

    public void setStartScreen3rdGroups(String startScreen3rdGroups) {
        this.startScreen3rdGroups = startScreen3rdGroups;
    }
}
