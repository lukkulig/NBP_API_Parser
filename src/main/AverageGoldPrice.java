package main;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class AverageGoldPrice {
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public AverageGoldPrice(Date startDate, Date endDate){
		this.startDate=startDate;
		this.endDate=endDate;
	}
	
	public String search(){
		Calendar today = Calendar.getInstance();
		Date date = new GregorianCalendar(2013,0,2).getTime();
		if(startDate.compareTo(date)<0)
			startDate=date;
		if(endDate.compareTo(today.getTime())>0)
			endDate=today.getTime();
		if(getDifferenceDays(startDate, endDate)<367){
			try {
				final URL url = new URL("http://api.nbp.pl/api/cenyzlota/"+sdf.format(this.startDate)+"/"+sdf.format(this.endDate)+"/?format=json");
				
				InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is);
			    double value = 0;
			    int counter=0;
			    while (parser.hasNext()) {
			        final Event event = parser.next();;
			        if (event.equals(Event.VALUE_NUMBER)) {
				            value += Double.parseDouble(parser.getString());
				            counter++;
				    }
			    }
			    DecimalFormat df = new DecimalFormat("#.00");
			    df.setRoundingMode(RoundingMode.CEILING);
			    return "Srednia cena zlota w dniach od "+sdf.format(this.startDate)+" do "+sdf.format(this.endDate)+": "+df.format(value/counter)+" z³\n";
			}
			catch (IOException e) {
				return "Srednia cena zlota w dniach od "+sdf.format(this.startDate)+" do "+sdf.format(this.endDate)+": brak danych...\n";
			}
		}else{
			Date fromDate=startDate;
			Date toDate= startDate;			
			toDate=addDays(toDate,367);
			double value=0;
		    int counter=0;
			try {
				while(!toDate.equals(endDate) && fromDate.compareTo(toDate)<0){
					if(getDifferenceDays(fromDate, endDate)<=367)
						toDate=endDate;
					final URL url = new URL("http://api.nbp.pl/api/cenyzlota/"+sdf.format(fromDate)+"/"+sdf.format(toDate)+"/?format=json");
					
					InputStream is = url.openStream();
					JsonParser parser = Json.createParser(is);
				    while (parser.hasNext()) {
				        final Event event = parser.next();;
				        if (event.equals(Event.VALUE_NUMBER)) {
					            value += Double.parseDouble(parser.getString());
					            counter++;
					    }
				    }
				    if(!toDate.equals(endDate) && fromDate.compareTo(toDate)<0){
				    	fromDate=addDays(toDate, 1);
				    	toDate=addDays(toDate,368);
				    }
				    
				}
			}
			catch (IOException e) {
				return "Srednia cena zlota w dniach od "+sdf.format(this.startDate)+" do "+sdf.format(this.endDate)+": brak danych...\n";
			}	
			DecimalFormat df = new DecimalFormat("#.00");
		    df.setRoundingMode(RoundingMode.CEILING);
			return "Srednia cena zlota w dniach od "+sdf.format(this.startDate)+" do "+sdf.format(this.endDate)+": "+df.format(value/counter)+" z³\n";
		
		}
	}
	
	private static long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	private static Date addDays(Date date,int days){
		Calendar c = Calendar.getInstance(); 
		c.setTime(date); 
		c.add(Calendar.DATE, days);
		date = c.getTime();
		return date;
	}
}
