package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {

	public static void main(String[] args){
		if(args.length==0) help();	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		switch (args[0]) {
			case "-gcpri":
				if(args.length!=3)
					err();
				try {
					Date date = sdf.parse(args[1]);
					GoldPrice gp = new GoldPrice(date);
					if(!args[2].matches("[a-zA-Z][a-zA-Z][a-zA-Z]"))
						err(args,2);
					CurrencyPrice cp = new CurrencyPrice(date,args[2]);
					System.out.println(gp.search());
					System.out.println(cp.search());
				} catch (ParseException e) {
					err(args,1);
				}
				
				
				break;
			case "-agpri":
				if(args.length!=3)
					err();
				
				Date date1=null;
				Date date2=null;
				try {
					date1 = sdf.parse(args[1]);
				} catch (ParseException e) {
					err(args,1);
				}
				try {
					date2 = sdf.parse(args[2]);
					AverageGoldPrice agp = new AverageGoldPrice(date1,date2);
					System.out.println(agp.search());
				} catch (ParseException e) {
					err(args,2);
				}
												
				
				break;
			case "-hiamp":
				if(args.length!=2)
					err();
				try {
					Date date = sdf.parse(args[1]);
					HighestAmplitude ha = new HighestAmplitude(date);
					System.out.println(ha.search());
				} catch (ParseException e) {
					err(args,1);
				}
				
				
				break;
			case "-lbrat":
				if(args.length!=2)
					err();
				try {
					Date date = sdf.parse(args[1]);
					LowestBuyingRate lbr = new LowestBuyingRate(date);
					System.out.println(lbr.search());
				} catch (ParseException e) {
					err(args,1);
				}
				
				break;
			case "-cubrd":
				if(args.length!=3)
					err();
				int n=0;
				try {
					n = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					err(args,1);
				}		
				try {
					Date date = sdf.parse(args[2]);
					
					BottomNCurrenciesByRatesDiff bncbrd = new BottomNCurrenciesByRatesDiff
						(n,date);
					System.out.println(bncbrd.search());
				} catch (ParseException e) {
					err(args,2);
				}	
				break;
			case "-cupri":
				if(args.length!=2)
					err();
				if(!args[1].matches("[a-zA-Z][a-zA-Z][a-zA-Z]"))
					err(args,1);
				LowestAndHighestCurrencyPrice lahcp = new LowestAndHighestCurrencyPrice(args[1]);
				System.out.println(lahcp.search());
				break;
			case "-drcpc":
				if(args.length!=8)
					err();
				if(!args[1].matches("[a-zA-Z][a-zA-Z][a-zA-Z]"))
					err(args,1);
				if(!args[2].matches("20[0-1][0-9],")||!args[3].matches("[0-1][0-9],")||!args[4].matches("[0-5]"))
					err(args,2,3,4);
				if(!args[5].matches("20[0-1][0-9],")||!args[6].matches("[0-1][0-9],")||!args[7].matches("[0-5]"))
					err(args,5,6,7);
				int year1 = Integer.parseInt(args[2].substring(0, args[2].length()-1));
				int year2= Integer.parseInt(args[5].substring(0, args[5].length()-1));
				int month1= Integer.parseInt(args[3].substring(0, args[3].length()-1));
				int month2= Integer.parseInt(args[6].substring(0, args[6].length()-1));
				int week1= Integer.parseInt(args[4]);
				int week2= Integer.parseInt(args[7]);
				DrawCurrencyPriceChanges dcpc = new DrawCurrencyPriceChanges
						(args[1], setStartDateFromYearMonthWeek(year1, month1, week1),setEndDateFromYearMonthWeek(year2, month2, week2));
				System.out.println(dcpc.search());
				break;
	
			default:
				System.err.println("Niepoprawne parametry!");
				help();
		}
	
	}
	
	private static void help(){
		System.out.println("Program pozwala na wypisanie danych o cenie zlota\n"
				+ "i poszczegolnych walut dostepnych w serwisie http://api.nbp.pl/.\n\n"
				+ "u¿ycie: nbp_api -parametr argumenty\n\n"
				+ String.format("%-35s\n\t%s\n","-gcpri data waluta","Wypisuje cenê z³ota oraz cenê waluty /waluta/ w dniu /data/.")
				+ String.format("\t%s\n","data - data w formacie \"yyyy-MM-dd\"")
				+ String.format("\t%s\n","waluta - trzyliterowy kod waluty\n")
				+ String.format("%-35s\n\t%s\n","-agpri data_pocz data_kon","Wypisuje œredni¹ cenê z³ota w okresie od /data_pocz/ do /data_kon/.")
				+ String.format("\t%s\n","data_pocz; data_kon - data w formacie \"yyyy-MM-dd\"\n")
				+ String.format("%-35s\n\t%s\n","-hiamp data","Wypisuje walutê, której kurs uleg³ najwiêkszym wahaniom od dnia /data.")
				+ String.format("\ts%s\n","data - data w formacie \"yyyy-MM-dd\"\n")
				+ String.format("%-35s\n\t%s\n","-lbrat data","Wypisuje walutê, której kurs kupna by³ najmniejszy w dniu /data/.")
				+ String.format("\t%s\n","data - data w formacie \"yyyy-MM-dd\"\n")
				+ String.format("%-35s\n\t%s\n","-cubrd iloœæ data","Wypisuje /iloœæ/ walut, o najmniejszej ró¿nicy pomiêdzy kursem")
				+ String.format("\t%s\n","sprzeda¿y a kursem kupna w dniu /data/.")
				+ String.format("\t%s\n","iloœæ - liczba walut do wypisania")
				+ String.format("\t%s\n","data - data w formacie \"yyyy-MM-dd\"\n")
				+ String.format("%-35s\n\t%s\n","-cupri waluta","Wypisuje daty, kiedy waluta /waluta/ by³a najtañsza i najdro¿sza.")
				+ String.format("\t%s\n","waluta - trzyliterowy kod waluty\n")
				+ String.format("%-35s\n\t%s\n","-drcpc waluta pocz_okr kon_okr","Rysuje wykres zmian ceny waluty /waluta/ w uk³adzie tygodniowym")
				+ String.format("\t%s\n","w okresie od /pocz_okr/ do /kon_okr/.")
				+ String.format("\t%s","waluta  - trzyliterowy kod waluty\n")
				+ String.format("\t%s\n","pocz_okr; kon_okr - okres w formacie \"yyyy, MM, w\" (w=0,1,..,5)\n")
		);
		System.exit(1);
	}
	
	private static void err(){
		System.err.println("Nieodpowiednia ilosc parametrow!");
		System.exit(1);
	}
	
	private static void err(String[] args, int i){
		System.err.println("Niepoprawny parametr nr "+i+" - \""+args[i]+"\"!");
		System.exit(1);
	}
	private static void err(String[] args, int x, int y, int z){
		System.err.println("Niepoprawny parametr nr "+x+" - \""+args[x]+" "+args[y]+" "+args[z]+"\"!");
		System.exit(1);
	}
	
	private static Date setStartDateFromYearMonthWeek(int year, int month, int week){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);  
		cal.set(Calendar.MONTH, month-1);  
		cal.set(Calendar.WEEK_OF_MONTH, week);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}
	
	private static Date setEndDateFromYearMonthWeek(int year, int month, int week){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);  
		cal.set(Calendar.MONTH, month-1);  
		cal.set(Calendar.WEEK_OF_MONTH, week);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		return cal.getTime();
	}

}
