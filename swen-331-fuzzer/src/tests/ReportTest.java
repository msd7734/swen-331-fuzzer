package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ReportTest {

	List<HtmlAnchor> links;
	List<HtmlForm>  forms;
	List<HtmlInput> inputs;
	
	@Before
	public void setUp() throws Exception {	
		WebClient wc = new WebClient();
		try {
			HtmlPage page = wc.getPage("http://www.google.com.br");

			links =  page.getAnchors();
			forms =  page.getForms();
			List<DomElement> elements = page.getElementsByTagName("input"); 

			for(DomElement htmlInput : elements){
				inputs.add((HtmlInput)htmlInput);
//				System.out.println(test.getNameAttribute() + "  " + test.getTypeAttribute() + " " + test.asText());
			}

		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testSetForms(){
		
	}
	
	@Test void testAddPageFound(){
		
	}
	
	@Test
	public void testSetInputs(){
		
	}
	
	@Test
	public void testSetCookies(){
		
	}
	
	@Test
	public void testShow() {
		fail("Not yet implemented");
	}

}
