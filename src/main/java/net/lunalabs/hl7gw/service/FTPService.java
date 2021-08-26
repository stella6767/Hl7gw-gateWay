package net.lunalabs.hl7gw.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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
import net.lunalabs.hl7gw.config.FTPUploader;

@RequiredArgsConstructor
@EnableAsync
@Service
public class FTPService {

	private static final Logger logger = LoggerFactory.getLogger(FTPService.class);

	//private final FTPUploader ftpUploader;

	
	@Async
	public void FTPTest() throws Exception {
		 FTPUploader ftpUploader = new FTPUploader("kist.lunalabs.net", "luna",
		 "new12#$!");

		//ftpUploader.getFtpBean();

		logger.debug("FTP TEST START");
		ftpUploader.uploadFile("C:\\kangminkyu\\FTPTEST.txt", "FTPTEST.txt", "/");
		ftpUploader.disconnect();
		logger.debug("FTP TEST DONE");

	}

	@Async
	public void ftpSendToCs(String filename) throws Exception { // Central Statino sever로 파일 전송
		logger.debug("file send to Cs");

		FTPUploader ftpUploader = new FTPUploader("kist.lunalabs.net", "luna", "new12#$!"); // 일단은 다른 서버에,

		ftpUploader.uploadFile(filename, "FTPTEST.txt", "/");
		ftpUploader.disconnect();
		logger.debug("FTP SEND DONE");

	}

}
