package org.jenkinsci.plugins.zap.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.stapler.bind.JavaScriptMethod;

public class AlertResult {

	protected Map<String, Integer> riskNumbers = new HashMap<String, Integer>(5);
	protected Map<String, Integer> reliabilityNumbers = new HashMap<String, Integer>(
			3);

	private Map<String, Alert> alerts = new HashMap<String, Alert>(8);

	public void addAlert(Alert alert) {
		Alert alert_dest = alerts.get(alert.getType());
		if (alert_dest == null) {
			alert_dest = alert;
			alerts.put(alert.getType(), alert);
			Integer nb = riskNumbers.get(alert.getRisk());
			if (nb == null) {
				riskNumbers.put(alert.getRisk(), Integer.valueOf(1));
			} else {
				riskNumbers.put(alert.getRisk(),
						Integer.valueOf(nb.intValue() + 1));
			}
			nb = reliabilityNumbers.get(alert.getReliability());
			if (nb == null) {
				reliabilityNumbers.put(alert.getReliability(),
						Integer.valueOf(1));
			} else {
				reliabilityNumbers.put(alert.getReliability(),
						Integer.valueOf(nb.intValue() + 1));
			}
			return;
		}

		// alert was already registered, just copy the URL
		alert_dest.addUrl(alert.getUrls().get(0));
	}

	public Collection<Alert> getAlerts() {
		return alerts.values();
	}

	@JavaScriptMethod
	public Alert getAlert(int index) {
		index--;
		System.out.println("Alert Nb " + index);
		if ((index < 0) || (index > alerts.size())) {
			return Alert.getNullAlert();
		}
		Alert a = (Alert) alerts.values().toArray()[index];
		return a;
	}

	public int getRiskNumber(Risk risk) {
		Integer nb = riskNumbers.get(risk.getLevel());
		if (nb == null) {
			return 0;
		}
		return nb.intValue();
	}

	public int getReliabilityNumber(Reliability reliability) {
		Integer nb = reliabilityNumbers.get(reliability.getLevel());
		if (nb == null) {
			return 0;
		}
		return nb.intValue();
	}

	public void addAlerts(Collection<Alert> alerts) {
		for (Alert a : alerts) {
			this.addAlert(a);
		}
	}
}
