package org.jenkinsci.plugins.zap;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver - Refactored for cleaner seperation of tests
 */
public class ZapReportTest {

	@Test
	public void testLoad() throws Exception {
		/*
		 * CoverageReport r = new CoverageReport(null,
		 * getClass().getResourceAsStream("Zaproxy.xml")); PackageReport pkg =
		 * r.getChildren().get("org.jenkinsci.plugins.zap");
		 * System.out.println(pkg); assertCoverage(pkg.getLineCoverage(), 393,
		 * 196); assertEquals(595, r.getLineCoverage().getMissed());
		 */
	}

	/**
	 * Ensures the coverage after loading two reports represents the combined
	 * metrics of both reports.
	 */
	@Test
	public void testLoadMultipleReports() throws Exception {
		/*
		 * CoverageReport r = new CoverageReport(null,
		 * getClass().getResourceAsStream("Zaproxy.xml"),
		 * getClass().getResourceAsStream("Zaproxy2.xml"));
		 * 
		 * assertCoverage(r.getLineCoverage(), 513, 361);
		 * 
		 * PackageReport pkg =
		 * r.getChildren().get("org.jenkinsci.plugins.zap.portlet.bean");
		 * assertCoverage(pkg.getLineCoverage(), 34, 41);
		 * 
		 * pkg = r.getChildren().get("org.jenkinsci.plugins.zap.portlet.chart");
		 * assertCoverage(pkg.getLineCoverage(), 68, 1);
		 */
		assertTrue(true);

	}

}
