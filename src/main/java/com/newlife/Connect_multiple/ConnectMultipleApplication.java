package com.newlife.Connect_multiple;

import com.newlife.Connect_multiple.service.IProbeModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class ConnectMultipleApplication implements CommandLineRunner {

	@Autowired
	private IProbeModuleService probeModuleService;

	public static void main(String[] args){
		ApplicationContext applicationContext = SpringApplication.run(ConnectMultipleApplication.class, args);
	}

	@Override
	public void run(String... args) {
		while (true) {
			try {
//				ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);
				probeModuleService.getStatusModulePeriodically();
				System.out.println("Test");
				Thread.sleep(15000);
			}
			catch (Exception e) {
				System.out.println("Kiểm tra status theo chu kì lỗi rồi");
				e.printStackTrace();
			}
		}
	}
}
