package com.project.HR.Connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrConnectApplication {

	public static void main(String[] args){

//		if (System.getenv("GOOGLE_APPLICATION_CREDENTIALS") == null){
//			System.out.println("Please set GOOGLE_APPLICATION_CREDENTIALS environment variable to <path to credentials.json>");
//			return;
//		}
		SpringApplication.run(HrConnectApplication.class, args);

	}

}
