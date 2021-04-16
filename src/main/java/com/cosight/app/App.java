package com.cosight.app;



import au.com.cosight.sdk.plugin.core.ICosightExecutionContext;
import au.com.cosight.sdk.plugin.core.drive.CosightDrive;
import au.com.cosight.sdk.plugin.core.drive.CosightDriveManager;
import com.amazonaws.services.s3.model.*;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.InputStream;

/**
 * spring boot command line sample application
 */
@SpringBootApplication
public class App implements CommandLineRunner  {

	private static Logger logger = LoggerFactory
			.getLogger(com.cosight.app.App.class);



	@Autowired
	private CosightDriveManager driveManager;

	@Autowired
	private ICosightExecutionContext cosightExecutionContext;

	@Autowired
	private ResourceLoader resourceLoader;


	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	// app entry
	// write ur code inside to what ever u
	@Override
	public void run(String... args) throws Exception {

		logger.info("========== STARTING PLUGIN  ====================================");
		Resource resource = resourceLoader.getResource("classpath:test.csv");

		InputStream input = resource.getInputStream();
		File f = new File("/tmp/test.csv"); // has 20G local temp storage
		FileUtils.copyInputStreamToFile(input,f);
		logger.info("parameter map {}", cosightExecutionContext.getParameters());
		String out =(String) cosightExecutionContext.getParameters().get("output-folder");
		logger.info("Uploading to {}",out);

		CosightDrive drive = driveManager.driveInstance();
		drive.copyLocal(f,out+"/upload-test.csv");
		logger.info("");

		S3Object object = drive.asS3Object(out+"/upload-test.csv");
		if (object != null) {
			logger.info("download test succesfull");
		}
		logger.info("{}",object.getKey());

		logger.info("copy file to /system/test-plugin/failed.csv should fail when running inside cosight system");
		boolean success = drive.copyLocal(f,"/system/test-plugin/failed.csv");
		logger.info("copy success {}",success);

		// download test



	}
}
