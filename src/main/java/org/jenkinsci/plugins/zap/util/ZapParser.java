package org.jenkinsci.plugins.zap.util;

import hudson.FilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.zap.model.Alert;
import org.jenkinsci.plugins.zap.model.AlertResult;
import org.xml.sax.SAXException;

public class ZapParser {
	private static final String rootXPath = "zap-report";
	private static final String alertXPath = rootXPath + "/alert";
	private static final String typeXPath = alertXPath + "/alert";
	private static final String attackXPath = alertXPath + "/attack";
	private static final String urlXPath = alertXPath + "/url";
	private static final String referenceXPath = alertXPath + "/reference";
	private static final String riskXPath = alertXPath + "/risk";
	private static final String otherXPath = alertXPath + "/other";
	private static final String reliabilityXPath = alertXPath + "/reliability";
	private static final String descriptionXPath = alertXPath + "/description";
	private static final String paramXPath = alertXPath + "/param";
	private static final String solutionXPath = alertXPath + "/solution";

	private FilePath file = null;

	public ZapParser() {
		// super(StringUtils.EMPTY);
	}

	public ZapParser(final FilePath file) {
		this.file = file;
	}

	public Collection<Alert> parse(final FilePath file)
			throws InvocationTargetException {
		try {
			String f = file.getRemote();
			File xmlDataFile = new File(f);
			final FileInputStream fis = new FileInputStream(xmlDataFile);

			Digester digester = new Digester();
			digester.setValidating(false);
			digester.setClassLoader(ZapParser.class.getClassLoader());

			digester.addObjectCreate(rootXPath, AlertResult.class);

			digester.addObjectCreate(alertXPath, Alert.class);

			digester.addCallMethod(typeXPath, "setType", 0);
			digester.addCallMethod(attackXPath, "setAttack", 0);
			digester.addCallMethod(urlXPath, "addUrl", 0);
			digester.addCallMethod(referenceXPath, "setReference", 0);
			digester.addCallMethod(riskXPath, "setRisk", 0);
			digester.addCallMethod(reliabilityXPath, "setReliability", 0);
			digester.addCallMethod(descriptionXPath, "setDescription", 0);
			digester.addCallMethod(otherXPath, "setOtherInfo", 0);
			digester.addCallMethod(paramXPath, "setParam", 0);
			digester.addCallMethod(solutionXPath, "setSolution", 0);

			digester.addSetNext(alertXPath, "addAlert");

			AlertResult result;
			result = (AlertResult) digester.parse(fis);
			if (result == null) {
				throw new SAXException("Input stream [" + file
						+ "]is not a ZAP file.");
			}

			Collection<Alert> alerts = result.getAlerts();
			return alerts;
		} catch (IOException exception) {
			throw new InvocationTargetException(exception);
		} catch (SAXException exception) {
			throw new InvocationTargetException(exception);
		}
	}

	public Collection<Alert> parse() throws InvocationTargetException {
		return parse(this.file);
	}

	private void numberAlerts(List<Alert> alerts) {
		int internalRef = 0;
		for (Alert alert : alerts) {
			// alert.setInternalReference(internalRef++);
		}

	}
}
