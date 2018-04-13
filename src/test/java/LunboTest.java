import java.util.List;

import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.StationAdEntity;
import com.bus.chelaile.mvc.AdvParam;

import scala.util.Random;

public class LunboTest {

    public static void main(String[] args) {
        
        
        
    }
    
    
    private BaseAdEntity calAdWeightAndByOut(AdvParam advParam, List<BaseAdEntity> stanAdsList) {
        // 获取所有符合规则的站点广告
        if (stanAdsList != null && stanAdsList.size() > 0) {
            int totalWeight = 0;
            for (BaseAdEntity entity : stanAdsList) {
//                logger.info("多个站点广告，选择一个： id={}, title={}, priority={}", entity.getId(), ((StationAdEntity) entity).getTitle(),
//                        entity.getPriority());
                if (((StationAdEntity) entity).getBuyOut() == 1) {
                    // 买断的广告按照优先级来， stanAdsList 之前已经按照优先级排序过
//                    logger.info("买断的广告, udid={}, advId={}", advParam.getUdid(), entity.getId());
                    return entity;
                }
                totalWeight += ((StationAdEntity) entity).getAdWeight();
            }
            if (totalWeight > 0) {
                int randomOut = new Random().nextInt(totalWeight); // 取随机值
                int indexWeight = 0;
                for (BaseAdEntity entity : stanAdsList) {
                    if ((indexWeight += ((StationAdEntity) entity).getAdWeight()) > randomOut) {
                        return entity;
                    }
                }
            } else {
                return stanAdsList.get(0); // 所有站点广告都没有权重，那么直接返回第一个（优先级最高那个）
            }
        } else {
            return null;
        }
//        logger.error("权重计算出现错误，没有广告站点返回了 , udid={}, stanAdsList.size={}", advParam.getUdid(), stanAdsList.size());
        return null;
    }
}
