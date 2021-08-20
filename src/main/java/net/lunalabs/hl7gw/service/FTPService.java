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

import net.lunalabs.hl7gw.emul.FTPUploader;


@EnableAsync
@Service
public class FTPService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(FTPService.class);

	
	@Async
	public void FTPTest() throws Exception {
		
      logger.debug("FTP TEST START");
      FTPUploader ftpUploader = new FTPUploader("kist.lunalabs.net", "luna", "new12#$!");
      ftpUploader.uploadFile("C:\\kangminkyu\\FTPTEST.txt", "FTPTEST.txt", "/");
      ftpUploader.disconnect();
      logger.debug("FTP TEST DONE");
      
	}
	
	
	

}
