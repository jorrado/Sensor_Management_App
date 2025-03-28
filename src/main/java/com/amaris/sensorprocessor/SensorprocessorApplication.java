package com.amaris.sensorprocessor;

import com.amaris.sensorprocessor.repository.GatewayDao;
import com.amaris.sensorprocessor.repository.SensorDao;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.amaris.sensorprocessor.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SensorprocessorApplication implements CommandLineRunner {

//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SensorDao sensorDao;

	@Autowired
	UserDao userDao;

	@Autowired
	GatewayDao gatewayDao;

	public static void main(String[] args) { SpringApplication.run(SensorprocessorApplication.class, args); }

	@Override
	public void run(String... args) {
		// TEST SENSOR DAO
//		logger.info("all sensors -> {}", sensorDao.findAllSensors());
		System.out.println("\u001B[34m" + "all sensors -> {}" + sensorDao.findAllSensors() + "\u001B[0m");
//		logger.info("sensor id dev_eui_001 -> {}", sensorDao.findByIdOfSensor("dev_eui_001"));
		System.out.println("\u001B[34m" + "sensor id dev_eui_001 -> {}" + sensorDao.findByIdOfSensor("dev_eui_001") + "\u001B[0m");
//		logger.info("delete dev_eui_009 -> number of row(s) deleted - {}", sensorDao.deleteByIdOfSensor("dev_eui_009"));
//		System.out.println("\u001B[34m" + "delete dev_eui_009 -> number of row(s) deleted - {}" + sensorDao.deleteByIdOfSensor("dev_eui_009") + "\u001B[0m");
//		logger.info("inserting dev_eui_022 -> {}", sensorDao.insertSensor(new Sensor("dev_eui_022", "device_005",
//				LocalDateTime.now(), true, "Batiment D", 1, "Bureau 110", "gateway_004")));
//		System.out.println("\u001B[34m" + "inserting dev_eui_022 -> {}" + sensorDao.insertSensor(new Sensor("dev_eui_022",
//				"device_005", LocalDateTime.now(), true, "Batiment D",
//				1, "Bureau 110", "gateway_004")) + "\u001B[0m");
//		logger.info("update dev_eui_002 -> {}", sensorDao.updateSensor(new Sensor("dev_eui_002",
//				"device_002", LocalDateTime.now().plusYears(10), false,
//				"Batiment Z", 10, "Bureau 110 000", "gateway_010")));
//		System.out.println("\u001B[34m" + "update dev_eui_002 -> {}" + sensorDao.updateSensor(new Sensor("dev_eui_002",
//				"device_002", LocalDateTime.now().plusYears(10), false,
//				"Batiment Z", 10, "Bureau 110 000", "gateway_010")) + "\u001B[0m");










		// TEST USER DAO
		System.out.println("\u001B[34m" + "all users -> {}" + userDao.findAllUsers() + "\u001B[0m");
		System.out.println("\u001B[34m" + "user id 1 -> {}" + userDao.findByUsername("user1") + "\u001B[0m");
//		System.out.println("\u001B[34m" + "delete user9 -> number of row(s) deleted - {}"
//				+ userDao.deleteByIdOfUser(user9) + "\u001B[0m");
//		System.out.println("\u001B[34m" + "inserting 22 -> {}" + userDao.insertUser(new User(
//				"user22", "Toto", "Dupond", "$2a$10$sS1tlLqD2vrQahfdHBKGKeGNeq7oPUSnWk8bEt/uQt4jt0Cyk3Xi2", "USER",
//				"toto.dupond@example.com")) + "\u001B[0m");
//		System.out.println("\u001B[34m" + "update 2 -> {}" + userDao.updateUser(new User(
//				"userJ", "Jannette", "Smith", "password1234", "boss final",
//				"jannette.smith@example.com")) + "\u001B[0m");

		// TEST GATEWAY DAO
		System.out.println("\u001B[34m" + "all gateways -> {}" + gatewayDao.findAllGateways() + "\u001B[0m");
		System.out.println("\u001B[34m" + "gateway id gateway_001 -> {}" + gatewayDao.findByIdOfGateway("gateway_001") + "\u001B[0m");
//		System.out.println("\u001B[34m" + "delete gateway_009 -> number of row(s) deleted - {}"
//				+ gatewayDao.deleteByIdOfGateway("gateway_009") + "\u001B[0m");
//		System.out.println("\u001B[34m" + "inserting gateway_022 -> {}" + gatewayDao.insertGateway(new Gateway("gateway_022",
//				LocalDateTime.now(),true, "Batiment E", 2, "Bureau 505")) + "\u001B[0m");
//		System.out.println("\u001B[34m" + "update gateway_002 -> {}" + gatewayDao.updateGateway(new Gateway("gateway_002",
//				LocalDateTime.now(), false, "Batiment Z", 8, "Bureau 725")) + "\u001B[0m");

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode("password123");
		System.out.println("\u001B[38;5;214m" + "@@@@@ => " + hashedPassword + "\u001B[0m");

		String dbPath = System.getenv("DB_PATH");
		System.out.println("DB_PATH: " + dbPath);
	}
}
