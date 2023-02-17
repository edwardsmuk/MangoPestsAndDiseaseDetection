package org.example.inference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class ModelInferenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModelInferenceApplication.class, args);
	}

	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofBytes(16000000L)); //16MB
		factory.setMaxRequestSize(DataSize.ofBytes(16000000L));
		return factory.createMultipartConfig();
	}

}
