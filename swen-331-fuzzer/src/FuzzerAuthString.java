/**
 * FuzzerAuthString is used by the Fuzzer to 
 * store custom credentials used for testing
 *
 */
public class FuzzerAuthString {
	
	private String username;
	private String password;
	
	/**
	 * Name: FuzzerAuthString
	 * Description: default constructor, sets the 
	 * 				username and password to empty strings
	 */
	public FuzzerAuthString(){
		username = "";
		password = "";
	}
	
	/**
	 * Name: setUsername
	 * Description: sets the Username for the auth string
	 * @param un	The Username being set in the authstring
	 */
	public void setUsername(String un)
	{
		username = un;
	}
	
	/**
	 * Name: setPass
	 * Description: Sets the password for the auth string
	 * @param pass	The password that is being set in the authstring
	 */
	public void setPass(String pass)
	{
		password = pass;
	}
	
	/**
	 * Name: getUsername
	 * Description: Returns the username of the authstring
	 * @returns	username	The authstring username
	 */
	public String getUsername(){
		return username;
	}
	
	/**
	 * Name: getPassword
	 * Description: Returns the password of the authstring
	 * @returns password	The authstring password 
	 */
	public String getPassword(){
		return password;
	}
	
}
