package net.lunalabs.hl7gw.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.utills.Common;



@Component
public class FTPUploader { 


	private static final Logger log = LoggerFactory.getLogger(FTPUploader.class);
	
	
	@Qualifier("MFtpClient")
	@Autowired
	private FTPClient client;
	
		
	public void uploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			client.storeFile(hostDir + fileName, input);
			// storeFile() 메소드가 전송하는 메소드
		}
	}
	
	
	public void disconnect() throws Exception {
		if (client.isConnected()) {
			try {
				client.logout();
				client.disconnect();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}

	public void CheckAndMakeDirectory(String path) throws Exception{
		boolean isExist;
		isExist = client.changeWorkingDirectory(path);
		// 없으면 폴더 생성
		if(!isExist){
			client.makeDirectory(path);
		}
	}
	

}
