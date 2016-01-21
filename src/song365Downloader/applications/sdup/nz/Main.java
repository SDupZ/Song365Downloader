package song365Downloader.applications.sdup.nz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.swing.text.html.HTML.Tag;



public class Main {
	Queue<String> visitQueue;
	List<String> visitedURLS;
	List<List<String>> artistDownloadURLS;
	List<String> finalURLS;
	List<String> artistURLS;
	
	String baseURL;
	
	String[] baseArtistURLs = {
		"https://www.song365.org/artist-digital.html",
		"https://www.song365.org/artist-a.html",
		"https://www.song365.org/artist-b.html",
		"https://www.song365.org/artist-c.html",
		"https://www.song365.org/artist-d.html",
		"https://www.song365.org/artist-e.html",
		"https://www.song365.org/artist-f.html",
		"https://www.song365.org/artist-g.html",
		"https://www.song365.org/artist-h.html",
		"https://www.song365.org/artist-i.html",
		"https://www.song365.org/artist-j.html",
		"https://www.song365.org/artist-k.html",
		"https://www.song365.org/artist-l.html",
		"https://www.song365.org/artist-m.html",
		"https://www.song365.org/artist-n.html",
		"https://www.song365.org/artist-o.html",
		"https://www.song365.org/artist-p.html",
		"https://www.song365.org/artist-q.html",
		"https://www.song365.org/artist-r.html",
		"https://www.song365.org/artist-s.html",
		"https://www.song365.org/artist-t.html",
		"https://www.song365.org/artist-u.html",
		"https://www.song365.org/artist-v.html",
		"https://www.song365.org/artist-w.html",
		"https://www.song365.org/artist-x.html",
		"https://www.song365.org/artist-y.html",
		"https://www.song365.org/artist-z.html"
	};
	
	private void checkWebPage(String URL){
		this.visitedURLS.add(URL);
		
		try{
			Document doc = Jsoup.connect(URL).userAgent("Mozilla").get();
			
			if (URL.contains("/download/")){
				Elements scripts = doc.select("script");
				String finalLink = getMP3URLFromScripts(scripts);
				finalURLS.add(finalLink);
				try{
					BufferedWriter outStream= new BufferedWriter(new FileWriter("links.txt", true));
					outStream.write(finalLink);	
					outStream.newLine();
					outStream.flush();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			Elements links = doc.select("a[href]");
						
			for (Element e : links){				
				checkElement(e, "/track/");
				checkElement(e, "/download/");
				checkElement(e, "/artist/");
				checkElement(e, "/album/");				
			}
		}catch(IOException e){
			e.printStackTrace();
			try{
				BufferedWriter outStream= new BufferedWriter(new FileWriter("exception.txt", true));
				outStream.write("EXCEPTION: " + URL);	
				outStream.newLine();
				outStream.flush();
			}catch(IOException e2){
				e.printStackTrace();
			}
		}
		
	}
	
	private void populateArtistList(){
		System.out.println("Populating Artist List...");
		for (String URL : baseArtistURLs){
			try{
				Document doc = Jsoup.connect(URL).userAgent("Mozilla").get();
				Elements links = doc.select("a[href]");
							
				for (Element e : links){		
					if (e.toString().contains("/artist/") && !e.toString().contains("/track/digg/") && !e.toString().contains("/album/digg/") && !e.toString().contains("/artist/digg/")){
						String trackURL = e.toString().substring(e.toString().indexOf("/artist/"));
						trackURL = trackURL.substring(0, trackURL.indexOf("\""));
						trackURL = this.baseURL + trackURL;
						this.artistURLS.add(trackURL);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
				try{
					BufferedWriter outStream = new BufferedWriter(new FileWriter("exception.txt", true));
					outStream.write("EXCEPTION: " + URL);	
					outStream.newLine();
					outStream.flush();
				}catch(IOException e2){
					e.printStackTrace();
				}
			}
		}	
		
		System.out.print("Sorting...");
		Collections.sort(artistURLS);
		System.out.println("DONE");
		System.out.print("Writing to file...");
		try{
			BufferedWriter outStream = new BufferedWriter(new FileWriter("artistList.txt"));
			for(String str: artistURLS) {
				outStream.write(str);	
				outStream.newLine();
			}
			outStream.close();
		}catch(IOException e){			
		}
		System.out.println("DONE");
	}
	
	private List<String> getSongsForArtist(String URL){
		List<String> result = new ArrayList<String>();
		//First need to convert it from: 
		//https://www.song365.me/artist/owl-city-33765.html
		//to this 
		//https://www.song365.me/artist/tracks/owl-city-33765.html
		String artistURL = URL.substring(0,URL.toString().indexOf("/artist/") + 8) +
				"tracks/" +
				URL.substring(URL.toString().indexOf("/artist/") + 8);
		
		System.out.println("Getting track links for artist: " + URL);
		try{
			Document doc = Jsoup.connect(artistURL).userAgent("Mozilla").get();
			Elements links = doc.select("a[href]");
						
			for (Element e : links){		
				if (e.toString().contains("/download/") && !e.toString().contains("/track/digg/") && !e.toString().contains("/album/digg/") && !e.toString().contains("/artist/digg/")){
					String trackURL = e.toString().substring(e.toString().indexOf("/download/"));
					trackURL = trackURL.substring(0, trackURL.indexOf("\""));
					trackURL = this.baseURL + trackURL;
					result.add(trackURL);
					System.out.println(trackURL);
				}
			}
			
			try{
				BufferedWriter outStream = new BufferedWriter(new FileWriter("downloadURLS.txt", true));
				for(String str: result) {
					outStream.write(str);	
					outStream.newLine();
				}
				outStream.close();
			}catch(IOException e){			
			}
		}catch(IOException e){
			e.printStackTrace();
			try{
				BufferedWriter outStream = new BufferedWriter(new FileWriter("exception.txt", true));
				outStream.write("EXCEPTION: " + URL);	
				outStream.newLine();
				outStream.flush();
			}catch(IOException e2){
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	private void checkElement (Element e, String s){
		if (e.toString().contains(s) && !e.toString().contains("/track/digg/") && !e.toString().contains("/album/digg/") && !e.toString().contains("/artist/digg/")){
			String trackURL = e.toString().substring(e.toString().indexOf(s));
			trackURL = trackURL.substring(0, trackURL.indexOf("\""));
			trackURL = this.baseURL + trackURL;
			
			if (!visitedURLS.contains(trackURL)){
				this.visitedURLS.add(trackURL);
				this.visitQueue.add(trackURL);
			}
		}
	}
	
	public void crawlWebsite(String URL){	
		this.baseURL = URL;		
		this.visitQueue.add(URL);
		
		System.out.println("----------------------BEGIN----------------------");
		while (!this.visitQueue.isEmpty()){
			String current = this.visitQueue.poll();
			//checkWebPage(current);	
			populateArtistList();	
			
			int count = 0;
			for (String artist : artistURLS){
				List<String> result = getSongsForArtist(artist);
				System.out.println(count + "/" + artistURLS.size());
				artistDownloadURLS.add(result);		
				count++;
			}
			
			try{
				BufferedWriter outStream= new BufferedWriter(new FileWriter("visited.txt", true));				
				outStream.write("QUEUE : " + this.visitQueue.size());
				outStream.newLine();
				outStream.write(current);	
				outStream.newLine();
				outStream.flush();
			}catch(IOException e){
				e.printStackTrace();
			}
		}		
		System.out.println("----------------------WRITING FILES----------------------");
		
		try{
			
			FileWriter writer = new FileWriter("finaloutput.txt"); 
			writer.write("BEGIN");
			for(String str: finalURLS) {
			  writer.write(str);
			}
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		for (String s : finalURLS){
			System.out.println(s);
		}
		
		System.out.println("-----------------------END-----------------------");
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public Main(){
		visitedURLS = new ArrayList<String>();		
		finalURLS = new ArrayList<String>();
		artistURLS = new ArrayList<String>();
		visitQueue = new LinkedList<String>();	
		artistDownloadURLS = new ArrayList<List<String>>();
		
	}
	
	public static void main(String[] args) {
		String URL = "https://www.song365.me";		//Make sure this doesnt have a "/" at the end.
		Main crawler = new Main();
		crawler.crawlWebsite(URL);
	}	
	
	public static String getMP3URLFromScripts(Elements scripts){
		for (Element e : scripts){
			String javascript = e.toString();
			if (javascript.contains("var hqurl")){
				String test = javascript.substring(e.toString().indexOf("var hqurl"));
				test = test.substring(0, test.indexOf(";"));
				test = test.substring(test.indexOf("'"));
				test = test.substring(1, test.length()-1);
				return test;
			}
		}
		return "";
	}
}
