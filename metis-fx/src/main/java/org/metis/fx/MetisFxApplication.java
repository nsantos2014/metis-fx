package org.metis.fx;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MetisFxApplication {

	public static void main(String[] args) {
		//SpringApplication.run(MetisFxApplication.class, args);
		log.info("Starting app");
		System.setProperty("javafx.preloader", AppPreloader.class.getCanonicalName());
		Application.launch(SpringbootJavaFxApplication.class, args);
	}

}
