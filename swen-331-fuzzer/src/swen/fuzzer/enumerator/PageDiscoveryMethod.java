package swen.fuzzer.enumerator;

public enum PageDiscoveryMethod {
	Crawled("Crawled"),
	Guessed("Guessed"),
	Root("Provided by user"),
	Login("Authentication redirect"),
	Unknown("Unknown");
	
	private String printedName;
	
	PageDiscoveryMethod(String printedName)
	{
		this.printedName = printedName;
	}
	
	public String getPrintedName()
	{
		return this.printedName;
	}
}
