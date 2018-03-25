package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class CurrencyPrice {

	private String date;
	private String currency;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public CurrencyPrice(Date date,String currency){
		this.date=sdf.format(date);
		this.currency=currency.toUpperCase();
	}
	
	public String search(){
		try {
			final URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"+this.currency+"/"+this.date+"/?format=json");
			
			InputStream is = url.openStream();
			JsonParser parser = Json.createParser(is);
		    String value = null;
		    while (parser.hasNext()) {
		        final Event event = parser.next();
		        if (event.equals(Event.VALUE_NUMBER)) {
		        	value = parser.getString();
		        }
		    }
		    return "Cena "+this.currency+" dnia "+this.date+": "+value+" z³\n";
		}
		catch (IOException e) {
			return "Cena "+this.currency+" dnia "+this.date+": brak danych...\n";
		}
	}
}
