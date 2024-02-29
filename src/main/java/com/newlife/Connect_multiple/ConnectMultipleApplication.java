package com.newlife.Connect_multiple;

import com.newlife.Connect_multiple.controller.CaptureController;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import com.newlife.Connect_multiple.entity.ProbeEntity;
import com.newlife.Connect_multiple.entity.ProbeOptionEntity;
import com.newlife.Connect_multiple.entity.ServerEntity;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import com.newlife.Connect_multiple.service.impl.ProbeModuleService;
import com.newlife.Connect_multiple.service.impl.ProbeService;
import com.newlife.Connect_multiple.util.ConstVariable;
import jdk.nashorn.internal.ir.TryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
@EnableMongoRepositories
//@Configuration
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ConnectMultipleApplication {

	private static ExecutorService executorService = Executors.newFixedThreadPool(10);

	public static void main(String[] args){
		readFileBroker(args[1]);
		ApplicationContext applicationContext = SpringApplication.run(ConnectMultipleApplication.class, args);
		ProbeService probeService = applicationContext.getBean(ProbeService.class);
		// Thêm mới probe(Server)
		String mesage = probeService.saveServer(createServer(), createProbeOption());
		ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);

		CaptureController captureController = applicationContext.getBean(CaptureController.class);
		// lấy trạng thaái module theo chu kì
		CompletableFuture.runAsync(() -> {
			while (true) {
				try {
					probeModuleService.getStatusModulePeriodically();
					System.out.println("Test");
					Thread.sleep(15000);
				}
				catch (Exception e) {
					System.out.println("Kiểm tra status theo chu kì lỗi rồi");
					e.printStackTrace();
				}
			}
		}, executorService);
		// lấy thông tin cpu theo chu kì
//		CompletableFuture.runAsync(() -> {
//			while (true) {
//				try {
//					Thread.sleep(2000);
//					System.out.println("Lấy thông tin cpu theo chu kì");
//					probeModuleService.getCpuUsage();
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}, executorService);
		// Kiểm tra trạng thái kết nối của probe
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(10000);
				probeService.checkConnectFromProbeToBroker();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}, executorService);

		CompletableFuture.runAsync(() -> {
			try {
				captureController.solveRestoreTime();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}, executorService);
	}
	private static void readFileBroker(String path) {
		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			while((line = reader.readLine()) != null) {
				String[] lines = line.split("=");
				if(lines[0].trim().toLowerCase().equals("username")) {
					ConstVariable.KEY_BROKER = lines[1].trim();
				}
				else if (lines[0].trim().toLowerCase().equals("password")){
					ConstVariable.SECRET_KEY_BROKER = lines[1].trim();
				} else if (lines[0].trim().toLowerCase().equals("ip")){
					System.out.print("IPADDRESS " + lines[1]);
					ConstVariable.IPADDRESS = lines[1].trim();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ServerEntity createServer() {
		ServerEntity server = new ServerEntity();
		server.setName("server");
		server.setServerIdConnect("server");
		return server;
	}
	private static ProbeOptionEntity createProbeOption() {
		ProbeOptionEntity probeOptionEntity = new ProbeOptionEntity();
		probeOptionEntity.setUserName("server");
		probeOptionEntity.setConnectionTimeOut(100);
		probeOptionEntity.setPassword("1234");
		probeOptionEntity.setKeepAlive(100);
		probeOptionEntity.setCleanSession(true);
		return probeOptionEntity;
	}

}
