import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.*;




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
					"\tWhere discover requires the --common-words option and test "+
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
		
		int custAuth = 0;
		int comWords = 0;
		int vec = 0;
		int sens = 0;
		int rand = 0;
		int slow = 0;
		
		List<String> commonWords = new ArrayList<String>();
		FuzzerAuthString authString = new FuzzerAuthString();
		
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
		
		if(args[comWords].contains("--common-words=")){
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
		
		if(args[custAuth].contains("--custom-auth="))
		{	
			authString = new FuzzerAuthString();
			String auth = args[custAuth].substring(14, args[custAuth].length());
			String[] cred = auth.split(" ");
			if(cred.length == 1)
			{
				if(cred[0].contains("u=")){
					authString.setUsername(cred[0].substring(2));
					
				}else if(cred[0].contains("p=")){
					authString.setPass(cred[0].substring(2));
				}
			}
			else if (cred.length == 2)
			{
				if(cred[0].contains("u=")){
					authString.setUsername(cred[0].substring(2));
					if(cred[1].contains("p=")){
						authString.setPass(cred[1].substring(2));
					}
				}else if(cred[0].contains("p=")){
					authString.setPass(cred[0].substring(2));
					if(cred[1].contains("u=")){
						authString.setUsername(cred[1].substring(2));
					}
				}
			}
		}
	
		String command = args[0];
		
		if (!(command.equals("discover") || command.equals("test")))
		{
			System.err.println("Usage: fuzz [discover | test] url OPTIONS");
			return;
		}
		
		String url = args[1];
		
		//Args debug block
		/*
		System.out.println("Debug: Your Fuzzer can successfully be created!");
		System.out.println(args[0] + "\n" + args[1]);
		if (custAuth > 0)
			System.out.println(args[custAuth]);
		if (comWords > 0)
			System.out.println(args[comWords]);
		 */
		
		Fuzzer fuzzer = new Fuzzer(url, commonWords);
	}
}
