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
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomFtpClient extends FTPClient { //싱글톤으로 쓰고 싶어서 만들었지만 안 먹힘

	private static final Logger logger = LoggerFactory.getLogger(CustomFtpClient.class);
	
	
	//this.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	
	@Override
	public void addProtocolCommandListener(ProtocolCommandListener listener) {
		// TODO Auto-generated method stub
		this.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}
	
	@Override
	public void connect(InetAddress host) throws SocketException, IOException {
		// TODO Auto-generated method stub
		this.connect("localhost", 21);
	}
	
	
	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub
		
		int reply = getReplyCode();
		
		if (!FTPReply.isPositiveCompletion(reply)) {
			disconnect();
			//throw new Exception("Exception in connecting to FTP Server");
			logger.info("Exception in connecting to FTP Server");
		}
		
	}
	
	
//	@Override
//	public void setDefaultPort(int port) {
//		// TODO Auto-generated method stub
//		setDefaultPort(21);
//	}
	
	@Override
	public boolean login(String username, String password) throws IOException {
		// TODO Auto-generated method stub
		return this.login("kyu","1234");
	}
	
	@Override
	public boolean setFileType(int fileType) throws IOException {
		// TODO Auto-generated method stub
		return this.setFileType(FTP.BINARY_FILE_TYPE);
	}
	
	
	@Override
	public void enterLocalPassiveMode() {
		// TODO Auto-generated method stub
		this.enterLocalPassiveMode();
	}
	
	
	
	// param( 보낼파일경로+파일명, 호스트에서 받을 파일 이름, 호스트 디렉토리 )
	public void uploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			boolean isSuccess = this.storeFile(hostDir + fileName, input);
			if (isSuccess){ 
				// 성공
				logger.debug("#File Upload Success");
				} else { 
					logger.debug("#File Upload Fail");
				} 
			
			// storeFile() 메소드가 전송하는 메소드
		}
	}

	public void customDisconnect() {
		if (this.isConnected()) {
			try {
				this.logout();
				this.disconnect();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}
	
	

	
}
