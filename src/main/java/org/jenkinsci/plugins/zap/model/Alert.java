package org.jenkinsci.plugins.zap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Represents a ZAP alert.
 * 
 * @author Patrick Roth
 */
@ExportedBean
final public class Alert implements Serializable {

	private static final long serialVersionUID = 4827496935232941L;

	private String type = null;
	private String attack = null;
	private Risk risk = null;
	private Reliability reliability = null;
	private String reference = null;
	private List<String> urls = null;
	private String description = null;
	private String otherInfo = null;
	private String param = null;
	private String solution = null;

	@DataBoundConstructor
	public Alert() {
	}

	public static Alert getNullAlert() {
		Alert a = new Alert();
		a.type = "";
		a.attack = "";
		a.risk = null;
		a.reliability = null;
		a.reference = "";
		a.urls = new ArrayList();
		a.description = "";
		a.otherInfo = "";
		a.param = "";
		a.solution = "";
		return a;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttack() {
		return attack;
	}

	public void setAttack(String attack) {
		this.attack = attack;
	}

	public String getRisk() {
		if (risk == null) {
			return "";
		}
		return risk.toString();
	}

	public void setRisk(String risk) {
		this.risk = Risk.valueOf(risk);
	}

	public String getReliability() {
		if (reliability == null) {
			return "";
		}
		return reliability.toString();
	}

	public void setReliability(String reliability) {
		this.reliability = Reliability.valueOf(reliability);
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void addUrl(String url) {
		if (urls == null) {
			urls = new ArrayList<String>(16);
		}
		this.urls.add(url);
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	/**
	 * Gets alert representation.
	 */
	public String toString() {
		String nextLine = "\n ";
		StringBuilder sb = new StringBuilder("\n[ alert:\n ");
		sb.append("type=").append(this.type).append(nextLine);
		sb.append("risk=").append(this.risk).append(nextLine);
		sb.append("reliability=").append(this.reliability).append(nextLine);
		sb.append("attack=").append(this.attack).append(nextLine);
		sb.append("urls=").append(this.urls).append(nextLine);
		sb.append("param=").append(this.param).append(nextLine);
		sb.append("reference=").append(this.reference).append(nextLine);
		sb.append("otherInfo=").append(this.otherInfo).append(nextLine);
		sb.append("description=").append(this.description).append(nextLine);
		sb.append("solution=").append(this.solution).append(nextLine);
		sb.append(']');
		return sb.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Alert alert = (Alert) o;

		if (!this.type.equals(alert.type)) {
			return false;
		}
		return true;

	}

	public int hashCode() {
		int result;
		result = type.hashCode();
		result += attack.hashCode();
		result += urls.hashCode();
		return result;
	}

}
