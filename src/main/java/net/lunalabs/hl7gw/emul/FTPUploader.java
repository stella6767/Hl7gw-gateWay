package net.lunalabs.hl7gw.emul;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUploader {

	FTPClient ftpClient = null;

	// param( host server ip, username, password ) 생성자
	public FTPUploader(String host, String user, String pwd) throws Exception {
		ftpClient = new FTPClient();
		ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		int reply;
		ftpClient.connect(host);// 호스트 연결
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftpClient.login(user, pwd);// 로그인
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();
	}

	// param( 보낼파일경로+파일명, 호스트에서 받을 파일 이름, 호스트 디렉토리 )
	public void uploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			this.ftpClient.storeFile(hostDir + fileName, input);
			// storeFile() 메소드가 전송하는 메소드
		}
	}

	public void disconnect() {
		if (this.ftpClient.isConnected()) {
			try {
				this.ftpClient.logout();
				this.ftpClient.disconnect();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}


}
