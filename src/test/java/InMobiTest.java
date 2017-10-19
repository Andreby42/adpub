import com.alibaba.fastjson.JSON;
import com.bus.chelaile.innob.enums.ConnType;
import com.bus.chelaile.innob.enums.Orientation;
import com.bus.chelaile.innob.net.RequestResponseManager;
import com.bus.chelaile.innob.request.*;
import com.bus.chelaile.innob.response.ad.NativeResponse;

/**
 * Created by tingx on 2016/12/22.
 */
public class InMobiTest {
    public static void main(String[] args) throws Exception {

        NativeImp nativeImp = new NativeImp(5);
        BannerImp bannerImp = new BannerImp();
        IOSDevice device = new IOSDevice();
        device.setIfa("4C2B4A9F-99EF-4A69-AEA9-880E52735B27");
        device.setIp("114.112.124.83");
        device.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko)\n"
                + "Version/8.0 Mobile/12D436 Safari/600.1.4");
        device.getGeo().setCity("beijing");
        device.getGeo().setCountry("CHN");
        // device.setConnectionType(ConnType.WIFI);
        device.setCarrier("ChinaMobile");
        // device.getExt().setOrientation(Orientation.VERTICAL);
        App test = new App("1474017533182","com.chelaile.lite");
        App androidStartScreenTest = new App("1472743668741", "com.ygkj.chelaile.standard");
        AndroidDevice androidDevice = new AndroidDevice();
        androidDevice.setIem("352136064687524");
        androidDevice.setIp("114.112.124.83");
        androidDevice.setUa("Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/MMB29S)");
        androidDevice.getGeo().setCity("beijing");
        androidDevice.getGeo().setCountry("CHN");
        // device.setConnectionType(ConnType.WIFI);
        androidDevice.setCarrier("ChinaMobile");
        // Request request = new Request(test, device, nativeImp);
        Request request = new Request(androidStartScreenTest, androidDevice, nativeImp);
        System.out.println(request.toString());
        RequestResponseManager rrm = new RequestResponseManager();
        // String response = rrm.fetchAdResponseContent(request);
        String response = rrm.fetchAdResponseAsString(request);
        System.out.println(response);
        NativeResponse nativeResponse = JSON.parseObject(response, NativeResponse.class);
        nativeResponse.setDecodeAd();
        nativeResponse.getDecodedAd(0).print();
    }
}
