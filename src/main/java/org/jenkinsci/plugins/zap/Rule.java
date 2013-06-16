package org.jenkinsci.plugins.zap;

import hudson.ExtensionPoint;
import hudson.model.Build;
import hudson.model.TaskListener;

import java.io.Serializable;

import org.jenkinsci.plugins.zap.report.ZapReport;

/**
 * Rule object encapsulates the logic to mark builds as "failed".
 * Such logic is used to mark builds as unstable when certain condition is met.
 *
 * <p>
 * The rule instances are persisted as a part of {@link Build}, so make sure
 * to make your class serializable. This is so that we can consistently mark
 * alert results even if the job configuration changes.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Rule implements Serializable, ExtensionPoint {
    public abstract void enforce(ZapReport report, TaskListener listener);

    private static final long serialVersionUID = 1L;
}
