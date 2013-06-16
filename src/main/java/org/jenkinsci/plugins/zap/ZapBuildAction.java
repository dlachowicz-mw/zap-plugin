package org.jenkinsci.plugins.zap;

import hudson.FilePath;
import hudson.model.Api;
import hudson.model.BuildListener;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.jenkinsci.plugins.zap.model.Alert;
import org.jenkinsci.plugins.zap.model.AlertResult;
import org.jenkinsci.plugins.zap.model.Risk;
import org.jenkinsci.plugins.zap.report.ZapReport;
import org.jenkinsci.plugins.zap.util.ZapGraph;
import org.jenkinsci.plugins.zap.util.ZapParser;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Build view extension by ZAP plugin.
 * 
 * As {@link AlertResult}, it retains the overall alert report.
 * 
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 * @author Patrick Roth
 */
public final class ZapBuildAction extends AlertResult implements
		HealthReportingAction, StaplerProxy, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4890487530181041472L;

	public final AbstractBuild<?, ?> owner;

	public final PrintStream logger;
	private transient WeakReference<ZapReport> report;

	private ZapGraph graph = null;

	/**
	 * Non-null if the alert has pass/fail rules.
	 */
	private final Rule rule;

	/**
	 * The thresholds that applied when this build was built.
	 * 
	 * @TODO add ability to trend thresholds on the graph
	 */
	private final ZapHealthReportThresholds thresholds;

	/**
	 * 
	 * @param owner
	 * @param rule
	 * @param ratios
	 *            The available alert ratios in the report. Null is treated the
	 *            same as an empty map.
	 * @param thresholds
	 */
	public ZapBuildAction(AbstractBuild<?, ?> owner, Rule rule,
			Collection<Alert> alerts, ZapHealthReportThresholds thresholds,
			BuildListener listener) {
		logger = listener.getLogger();
		this.owner = owner;
		this.rule = rule;
		this.thresholds = thresholds;
		addAlerts(alerts);
		graph = new ZapGraph(this);
	}

	public String getDisplayName() {
		return Messages.BuildAction_DisplayName();
	}

	public String getUrlName() {
		return "zap";
	}

	public ZapHealthReportThresholds getThresholds() {
		return thresholds;
	}

	/**
	 * Get the alert {@link hudson.model.HealthReport}.
	 * 
	 * @return The health report or <code>null</code> if health reporting is
	 *         disabled.
	 * @since 1.7
	 */
	public HealthReport getBuildHealth() {
		if ((thresholds != null) && (thresholds.isValid())) {
			int score = 0;
			int nb;
			ArrayList<Localizable> reports = new ArrayList<Localizable>(5);

			nb = checkRiskNumber(Risk.High, thresholds.getMaxHighRisk(), score);
			if (nb >= 0) {
				reports.add(Messages._BuildAction_HighRisk(nb));
			}

			nb = checkRiskNumber(Risk.Medium, thresholds.getMaxMediumRisk(),
					score);
			if (nb >= 0) {
				reports.add(Messages._BuildAction_MediumRisk(nb));
			}
			nb = checkRiskNumber(Risk.Low, thresholds.getMaxLowRisk(), score);
			if (nb >= 0) {
				reports.add(Messages._BuildAction_LowRisk(nb));
			}
			nb = checkRiskNumber(Risk.Informational,
					thresholds.getMaxInformationalRisk(), score);
			if (nb >= 0) {
				reports.add(Messages._BuildAction_InformationalRisk(nb));
			}

			if (score == 0) {
				reports.add(Messages._BuildAction_Perfect());
			}
			// Collect params and replace nulls with empty string
			Object[] args = reports.toArray(new Object[5]);
			for (int i = 4; i >= 0; i--)
				if (args[i] == null)
					args[i] = "";
				else
					break;
			return new HealthReport(nb, Messages._BuildAction_Description(
					args[0], args[1], args[2], args[3], args[4]));
		}
		return null;
	}

	private int checkRiskNumber(Risk risk, int threshold, int score) {
		Integer number = riskNumbers.get(risk);
		if (number == null) {
			return 0;
		}

		if (threshold > 0) {
			if (number.intValue() < threshold) {
				score += number;
				return number.intValue();
			}
		}
		return -1;
	}

	private static int updateHealthScore(int score, int min, int value, int max) {
		if (value >= max)
			return score;
		if (value <= min)
			return 0;
		assert max != min;
		final int scaled = (int) (100.0 * ((float) value - min) / (max - min));
		if (scaled < score)
			return scaled;
		return score;
	}

	public Object getTarget() {
		return getResult();
	}

	public AbstractBuild<?, ?> getBuild() {
		return owner;
	}

	protected ZapParser getZapReport(FilePath fp) throws IOException {
		ZapParser zp = null;
		// try {
		FilePath pathToXmlFile = new FilePath(fp, "xmlFile");

		zp = new ZapParser(pathToXmlFile);
		// zp.parse(pathToXmlFile);

		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return zp;
	}

	protected ZapParser getZapReport(File file) throws IOException {
		ZapParser zp = null;
		// try {
		FilePath pathToXmlFile = new FilePath(file);
		FilePath zapreport = new FilePath(pathToXmlFile,
				ZapPublisher.ZAP_XML_REPORT_NAME);

		zp = new ZapParser(zapreport);
		// zp.parse(zapreport);

		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return zp;
	}

	/**
	 * Obtains the detailed {@link ZapReport} instance.
	 */
	public synchronized ZapReport getResult() {

		if (report != null) {
			final ZapReport r = report.get();
			if (r != null)
				return r;
		}

		final File reportFolder = ZapPublisher.getZAPReport(owner);

		try {
			ZapReport r = new ZapReport(this, getZapReport(reportFolder));
			report = new WeakReference<ZapReport>(r);
			return r;
		} catch (IOException e) {
			logger.println("Failed to load " + reportFolder);
			e.printStackTrace(logger);
			return null;
		}
	}

	public ZapBuildAction getPreviousResult() {
		return getPreviousResult(owner);
	}

	/**
	 * Gets the previous {@link ZapBuildAction} of the given build.
	 */
	/* package */static ZapBuildAction getPreviousResult(
			AbstractBuild<?, ?> start) {
		AbstractBuild<?, ?> b = start;
		while (true) {
			b = b.getPreviousBuild();
			if (b == null)
				return null;
			if (b.getResult() == Result.FAILURE)
				continue;
			ZapBuildAction r = b.getAction(ZapBuildAction.class);
			if (r != null)
				return r;
		}
	}

	/**
	 * Constructs the object from ZAP XML files.
	 * 
	 * @throws IOException
	 *             if failed to parse the file.
	 * @throws InvocationTargetException
	 */
	public static ZapBuildAction load(AbstractBuild<?, ?> owner, Rule rule,
			ZapHealthReportThresholds thresholds, BuildListener listener,
			FilePath actualBuildDirRoot) throws IOException,
			InvocationTargetException {
		PrintStream logger = listener.getLogger();
		ZapParser parser = new ZapParser();
		FilePath xmlFile = new FilePath(actualBuildDirRoot,
				ZapPublisher.ZAP_XML_REPORT_NAME);

		Collection<Alert> alerts = parser.parse(xmlFile);
		logger.println("Reading ZAP proxy report from " + xmlFile);
		return new ZapBuildAction(owner, rule, alerts, thresholds, listener);
	}

	/**
	 * Constructs the object from ZAP XML files.
	 * 
	 * @throws IOException
	 *             if failed to parse the file.
	 * @throws InvocationTargetException
	 */
	public void load(FilePath actualBuildDirRoot) throws IOException,
			InvocationTargetException {
		ZapParser parser = new ZapParser();
		FilePath xmlFile = new FilePath(actualBuildDirRoot,
				ZapPublisher.ZAP_XML_REPORT_NAME);

		Collection<Alert> alerts = parser.parse(xmlFile);
		logger.println("Reading ZAP proxy report from " + xmlFile);
		this.addAlerts(alerts);
	}

	private static final Logger logGer = Logger.getLogger(ZapBuildAction.class
			.getName());

	// @Override
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Generates the graph that shows the alert trend up to this report.
	 */
	public void doGraph(StaplerRequest req, StaplerResponse rsp)
			throws IOException {
		this.graph.doGraph(req, rsp);
	}

	public Api getApi() {
		return new Api(this);
	}

}
