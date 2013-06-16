package org.jenkinsci.plugins.zap;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.jenkinsci.plugins.zap.util.ZapGraph;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Project view extension by ZAP plugin.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class ZapProjectAction implements Action {
	public final AbstractProject<?, ?> project;

	public ZapProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getDisplayName() {
		return Messages.ProjectAction_DisplayName();
	}

	public String getUrlName() {
		return "zap";
	}

	/**
	 * Gets the most recent {@link ZapBuildAction} object.
	 */
	public ZapBuildAction getLastResult() {
		for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b
				.getPreviousBuild()) {
			if (b.getResult() == Result.FAILURE)
				continue;
			ZapBuildAction r = b.getAction(ZapBuildAction.class);
			if (r != null) {
				FilePath actualBuildDirRoot = new FilePath(
						ZapPublisher.getZAPReport(b));
				try {
					r.load(actualBuildDirRoot);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return r;
			}
		}
		return null;
	}

	public void doGraph(StaplerRequest req, StaplerResponse rsp)
			throws IOException {
		if (getLastResult() != null) {
			ZapGraph zgraph = new ZapGraph(getLastResult());
			zgraph.doGraph(req, rsp);
		}
	}

	private static final Logger logger = Logger
			.getLogger(ZapBuildAction.class.getName());
}
