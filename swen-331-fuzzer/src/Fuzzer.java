import java.util.*;
import java.net.*;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.html.*;
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
	private List<String> knownCookies;
	
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
		this.authStr = null;
		this.targetSite = TargetSiteIdent.Other;
		
		webClient = new WebClient();
		cookieManager = webClient.getCookieManager();
		cookieManager.setCookiesEnabled(true);
		
		this.report = new Report();
	}
	
	/**
	 * Crawl and guess to access and catalog web pages, and print findings to standard output.
	 */
	public void discover() throws MalformedURLException,IOException,FailingHttpStatusCodeException
	{
		final HtmlPage rootPg = webClient.getPage(this.rootUrl);
		
		//throw exception here because we need a valid start point to do anything
		if (rootPg.getWebResponse().getStatusCode() != 200)
			throw new FailingHttpStatusCodeException(rootPg.getWebResponse());
		
		//Report the root page
		reportPage(rootPg, PageDiscoveryMethod.Root);
		
		List<HtmlAnchor> anchors = rootPg.getAnchors();
		
		//TODO: Add authentication handling for Bodgeit and DVWA
		switch(this.targetSite)
		{
		case Bodgeit:
			break;
		case DVWA:
			break;
		case Other:
		default:
			for (HtmlAnchor a : anchors)
				crawl(a.getHrefAttribute(), PageDiscoveryMethod.Crawled);
			for (String guess : this.guessList)
				crawl(guess, PageDiscoveryMethod.Guessed);
			break;
		}
		
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
		if (!this.visitedPaths.contains(getParentPath(url)))
		{
			String parentPath = getParentPath(url);
			this.visitedPaths.add(parentPath);
			
			for (String guess : this.guessList)
			{
				crawl(parentPath + guess, PageDiscoveryMethod.Guessed);
			}
			
		}
		
		//report result
		reportPage(pg, method);
		
		List<HtmlAnchor> anchors = pg.getAnchors();
		
		for (HtmlAnchor a : anchors)
			crawl(a.getHrefAttribute(), PageDiscoveryMethod.Crawled);
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
	public void setCustomAuthInfo(FuzzerAuthString authStr, TargetSiteIdent target)
	{
		this.authStr = authStr;
		this.targetSite = target;
	}
	
	/*
	 * Helper Methods
	 */
	
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
			String res = url.getProtocol() + "://" + url.getHost() + url.getPath();
			return res;
	}
	
	private static boolean urlStrictCompare(String u1, String u2)
	{
		return getCanonicalUrl(u1).equals(getCanonicalUrl(u2));
	}
}
