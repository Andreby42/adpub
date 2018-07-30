package test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.StartService;

public class StartTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		StartService ss = context.getBean(StartService.class);
		
		ss.init();
		
		ServiceManager ma = context.getBean(ServiceManager.class);
		ma.getColumntAds(null);
		
		System.out.println("success");

	}

}
