package org.jenkinsci.plugins.zap.report;

import hudson.FilePath;
import hudson.model.ModelObject;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jenkinsci.plugins.zap.ZapBuildAction;
import org.jenkinsci.plugins.zap.model.Alert;
import org.jenkinsci.plugins.zap.model.AlertResult;
import org.jenkinsci.plugins.zap.util.ZapParser;

/**
 * Root object of the alert report.
 * 
 * @author Kohsuke Kawaguchi
 * @author Ognjen Roth
 */
public final class ZapReport implements ModelObject {
	private static final String name = "zap";

	private final ZapBuildAction action;

	private AlertResult results;

	private ZapReport(ZapBuildAction action) {
		this.action = action;
	}

	/**
	 * Loads the XML files. Creates the reporting objects and the report tree.
	 * 
	 * @param action
	 * @param reports
	 * @throws IOException
	 */
	public ZapReport(ZapBuildAction action, ZapParser parser)
			throws IOException {
		this(action);
		try {

			action.logger.println("[ZAP plugin] Loading alert file...");

			//FilePath fp = action.getBuild().getWorkspace();
			//File fp = action.getBuild().getRootDir();
			Collection<Alert> alerts = parser.parse();
			this.results = new AlertResult();
			results.addAlerts(alerts);

			action.logger.println("[ZAP plugin] Done.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDisplayName() {
		return name;
	}
	public AlertResult getResult() {
		return this.results;
	}

	public ZapReport getPreviousResult() {
		ZapBuildAction prev = action.getPreviousResult();
		if (prev != null)
			return prev.getResult();
		else
			return null;
	}

	public AbstractBuild<?, ?> getBuild() {
		return action.owner;
	}

}
