package net.lunalabs.hl7gw.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@Configuration
public class CustomFtpClient { //싱글톤으로 쓰고 싶어서 만들었지만 안 먹힘

	private static final Logger logger = LoggerFactory.getLogger(CustomFtpClient.class);
	
	private final Common common;
	
	  @Bean("MFtpClient")
	  public FTPClient ftpClient() throws Exception {

		  logger.debug("싱글톤");
		  
		  FTPClient ftpClient = new FTPClient();
		  
			ftpClient.setDefaultPort(common.ftpPort);
			ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
			int reply;
			ftpClient.connect(common.ip);// 호스트 연결
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}
			ftpClient.login(common.ftpUser, common.ftpPwd);// 로그인
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
		  
			return ftpClient;
	  }
	
	

	
}
