package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class HighestAmplitude {
	
	private Date date;
	private Date today;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public HighestAmplitude(Date date){
		this.date=date;
		this.today=Calendar.getInstance().getTime();
	}
	
	public String search(){
		if(getDifferenceDays(date, today)<93){
			try {
				final URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/a/"+sdf.format(this.date)+"/"+sdf.format(this.today)+"/?format=json");
				
				InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is);
				String key = null;
				Map<String,Double> maxValues = new HashMap<String,Double>();
				Map<String,Double> minValues = new HashMap<String,Double>();
			    while (parser.hasNext()) {
			        final Event event = parser.next();
			        if (event.equals(Event.KEY_NAME)) {
		        		if(parser.getString().equals("currency"))
		        			if(parser.next().equals(Event.VALUE_STRING))
		        				key = parser.getString();
		        		if(parser.getString().equals("code"))
		        			if(parser.next().equals(Event.VALUE_STRING))
		        				key= parser.getString()+" ("+key+")";
		        		if(parser.getString().equals("mid"))
		        			if(parser.next().equals(Event.VALUE_NUMBER)){
		        				double value=Double.parseDouble(parser.getString());
					        	if(maxValues.containsKey(key)){
					        		if(maxValues.get(key)<value) 
					        			maxValues.replace(key, value);
					        	}else{
					        		maxValues.put(key, value);
					        	}
					        	if(minValues.containsKey(key)){
					        		if(minValues.get(key)>value)
					        			minValues.replace(key, value);
					        	}else{
					        		minValues.put(key, value);
					        	}
			        	}
			        }
			    }
				Map<String,Double> amplitudes = new HashMap<String,Double>();
			    calculateAmplitudes(amplitudes, maxValues,minValues);	
			    return "Waluta, której kurs, pocz¹wszy od "+sdf.format(this.date)+", uleg³ najwiêkszym wahaniom: "+findHighestAmplitude(amplitudes)+"\n";
			}
			catch (IOException e) {
				return "Waluta, której kurs, pocz¹wszy od "+sdf.format(this.date)+", uleg³ najwiêkszym wahaniom: brak danych...\n";			
			}
		}else{
			Date fromDate=date;
			Date toDate= date;			
			toDate=addDays(toDate,93);
			Map<String,Double> maxValues = new HashMap<String,Double>();
			Map<String,Double> minValues = new HashMap<String,Double>();
			double value=0;
			try {
				while(!toDate.equals(today)){
					if(getDifferenceDays(fromDate,today)<=93)
						toDate=today;
					final URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/a/"+sdf.format(fromDate)+"/"+sdf.format(toDate)+"/?format=json");
					
					InputStream is = url.openStream();
					JsonParser parser = Json.createParser(is);
					String key = null;					
				    while (parser.hasNext()) {
				        final Event event = parser.next();
				        if (event.equals(Event.KEY_NAME)) {
			        		if(parser.getString().equals("currency"))
			        			if(parser.next().equals(Event.VALUE_STRING))
			        				key = parser.getString();
			        		if(parser.getString().equals("code"))
			        			if(parser.next().equals(Event.VALUE_STRING))
			        				key= parser.getString()+" ("+key+")";
			        		if(parser.getString().equals("mid"))
			        			if(parser.next().equals(Event.VALUE_NUMBER)){
			        				value=Double.parseDouble(parser.getString());
						        	if(maxValues.containsKey(key)){
						        		if(maxValues.get(key)<value) 
						        			maxValues.replace(key, value);
						        	}else{
						        		maxValues.put(key, value);
						        	}
						        	if(minValues.containsKey(key)){
						        		if(minValues.get(key)>value)
						        			minValues.replace(key, value);
						        	}else{
						        		minValues.put(key, value);
						        	}
				        	}
				        }
				    }
				    if(!toDate.equals(today)){
				    	fromDate=addDays(toDate, 1);
				    	toDate=addDays(toDate,94);
				    }
				    
				}
			}
			catch (IOException e) { 
				return "Waluta, której kurs, pocz¹wszy od "+sdf.format(this.date)+", uleg³ najwiêkszym wahaniom: brak danych...\n";			
			}	
			Map<String,Double> amplitudes = new HashMap<String,Double>();
		    calculateAmplitudes(amplitudes, maxValues,minValues);	
		    return "Waluta, której kurs, pocz¹wszy od "+sdf.format(this.date)+", uleg³ najwiêkszym wahaniom: "+findHighestAmplitude(amplitudes)+"\n";
		}
	}
	
	private void calculateAmplitudes(Map<String, Double> amplitudes, Map<String, Double> maxValues, Map<String, Double> minValues){
		for (Map.Entry<String, Double> entry: maxValues.entrySet()) {
			String key = entry.getKey();
			Double maxValue = entry.getValue();
			Double minValue = minValues.get(key);
			amplitudes.put(key, maxValue-minValue);			
		}
		
	}
	
	private String findHighestAmplitude(Map<String, Double> amplitudes){
		return Collections.max(amplitudes.entrySet(), Map.Entry.comparingByValue()).getKey();		
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
