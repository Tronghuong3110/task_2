package com.newlife.Connect_multiple;

import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import com.newlife.Connect_multiple.entity.ProbeEntity;
import com.newlife.Connect_multiple.entity.ProbeOptionEntity;
import com.newlife.Connect_multiple.entity.ServerEntity;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import com.newlife.Connect_multiple.service.impl.ProbeModuleService;
import com.newlife.Connect_multiple.service.impl.ProbeService;
import com.newlife.Connect_multiple.util.ConstVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
public class ConnectMultipleApplication {

	@Autowired
	private IProbeModuleService probeModuleService;
	private static ExecutorService executorService = Executors.newFixedThreadPool(3);

	public static void main(String[] args){
		readFileBroker(args[1]);
		ApplicationContext applicationContext = SpringApplication.run(ConnectMultipleApplication.class, args);
		ProbeService probeService = applicationContext.getBean(ProbeService.class);
		// Thêm mới probe(Server)
		String mesage = probeService.saveServer(createServer(), createProbeOption());
		ProbeModuleService probeModuleService = applicationContext.getBean(ProbeModuleService.class);
		System.out.println(mesage);
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
		CompletableFuture.runAsync(() -> {
			while (true) {
				try {
					Thread.sleep(2000);
					System.out.println("Lấy thông tin cpu theo chu kì");
					probeModuleService.getCpuUsage();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
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
