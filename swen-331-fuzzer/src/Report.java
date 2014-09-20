import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
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
	
	public void test()
	{
		WebClient wc = new WebClient();
		try {
			HtmlPage page = wc.getPage("http://www.google.com.br");
			
			List<HtmlAnchor> links =  page.getAnchors();
			List<HtmlForm>  forms =  page.getForms();
			List<DomElement> inputs = page.getElementsByTagName("input");
			
			for(DomElement htmlInput : inputs){
					HtmlInput test = (HtmlInput)htmlInput;
					System.out.println(test.getNameAttribute() + "  " + test.getTypeAttribute() + " " + test.asText());
			}
			
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
