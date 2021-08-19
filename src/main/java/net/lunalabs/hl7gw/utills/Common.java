package net.lunalabs.hl7gw.utills;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import net.lunalabs.hl7gw.dto.PR100RespDto;
import net.lunalabs.hl7gw.emul.GwEmulThread;



/**
 * 
 * @FileName  : Common.java
 * @Project   : HL7GW_Emul
 * @Date      : 2021. 07. 16. 
 * @작성자      : winix
 * @설명       : 공통 사용 메소드
 */


@Component
public class Common {
	
	
	public ByteBuffer str_to_bb(String msg){
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		CharsetDecoder decoder = charset.newDecoder();
		  try{
		    return encoder.encode(CharBuffer.wrap(msg));
		  }catch(Exception e){e.printStackTrace();}
		  return null;
		}
	
	
	public static List<PR100RespDto> searchPatientID(List<PR100RespDto> pr100RespDtos, List<PR100RespDto> fakeList, String searchWord) {

		
		
		for (PR100RespDto pr100RespDto : pr100RespDtos) {
			
			
			
			boolean a = ((Integer)pr100RespDto.getPatientId()).toString().contains("searchWord");
			
			String b = ((Integer)pr100RespDto.getPatientId()).toString();
			
			//logger.debug("문자열 변환: " + b);					
			//logger.debug("pr100RespDto: " + pr100RespDto);
			//logger.debug("진실은: "  + a);
			
			if(((Integer)pr100RespDto.getPatientId()).toString().contains(searchWord)) {
				fakeList.add(pr100RespDto);
			}						
			
		}
		
		
		return fakeList;
		
	}
	
	public static List<String> generateRandomString() {
		 
	    int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = false;
	    
	    
	    List<String> test = new ArrayList<>();
	    
	    
	    for (int i = 0; i < 100; i++) {
		    String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

		    System.out.println("random last name: " + generatedString);
		    
		    test.add(generatedString);
		}
	    

	    return test;
	}
	
	
	public static List<PR100RespDto> searchName(List<PR100RespDto> pr100RespDtos, List<PR100RespDto> fakeList, String searchWord) {

		
		
		
		for (PR100RespDto pr100RespDto : pr100RespDtos) {
			
			
			
			boolean a = ((Integer)pr100RespDto.getPatientId()).toString().contains("searchWord");
			
			String name = (pr100RespDto.getFirstName()) + pr100RespDto.getLastName();
			
			//logger.debug("문자열 변환: " + b);					
			//logger.debug("pr100RespDto: " + pr100RespDto);
			//logger.debug("진실은: "  + a);
			
			if(name.contains(searchWord)) {
				fakeList.add(pr100RespDto);
			}						
			
		}
		
		
		return fakeList;
		
	}
	
	
	
	
	public static List<PR100RespDto> createDummyPatients() {
		List<PR100RespDto> patients = IntStream.range(0, 100)
				.mapToObj(i -> PR100RespDto.builder()
								.patientId(i)
//								.firstName(generateRandomString())
								.firstName(GwEmulThread.fakeName.get(i))
								.lastName("lastname")
								.gender(0)
								.age(20)
								.height(180)
								.weight(80)
								.commnet("dummy data")
								.lastSession(parseLocalDateTime())
								.build()
						
						)
				.collect(Collectors.toList());
		
		return patients;
		
	}
	
	
	public static String parseLocalDateTime() {
		
//		String dummydate = (LocalDateTime.parse(LocalDateTime.now(), 
//				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString());
		
		String dummydate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		
		return dummydate;
	}
	
	

	
	
	
	/**
	 * UNIQ한 IMSI값 생성
	 * miliseconds + random 4자리
	 * @return
	 */
	public String generateImsi(){
		
		String imsi = "";
		
		long mili = System.currentTimeMillis();

		imsi += mili;
		imsi = imsi.substring(2);
		
		int tail = (int)(Math.random()*10000);
		
		if(tail < 10){
			imsi += "000"+tail;
		}else if(tail < 100){
			imsi += "00"+tail;
		}else if(tail < 1000){
			imsi += "0"+tail;
		}else{
			imsi += tail;
		}

		return imsi;
		
	}
	
	/**
	 * pr_flag값 랜덤 생성 (I, U, D)
	 * @return
	 */
	public String generatePrFlag(){
		
		String pr_flag = "I";
		List flag_list = new ArrayList();
		
		for(int i=0; i < 3; i++){
			flag_list.add("I");
		}
		flag_list.add("U");
		flag_list.add("D");
		
		if(!flag_list.isEmpty() && flag_list.size() > 0){
			int random = (int)(Math.random()*flag_list.size());
			pr_flag = flag_list.get(random).toString();
		}
		
		return pr_flag;
	}
	
	/**
	 * proc_rslt값 랜덤 생성 (1:성공, 2, 3)
	 * @return
	 */
	public int generateProcRslt(){
		
		/*int proc_rslt = 03;
		
		int random = (int)(Math.random()*100);
		
		if(random > 5){
			proc_rslt = 01;
		}else if(random > 1){
			proc_rslt = 02;
		}*/
		
		// 임시로 01 반환
		int proc_rslt = 01;
		
		return proc_rslt;
	}
	
	/**
	 * 현재 시간 반환
	 * @return
	 */
	public String getCurrentTime(){
		
		SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
		String now = df.format(new Date());
		
		return now;
	}
	
	/**
	 * 주기에 따라 날짜 반환
	 * @param period
	 * @return
	 */
	public String getPastTime(int period){
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, period);
	     
	    DateFormat df = new SimpleDateFormat("yyyyMMdd");
	    String past = df.format(cal.getTime());
		    
	    return past;
	}
	

	
}
