package org.jenkinsci.plugins.zap.model;

public enum Risk {

	High("High"), //
	Medium("Medium"), //
	Low("Low"), //
	Informational("Informational"), //
	FalsePositive("False positive");

	private String level;
	private String levelshort;

	private Risk(String level) {
		this.level = level;
		this.levelshort = level.toLowerCase();
		int pos_space = levelshort.indexOf(" ");
		if (pos_space > 0) {
			this.levelshort = levelshort.substring(pos_space, pos_space + 1);
		}
	}

	public String getLevel() {
		return level;
	}

	public String getLevelShort() {
		return levelshort;
	}
}