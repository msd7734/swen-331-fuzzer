import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class Report {

	List<Page> pages = new ArrayList<Page>();
	
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
		for (Page reportContent : pages) {
			if(reportContent.getURL().equals(p.getURL()))
			{
				throw new RuntimeException("You tried to pass the same URL twice to the report");
			}
		}
		pages.add(p);
	}
	
	/**
	 * 
	 * @param URL: string containing URL page
	 * @param forms: list of found forms
	 */
	public void setPageForm(String URL, List<HtmlForm> forms){
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
	 * @param URL: string containing URL page
	 * @param inputs: list of found inputs 
	 */
	public void setPageInput(String URL, List<HtmlInput> inputs){
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
	public void setPageCookies(String URL, List<Cookie> cookies){
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
	

	private Page getPageByURL(String URL){
		Page page = null;
		for (Page reportContent : pages) {
			if(reportContent.getURL() == URL){
				page = reportContent;
				break;
			}
		}
		return page;
	}
	
	public void show()
	{
		
	}
	
}
