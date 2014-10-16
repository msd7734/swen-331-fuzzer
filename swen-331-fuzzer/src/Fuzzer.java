import java.util.*;
import java.net.*;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLAnchorElement;
import com.gargoylesoftware.htmlunit.util.*;

import swen.fuzzer.enumerator.*;
import swen.fuzzer.report.Report;

//Matt: Remember to pass enum string value to report instead of enum itself
//		when reporting whether a page was accessed via crawl, guess, etc.



public class Fuzzer {
	private static final String[] extensions =  {"", ".htm", ".html", ".php", ".asp", ".aspx",
		".cshtml", ".xhtml", ".txt", ".js", ".css", ".master", ".json", ".phtml"
	};
	
	//For these kinds of lists, some other object may be used later if convenient
	private List<String> visitedUrls;
	//these are the results of Java's URL.getPath()
	private List<String> visitedPaths;
	
	private String rootUrl;
	private List<String> commonWords;
	private List<String> guessList;
	private List<String> vectors;
	private List<String> sensitive;
	private FuzzerAuthString authStr;
	private int slowTest;
	private boolean random;
	private TargetSiteIdent targetSite;
	
	private final WebClient webClient;
	private final CookieManager cookieManager;
	
	private final Report report;
	
	public Fuzzer(String rootUrl, List<String> commonWords)
	{
		this.visitedUrls = new ArrayList<String>();
		//maybe wait to add root url to visited until discover() is called
		//in addition, this will probably need some validation for similar but same urls 
		this.visitedUrls.add(rootUrl);
		this.visitedPaths = new ArrayList<String>();
		this.visitedPaths.add(getParentPath(getURL(rootUrl).getPath()));
		this.rootUrl = rootUrl;
		this.commonWords = commonWords;
		this.guessList = getGuessList();
		this.authStr = new FuzzerAuthString();
		this.vectors = null;
		this.sensitive = null;
		this.slowTest = 500;
		this.random = false;
		this.targetSite = TargetSiteIdent.Other;
		webClient = new WebClient();
		WebClientOptions options = webClient.getOptions();
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setPrintContentOnFailingStatusCode(false);
		options.setJavaScriptEnabled(false);
		
		cookieManager = webClient.getCookieManager();
		cookieManager.setCookiesEnabled(true);
		
		this.report = new Report();
	}
	
	public void execute(String command) throws MalformedURLException, FailingHttpStatusCodeException, IOException{
		discover();
		if(command.equals("test")){
			test();
		}
		this.report.show();
	}
	
	
	/**
	 * Crawl and guess to access and catalog web pages, and print findings to standard output.
	 */
	public void discover() throws MalformedURLException,IOException,
		FailingHttpStatusCodeException
	{
		final HtmlPage rootPg = webClient.getPage(this.rootUrl);
		
		//throw exception here because we need a valid start point to do anything
		if (rootPg.getWebResponse().getStatusCode() != 200)
			throw new FailingHttpStatusCodeException(rootPg.getWebResponse());
		//Report the root page
		reportPage(rootPg, PageDiscoveryMethod.Root);
		
		List<HtmlAnchor> anchors = rootPg.getAnchors();
		
		String parent = getParentPath(rootPg.getUrl().toString());
		String redirectURL = "";
		if(rootPg.getUrl().toString().equals("http://127.0.0.1/dvwa/login.php"))
		{
			redirectURL = loginDVWA(rootPg);
		}
		else if(rootPg.getUrl().toString().equals("http://127.0.0.1:8080/bodgeit/login.jsp"))
				{
					loginBodgeit(rootPg);
				}
		
		List<String> list = resolveAnchors(rootPg, anchors);
		if(!redirectURL.equals(""))
			list.add(redirectURL);
		
		for (String a : list)
		{
				if (onSameSite(a, rootPg.getUrl().toString()))
				{
					try {
						crawl(a, PageDiscoveryMethod.Crawled);
					}
					catch (IllegalArgumentException iae) {
						
					}
				}
		}
		for (String guess : this.guessList)
			crawl(parent+guess, PageDiscoveryMethod.Guessed);

	}
	
	private void crawl(String url, PageDiscoveryMethod method) throws MalformedURLException,IOException
	{	
		if (this.visitedUrls.contains(url))
			return;
		
		final HtmlPage pg = webClient.getPage(url);
		
		if (pg.getWebResponse().getStatusCode() != 200)
			return;
		
		this.visitedUrls.add(url);
		
		//report result
		reportPage(pg, method);
		
		if (!this.visitedPaths.contains(getParentPath(url)))
		{
			String parentPath = getParentPath(url);
			this.visitedPaths.add(parentPath);
			
			//Don't crawl guessed pages
			if (method != PageDiscoveryMethod.Guessed)
			{
				for (String guess : this.guessList)
				{
					crawl(parentPath + guess, PageDiscoveryMethod.Guessed);
				}
			}
			
		}
		
		//Don't crawl guessed pages
		if (method != PageDiscoveryMethod.Guessed)
		{
			List<HtmlAnchor> anchors = pg.getAnchors();
			for (String a : resolveAnchors(pg, anchors))
			{
				if (onSameSite(a, url))
					crawl(a, PageDiscoveryMethod.Crawled);
			}
		}
	}
	
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		
		List<swen.fuzzer.report.Page> allPages = this.report.getPages();
		
		
		
		ArrayList<String> allFormPages = new ArrayList<String>();
		ArrayList<String> otherPages = new ArrayList<String>();
		
		
		for(swen.fuzzer.report.Page url : allPages)
		{
			if(url.getType()!= PageDiscoveryMethod.Guessed.getPrintedName()){
				HtmlForm  form = null;
				final HtmlPage pg = webClient.getPage(url.getURL().toString());
				if(!pg.getForms().isEmpty()){
					form = pg.getForms().get(0);
				}
				if(form != null){
					allFormPages.add(pg.getUrl().toString());
				}
				else{
					otherPages.add(pg.getUrl().toString());
				}
			}
		}
		
		if(random){
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(allFormPages.size());
			String randomItem = allFormPages.get(index);
			allFormPages.removeAll(allFormPages);
			otherPages.removeAll(otherPages);
			allFormPages.add(randomItem);
		}
		
		//run vectors on allFormPages
		for(String url : allFormPages){
			final HtmlPage testPage = webClient.getPage(url);
			Cookie sec = new Cookie("127.0.0.1", "security", "low");
			cookieManager.removeCookie(cookieManager.getCookie("security"));
			cookieManager.addCookie(sec);
			
			List<HtmlForm> allForms = testPage.getForms();
			for(HtmlForm form : allForms){
				//get all the text areas
				List<HtmlElement> inputs = form.getElementsByTagName("input");
				ArrayList<HtmlInput> htmlInput = new ArrayList<HtmlInput>();
				for(DomElement i : inputs){
					htmlInput.add((HtmlInput) i);
				}
				int randomIndex = -1;
				if(random){
					Random randomGenerator = new Random();
					randomIndex = randomGenerator.nextInt(htmlInput.size());
				}
				for(String vector : vectors){
					HtmlSubmitInput submit = null;
					for(HtmlInput hInput : htmlInput)
					{
						if(!hInput.getTypeAttribute().equalsIgnoreCase("submit"))
						{
							//set the input to the vector
							if(random){
								if(hInput.equals(htmlInput.get(randomIndex))){
									hInput.setValueAttribute(vector);
								}
							}else{
								hInput.setValueAttribute(vector);
							}
							
						}
						else // it is the submit button
						{
							submit = (HtmlSubmitInput) hInput;
						}
						
					}
					//for each text area set the input to the vector string
					submit.click();
				}
			}
			//Analyze the page
			analyze(url);
		}
		//Analyze the rest of the pages without forms
		for(String o : otherPages)
		{
			analyze(o);
		}

		
	}
	
	public void analyze(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		WebClientOptions options = webClient.getOptions();
		options.setJavaScriptEnabled(true);
		final ArrayList<String> collectedAlerts = new ArrayList<String>();
		
	    webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
	    final HtmlPage testPage = webClient.getPage(url);

	    
	    WebResponse response = testPage.getWebResponse();
	    List<DomElement> bodyElement = testPage.getElementsByTagName("body");
	    String rawText = "";
	    
		
		if (response.getStatusCode() != 200)
		{
			report.setPageIssue(url, TestIssue.ErrorStatus);
		}
		//sensitive 
		
		boolean sensitiveTest = false;
			    
		for(DomElement d : bodyElement){
	    	rawText += d.getTextContent();
	    }
		for(String sens : sensitive){
			if(rawText.contains(sens)){
				sensitiveTest = true;
			}	
		}
		
		if(sensitiveTest){
			report.setPageIssue(url, TestIssue.SensitiveData);
		}
		
		if(collectedAlerts.contains("XXS") || rawText.contains("TABLE_SCHEMA") ){
			report.setPageIssue(url, TestIssue.Sanitization);
		}
		if(response.getLoadTime() > slowTest){
			report.setPageIssue(url, TestIssue.Slow);
		}
		
		
	    
		options.setJavaScriptEnabled(false);
	}
	
	//
	
	/*
	 * Accessors and Modifiers
	 */
	
	/**
	 * Attempt to employ custom credentials at root URL.
	 * @param authStr FuzzerAuthString containing username and/or password
	 * @param target Enum id for which site this authstring belongs to (sites are hardcoded)
	 * 
	 */
	public void setCustomAuthInfo(String customAuth)
	{
		if(customAuth.equalsIgnoreCase("dvwa"))
		{
			this.authStr.setUsername("admin");
			this.authStr.setPass("password");
		}
		else if(customAuth.equalsIgnoreCase("bodgeit"))
		{
			this.authStr.setUsername("admin");
			this.authStr.setPass("password");
		}
	}
	
	/**
	 * Set the list of Vectors to be used
	 * @param vectorList The list of vectors provided by the user
	 * 
	 */
	
	public void setVectors(List<String> vectorList)
	{	
		this.vectors = vectorList;
	}
	
	/**
	 * Set the list of Sensitive information to be used
	 * @param sensitiveList The list of sensitive information provided by the user
	 * 
	 */
	public void setSensitive(List<String> sensitiveList){
		this.sensitive = sensitiveList;
	}
	
	/**
	 * Set whether the program tests for delayed response 
	 * @param test The boolean that tells the program whether 
	 * 		  or not to check for a delayed response
	 * 
	 */
	public void setSlow(int miliseconds){
		this.slowTest = miliseconds;
	}
	
	/**
	 * Tells the program to randomly choose a page
	 * and input fiel to test all the vectors in.
	 * 
	 */
	public void setRandom(){
		this.random = true;
	}
	
	/*
	 * Helper Methods
	 */
	
	private List<String> resolveAnchors(HtmlPage page, List<HtmlAnchor> anchors)
	{
		try 
		{
			List<String> fullUrls = new ArrayList<String>(anchors.size());
			for (HtmlAnchor a : anchors)
			{
				URI current = new URI(page.getUrl().toString());
				URI result = current.resolve(a.getHrefAttribute());
				fullUrls.add(result.toString());
			}
			return fullUrls;
		}
		catch (URISyntaxException urise)
		{
			System.err.println(urise.getMessage());
			return new ArrayList<String>();
		}
	}
	
	private void reportPage(HtmlPage pg, PageDiscoveryMethod method)
	{
		this.report.addPageFound(pg, method.getPrintedName());
		String url = pg.getUrl().toString();
		
		this.report.setPageLinks(url, pg.getAnchors());
		this.report.setPageForms(url,  pg.getForms());
		
		List<DomElement> domInputs = pg.getElementsByTagName("input");
		List<HtmlInput> inputs = new ArrayList<HtmlInput>(domInputs.size());
		
		for (DomElement i : domInputs)
			inputs.add((HtmlInput) i);
		
		this.report.setPageInputs(url, inputs);
		this.report.setPageCookies(url, cookieManager.getCookies());
	}
	
	private List<String> getGuessList()
	{
		List<String> res = new ArrayList<String>(this.extensions.length * this.commonWords.size());
		
		for (String ext : this.extensions)
			for (String word : this.commonWords)
				res.add(word+ext);
		
		return res;
	}

	private String loginDVWA(HtmlPage rootPg) throws IOException
	{
		HtmlForm form =  rootPg.getForms().get(0);
		HtmlTextInput userName = form.getInputByName("username");
		HtmlPasswordInput password = form.getInputByName("password");
		HtmlSubmitInput button = form.getInputByName("Login");
		
		userName.setValueAttribute(this.authStr.getUsername());
		password.setValueAttribute(this.authStr.getPassword());
		HtmlPage p2 = button.click();
		
		return p2.getUrl().toString();
	}
	
	private void loginBodgeit(HtmlPage rootPg) throws IOException
	{
		HtmlForm form =  rootPg.getForms().get(0);
		HtmlTextInput userName = form.getInputByName("username");
		HtmlPasswordInput password = form.getInputByName("password");
		HtmlSubmitInput button = form.getInputByValue("Login");
		
		userName.setValueAttribute(this.authStr.getUsername());
		password.setValueAttribute(this.authStr.getPassword());
		button.click();
	}
	
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
	
	private static boolean onSameSite(String path1, String path2)
	{
		URL url1 = getURL(path1);
		URL url2 = getURL(path2);
		String host1 = url1.getProtocol() + "://" + url1.getHost() + url1.getPort();
		String host2 = url2.getProtocol() + "://" + url2.getHost() + url2.getPort();
		return (host1.equals(host2));
	}
	
	private static String getParentPath(String path)
	{
		try {
			URI uri = new URI(path);
			URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
			return parent.toString();
		}
		catch (URISyntaxException use)
		{
			System.err.println(path +
					" was attempted to be treated as a URI path, but was malformed.");
			return null;
		}
	}
	
	private static boolean inSameDir(String path1, String path2)
	{
		return getParentPath(path1).equals(getParentPath(path2));
	}
	
	private static String getCanonicalUrl(String urlStr)
	{
			URL url = getURL(urlStr);
			String res = url.getProtocol() + "://" + url.getHost() +
					url.getPort() +
					url.getPath();
			return res;
	}
	
	private static boolean urlStrictCompare(String u1, String u2)
	{
		return getCanonicalUrl(u1).equals(getCanonicalUrl(u2));
	}
	
}
