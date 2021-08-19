package net.lunalabs.hl7gw.utills;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import net.lunalabs.hl7gw.dto.PR100RespDto;

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
	
	
	public static List<PR100RespDto> createDummyPatients() {
		List<PR100RespDto> patients = IntStream.range(0, 50)
				.mapToObj(i -> PR100RespDto.builder()
								.patientId(i)
								.firstName("첫번째 이름")
								.lastName("마지막 이름")
								.gender("남")
								.age(20)
								.height(180)
								.weight(80)
								.commnet("더미데이터")
								.lastSession(LocalDateTime.now().toString())
								.build()
						
						)
				.collect(Collectors.toList());
		
		return patients;
		
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
