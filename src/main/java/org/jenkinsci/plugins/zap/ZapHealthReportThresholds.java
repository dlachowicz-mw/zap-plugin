package org.jenkinsci.plugins.zap;

import java.io.Serializable;

/**
 * Holds the configuration details for {@link hudson.model.HealthReport}
 * generation
 * 
 * @author Patrick Roth
 * @since 1.0
 */
public class ZapHealthReportThresholds implements Serializable {
	private static final long serialVersionUID = 2666973378689507313L;

	private Integer maxHighRisk;
	private Integer maxMediumRisk;
	private Integer maxLowRisk;
	private Integer maxInformationalRisk;
	private Integer minHighRisk;
	private Integer minMediumRisk;
	private Integer minLowRisk;
	private Integer minInformationalRisk;

	public ZapHealthReportThresholds(String maxHighRisk, String maxMediumRisk,
			String maxLowRisk, String maxInformationalRisk, String minHighRisk,
			String minMediumRisk, String minLowRisk, String minInformationalRisk) {
		super();
		this.maxHighRisk = convertToInteger(maxHighRisk);
		this.maxMediumRisk = convertToInteger(maxMediumRisk);
		this.maxLowRisk = convertToInteger(maxLowRisk);
		this.maxInformationalRisk = convertToInteger(maxInformationalRisk);
		this.minHighRisk = convertToInteger(minHighRisk);
		this.minMediumRisk = convertToInteger(minMediumRisk);
		this.minLowRisk = convertToInteger(minLowRisk);
		this.minInformationalRisk = convertToInteger(minInformationalRisk);
	}

	public int getMaxHighRisk() {
		return maxHighRisk;
	}

	public int getMaxMediumRisk() {
		return maxMediumRisk;
	}

	public int getMaxLowRisk() {
		return maxLowRisk;
	}

	public int getMaxInformationalRisk() {
		return maxInformationalRisk;
	}

	public int getMinHighRisk() {
		return minHighRisk;
	}

	public int getMinMediumRisk() {
		return minMediumRisk;
	}

	public int getMinLowRisk() {
		return minLowRisk;
	}

	public int getMinInformationalRisk() {
		return minInformationalRisk;
	}

	public ZapHealthReportThresholds() {
	}

	public boolean isValid() {
		return isValid(minHighRisk, maxHighRisk)
				&& isValid(minMediumRisk, maxMediumRisk)
				&& isValid(minLowRisk, maxLowRisk)
				&& isValid(minInformationalRisk, maxInformationalRisk);
	}

	private Integer convertToInteger(String value) {
		if (value == null) {
			return null;
		}
		return Integer.valueOf(value);
	}

	private boolean isValid(Integer minValue, Integer maxValue) {
		if ((minValue == null) || (maxValue == null)) {
			return true;
		}
		if (minValue > maxValue) {
			return false;
		}
		return true;
	}

}
