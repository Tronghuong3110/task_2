package com.newlife.Connect_multiple;

import com.newlife.Connect_multiple.service.IProbeModuleService;
import com.newlife.Connect_multiple.service.impl.ProbeModuleService;
import com.newlife.Connect_multiple.util.ConstVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;


@SpringBootApplication
public class ConnectMultipleApplication {

	@Autowired
	private IProbeModuleService probeModuleService;

	public static void main(String[] args){
		readFileBroker(args[1]);
		ApplicationContext applicationContext = SpringApplication.run(ConnectMultipleApplication.class, args);
		while (true) {
			try {
				ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);
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

//	@Override
//	public void run(String... args) {
//		while (true) {
//			try {
////				ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);
//				probeModuleService.getStatusModulePeriodically();
//				System.out.println("Test");
//				Thread.sleep(15000);
//			}
//			catch (Exception e) {
//				System.out.println("Kiểm tra status theo chu kì lỗi rồi");
//				e.printStackTrace();
//			}
//		}
//	}

	private static void readFileBroker(String path) {
		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			while((line = reader.readLine()) != null) {
				String[] lines = line.split("=");
				if(lines[0].trim().equals("username")) {
					ConstVariable.username = lines[1].trim();
				}
				else if (lines[0].trim().equals("password")){
					ConstVariable.password = lines[1].trim();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
