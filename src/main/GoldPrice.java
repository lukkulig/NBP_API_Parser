package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class GoldPrice {
	
	private String date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public GoldPrice(Date date){
		this.date=sdf.format(date);
	}
	
	public String search(){
		try {
			final URL url = new URL("http://api.nbp.pl/api/cenyzlota/"+this.date+"/?format=json");
			
			InputStream is = url.openStream();
			JsonParser parser = Json.createParser(is);
		    String value = null;
		    while (parser.hasNext()) {
		        final Event event = parser.next();
		        if (event.equals(Event.VALUE_NUMBER)) {
		        	value = parser.getString();
		        }
		    }
		    return "Cena zlota dnia "+this.date+": "+value+" z³";
		}
		catch (IOException e) {
			return "Cena zlota dnia "+this.date+": brak danych...";
		}
	}
	
}
