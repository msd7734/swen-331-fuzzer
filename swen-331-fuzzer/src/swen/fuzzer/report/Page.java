package swen.fuzzer.report;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class Page {

	private final String URL;
	private final String type;
	private List<HtmlForm> forms = new ArrayList<HtmlForm>();
	private List<HtmlInput> inputs = new ArrayList<HtmlInput>();
	private Set<Cookie> cookies = new HashSet<Cookie>();
	
	public Page(String url, String type) {
		this.URL = url;
		this.type = type;
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

	public Set<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(Set<Cookie> cookies) {
		this.cookies = cookies;
	}

	public String getURL() {
		return URL;
	}

	public String getType() {
		return type;
	}
	
	
	
	
}
