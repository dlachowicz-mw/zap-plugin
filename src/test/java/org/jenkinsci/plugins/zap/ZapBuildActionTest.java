package org.jenkinsci.plugins.zap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Logger;

import hudson.FilePath;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.zap.model.Risk;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Kohsuke Kawaguchi
 * @author Patrick Roth
 */
public class ZapBuildActionTest extends AbstractZapTestBase {

	private BuildListener listener = null;

	@Before
	public void init() {
		listener = mock(BuildListener.class);
		PrintStream ps = mock(PrintStream.class);
		Mockito.when(listener.getLogger()).thenReturn(ps);
	}

	@Test
	public void testLoadReport1() throws Exception {
		File path = new File("./src/test/resources/test-job/workspace/test/target/zap/");
		ZapBuildAction ba = ZapBuildAction.load(null, null,
				new ZapHealthReportThresholds("0","0","0","0","0","0","0","0"), 
				listener,
				new FilePath(path));

		assertEquals("Checking high risk", 0, ba.getRiskNumber(Risk.High));
		assertEquals("Checking medium risk", 1, ba.getRiskNumber(Risk.Medium));
		assertEquals("Checking low risk", 2, ba.getRiskNumber(Risk.Low));
		assertEquals("Checking informational risk", 1,
				ba.getRiskNumber(Risk.Informational));
		assertEquals("Checking false positive risk", 0,
				ba.getRiskNumber(Risk.FalsePositive));
	}

	/*
	 * @Test public void testLoadReport2() throws Exception { ZaproxyBuildAction
	 * r = ZaproxyBuildAction.load(null,null, new
	 * ZaproxyHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0,
	 * 0), getClass().getResourceAsStream("Zaproxy2.xml")); assertEquals(76,
	 * r.clazz.getPercentage()); assertEquals(41, r.line.getPercentage());
	 * assertCoverage(r.clazz, 9, 28); assertCoverage(r.method, 122, 116);
	 * assertCoverage(r.line, 513, 361); assertCoverage(r.branch, 224, 66);
	 * assertCoverage(r.instruction, 2548, 1613); assertCoverage(r.complexity,
	 * 246, 137); assertEquals(
	 * "Coverage: Classes 9/28 (76%). Methods 122/116 (49%). Lines 513/361 (41%). Branches 224/66 (23%). Instructions 2548/1613 (39%)."
	 * , r.getBuildHealth().getDescription()); }
	 * 
	 * @Test public void testLoadMultipleReports() throws Exception {
	 * ZaproxyBuildAction r = ZaproxyBuildAction.load(null,null, new
	 * ZaproxyHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0,
	 * 0), getClass().getResourceAsStream("Zaproxy.xml"),
	 * getClass().getResourceAsStream("Zaproxy2.xml")); assertEquals(76,
	 * r.clazz.getPercentage()); assertEquals(41, r.line.getPercentage());
	 * assertCoverage(r.clazz, 9, 28); assertCoverage(r.method, 122, 116);
	 * assertCoverage(r.line, 513, 361); assertCoverage(r.branch, 224, 66);
	 * assertCoverage(r.instruction, 2548, 1613); assertCoverage(r.complexity,
	 * 246, 137); assertEquals(
	 * "Coverage: Classes 9/28 (76%). Methods 122/116 (49%). Lines 513/361 (41%). Branches 224/66 (23%). Instructions 2548/1613"
	 * + " (39%).", r.getBuildHealth().getDescription()); }
	 */
}
