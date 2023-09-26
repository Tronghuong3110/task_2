package com.example.demo1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo1.util.JsonUtil;
import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import java.io.*;
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
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			MqttClient client = new MqttClient(infoProbe.getBroker(), infoProbe.getClientId(), persistence);

			// MQTT connection option
			MqttConnectOptions connectOptions = new MqttConnectOptions();
			connectOptions.setUserName(infoProbe.getUsername());
			connectOptions.setPassword(infoProbe.getPassword().toCharArray());
			connectOptions.setKeepAliveInterval(infoProbe.getKeepAlive());
			connectOptions.setCleanSession(Boolean.valueOf(infoProbe.getCleanSession()));

			// set callback
			System.out.println("Subscribe = " + infoProbe.getSubTopic());
			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable throwable) {
					// reconnect
					while (!client.isConnected()) {
						try {
							System.out.println("Đang kết nối lại...");
							Thread.sleep(5000);
							connectOptions.setUserName(infoProbe.getUsername());
							connectOptions.setPassword(infoProbe.getPassword().toCharArray());
							client.connect(connectOptions);
							client.subscribe(infoProbe.getSubTopic());
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
					// Xác nhân với server đã client đã nhận được lệnh
					JSONObject jsonObject = JsonUtil.parseJson(json);
					String messageToServer = "";
					String cmdIdCurrent = (String) jsonObject.get("idCmdHistory");
					String moduleIdCurrent = (String) jsonObject.get("idProbeModule");
					// Lần trước đó chưa nhận được lệnh có idCmd và idModule rồi
					String response = "";
					messageToServer = "Client đã nhận được lệnh ";
					if(os.toLowerCase().contains("windows")) {
						messageToServer.concat((String) jsonObject.get("cmd_win"));
					}
					else {
						messageToServer.concat((String) jsonObject.get("cmd_linux"));
					}
					if(!cmdId.equals(cmdIdCurrent) && !moduleId.equals(moduleIdCurrent)) {
						// gán lại để lần sau kiểm tra
						// status = ok: đã nhận được lệnh và lệnh chưa từng đc nhận trc đó
						cmdId = cmdIdCurrent;
						moduleId = moduleIdCurrent;
						response = JsonUtil.createJson(json, messageToServer, "OK", null, null, null);
					}
					else {
						Boolean checkModule = checkModule(jsonObject);
						// TH lệnh đang được chay
						if(checkModule) {
							// status = restart: đã nhận được lệnh, lệnh đó đã được nhận và đang chạy trước đó
							response = JsonUtil.createJson(json, messageToServer, "restart", null, null, null);
						}
						// TH lệnh nhận được chưa chạy
						else {
							response = JsonUtil.createJson(json, messageToServer, "OK", null, null, null);
						}
					}
					// gửi thông báo đã nhận được lệnh tới server
					sendMessage(infoProbe, response, client);
					// chạy lệnh
					String message = runModule(jsonObject);
					String statusModule = message.equals("success") ? "1" : "3";
					response = JsonUtil.createJson(json, message, "true", statusModule, null, null);
					sendMessage(infoProbe, response, client);
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

				}
			});

			client.connect(connectOptions);
			client.subscribe(infoProbe.getSubTopic());
			return client;
		}
		catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
			return null;
		}
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
	private static Boolean checkModule(JSONObject jsonObject) {
		String commandLine = "";
		if(os.toLowerCase().contains("windows")) {
			commandLine = (String) jsonObject.get("cmd_win");
		}
		else {
			commandLine = (String) jsonObject.get("cmd_linux");
		}
		List<ProcessInfo> listProcess = getListProcess();
		String finalCommandLine = commandLine;
		return listProcess.stream()
				.map(ProcessInfo :: getCommandLine)
				.anyMatch(command -> command.equals(finalCommandLine));
	}
	private static String runModule(JSONObject jsonObject) {
		boolean isWindows = os.toLowerCase().startsWith("windows");
		try {
			ProcessBuilder builder = new ProcessBuilder();
			String command = "";
			if(isWindows) {
				command = (String) jsonObject.get("cmd_win");
				builder.command("cmd.exe", "/c", command);
			}else {
				command = (String) jsonObject.get("cmd_linux");
				builder.command("sh", "-c", command);
			}
			Process process = builder.start();
			Boolean existsModule  = true;
			for(int i = 1; i <= 2; i++) {
				if(!checkModule(jsonObject)) {
					existsModule = false;
					break;
				}
				Thread.sleep(5);
			}
			if(existsModule) {
				return "success";
			}
			return "fail";
		}
		catch (Exception e) {
			System.out.println("Run command line error!");
			e.printStackTrace();
			return "fail";
		}
	}
	private static List<ProcessInfo> getListProcess() {
		List<ProcessInfo> listProcess = new ArrayList<>();
		if(os.toLowerCase().contains("windows")) {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
						"wmic process get Caption,Processid,Commandline");
				Process process = processBuilder.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split("\\s+");
					String command = parts[0];
					String PID = parts[parts.length - 1];
					String commandLine = solveCommandLineInWindows(parts);
					listProcess.add(new ProcessInfo(Long.valueOf(PID), command, commandLine));
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
					String command = processHandle.info().command().orElse(null);
					String[] args = processHandle.info().arguments().orElse(null);
					String commandLine = processHandle.info().commandLine().orElse(null);
					String argument = "";
					for(String arg : args) {
						argument += arg + " ";
					}
					listProcess.add(new ProcessInfo(PID, command, commandLine, argument));
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
	private static String solveCommandLineInWindows(String[] commandLine) {
		String command = "";
		for(int i = 1; i < commandLine.length-1; i++) {
			command += commandLine[i] + " ";
		}
		return command;
	}
	private static void sendMessage(InfoProbe infoProbe, String response, MqttClient client) {
		int qos = 0;
		try {
			MqttMessage message = new MqttMessage(response.getBytes());
			message.setQos(qos);
			client.publish(infoProbe.getPubTopic(), message);
		}
		catch (MqttException e) {
			System.out.print("Send message to server error");
		}
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

	public static void main(String[] args) throws Exception {
		os = System.getProperty("os.name");
		while (true) {
			String pathFile = getArgument(args);
			System.out.println("Đường dẫn tới file config: " + pathFile);
			InfoProbe infoProbe = readFile(pathFile);

			System.out.println("Os = " + os);
		}
	}
}
