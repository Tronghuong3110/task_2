package com.example.demo1;

import com.example.demo1.util.JsonUtil;
import com.example.demo1.util.TokenUtil;
import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.*;

public class Demo1Application {
	private static String os = "";
	private static String cmdId = "";
	private static String moduleId = "";
	private static Map<String, String> action = new ConcurrentHashMap<>();
	private static ExecutorService executorService = Executors.newFixedThreadPool(100);
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
	// kiểm tra module có đang chạy hay đã dừng hay bị lỗi
	private static String checkModule(JSONObject jsonObject, List<ProcessInfo> listProcess) {
		String commandLine = null;
		String action = "";
		boolean isWindows = os.toLowerCase().startsWith("windows");
		if(jsonObject.containsKey("action")) {
			action = (String) jsonObject.get("action");
		}
		if(os.toLowerCase().contains("windows")) {
			commandLine = (String) jsonObject.get("CommandLine");
		}
		else {
			commandLine = (String) jsonObject.get("CommandLine");
		}
		for(ProcessInfo processInfo : listProcess) {
//			if(action.equals("run")) {
			if(!isWindows && processInfo.getCommandLine().trim().toLowerCase().contains(commandLine.trim().toLowerCase())) {
				System.out.println("Hệ điều hành khác windows");
				System.out.println("Command Line 1 " + commandLine);
				System.out.println("Command Line 2 " + processInfo.getCommandLine());
				System.out.println("Process id " + processInfo.getpId());
				return processInfo.getpId() + " " + processInfo.getCommand();
			}
			else if(processInfo.getCommandLine().trim().toLowerCase().contains(commandLine.trim().toLowerCase())) {
				System.out.println("Hệ điều hành windows");
				System.out.println("Command Line 1 " + commandLine);
				System.out.println("Command Line 2 " + processInfo.getCommandLine());
				System.out.println("Process id " + processInfo.getpId());
				return processInfo.getpId() + " " + processInfo.getCommand();
			}
		}
		return -1 + " ";
	}
	// chạy module
	private static String runModule(JSONObject jsonObject) {
		List<ProcessInfo> listProcess;
		boolean isWindows = os.toLowerCase().startsWith("windows"); // kiểm tra xem hệ điều hành có phải là windows không
		String PID;
		listProcess = getListProcess(); // lấy ra danh sách các process trong task manager
		PID = checkModule(jsonObject, listProcess);
		System.out.println("PID lần 1 " + PID);
		if(jsonObject.get("idProbeModule").equals(moduleId) && jsonObject.get("idCmdHistory").equals(cmdId)) {
			if(!PID.split(" ")[0].equals("-1")) {
				return "success " + PID;
			}
		}
		// Kiểm tra đang yêu cầu chạy có đang chạy không(kiểm tra theo command line)
		if(!PID.split(" ")[0].equals("-1")) { // module có đang chạy
			System.out.println("Module có đang chạy");
			System.out.println("PID " + PID);
			return "success " + PID;
		}
		try {
			String typeModule = jsonObject.get("typeModule").toString();
			moduleId = (String) jsonObject.get("idProbeModule");
			cmdId = (String) jsonObject.get("idCmdHistory");
			ProcessBuilder builder = new ProcessBuilder();
			String command = "";
			Process p = null;
			if(isWindows) {
				String path = jsonObject.get("path").toString();
				if(!typeModule.equals("exe")) {
					command = "java -jar " + jsonObject.get("cmd_win");
					String[] t = new String[] { "cmd.exe", "/c", "start", "cmd.exe", "/K", "\" cd " + path + " && " + command };
					builder.command(t);
				}
				else {
					command = jsonObject.get("cmd_win").toString();
					String[] t = new String[] { "cmd.exe", "/c", "start", "cmd.exe", "/k", command};
//					Runtime.getRuntime().exec(command);
//					builder.command(t);
					p = Runtime.getRuntime().exec(t);
				}
			}else{
				String path = jsonObject.get("path").toString();
				Object arg = jsonObject.get("arg");
				if(path != null) {
					command = "cd " + path + " ;";
				}
				if(!typeModule.equals("exe")) {
					command += "java -jar ";
				}
				command += (String) jsonObject.get("cmd_linux");
				String [] cmd ={"gnome-terminal", "--", "bash", "-c", command + "; exec bash"};
				System.out.println("Command Line " + Arrays.toString(cmd));
				builder.command(cmd);
				p = Runtime.getRuntime().exec(cmd);
			}
//			Process process = builder.start();
			Thread.sleep(2000);

			BufferedReader output = getOutput(p);
			BufferedReader error = getError(p);
			String ligne = "";
			while ((ligne = output.readLine()) != null) {
				System.out.println("OK " + ligne);
			}

			while ((ligne = error.readLine()) != null) {
				System.out.println("Error " + ligne);
			}

			listProcess = getListProcess();
			Boolean existsModule  = true;
			for(int i = 1; i <= 2; i++) {
				PID = checkModule(jsonObject, listProcess);
				// không còn tồn tại trên task manager
				if(PID.split(" ")[0].equals("-1")) {
					existsModule = false;
					break;
				}
				Thread.sleep(2000);
			}
			System.out.println("PID lần cuối cùng trước khi trả về " + PID);
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
		String idProbeModule = jsonObject.get("idProbeModule").toString();
		action.put(idProbeModule, "stop");
		ProcessBuilder processBuilder = new ProcessBuilder();
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
			String[] cmds = new String[1000];
			if(isWindows) {
//				command = (String) jsonObject.get("cmd_win"); // câu lệnh stop đối với windows
//				cmd /c taskkill /F /PID
				cmds = new String[]{"cmd", "/c", "taskkill", "/F", "/PID", jsonObject.get("PID").toString()};
			}
			else{
				cmds = new String[]{"pkill", "-f", jsonObject.get("CommandLine").toString()};
			}
			System.out.println("PID " + PID.split(" ")[0]);
//			if(!PID.split(" ")[0].equals("-1")) { // module vẫn đang chạy ==> tiến hành dừng module
				// có thay đổi trong câu lệnh dừng ==> chưa làm
			System.out.println("Command pkill " + command + jsonObject.get("CommandLine"));
			processBuilder.command(cmds);
			processBuilder.start();
//			}

			// kiểm tra lại module xem còn đang chạy không
			listProcess = getListProcess();
			PID = checkModule(jsonObject, listProcess);
			System.out.println("PID " + PID.split(" ")[0]);
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
						if(!PID.trim().isEmpty() && command.toLowerCase().startsWith("p")) {
							if(command.toLowerCase().startsWith("p")) {
								System.out.println("==========================================");
								System.out.println("Command " + command);
								System.out.println("==========================================");
							}
							listProcess.add(new ProcessInfo(PID, command, commandLine));
						}
					}
					catch (Exception e) {
						System.out.println("Ép kiểu lỗi rồi!!(Line 229)");
						e.printStackTrace();
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
				List<ProcessHandle> listProcessHandel = processes.collect(Collectors.toList());
				for(ProcessHandle processHandle : listProcessHandel) {
					String PID = String.valueOf(processHandle.pid());
					String caption = processHandle.info().command().orElse(null);
					String commandLine = processHandle.info().commandLine().orElse(null);
					String argument = "";
					if(commandLine != null) {
						listProcess.add(new ProcessInfo(PID, caption, commandLine, argument));
					}
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
		InfoProbe infoProbe = new InfoProbe();
		try {
			FileReader fileReader=new FileReader(pathFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			JSONObject jsonObject = new JSONObject();
			JSONObject loginInfo = new JSONObject();
			while ((line = bufferedReader.readLine()) != null) {
				if(line.trim().startsWith("#")) {
					continue;
				}
				String[] lines = line.split("=");
				if (lines.length >= 2) {
					String key = lines[0].trim();
					String value = lines[1].trim();
					// nếu là chuỗi mã hóa username, password, topic
					if (key.equals("login")) {
						loginInfo = TokenUtil.deCodeToken(value, "newlife123@");
						continue;
					}
					jsonObject.put(key, value);
				}
			}
			try {
				infoProbe.setBroker(jsonObject.get("brokerUrl").toString());
				infoProbe.setClientId(jsonObject.get("clientId").toString());
				infoProbe.setConnectionTimeOut(Integer.parseInt(jsonObject.get("connectionTimeOut").toString()));
				infoProbe.setPassword(loginInfo.get("password").toString());
				infoProbe.setUsername(loginInfo.get("username").toString());
				infoProbe.setPubTopic(loginInfo.get("topic").toString());
				infoProbe.setIdProbe(Integer.parseInt(jsonObject.get("idProbe").toString()));
				infoProbe.setKeepAlive(Integer.parseInt(jsonObject.get("keepAlive").toString()));
			}
			catch (NumberFormatException nu) {
				System.out.println("Chuyển từ string sang number lỗi rồi!!");
				nu.printStackTrace();
			}
			return infoProbe;
		}
		catch (Exception e) {
			System.out.println("Đọc file lỗi rồi!!");
			e.printStackTrace();
			return null;
		}
	}
	// kiểm tra trạng thái của các module trong probe theo chu kì
	private static String checkStatus(JSONObject jsonObject) {
		// lấy ra danh sách toàn bộ process của client
		List<ProcessInfo> listProcess = getListProcess();
		// lấy ra danh sách thông tin module của client được gửi từ server
		JSONArray jsonArray = (JSONArray) jsonObject.get("listModule");
		JSONArray jsonListStatus = new JSONArray();
		// kiểm tra status của từng module dựa vào danh sách process
		for(Object object : jsonArray) {
			JSONObject json = (JSONObject) object;
			String idProbeModule = json.get("id_probe_module").toString();
			// kiểm tra xem module đang xét trươc đó có bị yêu cầu dừng không?
			// TH có thì bỏ qua việc kiểm tra trạng thái của module đó
			if(action.containsKey(idProbeModule) && action.get(idProbeModule).equals("stop")) {
				action.remove(idProbeModule);
				continue;
			}
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
				String checkPending = json.get("pending") != null ? (String) json.get("pending") : null;
				System.out.println("Check pending " + checkPending);
				if(checkFileLog(pathLog, checkPending)) { // module vẫn chạy bình thường ==> Running
					jsonStatus.put("status", "Running");
				}
				else {
					jsonStatus.put("status", "Pending"); // module đang bị treo ==> pending
				}
			}
			jsonStatus.put("probeName", jsonObject.get("probeName"));
			jsonListStatus.add(jsonStatus);
		}
		JSONArray memories = readMemory();
		return JsonUtil.createJsonStatus("Danh sách trạng thái các module của probe", jsonListStatus, (String) jsonObject.get("id_probe"), memories);
	}
	// kiêểm tra file log có đẩy data ra không
	private static Boolean checkFileLog(String pathLog, String checkPending) {
		if( checkPending == null || checkPending.equals("false")) {
			return true;
		}
//		return true;
		try {
			System.out.println("===============================================================");
			File file = new File(pathLog);
			long fileLastModifiedDate = file.lastModified();
			if(!file.canRead() || fileLastModifiedDate == 0) {
				return false;
			}
			String fileName = file.getAbsoluteFile().getName();
			System.out.println(fileLastModifiedDate);
			Date date = new Date(fileLastModifiedDate);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String myDate = simpleDateFormat.format(date);
			System.out.println("Last Modified Date of the FileName:" + fileName + "\t" + myDate);
			System.out.println("===============================================================");
			Double currentTime = Double.valueOf(System.currentTimeMillis());
			System.out.println("Thời gian chênh lệch " + Math.abs(currentTime - fileLastModifiedDate) / 60000);
			if(Math.abs(currentTime - fileLastModifiedDate) / 60000 <= 10) {
				return true;
			}
			return false;
		} catch (Exception e) {
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
			connectOptions.setCleanSession(true);
			connectOptions.setAutomaticReconnect(true);
			return connectOptions;
		}
		catch (Exception e) {
			System.out.println("Create connection error");
			e.printStackTrace();
			return null;
		}
	}
	// Lấy ra dung lượng còn trống của từng ổ cứng
	private static JSONArray readMemory() {
		JSONArray jsonArray = new JSONArray();
		for(Path root : FileSystems.getDefault().getRootDirectories()) {
			JSONObject jsonObject = new JSONObject();
			System.out.print(root + " : "); // Tên disk
			jsonObject.put("nameDisk", root.toString());
			if(Files.exists(root)) {
				try {
					FileStore store = Files.getFileStore(root);
					// dung lượng còn trống
					System.out.print(String.format("%.1f", Double.valueOf(store.getUsableSpace()) / 1073741824));
					jsonObject.put("memory_free", String.format("%.1f", Double.valueOf(store.getUsableSpace()) / 1073741824));
					// dung lượng tổng cộng của ổ
					System.out.println(String.format(", Tổng cộng = %.1f", Double.valueOf(store.getTotalSpace()) / 1073741824));
					jsonObject.put("memory_total", String.format("%.1f", Double.valueOf(store.getTotalSpace()) / 1073741824));
					jsonArray.add(jsonObject);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonArray;
	}
	// lấy giá trị load average
	private static JSONObject getLoadAverage(Integer idProbe) {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		try {
			DateTimeFormatter format12H = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
			DateTimeFormatter format24H = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			Long unix_time = System.currentTimeMillis();
			Process process = Runtime.getRuntime().exec("mpstat -P ALL 1 1");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = bufferedReader.readLine();
			// lấy ngày
			String localDateTime = line.split("\\s+")[3].replaceAll("/", "-");
			line = bufferedReader.readLine();
			if(line.trim().equals("")) {
				line = bufferedReader.readLine();
			}
			int index = 0;
			String[] ds = line.split("\\s+");
			for(int i = 0; i < ds.length; i++) {
				if(ds[i].trim().equals("CPU") || ds[i].trim().equals("Cpu") || ds[i].trim().equals("cpu")) {
					index = i;
					break;
				}
//				System.out.println(ds[i]);
			}

			while((line = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = new JSONObject();
				if(!line.contains("Average") && !line.contains("CPU") && !line.equals(" ")) {
					System.out.println("Index " + index);
					String[] cpu_loads = line.trim().split("\\s+");
					if(cpu_loads.length >= 4) {
						try {
							double total = (double) Math.round(Double.parseDouble(cpu_loads[cpu_loads.length - 1].trim().replaceAll(",", ".")));
							if(cpu_loads[index].equals("all")) {
								jsonObject.put("name", "cpu");
							}
							else {
								jsonObject.put("name", "cpu" + cpu_loads[index]);
							}
							System.out.println("Cpu usage " + total);
							jsonObject.put("cpu_usage", (100 - total));
							if((100 - total) >= 95) {
								json.put("check", 1);
							}
							jsonArray.add(jsonObject);
							if(!json.containsKey("time")) {
								String time = localDateTime;
								for(int j = 0; j < index; j++) {
									time += " " + cpu_loads[j];
								}
								try {
									LocalDateTime dateTime = LocalDateTime.parse(time.trim(), format24H);
									time = format24H.format(dateTime);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
								json.put("time", time.trim());
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(!json.containsKey("check")) {
				json.put("check", 0);
			}
			json.put("list_cpu_core", jsonArray);
			json.put("id_probe", idProbe);
			json.put("unix_time", unix_time);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	// đọc giá trị ram
	private static JSONObject getMemoryRam() {
		try {
			JSONObject jsonObject = new JSONObject();
			com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			long totalMemory = osBean.getTotalPhysicalMemorySize();
			long usedMemory = totalMemory - osBean.getFreePhysicalMemorySize();
			double totalMemoryGB = totalMemory / (1024.0 * 1024 * 1024);
			double usedMemoryGB = usedMemory / (1024.0 * 1024 * 1024);
			jsonObject.put("total_ram", totalMemoryGB);
			jsonObject.put("used_ram", usedMemoryGB);
			return jsonObject;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedReader getOutput(Process p) {
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	private static BufferedReader getError(Process p) {
		return new BufferedReader(new InputStreamReader(p.getErrorStream()));
	}

	private static String getCpu(Integer idProbe) {
		JSONObject load_average = getLoadAverage(idProbe);
		return JsonUtil.createJsonGetCpu(load_average);
    }
	public static void main(String[] args) throws Exception {
		os = System.getProperty("os.name");
		String pathFile = getArgument(args);
		System.out.println("Đường dẫn tới file config: " + pathFile);
		InfoProbe infoProbe = readFile(pathFile); // lấy thông tin client từ file config

		MqttConnectOptions connectOptions = createOption(infoProbe);
//		while (true) {
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient client = new MqttClient(infoProbe.getBroker(), infoProbe.getClientId(), persistence);
		CompletableFuture.runAsync(() -> {
			try {
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
					public void messageArrived(String s, MqttMessage mqttMessage) {
	//					CompletableFuture.runAsync(() -> {
						String json = new String(mqttMessage.getPayload());
						JSONObject jsonObject = JsonUtil.parseJson(json);
						try {
							if(!jsonObject.containsKey("check") || (jsonObject.containsKey("check") && !jsonObject.get("check").equals("true"))) {
								System.out.println("Lần gửi thứ " + mqttMessage.getId());
								System.out.println("Message " + json);
								Boolean check = false;
								// Xác nhân với server đã client đã nhận được lệnh
								String messageToServer = "";

								String response = "";
								messageToServer = "Client " + infoProbe.getClientId() + " đã nhận được lệnh ";
								if(!jsonObject.get("action").equals("getStatus") && !jsonObject.get("action").equals("getCPU")) {
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
									try {
										message = runModule(jsonObject);
										System.out.println("Kết quả chạy: " + message);
										statusModule = message.split(" ")[0].equals("success") ? "1" : "2";
										response = JsonUtil.createJson(json, message.split(" ")[0], "true", statusModule, null, null, message.split(" ")[1], null);
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
								else if(action.equals("getCPU")) {
									try {
										Integer idProbe;
										if(jsonObject.containsKey("id_probe")) {
											idProbe = Integer.parseInt(jsonObject.get("id_probe").toString());
										} else {
											idProbe = null;
										}
	//                                        CompletableFuture res = CompletableFuture.supplyAsync(() -> {
	//											return getCpu(idProbe);
	//										}, executorService);
	//										response = (String) res.get();
	//										response =
	//										MqttMessage messageCheckCpu = new MqttMessage()
									}
									catch (Exception e) {
										e.printStackTrace();
									}
								}
	//								Thread.sleep(1000);
								// gửi thông báo kết quả tới server
								System.out.println("response 2" + response);
								messageMqtt = new MqttMessage(response.getBytes());
								messageMqtt.setQos(2);
								client.publish(infoProbe.getPubTopic(), messageMqtt);
							}
						}
						catch (Exception e) {
							System.out.println("Gửi phản hồi lại cho server lỗi rồi!!(Line 455)");
							e.printStackTrace();
						}
	//					}, executorService);
					}
					@Override
					public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
					}
				});
				client.subscribe(infoProbe.getPubTopic());
				System.out.println("Client subscribe tới topic " + infoProbe.getPubTopic());
			}
			catch (MqttException me) {
				System.out.println("reason " + me.getReasonCode());
				System.out.println("msg " + me.getMessage());
				System.out.println("loc " + me.getLocalizedMessage());
				System.out.println("cause " + me.getCause());
				System.out.println("excep " + me);
				me.printStackTrace();
			}
		}, executorService);

		CompletableFuture.runAsync(() -> {
			while (true) {
				try {
					Thread.sleep(2000);
					String tmp = getCpu(infoProbe.getIdProbe());
					MqttMessage message = new MqttMessage(tmp.getBytes());
					message.setQos(2);
					client.publish(infoProbe.getPubTopic(), message);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, executorService);
	}
}


/*
*
*
* if(line.startsWith("cpu")) {
					String[] tmp = line.split("\\s+");
					double total = 0;
					for(int i = 1; i < tmp.length; i++) {
						try {
							double cpu = Double.parseDouble(tmp[i].trim());
							total += cpu;
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					Double cpu_usage = (Double.parseDouble(tmp[4]) / total ) * 100;
					jsonObject.put("cpu_usage", 100 - cpu_usage);
					jsonObject.put("name", tmp[0]);
					jsonArray.add(jsonObject);
					if((100 - cpu_usage) >= 95) {
						json.put("check", 1);
					}
				}
*
* */
















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

//			InfoProbe infoProbe = new InfoProbe();
//			infoProbe.setUsername("client1");
//			infoProbe.setKeepAlive(100);
//			infoProbe.setConnectionTimeOut(100);
//			infoProbe.setBroker("tcp://192.168.27.102:1883");
//			infoProbe.setPassword("1234");
//			infoProbe.setCleanSession("true");
//			infoProbe.setPubTopic("client_123456789");
//			infoProbe.setClientId("client1");
//		List<ProcessInfo> processInfos = getListProcess();
//		for(ProcessInfo processInfo : processInfos) {
//			System.out.println("PID " + processInfo.getpId());
//			System.out.println("CommandLine " + processInfo.getCommandLine());
//			System.out.println("===================================================================================");
//		}

// gnome-terminal -- bash -c cd /home/tronghuong/Desktop/probe_test/file_run ;./test