import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.*;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;




public class Program {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{	
		if (args.length < 3)
		{
			System.err.print("Usage: fuzz [discover | test] url OPTIONS\n" +
					"Where discover requires the --common-words option and test "+
					"requires all discover options and the --vectors and --sensitive options.");
			return;
		}
		else if (!(args[0].equals("discover") || args[0].equals("test")))
		{
			System.err.println("Usage: fuzz [discover | test] url OPTIONS");
			return;
		}
		else
		{
			try {
				URL url = new URL(args[1]);
			}
			catch (MalformedURLException murle) {
				System.err.println(args[1] + " is not a valid URL.");
				return;
			}
		}
		
		int custAuth = -1;
		int comWords = -1;
		int vec = -1;
		int sens = -1;
		int rand = -1;
		int slow = -1;
		
		List<String> commonWords = new ArrayList<String>();
		String customAuth = null;
		List<String> vectors = new ArrayList<String>();
		List<String> sensitive = new ArrayList<String>();
		boolean randVectorTesting = false;
		int slowResp = 500; //milliseconds
		
		String command = args[0];
		boolean isDiscover = (command.equals("discover"));
		boolean isTest = (command.equals("test"));
		
		if (!(isDiscover || isTest))
		{
			System.err.println("Usage: fuzz [discover | test] url OPTIONS");
			return;
		}
		
		String url = args[1];
		
		int index = 2;
		List<String> options = Arrays.asList(args).subList(2, args.length);
		for(String s : options)
		{
			if(s.contains("--custom-auth=")){
				custAuth = index;	
			}else if(s.contains("--common-words=")){
				comWords = index;
			}else if(s.contains("--vectors=")){
				vec = index;
			}else if(s.contains("--sensitive=")){
				sens = index;
			}else if(s.contains("--random=")){
				rand = index;
			}else if(s.contains("--slow=")){
				slow = index;
			}
			++index;
		}
		
		if(comWords != -1){
			String filePath = args[comWords].substring(15);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				commonWords.add(line);
			}
			reader.close();
		}
		
		else {
			System.err.println("--common-words=[File name] is a required parameter.");
			return;
		}
		
		if(custAuth != -1)
		{	
			String auth = args[custAuth].substring(14, args[custAuth].length());
			
			if(auth.equalsIgnoreCase("dvwa"))
			{
				customAuth = "dvwa";
			}
			else if (auth.equalsIgnoreCase("bodgeit"))
			{
				customAuth = "bodgeit";
			}
			else{
				System.out.println("The parameter --custom-auth=  was not correct");
			}
		}
		
		if (vec != -1) {
			String filePath = args[vec].substring(10);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				vectors.add(line);
			}
			reader.close();
		}
		else if (isTest) {
			System.err.println("The test command requires the --vectors and --sensitive arguments.");
			return;
		}
	
		if (sens != -1) {
			String filePath = args[sens].substring(12);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sensitive.add(line);
			}
			reader.close();
		}
		else if (isTest) {
			System.err.println("The test command requires the --vectors and --sensitive arguments.");
			return;
		}
		
		if (rand != -1) {
			randVectorTesting = (args[rand].substring(9).toLowerCase().equals("true"));		
		}
		
		if (slow != -1) {
			try {

				slowResp = Integer.parseInt(args[slow].substring(7));
			}
			catch (NumberFormatException nfe) {
				//keep slow at default of 500ms
			}
		}
		
		
		Fuzzer fuzzer = new Fuzzer(url, commonWords);
		if (customAuth != null)
			fuzzer.setCustomAuthInfo(customAuth);
		if (vectors.size() > 0)
			fuzzer.setVectors(vectors);
		if (sensitive.size() > 0)
			fuzzer.setSensitive(sensitive);
		if (randVectorTesting)
			fuzzer.setRandom();
		fuzzer.setSlow(slowResp);
		try {
			if(isDiscover){
				fuzzer.execute("discover");
			}
			else{
				fuzzer.execute("test");
			}
		}
		catch (FailingHttpStatusCodeException fhsce) {
			System.err.println("That URL returned a status code of " + fhsce.getStatusCode());
		}
	}
}
