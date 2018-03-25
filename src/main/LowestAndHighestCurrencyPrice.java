package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class LowestAndHighestCurrencyPrice {
	private Date startDate;
	private Date today;
	private String currency;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public LowestAndHighestCurrencyPrice(String currency){
		this.startDate=new GregorianCalendar(2002,0,2).getTime();
		this.today=Calendar.getInstance().getTime();
		this.currency=currency.toUpperCase();
	}
	
	public String search(){
		if(getDifferenceDays(startDate, today)<=367){
			try {
				final URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"+this.currency+"/"+sdf.format(this.startDate)+"/"+sdf.format(this.today)+"/?format=json");
				
				InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is);
				String key = null;
				Map<String,Double> rates = new HashMap<String,Double>();
			    while (parser.hasNext()) {
			    	final Event event = parser.next();
			        if (event.equals(Event.KEY_NAME)) {
		        		if(parser.getString().equals("effectiveDate"))
		        			if(parser.next().equals(Event.VALUE_STRING))
		        				key = parser.getString();
		        		if(parser.getString().equals("mid"))
		        			if(parser.next().equals(Event.VALUE_NUMBER)){
		        				double value=Double.parseDouble(parser.getString());
		        				rates.put(key, value);
			        	}
			        }
			    }
			    return "Cena "+this.currency+":\n-najnizsza dnia - "+findLowestPriceDate(rates)+"\n-najwyzsza dnia - "+findHighestPriceDate(rates)+"\n";
			}
			catch (IOException e) {
				return "Cena "+this.currency+": brak waluty "+this.currency+"...\n";
			}
		}else{
			Date fromDate=startDate;
			Date toDate= startDate;			
			toDate=addDays(toDate,367);
			Map<String,Double> rates = new HashMap<String,Double>();
			try {
				while(!toDate.equals(today)){
					if(getDifferenceDays(fromDate,today)<=367)
						toDate=today;
					final URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"+this.currency+"/"+sdf.format(fromDate)+"/"+sdf.format(toDate)+"/?format=json");
					InputStream is = url.openStream();
					JsonParser parser = Json.createParser(is);
					String key = null;
					
				    while (parser.hasNext()) {
				    	final Event event = parser.next();
				        if (event.equals(Event.KEY_NAME)) {
			        		if(parser.getString().equals("effectiveDate"))
			        			if(parser.next().equals(Event.VALUE_STRING))
			        				key = parser.getString();
			        		if(parser.getString().equals("mid"))
			        			if(parser.next().equals(Event.VALUE_NUMBER)){
			        				double value=Double.parseDouble(parser.getString());
			        				rates.put(key, value);
				        	}
				        }
				    }
				    if(!toDate.equals(today)){
				    	fromDate=addDays(toDate, 1);
				    	toDate=addDays(toDate,368);
				    }
				    
				}
			}
			catch (IOException e) { 
				return "Cena "+this.currency+": brak waluty "+this.currency+"...\n";
			}
			return "Cena "+this.currency+":\n-najnizsza dnia - "+findLowestPriceDate(rates)+"\n-najwyzsza dnia - "+findHighestPriceDate(rates)+"\n";
		}
	}

	private String findHighestPriceDate(Map<String, Double> rates) {
		return Collections.max(rates.entrySet(), Map.Entry.comparingByValue()).getKey();
	}

	private String findLowestPriceDate(Map<String, Double> rates) {
		return Collections.min(rates.entrySet(), Map.Entry.comparingByValue()).getKey();
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
