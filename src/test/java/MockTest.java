import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bus.chelaile.common.cache.ICache;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.StartService;
import com.bus.chelaile.service.impl.OtherManager;

import junit.framework.TestCase;

public class MockTest extends TestCase{

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		StartService ss = context.getBean(StartService.class);
		
		ss.init();
		
		ServiceManager ma = context.getBean(ServiceManager.class);
		ma.getColumntAds(null);
		
		System.out.println("success");

	}
	@Test
    public void testToEuros() throws IOException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		StartService ss = context.getBean(StartService.class);
		
		ss.init();
		
		OtherManager other = context.getBean(OtherManager.class);
		
		//other.doServiceList(advParam, showType, queryParam)
 
    	ICache mock = EasyMock.createMock(ICache.class);
        EasyMock.expect(mock.get("nihao")).andReturn("1.2");
        EasyMock.expect(mock.get("nihao1")).andReturn("3.2").andReturn("8");
        EasyMock.replay(mock);
        System.out.println(mock.get("nihao"));
        System.out.println(mock.get("nihao1"));
        System.out.println(mock.get("nihao1"));
        System.out.println(mock.get("nihao1")+"12");
    }

}
