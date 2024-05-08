package com.projak.psb.dms.reports.plugin.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversion {
	
	public static String getConvertedDate(String date){
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        
		try {
        
			Date start_date = inputFormat.parse(date);

            String date_formatted = outputFormat.format(start_date);
            
            System.out.println(date_formatted);
            
            return date_formatted;

        } catch (ParseException e) {
        
        	e.printStackTrace();
            
        	return null;
        }
	}
    public static void main(String[] args) {
        DateConversion dateConversion = new DateConversion();
        
        dateConversion.getConvertedDate("2023-09-01 00:00:00");
    }
}
