import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class Program {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{	
		int custAuth = 0;
		int comWords = 0;
		int vec = 0;
		int sens = 0;
		int rand = 0;
		int slow = 0;
		
		int index = 0;
		for(String s : args)
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
		
		if(args[custAuth].contains("--custom-auth="))
		{	
			FuzzerAuthString authString = new FuzzerAuthString();
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
		
		if(args[comWords].contains("--common-words=")){
			String filePath = args[comWords].substring(15);
			List<String> commonWords = new ArrayList<String>();
			
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				commonWords.add(line);
			}
			
		}
		
	}
}
