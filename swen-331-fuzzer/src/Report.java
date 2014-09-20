import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class Report {

	List<Page> pages = new ArrayList<Page>();
	
	/**
	 * @param page : Just add the HtmlPage you found
	 * @param type : Just add a String of how you found the page. Ex.: "guessed"
	 */
	public void addPage(HtmlPage page, String type)
	{
		Page p = new Page(page.getUrl().toString());
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
	
	public boolean save()
	{
		return true;
	}
	
	public void show()
	{
		
	}
	
}
