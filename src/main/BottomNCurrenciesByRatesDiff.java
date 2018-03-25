package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class BottomNCurrenciesByRatesDiff {
	private String date;
	private int n;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public BottomNCurrenciesByRatesDiff(int n, Date date){
		this.n=n;
		this.date=sdf.format(date);
	}
	
	public String search(){
		try {
			final URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/c/"+this.date+"/?format=json");
			
			InputStream is = url.openStream();
			JsonParser parser = Json.createParser(is);
			String key = null;
			Map<String,Double> buyingRates = new HashMap<String,Double>();
			Map<String,Double> sellingRates = new HashMap<String,Double>();
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
	        		if(parser.getString().equals("ask"))
	        			if(parser.next().equals(Event.VALUE_NUMBER)){
	        				double value=Double.parseDouble(parser.getString());
	        				sellingRates.put(key, value);
	        				
		        	}
		        }
		    }	
			Map<String,Double> ratesDifferences = new HashMap<String,Double>();
		    calculateDifferences(ratesDifferences, sellingRates,buyingRates);
		    return n+" walut,o najmniejszej ró¿nicy pomiêdzy kursem sprzeda¿y a kursem kupna w dniu "+this.date+": \n"+findNLowest(ratesDifferences);
		}
		catch (IOException e) {
			return n+" walut,o najmniejszej ró¿nicy pomiêdzy kursem sprzeda¿y a kursem kupna w dniu "+this.date+": brak danych...";			
		}
	}

	
	private void calculateDifferences(Map<String, Double> ratesDifferences,
			Map<String, Double> sellingRates, Map<String, Double> buyingRates) {
		for (Map.Entry<String, Double> entry: sellingRates.entrySet()) {
			String key = entry.getKey();
			Double sellingRate = entry.getValue();
			Double buyingRate = buyingRates.get(key);
			ratesDifferences.put(key, sellingRate-buyingRate);			
		}
	}

	private String findNLowest(Map<String, Double> buyingRates){
		String result="";
		buyingRates=buyingRates.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue())
			.collect(Collectors.toMap(
					Map.Entry::getKey, 
					Map.Entry::getValue,
					(e1,e2)->e1,
					LinkedHashMap::new));
		ArrayList<String> list= new ArrayList<String>(buyingRates.keySet());
		for(int i=0;i<n;i++)
			result+=list.get(i)+"\n";
		
		return result;		
	}
}
