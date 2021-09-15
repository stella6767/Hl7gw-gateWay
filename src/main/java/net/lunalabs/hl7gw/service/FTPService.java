package net.lunalabs.hl7gw.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.CustomFtpClient;
import net.lunalabs.hl7gw.config.FTPUploader;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@EnableAsync
@Service
public class FTPService {

	private static final Logger logger = LoggerFactory.getLogger(FTPService.class);

	private final FTPUploader ftpUploader;

	
	@Async
	public void ftpSendToCs2(String filePath) throws Exception { // Central Statino sever로 파일 전송

		
		String pattern = Pattern.quote(System.getProperty("file.separator"));

		 		
		logger.debug("filePath: " + filePath);
		File fileTest = new File(filePath);
		String fileName = fileTest.getName();
		logger.debug("simpleFileName: " + fileName);
		
		logger.debug(fileTest.getAbsolutePath());
		logger.debug(fileTest.getParent());
		logger.debug(fileTest.getParentFile().toString());
		logger.debug(fileTest.getCanonicalPath());
		
		String[] parentStrings =fileTest.getParent().split(pattern);
		
		logger.debug(parentStrings.toString());
		
		logger.debug(parentStrings[parentStrings.length -2]); //부모 폴더의 부모폴더이름을 뽑아낸다.
		
		String folderName = parentStrings[parentStrings.length -2];
		
		
		ftpUploader.CheckAndMakeDirectory(File.separator + folderName);
		
		logger.debug("FTP SEND START");
		
		ftpUploader.uploadFile(filePath, fileName, File.separator+ folderName + File.separator); //파일 전송시 공백있는 이름의 파일을 전송하면 안 됨, 위에서 이미 공백을 다 제거했기 때문.
		//ftpUploader.disconnect();
		logger.debug("FTP SEND DONE");
				
	}
	
	


}
