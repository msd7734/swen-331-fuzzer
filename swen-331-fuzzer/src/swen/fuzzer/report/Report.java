package swen.fuzzer.report;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import swen.fuzzer.enumerator.TestIssue;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class Report {

	private List<Page> pages = new ArrayList<Page>();
	
	
	public List<Page> getPages() {
		return pages;
	}

	/**
	 * @param page : Just add the HtmlPage you found
	 * @param type : Just add a String of how you found the page. Ex.: "guessed"
	 * 
	 * The purpose of this method is keep track of found pages and they were found, so 
	 * when a new page is found call this method.
	 */
	public void addPageFound(HtmlPage page, String type)
	{
		Page p = new Page(page.getUrl().toString(), type);
		for (Page reportContent : this.pages) {
			if(reportContent.getURL().equals(p.getURL()))
			{
				throw new RuntimeException("You tried to pass the same URL twice to the report");
			}
		}
		this.pages.add(p);
	}
	
	/**
	 * 
	 * @param URL: string containing URL page
	 * @param forms: list of found forms
	 */
	public void setPageForms(String URL, List<HtmlForm> forms){
		Page page = getPageByURL(URL);
		if(page == null)
		{
			throw new RuntimeException("There is no page with this URL in the report");
		}
		else
		{
			page.setForms(forms);
		}
	}
	
	
	/**
	 * 
	 * @param URL: string containing URL page
	 * @param links: list of found links
	 */
	public void setPageLinks(String URL, List<HtmlAnchor> links){
		Page page = getPageByURL(URL);
		if(page == null)
		{
			throw new RuntimeException("There is no page with this URL in the report");
		}
		else
		{
			page.setLinks(links);
		}
	}
	
	
	/**
	 * @param URL: string containing URL page
	 * @param inputs: list of found inputs 
	 */
	public void setPageInputs(String URL, List<HtmlInput> inputs){
		Page page = getPageByURL(URL);
		if(page == null)
		{
			throw new RuntimeException("There is no page with this URL in the report");
		}
		else
		{
			page.setInputs(inputs);
		}
	}
	
	/**
	 * 
	 * @param URL: string cointaing URL page
	 * @param cookies: list of found cookies 
	 */
	public void setPageCookies(String URL, Set<Cookie> cookies){
		Page page = getPageByURL(URL);
		if(page == null)
		{
			throw new RuntimeException("There is no page with this URL in the report");
		}
		else
		{
			page.setCookies(cookies);
		}
	}
	
	/**
	 * 
	 * @param URL: string cointaing URL page
	 * @param issue: a enum type of TestIssue, represents an issue found 
	 */
	public void setPageIssue(String URL, TestIssue issue)
	{
		Page page = getPageByURL(URL);
		if(page == null)
		{
			throw new RuntimeException("There is no page with this URL in the report");
		}
		else
		{
			page.getIssues().add(issue);
		}
	}
	

	public Page getPageByURL(String URL){
		Page page = null;
		for (Page reportContent : this.pages) {
			if(reportContent.getURL().equals(URL)){
				page = reportContent;
				break;
			}
		}
		return page;
	}
	
	public void show()
	{
		printStats();
		System.out.println();
		List<String> exploredUrl = new ArrayList<String>();
		for (Page page : pages) {
			if(!isExplored(page, exploredUrl))
			{
				exploredUrl.add(removeParameter(page.getURL()));
				System.out.printf("%s (%s)\n",removeParameter(page.getURL()),page.getType());
				printForms(page);
				printLinks(page);
				printInputs(page);
				printCookies(page);
				System.out.println();
			}
		}
	}
	
	private boolean isExplored(Page page, List<String> exploredUrl)
	{
		boolean status = false;
		for (String url : exploredUrl) {
			if(url.equalsIgnoreCase(removeParameter(page.getURL())))
			{
				status = true;
				break;
			}
		}
		return status;
	}
	
	private String removeParameter(String url)
	{
		String[] splitedUrl  = url.split("\\?");
		return splitedUrl[0];
	}
	
	private void printStats()
	{
		Integer numberGuessed = 0;
		Integer numberCrawled = 0;
		Integer numberLinks = 0;
		Integer numberForms = 0;
		Integer numberInputs = 0;
		Integer numberCookies = 0;
		List<String> exploredUrl = new ArrayList<String>();
		for (Page page : pages) {
			if(!isExplored(page, exploredUrl))
			{
				exploredUrl.add(removeParameter(page.getURL()));
				if(page.getType().equalsIgnoreCase("guessed"))
					numberGuessed++;
				else
					numberCrawled++;
				numberLinks += page.getLinks().size();
				numberForms += page.getForms().size();
				numberInputs += page.getInputs().size();
				numberCookies += page.getActualCookies().size();
			}
		}
		
		System.out.printf("Crawled %d pages \n",numberCrawled);
		System.out.printf("Found %d links \n",numberLinks);
		System.out.printf("Found %d forms \n",numberForms);
		System.out.printf("Found %d inputs \n",numberInputs);
		System.out.printf("Found %d cookies \n",numberCookies);
		System.out.printf("Successfully guessed %d urls \n",numberGuessed);
	}
	
	private void printForms(Page page)
	{
		List<HtmlForm> formList = page.getForms();
		
		for (HtmlForm htmlForm : formList) {
			System.out.printf("- Form \" %s \" \n",htmlForm.getNameAttribute());
		}
	}
	
	private void printLinks(Page page)
	{
		List<HtmlAnchor> anchorList = page.getLinks();
		
		for (HtmlAnchor htmlAnchor : anchorList) {
			System.out.printf("- Link \" %s \" \n",htmlAnchor.getHrefAttribute());
		}
	}
	
	private void printInputs(Page page)
	{
		List<HtmlInput> inputList = page.getInputs();
		
		for (HtmlInput htmlInput : inputList) {
			System.out.printf("- %s input \" %s \" \n",htmlInput.getTypeAttribute(), htmlInput.getNameAttribute());
		}
	}
	
	private void printCookies(Page page)
	{
		Set<Cookie> actualCookies = page.getActualCookies();
		Set<Cookie> oldCookies = page.getOldCookies();
		for (Cookie cookie : actualCookies) {
			Cookie c = page.containsCookie(cookie, oldCookies);
			if(c != null)
				System.out.printf("- Cookie %s changed its value from \"%s\" to \"%s\" \n",cookie.getName(), c.getValue(), cookie.getValue());
			else
				System.out.printf("- Cookie %s was created with value \"%s\" \n",cookie.getName(),cookie.getValue());
		}
	}
}
