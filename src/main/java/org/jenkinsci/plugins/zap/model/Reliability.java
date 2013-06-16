package org.jenkinsci.plugins.zap.model;

public enum Reliability {
	Warning("Warning");

	private String level;

	private Reliability(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}

}
