package com.example.demo1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo1.util.JsonUtil;
import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import java.lang.*;

public class Demo1Application {
	private static String os = "";
	private static String cmdId = "";
	private static String moduleId = "";
	private static String getArgument(String[] args) {
		Options options = new Options();
		Option input = new Option("f", "file", true, "input file path");
		input.setRequired(true);
		options.addOption(input);
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			System.exit(1);
		}
		String inputFilePath = cmd.getOptionValue("file");
		if(inputFilePath == null) {
			return System.getProperty("user.dir");
		}
		return inputFilePath;
	}
	private static MqttClient connectBroker(InfoProbe infoProbe) {
//		try {


			// MQTT connection option
//			MqttConnectOptions connectOptions = new MqttConnectOptions();
//			connectOptions.setUserName(infoProbe.getUsername());
//			connectOptions.setPassword(infoProbe.getPassword().toCharArray());
//			connectOptions.setKeepAliveInterval(infoProbe.getKeepAlive());
//			connectOptions.setCleanSession(Boolean.valueOf(infoProbe.getCleanSession()));

			// set callback
			System.out.println("Subscribe = " + infoProbe.getSubTopic());

//			return client;
//		}
		return null;
	}
	private static JSONObject deCodeToken(String token, String secretKey) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token);
			String jsonObjectStr = decodedJWT.getClaim("data").asString();
			JSONObject jsonObject = JsonUtil.parseJson(jsonObjectStr);
			return jsonObject;
		}
		catch (Exception e) {
			System.out.println("Giải mã token lỗi rồi!");
			e.printStackTrace();
			return null;
		}
	}
	private static String checkModule(JSONObject jsonObject, List<ProcessInfo> listProcess) {
		String commandLine = null;
		String action = "";
		if(jsonObject.containsKey("action")) {
			action = (String) jsonObject.get("action");
		}
		if(os.toLowerCase().contains("windows")) {
			commandLine = (String) jsonObject.get("cmd_win");
		}
		else {
			commandLine = (String) jsonObject.get("cmd_linux");
		}
		for(ProcessInfo processInfo : listProcess) {
//			if(action.equals("run")) {
				if(processInfo.getCommandLine().trim().toLowerCase().equals(commandLine.trim().toLowerCase())) {
					System.out.println("Command Line 1 " + commandLine);
					System.out.println("Command Line 2 " + processInfo.getCommandLine());
					return processInfo.getpId() + " " + processInfo.getCommand();
				}
//			}
//			else if(action.equals("stop")) {
//				if(processInfo.getpId() == Long.parseLong((String) jsonObject.get("PID"))) {
//					System.out.println("PID 1" + jsonObject.get("PID"));
//					System.out.println("Command Line 2 " + processInfo.getpId());
//					return processInfo.getpId();
//				}
//			}
//			else if (action.isEmpty()){
//				// kiểm tra status của module theo chu kì
//				if(processInfo.getpId() == Long.parseLong((String) jsonObject.get("PID")) &&
//						processInfo.getCommandLine().trim().toLowerCase().equals(commandLine.trim().toLowerCase())) {
//					System.out.println("PID 1 (status) " + jsonObject.get("PID"));
//					System.out.println("PID 2 (status)" + processInfo.getpId());
//					System.out.println("Command Line 1 (status) " + commandLine);
//					System.out.println("Command Line 2 (status) " + processInfo.getCommandLine());
//					return processInfo.getpId();
//				}
//			}
		}
		return -1 + " ";
	}
	// chạy module
	private static String runModule(JSONObject jsonObject) {
		List<ProcessInfo> listProcess = new ArrayList<>();
		boolean isWindows = os.toLowerCase().startsWith("windows");
		String PID = null; // lưu PID + processName
		listProcess = getListProcess();
		PID = checkModule(jsonObject, listProcess);
		if(jsonObject.get("idProbeModule").equals(moduleId) && jsonObject.get("idCmdHistory").equals(cmdId)) {
			if(!PID.split(" ")[0].equals("-1")) { // ddang chay
				return "success " + PID;
			}
		}
		// Kiểm tra đang yêu cầu chạy có đang chạy không(kiểm tra theo command line)
		if(!PID.split(" ")[0].equals("-1")) { // có đang chạy
			return "success " + PID;
		}
		try {
			moduleId = (String) jsonObject.get("idProbeModule");
			cmdId = (String) jsonObject.get("idCmdHistory");
			ProcessBuilder builder = new ProcessBuilder();
			String command = "";
			if(isWindows) {
				command = (String) jsonObject.get("cmd_win");
				builder.command("cmd.exe", "/c", command);
			}else{
				command = (String) jsonObject.get("cmd_linux");
				builder.command("sh", "-c", command);
			}
			Process process = builder.start();
			listProcess = getListProcess();
			Boolean existsModule  = true;
			for(int i = 1; i <= 2; i++) {
				PID = checkModule(jsonObject, listProcess);
				// không còn tồn tại trên task manager
				if(PID.split(" ")[0].equals("-1")) {
					existsModule = false;
					break;
				}
				Thread.sleep(5000);
			}
			if(existsModule) {
				// chạy thành công ==> trả về success + processId + nameProcess
				return "success " + PID;
			}
			return "fail " + PID;
		}
		catch (Exception e) {
			System.out.println("Run command line error!");
			e.printStackTrace();
			return "fail " + "-1" + " ";
		}
	}
	// dừng module
	private static String stopModule(JSONObject jsonObject) {
		List<ProcessInfo> listProcess = getListProcess();
		boolean isWindows = os.toLowerCase().startsWith("windows");
		String PID = checkModule(jsonObject, listProcess); // lấy ra process id của module đang chạy
		// kiểm tra xem lệnh này có đc gửi từ trc đó không
		if(jsonObject.get("idProbeModule").equals(moduleId) && jsonObject.get("idCmdHistory").equals(cmdId)) {
			if(PID.split(" ")[0].equals("-1")) { // module dang khong chay
				return "success " + 0 + " " + null;
			}
		}
		try {
			moduleId = (String) jsonObject.get("idProbeModule");
			cmdId = (String) jsonObject.get("idCmdHistory");
			String command = "";
			if(isWindows) {
				command = (String) jsonObject.get("cmd_win"); // câu lệnh stop đối với windows
			}else{
				command = (String) jsonObject.get("cmd_linux"); // câu lệnh stop đối với linux
			}
			String processName = (String) jsonObject.get("nameProcess"); // tên process để thực hiện dừng
			if(!PID.split(" ")[0].equals("-1")) { // module vẫn đang chạy ==> tiến hành dừng module
				// có thay đổi trong câu lệnh dừng ==> chưa làm
				Process process = Runtime.getRuntime().exec(command + " " + jsonObject.get("PID"));
			}

			// kiểm tra lại module xem còn đang chạy không
			listProcess = getListProcess();
			PID = checkModule(jsonObject, listProcess);
			// TH dừng module thành công
			if(PID.split(" ")[0].equals("-1")) {
				return "success " + 0 + " null";
			}
			// TH module nhưng chưa dừng
			// kiểm tra PID mới có trùng với PID cũ không
			// nếu trùng ==> lỗi không dừng được module
			if(!PID.split(" ")[0].equals(jsonObject.get("PID"))) {
				// TH PID mới khác PID cũ ==> kill lại
				Process process = Runtime.getRuntime().exec(command + " " + PID.split(" ")[0]);
				listProcess = getListProcess();
				PID = checkModule(jsonObject, listProcess);
				// TH dừng module thành công
				if(PID.split(" ")[0].equals("-1")) {
					return "success " + 0 + " null";
				}
			}
			return "fail " + -1 + " null";
		}
		catch (Exception e) {
			System.out.println("Stop module error!");
			e.printStackTrace();
			return "fail " + PID;
		}
	}
	// lấy ra danh sách các tiến trình đanh chạy trên hệ thống
	private static List<ProcessInfo> getListProcess() {
		List<ProcessInfo> listProcess = new ArrayList<>();
		if(os.toLowerCase().contains("windows")) {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
						"WMIC path win32_process get Caption,Processid,Commandline");
				Process process = processBuilder.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split("\\s+");
					String command = parts[0];
					String PID = parts[parts.length - 1];
					String commandLine = solveCommandLineInWindows(parts);
					try {
//						System.out.println("PID " + PID + " " + PID.trim().isEmpty());
						if(!PID.trim().isEmpty()) {
							listProcess.add(new ProcessInfo(Long.parseLong(PID), command, commandLine));
						}
					}
					catch (NumberFormatException nu) {
//						System.out.println("");
//						continue;
//						nu.printStackTrace();
					}
				}
			}
			catch (Exception e) {
				System.out.println("Get all process in windows error!");
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		else {
			try {
				Stream<ProcessHandle> processes = ProcessHandle.allProcesses();
				List<ProcessHandle> listProcessHandel = processes.toList();
				for(ProcessHandle processHandle : listProcessHandel) {
					Long PID = processHandle.pid();
					String caption = processHandle.info().command().orElse(null);
					String[] args = processHandle.info().arguments().orElse(null);
					String commandLine = processHandle.info().commandLine().orElse(null);
					String argument = "";
					for(String arg : args) {
						argument += arg + " ";
					}
					listProcess.add(new ProcessInfo(PID, caption, commandLine, argument));
				}
			}
			catch (Exception e) {
				System.out.println("Get all process in linux error!");
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		return listProcess;
	}
	// tách thành caption, PID, Command line từ chuỗi lấy ra được trong task manager của windows
	private static String solveCommandLineInWindows(String[] commandLine) {
		String command = "";
		for(int i = 1; i < commandLine.length-1; i++) {
			command += commandLine[i] + " ";
		}
		return command;
	}
	private static InfoProbe readFile(String pathFile) {
		try {
			File file = new File(pathFile);
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			JSONObject jsonObject = deCodeToken(properties.getProperty("token"), "tronghuong");
			InfoProbe infoProbe = new InfoProbe();
			infoProbe.setBroker(properties.getProperty("broker"));
			infoProbe.setClientId((String) jsonObject.get("clientId"));
			infoProbe.setPassword((String) jsonObject.get("password"));
			infoProbe.setCleanSession(properties.getProperty("cleanSession"));
			infoProbe.setPubTopic((String) jsonObject.get("pubtopic"));
			infoProbe.setSubTopic((String) jsonObject.get("subtopic"));
			infoProbe.setConnectionTimeOut(Integer.parseInt(properties.getProperty("connectionTimeOut")));
			infoProbe.setUsername(properties.getProperty("username"));
			infoProbe.setKeepAlive(Integer.parseInt(properties.getProperty("keepAlive")));

			String username = properties.getProperty("username");
			System.out.println("username" + username);
			return infoProbe;
		}
		catch (Exception e) {
			System.out.println("Read file error");
			e.printStackTrace();
			return null;
		}
	}
	// kiểm tra trạng thái của các module trong probe
	private static String checkStatus(JSONObject jsonObject) {
		// lấy ra danh sách toàn bộ process của client
		List<ProcessInfo> listProcess = getListProcess();
		// lấy ra danh sách thông tin module của client được gửi từ server
		JSONArray jsonArray = (JSONArray) jsonObject.get("listModule");
		JSONArray jsonListStatus = new JSONArray();
		// kiểm tra status của từng module dựa vào danh sách process
		for(Object object : jsonArray) {
			JSONObject json = (JSONObject) object;
			JSONObject jsonStatus = new JSONObject();
			String pId = checkModule(json, listProcess);
			System.out.println("PID "+ pId.split(" ")[0]);
			// TH module không còn chạy
			jsonStatus.put("id_probe_module", json.get("id_probe_module"));
			if (pId.split(" ")[0].equals("-1")) { // module không còn chạy ==> lỗi
				jsonStatus.put("status", "Failed");
			}
			else {
				String pathLog = (String) json.get("path_log");
				if(checkFileLog(pathLog)) { // module vẫn chạy bình thường ==> Running
					jsonStatus.put("status", "Running");
				}
				else {
					jsonStatus.put("status", "Pending"); // module đang bị treo ==> pending
				}
			}
			jsonListStatus.add(jsonStatus);
		}
		return JsonUtil.createJsonStatus("Danh sách trạng thái các module của probe", jsonListStatus, (String) jsonObject.get("id_probe"));
	}
	// kiêểm tra file log có đẩy data ra không
	private static Boolean checkFileLog(String pathLog) {
		FileTime fileTime;
		try {
			fileTime = Files.getLastModifiedTime(Path.of(pathLog));
			LocalDateTime ldt = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
			LocalDateTime nowTime = LocalDateTime.now();
			// thoi gian chenh lech giua lan chinh sua gan nhat toi hien tai
			Duration thoiGian = Duration.between(ldt, nowTime);

			// module có đẩy ra file log bình thường
//			if (thoiGian.toMinutes() <= 1) {
//				return true;
//			}
			// module không đẩy dữ liệu ra file log
			return true;
		} catch (IOException e) {
			System.err.println("Cannot get the last modified time - " + e);
			return false;
		}
	}
	private static MqttConnectOptions createOption(InfoProbe infoProbe) {
		try {
			MqttConnectOptions connectOptions = new MqttConnectOptions();
			connectOptions.setUserName(infoProbe.getUsername());
			connectOptions.setPassword(infoProbe.getPassword().toCharArray());
			connectOptions.setKeepAliveInterval(infoProbe.getKeepAlive());
			connectOptions.setCleanSession(Boolean.valueOf(infoProbe.getCleanSession()));
			return connectOptions;
		}
		catch (Exception e) {
			System.out.println("Create connection error");
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) throws Exception {
		os = System.getProperty("os.name");
//		String pathFile = getArgument(args);
//		System.out.println("Đường dẫn tới file config: " + pathFile);
//		InfoProbe infoProbe = readFile(pathFile); // lấy thông tin client từ file config
			InfoProbe infoProbe = new InfoProbe();
			infoProbe.setUsername("client1");
			infoProbe.setKeepAlive(100);
			infoProbe.setConnectionTimeOut(100);
			infoProbe.setBroker("tcp://localhost:1883");
			infoProbe.setPassword("1234");
			infoProbe.setCleanSession("true");
			infoProbe.setPubTopic("client_123456789");
			infoProbe.setClientId("client1");
			MqttConnectOptions connectOptions = createOption(infoProbe);
//		while (true) {
			try {
				MemoryPersistence persistence = new MemoryPersistence();
				MqttClient client = new MqttClient(infoProbe.getBroker(), infoProbe.getClientId(), persistence);
				client.connect(connectOptions);
				System.out.println(infoProbe.getUsername() + " " + infoProbe.getPassword() + " " + infoProbe.getClientId());
				client.setCallback(new MqttCallback() {
					@Override
					public void connectionLost(Throwable throwable) {
						// reconnect
						while (!client.isConnected()) {
							try {
								System.out.println("Đang kết nối lại...");
								Thread.sleep(5000);
								client.reconnect();
								client.subscribe(infoProbe.getPubTopic());
								System.out.println("Kết nối lại thành công!!");
							}
							catch(InterruptedException | MqttException e) {
								e.printStackTrace();
							}
						}
					}
					@Override
					public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
						String json = new String(mqttMessage.getPayload());
						JSONObject jsonObject = JsonUtil.parseJson(json);
						if(!jsonObject.containsKey("check") || (jsonObject.containsKey("check") && !jsonObject.get("check").equals("true"))) {
							System.out.println("Lần gửi thứ " + mqttMessage.getId());
							System.out.println("Message " + json);
							// Xác nhân với server đã client đã nhận được lệnh
							String messageToServer = "";

							String response = "";
							messageToServer = "Client đã nhận được lệnh ";
							if(!jsonObject.get("action").equals("getStatus")) {
								if(os.toLowerCase().contains("windows")) {
									messageToServer.concat((String) jsonObject.get("cmd_win"));
								}
								else {
									messageToServer.concat((String) jsonObject.get("cmd_linux"));
								}
							}
							response = JsonUtil.createJson(json, messageToServer, "OK", null, null, null, "", "");

							// gửi thông báo đã nhận được lệnh tới server
							System.out.println("response 1" + response);
							MqttMessage messageMqtt = new MqttMessage(response.getBytes());
							messageMqtt.setQos(2);
							client.publish(infoProbe.getPubTopic(), messageMqtt);

							String action = (String) jsonObject.get("action");
							String message = null;
							String statusModule = null;
							// chạy module
							if(action.equals("run")) {
								// chạy lệnh
								try {
									message = runModule(jsonObject);
									statusModule = message.split(" ")[0].equals("success") ? "1" : "2";
									response = JsonUtil.createJson(json, message.split(" ")[0], "true", statusModule, null, null, message.split(" ")[1], message.split(" ")[2]);
								}
								catch (Exception e) {
									e.printStackTrace();
									response = JsonUtil.createJson(json, "Xảy ra lỗi trong quá trình chạy lệnh", "OK", null, null, null, "", "");
								}
							}
							// dừng module
							else if(action.equals("stop")) {
								try {
									message = stopModule(jsonObject);
									statusModule = message.split(" ")[0].equals("success") ? "2" : "1";
									response = JsonUtil.createJson(json, message.split(" ")[0], "true", statusModule, null, null, message.split(" ")[1], message.split(" ")[2]);
								}
								catch (Exception e) {
									e.printStackTrace();
									response = JsonUtil.createJson(json, "Xảy ra lỗi trong quá trình dừng lệnh", "OK", null, null, null, "", "");
								}
							}
							// kiểm tra status module theo chu kì
							else if (action.equals("getStatus")) {
								try {
									response = checkStatus(jsonObject);
								}
								catch (Exception e) {
									e.printStackTrace();
									response = JsonUtil.createJson(json, "Xảy ra lỗi trong quá trình kiểm tra trạng thái lệnh", "OK", null, null, null, "", "");
								}
							}

							// gửi thông báo kết quả tới server
							System.out.println("response 2" + response);
							messageMqtt = new MqttMessage(response.getBytes());
							messageMqtt.setQos(2);
							client.publish(infoProbe.getPubTopic(), messageMqtt);
						}
					}
					@Override
					public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
					}
				});
				client.subscribe(infoProbe.getPubTopic());
			}
			catch (MqttException me) {
				System.out.println("reason " + me.getReasonCode());
				System.out.println("msg " + me.getMessage());
				System.out.println("loc " + me.getLocalizedMessage());
				System.out.println("cause " + me.getCause());
				System.out.println("excep " + me);
				me.printStackTrace();
//				return null;
			}
//		}
	}
}


//	private static void sendMessage(InfoProbe infoProbe, String response, MqttClient client) {
//		int qos = 0;
//		try {
//			MqttMessage message = new MqttMessage(response.getBytes());
//			message.setQos(qos);
//			client.publish(infoProbe.getPubTopic(), message);
//		}
//		catch (MqttException e) {
//			System.out.print("Send message to server error");
//		}
//	}
