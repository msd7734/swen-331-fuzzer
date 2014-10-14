package swen.fuzzer.enumerator;

public enum TestIssue {

	Slow("Slow"),
	Sanitization("Sanitization"),
	ErrorStatus("ErrorStatus"),
	SensitiveData("SensitiveData");
	
	private final String type;
	
	public String getType() {
		return type;
	}

	private TestIssue(String type) {
		this.type = type;
	}
}
