package org.jenkinsci.plugins.zap;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.tools.ant.DirectoryScanner;
import org.jenkinsci.plugins.zap.model.Risk;
import org.jenkinsci.plugins.zap.report.ZapReport;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * {@link Publisher} that captures ZAP alert reports.
 * 
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 * @author Patrick Roth
 * 
 */
public class ZapPublisher extends Recorder {

	public static final String ZAP_XML_REPORT_NAME = "zap-report.xml";

	/**
	 * {@link hudson.model.HealthReport} thresholds to apply.
	 */
	public ZapHealthReportThresholds healthReports = new ZapHealthReportThresholds();

	/**
	 * Variables containing the configuration set by the user.
	 */
	private final String xmlPattern;

	/**
	 * Rule to be enforced. Can be null.
	 * 
	 * TODO: define a configuration mechanism.
	 */
	private Rule rule;

	private final String maximumHighRisk;
	private final String maximumMediumRisk;
	private final String maximumLowRisk;
	private final String maximumInformationalRisk;
	private final String minimumHighRisk;
	private final String minimumMediumRisk;
	private final String minimumLowRisk;
	private final String minimumInformationalRisk;

	/**
	 * Loads the configuration set by user.
	 * 
	 */
	@DataBoundConstructor
	public ZapPublisher(String xmlPattern, String maximumHighRisk,
			String maximumMediumRisk, String maximumLowRisk,
			String maximumInformationalRisk, String minimumHighRisk,
			String minimumMediumRisk, String minimumLowRisk,
			String minimumInformationalRisk) {
		this.xmlPattern = xmlPattern;
		this.maximumHighRisk = maximumHighRisk;
		this.maximumMediumRisk = maximumMediumRisk;
		this.maximumLowRisk = maximumLowRisk;
		this.maximumInformationalRisk = maximumInformationalRisk;
		this.minimumHighRisk = minimumHighRisk;
		this.minimumMediumRisk = minimumMediumRisk;
		this.minimumLowRisk = minimumLowRisk;
		this.minimumInformationalRisk = minimumInformationalRisk;
	}

	public String getXmlPattern() {
		return xmlPattern;
	}

	public String getMaximumHighRisk() {
		return maximumHighRisk;
	}

	public String getMaximumMediumRisk() {
		return maximumMediumRisk;
	}

	public String getMaximumLowRisk() {
		return maximumLowRisk;
	}

	public String getMinimumHighRisk() {
		return minimumHighRisk;
	}

	public String getMinimumMediumRisk() {
		return minimumMediumRisk;
	}

	public String getMinimumLowRisk() {
		return minimumLowRisk;
	}

	protected static String resolveFilePaths(AbstractBuild<?, ?> build,
			BuildListener listener, String input) {
		try {

			return build.getEnvironment(listener).expand(input);

		} catch (Exception e) {
			listener.getLogger().println(
					"Failed to resolve parameters in string \"" + input
							+ "\" due to following error:\n" + e.getMessage());
		}
		return input;
	}

	protected static FilePath[] resolveDirPaths(AbstractBuild<?, ?> build,
			BuildListener listener, final String input) {
		final PrintStream logger = listener.getLogger();
		FilePath[] directoryPaths = null;
		try {
			directoryPaths = build.getWorkspace().act(
					new FilePath.FileCallable<FilePath[]>() {
						public FilePath[] invoke(File f, VirtualChannel channel)
								throws IOException {
							ArrayList<FilePath> localDirectoryPaths = new ArrayList<FilePath>();
							String[] includes = input.split(",");
							DirectoryScanner ds = new DirectoryScanner();

							ds.setIncludes(includes);
							ds.setCaseSensitive(false);
							ds.setBasedir(f);
							ds.scan();
							String[] dirs = ds.getIncludedDirectories();

							for (String dir : dirs) {
								localDirectoryPaths.add(new FilePath(new File(
										dir)));
							}
							FilePath[] lfp = {};// trick to have an empty array
												// as a parameter, so the
												// returned array will contain
												// the elements
							return localDirectoryPaths.toArray(lfp);
						}
					});

		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
		return directoryPaths;
	}

	/*
	 * Entry point of this report plugin.
	 * 
	 * @see
	 * hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild
	 * , hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		final PrintStream logger = listener.getLogger();
		FilePath[] matchedXmlFiles = null;
		FilePath actualBuildDirRoot = null;
		FilePath actualBuildXmlDir = null;

		logger.println("[ZAP plugin] Collecting ZAP alert data...");

		EnvVars env = build.getEnvironment(listener);
		env.overrideAll(build.getBuildVariables());

		if (xmlPattern == null) {
			if (build.getResult().isWorseThan(Result.UNSTABLE))
				return true;

			logger.println("[ZAP plugin] ERROR: Missing configuration!");
			build.setResult(Result.FAILURE);
			return true;
		} else {
			logger.println("[ZAP plugin] " + xmlPattern
					+ " locations are configured");
		}
		actualBuildDirRoot = new FilePath(getZAPReport(build));
		// actualBuildXmlDir = new FilePath(actualBuildDirRoot, "xmlFiles");
		//
		matchedXmlFiles = build.getWorkspace().list(
				resolveFilePaths(build, listener, xmlPattern));
		logger.print("[ZAP plugin] Saving matched XML files: ");
		int i = 0;
		for (FilePath file : matchedXmlFiles) {
			FilePath fullXmlName = new FilePath(actualBuildDirRoot,
					ZAP_XML_REPORT_NAME);
			file.copyTo(fullXmlName);
			logger.print(" " + file.getRemote());
			++i;
		}

		logger.println("[ZAP plugin] BuildENV: "
				+ build.getEnvironment(listener).toString());
		healthReports = new ZapHealthReportThresholds(maximumHighRisk,
				maximumMediumRisk, maximumLowRisk, maximumInformationalRisk,
				minimumHighRisk, minimumMediumRisk, minimumLowRisk,
				minimumInformationalRisk);

		logger.println("\n[ZAP plugin] Loading XML file..");
		ZapBuildAction action = null;
		try {
			action = ZapBuildAction.load(build, rule, healthReports, listener,
					actualBuildDirRoot);
		} catch (InvocationTargetException e) {
			logger.println("\n[ZAP plugin] Didn't manage to read or to process XML file: "
					+ e.getLocalizedMessage());
		}

		if (action != null) {
			build.getActions().add(action);

			logger.println("[ZAP plugin] Publishing the results..");
			final ZapReport result = action.getResult();
			if (result == null) {
				logger.println("ZAP: Could not parse alert results. Setting Build to failure.");
				build.setResult(Result.FAILURE);
			}
			Result r = checkResult(action);
			if (action.getBuild().getResult().isBetterThan(r)) {
				build.setResult(r);
			}
		}
		return true;
	}

	public Result checkResult(ZapBuildAction action) {
		if (compareRiskToThreshold(action.getRiskNumber(Risk.High), action
				.getThresholds().getMaxHighRisk())
				|| compareRiskToThreshold(action.getRiskNumber(Risk.Medium),
						action.getThresholds().getMaxMediumRisk())
				|| compareRiskToThreshold(action.getRiskNumber(Risk.Low),
						action.getThresholds().getMaxLowRisk())) {
			return Result.FAILURE;
		}

		if (compareRiskToThreshold(action.getRiskNumber(Risk.High), action
				.getThresholds().getMinHighRisk())
				|| compareRiskToThreshold(action.getRiskNumber(Risk.Medium),
						action.getThresholds().getMinMediumRisk())
				|| compareRiskToThreshold(action.getRiskNumber(Risk.Low),
						action.getThresholds().getMinLowRisk())) {
			return Result.UNSTABLE;
		}
		return Result.SUCCESS;
	}

	private boolean compareRiskToThreshold(int risk, Integer threshold) {
		if (threshold == null) {
			return false;
		}
		if (risk > threshold.intValue()) {
			return true;
		}
		return false;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new ZapProjectAction(project);
	}

	// @Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	/**
	 * Gets the directory to store report files
	 */
	static File getZAPReport(AbstractBuild<?, ?> build) {
		return new File(build.getRootDir(), "zap");
	}

	@Override
	public BuildStepDescriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		public DescriptorImpl() {
			super(ZapPublisher.class);
		}

		@Override
		public String getDisplayName() {
			return Messages.ZapPublisher_DisplayName();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

	}

	private static final Logger logger = Logger.getLogger(ZapPublisher.class
			.getName());
}
