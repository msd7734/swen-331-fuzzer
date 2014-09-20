import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class Page {

	private final String URL;
	private List<HtmlForm> forms = new ArrayList<HtmlForm>();
	private List<HtmlInput> inputs = new ArrayList<HtmlInput>();
	private List<Cookie> cookies = new ArrayList<Cookie>();
	
	public Page(String url) {
		this.URL = url;
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

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public String getURL() {
		return URL;
	}
	
	
	
	
}
