import java.util.ArrayList;


public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{	
		
		if(args[0].contains("--custom-auth="))
		{	
			FuzzerAuthString authString = new FuzzerAuthString();
			String auth = args[0].substring(14, args[0].length());
			System.out.println(auth);
			String[] cred = auth.split(" ");
			System.out.println(cred.length);
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
	}
}
