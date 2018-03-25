package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class DrawCurrencyPriceChanges {
	private Date startDate;
	private Date endDate;
	private String currency;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public DrawCurrencyPriceChanges(String currency, Date startDate, Date endDate){
		this.startDate=startDate;
		this.endDate=endDate;
		this.currency=currency.toUpperCase();
	}
	
	public String search(){
		Calendar today = Calendar.getInstance();
		Date date = new GregorianCalendar(2002,0,2).getTime();
		if(startDate.compareTo(date)<0)
			startDate=date;
		if(endDate.compareTo(today.getTime())>0)
			endDate=today.getTime();
		if(getDifferenceDays(startDate, endDate)<367){
			try {
				final URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"+this.currency+"/"+sdf.format(this.startDate)+"/"+sdf.format(this.endDate)+"/?format=json");
				
				InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is);
				Date key = null;
				Map<Date,Double> rates = new LinkedHashMap<Date,Double>();
			    while (parser.hasNext()) {
			    	final Event event = parser.next();
			        if (event.equals(Event.KEY_NAME)) {
		        		if(parser.getString().equals("effectiveDate"))
		        			if(parser.next().equals(Event.VALUE_STRING)){
		        				key=sdf.parse(parser.getString());
		        			}
		        		if(parser.getString().equals("mid"))
		        			if(parser.next().equals(Event.VALUE_NUMBER)){
		        				double value=Double.parseDouble(parser.getString());
		        				rates.put(key, value);
			        	}
			        }
			    }
			    Map<Date, Double> treeMap = new TreeMap<Date, Double>(
			    		new Comparator<Date>(){
							@Override
							public int compare(Date o1, Date o2) {
								Calendar cal1 = Calendar.getInstance();
								Calendar cal2 = Calendar.getInstance();
								cal1.setTime(o1);
								cal2.setTime(o2);
								if(cal1.get(Calendar.DAY_OF_WEEK)==cal2.get(Calendar.DAY_OF_WEEK))
									return o1.compareTo(o2);
								else if(cal1.get(Calendar.DAY_OF_WEEK)>cal2.get(Calendar.DAY_OF_WEEK))
									return 1;
								else
									return -1;
						     }    			
			    		});
			    
			    treeMap.putAll(rates);
			    return "Wykres zmian ceny "+this.currency+" od "+sdf.format(startDate)+" do "+sdf.format(endDate)+":\n" +mapToString(treeMap,getWeeksNumbers(rates));
			}
			catch (IOException | ParseException e) {
				return "Wykres zmian ceny "+this.currency+" od "+sdf.format(startDate)+" do "+sdf.format(endDate)+": brak danych...";
			}
		}else{
			Date fromDate=startDate;
			Date toDate= startDate;			
			toDate=addDays(toDate,367);
			Map<Date,Double> rates = new LinkedHashMap<Date,Double>();
			try {
				while(!toDate.equals(endDate)){
					if(getDifferenceDays(fromDate,endDate)<=367)
						toDate=endDate;
					final URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"+this.currency+"/"+sdf.format(fromDate)+"/"+sdf.format(toDate)+"/?format=json");
					
					InputStream is = url.openStream();
					JsonParser parser = Json.createParser(is);
					Date key = null;
				    while (parser.hasNext()) {
				    	final Event event = parser.next();
				        if (event.equals(Event.KEY_NAME)) {
			        		if(parser.getString().equals("effectiveDate"))
			        			if(parser.next().equals(Event.VALUE_STRING)){
			        				key=sdf.parse(parser.getString());
			        			}
			        		if(parser.getString().equals("mid"))
			        			if(parser.next().equals(Event.VALUE_NUMBER)){
			        				double value=Double.parseDouble(parser.getString());
			        				rates.put(key, value);
				        	}
				        }
				    }
				    if(!toDate.equals(endDate)){
				    	fromDate=addDays(toDate, 1);
				    	toDate=addDays(toDate,94);
				    }
				   

				}
			}
			catch (IOException | ParseException e) { 
				return "Wykres zmian ceny "+this.currency+" od "+sdf.format(startDate)+" do "+sdf.format(endDate)+": brak danych...";
			}
			 Map<Date, Double> treeMap = new TreeMap<Date, Double>(
			    		new Comparator<Date>(){
							@Override
							public int compare(Date o1, Date o2) {
								Calendar cal1 = Calendar.getInstance();
								Calendar cal2 = Calendar.getInstance();
								cal1.setTime(o1);
								cal2.setTime(o2);
								if(cal1.get(Calendar.DAY_OF_WEEK)==cal2.get(Calendar.DAY_OF_WEEK))
									return o1.compareTo(o2);
								else if(cal1.get(Calendar.DAY_OF_WEEK)>cal2.get(Calendar.DAY_OF_WEEK))
									return 1;
								else
									return -1;
						     }    			
			    		});
			    
			treeMap.putAll(rates);
		    return "Wykres zmian ceny "+this.currency+" od "+sdf.format(startDate)+" do "+sdf.format(endDate)+":\n" +mapToString(treeMap,getWeeksNumbers(rates));
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
	
	private static Map<String,Integer> getWeeksNumbers(Map<Date, Double> map){
		int week=1;
		Map<String,Integer> counter = new HashMap<String, Integer>();
		for (Map.Entry<Date, Double> entry : map.entrySet()) {
			String weekOfYear=new SimpleDateFormat("w-yyyy").format(entry.getKey());
			if(!counter.containsKey(weekOfYear)){
				counter.put(weekOfYear, week);
				week++;
			}
			
		}
		return counter;
	}
	
	private static String mapToString(Map<Date, Double> map,Map<String,Integer> counter){
		String result="";
		SimpleDateFormat df = new SimpleDateFormat("EEE");
		double min = findLowestPrice(map);
		double max = findHighestPrice(map);
		double highestAmplitude=max-min;
		String  c = "#";
		for (Map.Entry<Date, Double> entry : map.entrySet()) {			
			String weekOfYear=new SimpleDateFormat("w-yyyy").format(entry.getKey());
			double amplitude = entry.getValue()-min;
			double ratio=amplitude/highestAmplitude;
			result+=String.format("%-7s",df.format(entry.getKey())+counter.get(weekOfYear))
					+": "+String.join("", Collections.nCopies((int) (ratio*50)+1, c))
					+" "+entry.getValue()+" zł\n";			
		}
		return result;
	}
	
	private static double findHighestPrice(Map<Date, Double> rates) {
		return Collections.max(rates.entrySet(), Map.Entry.comparingByValue()).getValue();
	}

	private static double findLowestPrice(Map<Date, Double> rates) {
		return Collections.min(rates.entrySet(), Map.Entry.comparingByValue()).getValue();
	}
}
