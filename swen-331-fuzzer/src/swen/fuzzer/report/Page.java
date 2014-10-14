package swen.fuzzer.report;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import swen.fuzzer.enumerator.TestIssue;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class Page {

	private final String URL;
	private final String type;
	private List<HtmlForm> forms = new ArrayList<HtmlForm>();
	private List<HtmlInput> inputs = new ArrayList<HtmlInput>();
	private List<HtmlAnchor> links= new ArrayList<HtmlAnchor>();
	private Set<Cookie> actualCookies = new HashSet<Cookie>();
	private Set<Cookie> oldCookies = new HashSet<Cookie>();
	private List<TestIssue> issues = null;
	
	public List<TestIssue> getIssues() {
		return issues;
	}

	public Page(String url, String type) {
		this.URL = url;
		this.type = type;
		this.issues = new ArrayList<TestIssue>();
	}

	public List<HtmlForm> getForms() {
		return forms;
	}

	public void setForms(List<HtmlForm> forms) {
		this.forms = forms;
	}

	public List<HtmlInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<HtmlInput> inputs) {
		this.inputs = inputs;
	}
	
	public String getURL() {
		return URL;
	}

	public String getType() {
		return type;
	}
	
	public List<HtmlAnchor> getLinks() {
		return links;
	}

	public void setLinks(List<HtmlAnchor> links) {
		this.links = links;
	}
	
	public Set<Cookie> getActualCookies() {
		return actualCookies;
	}

	public Set<Cookie> getOldCookies() {
		return oldCookies;
	}

	public void setCookies(Set<Cookie> cookies) {
		for (Cookie cookie : cookies) {
			Cookie c = containsCookie( cookie , actualCookies);
			if(c != null)
			{
				saveCookieState(c);
				actualCookies.remove(c);
			}
			actualCookies.add(cookie);
		}
		
	}
	
	private void saveCookieState(Cookie cookie)
	{
		Cookie c = containsCookie(cookie, oldCookies);
		if(c != null)
		{
			oldCookies.remove(c);
		}
		oldCookies.add(cookie);
	}
	
	public Cookie containsCookie(Cookie c, Set<Cookie> cookies)
	{
		Cookie result = null;
		for (Cookie cookie : cookies) {
			if( c.getName().equals(cookie.getName()))
			{
				result = cookie;
				break;
			}
		}
		return result;
	}
}
