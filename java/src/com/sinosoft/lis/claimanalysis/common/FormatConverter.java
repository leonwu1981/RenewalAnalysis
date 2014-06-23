package com.sinosoft.lis.claimanalysis.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatConverter {
	
	public static Date getDate (String s) {
		Date date = null;
		if( !(s == null || "null".equals(s) || "".equals(s)) ) {
			java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");            
            try {
				date = fmt.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
	
	public static String getString (String s) {
		if( s == null || "null".equals(s) ) {
			return "";
		} else {
			return s.replace(',', '£¬');
		}
	}
	
	public static double getDouble (String s) {
		if( s == null || "null".equals(s) || "".equals(s) ) {
			return 0;
		} else {
			if(s.endsWith("%")){
				s = s.substring(0, s.length()-1);
				return Double.parseDouble(s)/100;
			}
			return Double.parseDouble(s);
		}
	}
	public static int getInt (String s) {
		if( s == null || "null".equals(s) || "".equals(s) ) {
			return 0;
		} else {
			return Integer.parseInt(s);
		}
	}
	
	public static String dateFm ( Date date ) {
		String s = "";
		if( date!= null ) {
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
			try{
				s = dateFm.format(date);
			}catch(Exception e){
				return "";
			}
		}
		return s;
	}

}
