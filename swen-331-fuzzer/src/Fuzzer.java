import java.util.*;
import java.net.*;
import com.gargoylesoftware.htmlunit.*;

public class Fuzzer {
	//For these kinds of list, some other object may be used later if convenient
	private List<String> visitedUrls;
	private List<String> visitedDirs;
	private List<String> knownCookies; 
	
	private String rootUrl;
	private List<String> commonWords;
	private FuzzerAuthString authStr;
	
	
	public Fuzzer(String rootUrl, List<String> commonWords)
	{
		this.visitedUrls = new ArrayList<String>();
		this.visitedDirs = new ArrayList<String>();
		this.rootUrl = rootUrl;
		this.commonWords = commonWords;
		this.authStr = null;
	}
	
	/**
	 * Crawl and guess to access web pages.
	 */
	public void discover()
	{
		return;
	}
	
	/*
	 * Accessors and Modifiers
	 */
	
	/**
	 * Attempt to employ custom credentials at root URL.
	 * @param authStr FuzzerAuthString containing username and/or password
	 * 
	 */
	public void SetCustomAuthString(FuzzerAuthString authStr)
	{
		this.authStr = authStr;
	}
	
	/*
	 * Helper Methods
	 */

	private static boolean UrlStrictCompare(String u1, String u2)
	{
		try {
			URL url1 = new URL(u1);
			URL url2 = new URL(u2);
			String trunc1 = url1.getProtocol() + "://" + url1.getHost() + url1.getPath();
			String trunc2 = url2.getProtocol() + "://" + url2.getHost() + url2.getPath();
			return (trunc1.equals(trunc2));
		}
		catch (MalformedURLException murle) {
			System.err.println("Attempted to compare invalid URL:\n" + murle.getMessage());
			return false;
		}
	}
}
