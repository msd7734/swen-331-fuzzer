package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import swen.fuzzer.report.Report;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class ReportTest {

	Report r = new Report();
	List<HtmlAnchor> links;
	List<HtmlForm>  forms;
	List<HtmlInput> inputs = new ArrayList<HtmlInput>();
	Set<Cookie> cookies;
	HtmlPage page;
	
	@Before
	public void setUp() throws Exception {	
		WebClient wc = new WebClient();
		try {
				page = wc.getPage("http://www.google.com");
				links =  page.getAnchors();
				forms =  page.getForms();
				cookies = wc.getCookieManager().getCookies();
				List<DomElement> elements = page.getElementsByTagName("input"); 

			for(DomElement htmlInput : elements){
				inputs.add((HtmlInput) htmlInput);
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
	public void testAddPageFound(){
		this.r.addPageFound(page, "guessed");
		assertNotNull(r.getPageByURL(page.getUrl().toString()));
	}
	
	@Test
	public void testSetForms(){
		this.r.addPageFound(page, "guessed");
		this.r.setPageForm(page.getUrl().toString(), forms);
		assertTrue(r.getPageByURL(page.getUrl().toString()).getForms().size() > 0);
	}
	
	@Test
	public void testSetInputs(){
		this.r.addPageFound(page, "guessed");
		this.r.setPageInput(page.getUrl().toString(), inputs);
		assertTrue(r.getPageByURL(page.getUrl().toString()).getInputs().size() > 0);
	}
	
	@Test
	public void testSetCookies(){
		this.r.addPageFound(page, "guessed");
		this.r.setPageCookies(page.getUrl().toString(), cookies);
		assertTrue(r.getPageByURL(page.getUrl().toString()).getCookies().size() > 0);
	}
	
	@Test
	public void testShow() {
		fail("Not yet implemented");
	}

}
