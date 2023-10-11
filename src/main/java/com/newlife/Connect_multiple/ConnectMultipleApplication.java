package com.newlife.Connect_multiple;

import com.newlife.Connect_multiple.service.impl.ProbeModuleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ConnectMultipleApplication {
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(ConnectMultipleApplication.class, args);
//		while (true) {
//			try {
//				ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);
//				probeModuleService.getStatusModulePeriodically();
//				System.out.println("Test");
//				Thread.sleep(30000);
//			}
//			catch (Exception e) {
//				System.out.println("Kiểm tra status theo chu kì lỗi rồi");
//				e.printStackTrace();
//			}
//		}
	}
}
