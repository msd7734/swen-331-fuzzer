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
	private FuzzerAuthString authStr;
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
			HtmlForm form =  rootPg.getForms().get(0);
			HtmlTextInput userName = form.getInputByName("username");
			HtmlPasswordInput password = form.getInputByName("password");
			HtmlSubmitInput button = form.getInputByName("Login");
			
			userName.setValueAttribute(this.authStr.getUsername());
			password.setValueAttribute(this.authStr.getPassword());
			HtmlPage p2 = button.click();
			redirectURL = p2.getUrl().toString();
		}
		List<String> list = resolveAnchors(rootPg, anchors);
		if(!redirectURL.equals(""))
			list.add(redirectURL);
		//TODO: Add authentication handling for Bodgeit and DVWA
		
		for (String a : list)
			crawl(a, PageDiscoveryMethod.Crawled);
		for (String guess : this.guessList)
			crawl(parent+guess, PageDiscoveryMethod.Guessed);
		/*
		switch(this.targetSite)
		{
		case Bodgeit:
			break;
		case DVWA: 
			break;
		case Other:
		default:
			
			break;
		}*/
		
		//after fully crawling, print the final report result
		this.report.show();
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
			
			for (String guess : this.guessList)
			{
				crawl(parentPath + guess, PageDiscoveryMethod.Guessed);
			}
			
		}

		List<HtmlAnchor> anchors = pg.getAnchors();
		
		for (String a : resolveAnchors(pg, anchors))
			crawl(a, PageDiscoveryMethod.Crawled);
	}
	
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
			/*this.username = "admin";
			this.password = "password";*/
		}
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
	
	public static void main(String[] args) {
		Fuzzer fuzzer = new Fuzzer("http://www.google.com", new ArrayList<String>());
	}
}
