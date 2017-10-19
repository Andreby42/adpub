package com.bus.chelaile.strategy;

import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.strategy.AdStrategyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by tingx on 2016/11/28.
 */
public class OwnAdBlockStrategy extends DefaultStrategy {
    protected static final Logger logger = LoggerFactory
            .getLogger(OwnAdBlockStrategy.class);

    public OwnAdBlockStrategy(AdStrategyParam adStrategyParam) {
        super(adStrategyParam);
    }

    @Override
    public AdCategory getOwnAd(String udid, String platform,
                               Map<AdCategory, Integer> adHistoryMap, List<AdInfo> availableAds) {
        String date = new SimpleDateFormat("yyyyMMdd").format(Calendar
                .getInstance().getTime());
        AnalysisLog.info("{},{},{}", date, udid, getFullStrategyName());
        return null;
    }

    @Override
    public String getStrategyName() {
        return String.format("%s|%s", "own_ad_block", "");
    }
}
