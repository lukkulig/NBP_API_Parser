package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class LowestBuyingRate {
	private String date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public LowestBuyingRate(Date date){
		this.date=sdf.format(date);
	}
	
	public String search(){
		try {
			final URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/c/"+this.date+"/?format=json");
			
			InputStream is = url.openStream();
			JsonParser parser = Json.createParser(is);
			String key = null;
			Map<String,Double> buyingRates = new HashMap<String,Double>();
		    while (parser.hasNext()) {
		        final Event event = parser.next();
		        if (event.equals(Event.KEY_NAME)) {
	        		if(parser.getString().equals("currency"))
	        			if(parser.next().equals(Event.VALUE_STRING))
	        				key = parser.getString();
	        		if(parser.getString().equals("code"))
	        			if(parser.next().equals(Event.VALUE_STRING))
	        				key= parser.getString()+" ("+key+")";
	        		if(parser.getString().equals("bid"))
	        			if(parser.next().equals(Event.VALUE_NUMBER)){
	        				double value=Double.parseDouble(parser.getString());
	        				buyingRates.put(key, value);
		        	}
		        }
		    }	
		    return "Waluta, której kurs kupna by³ najmniejszy w dniu "+this.date+": "+findLowestBuyingRate(buyingRates)+"\n";
		}
		catch (IOException e) {
			return "Waluta, której kurs kupna by³ najmniejszy w dniu "+this.date+": brak danych...\n";			
		}
	}

	
	private String findLowestBuyingRate(Map<String, Double> buyingRates){
		return Collections.min(buyingRates.entrySet(), Map.Entry.comparingByValue()).getKey();		
	}
}
