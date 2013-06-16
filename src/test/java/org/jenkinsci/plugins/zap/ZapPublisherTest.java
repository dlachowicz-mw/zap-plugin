package org.jenkinsci.plugins.zap;

import static org.junit.Assert.*;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Logger;

import org.jenkinsci.plugins.zap.model.Alert;
import org.jenkinsci.plugins.zap.util.ZapParserTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * 
 * @autor manuel_carrasco
 */
public class ZapPublisherTest extends AbstractZapTestBase {
	private ZapPublisher publisher = null;
	private ZapBuildAction action = null;
	Collection<Alert> alerts = null;
	BuildListener listener = null;

	@Before
	public void initialize() throws InvocationTargetException {
		alerts = ZapParserTest.getAlerts();
		listener = Mockito.mock(BuildListener.class);
		Mockito.when(listener.getLogger()).thenReturn(null);
		publisher = new ZapPublisher("xmlPattern", "0", "0", "0", "0", "0",
				"0", "0", "0");
	}

	@Test
	public void testCheckResult_FailureMedium() {
		action = new ZapBuildAction(null, null, alerts,
				new ZapHealthReportThresholds("10", "0", "10", "10", "10", "0",
						"10", "10"), listener);

		assertEquals(Result.FAILURE, publisher.checkResult(action));
	}

	@Test
	public void testCheckResult_FailureLow() {
		action = new ZapBuildAction(null, null, alerts,
				new ZapHealthReportThresholds("10", "10", "0", "10", "10",
						"10", "0", "10"), listener);

		assertEquals(Result.FAILURE, publisher.checkResult(action));
	}

	@Test
	public void testCheckResult_UnstableMedium() {
		action = new ZapBuildAction(null, null, alerts,
				new ZapHealthReportThresholds("0", "10", "10", "10", "0", "0",
						"10", "10"), listener);

		assertEquals(Result.UNSTABLE, publisher.checkResult(action));
	}

	@Test
	public void testCheckResult_UnstableLow() {
		action = new ZapBuildAction(null, null, alerts,
				new ZapHealthReportThresholds("0", "10", "10", "10", "0", "10",
						"1", "10"), listener);

		assertEquals(Result.UNSTABLE, publisher.checkResult(action));
	}


	@Test
	public void testCheckResult_Success() {
		action = new ZapBuildAction(null, null, alerts,
				new ZapHealthReportThresholds("0", "10", "10", "10", "0", "10",
						"10", "10"), listener);

		assertEquals(Result.SUCCESS, publisher.checkResult(action));
	}
	/*
	 * @Test public void testLocateReports() throws Exception {
	 * 
	 * // Create a temporary workspace in the system File w =
	 * File.createTempFile("workspace", ".test"); w.delete(); w.mkdir();
	 * w.deleteOnExit(); FilePath workspace = new FilePath(w);
	 * 
	 * // Create 4 files in the workspace File f1 =
	 * File.createTempFile("Zaproxy", ".xml", w); f1.deleteOnExit(); File f2 =
	 * File.createTempFile("anyname", ".xml", w); f2.deleteOnExit(); File f3 =
	 * File.createTempFile("Zaproxy", ".xml", w); f3.deleteOnExit(); File f4 =
	 * File.createTempFile("anyname", ".xml", w); f4.deleteOnExit();
	 * 
	 * 
	 * // Create a folder and move there 2 files File d1 = new
	 * File(workspace.child("subdir").getRemote()); d1.mkdir();
	 * d1.deleteOnExit();
	 * 
	 * File f5 = new
	 * File(workspace.child(d1.getName()).child(f3.getName()).getRemote()); File
	 * f6 = new
	 * File(workspace.child(d1.getName()).child(f4.getName()).getRemote());
	 * f3.renameTo(f5); f4.renameTo(f6); f5.deleteOnExit(); f6.deleteOnExit();
	 * 
	 * // Look for files in the entire workspace recursively without providing
	 * // the includes parameter FilePath[] reports =
	 * ZaproxyPublisher.locateCoverageReports(workspace, "**e/Zaproxy*.xml");
	 * Assert.assertEquals(2 , reports.length);
	 * 
	 * // Generate a includes string and look for files String includes =
	 * f1.getName() + "; " + f2.getName() + "; " + d1.getName(); reports =
	 * ZaproxyPublisher.locateCoverageReports(workspace, includes);
	 * Assert.assertEquals(3, reports.length);
	 * 
	 * // Save files in local workspace FilePath local =
	 * workspace.child("coverage_localfolder");
	 * ZaproxyPublisher.saveCoverageReports(local, reports);
	 * Assert.assertEquals(3, local.list().size()); local.deleteRecursive();
	 * 
	 * }
	 */

}