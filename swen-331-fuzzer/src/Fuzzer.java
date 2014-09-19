import java.util.*;
import java.net.*;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.html.*;

//Matt: Remember to pass enum string value to report instead of enum itself
//		when reporting whether a page was accessed via crawl, guess, etc.

public class Fuzzer {
	//For these kinds of list, some other object may be used later if convenient
	private List<String> visitedUrls;
	private List<String> visitedPaths;
	private List<String> knownCookies; 
	
	private String rootUrl;
	private List<String> commonWords;
	private FuzzerAuthString authStr;
	
	private final WebClient webClient;
	private final CookieManager cookieManager;
	
	
	public Fuzzer(String rootUrl, List<String> commonWords)
	{
		this.visitedUrls = new ArrayList<String>();
		this.visitedUrls.add(rootUrl);
		this.visitedPaths = new ArrayList<String>();
		this.visitedPaths.add(getURL(rootUrl).getPath());
		this.rootUrl = rootUrl;
		this.commonWords = commonWords;
		this.authStr = null;
		
		webClient = new WebClient();
		cookieManager = webClient.getCookieManager();
		cookieManager.setCookiesEnabled(true);
	}
	
	/**
	 * Crawl and guess to access and catalog web pages.
	 */
	public void discover() throws MalformedURLException,IOException
	{
		final HtmlPage rootPg = webClient.getPage(this.rootUrl);
		
		List<HtmlAnchor> anchors = rootPg.getAnchors();
		List<HtmlForm> forms = rootPg.getForms();
		DomNodeList<DomElement> inputs = rootPg.getElementsByTagName("input");
		
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
	public void setCustomAuthString(FuzzerAuthString authStr)
	{
		this.authStr = authStr;
	}
	
	/*
	 * Helper Methods
	 */

	//Convert our string representations into URLs for utility's sake
	//Encapsulating it like this avoids a ton of try/catch blocks 
	private static URL getURL(String str)
	{
		try {
			URL res = new URL(str);
			return res;
		}
		catch (MalformedURLException murle) {
			System.err.println(str +
					" was attempted to be treated as a URL, but was malformed.");
			return null;
		}
	}
	
	private static boolean urlStrictCompare(String u1, String u2)
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
