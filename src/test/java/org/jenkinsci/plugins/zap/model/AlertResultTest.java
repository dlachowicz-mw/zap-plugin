package org.jenkinsci.plugins.zap.model;

import static org.junit.Assert.assertEquals;
import hudson.FilePath;
import org.jenkinsci.plugins.zap.util.ZapParser;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

/**
 * @author Patrick Roth
 */
public class AlertResultTest {
	private ZapParser parser = new ZapParser();
	private File path = new File(
			"./src/test/resources/test-job/workspace/test/target/zap/");
	private FilePath fp = new FilePath(path);
	private FilePath file = new FilePath(fp, "zap-report.xml");

	@Test
	public void testAddAlerts() throws Exception {
		Collection<Alert> alerts = parser.parse(file);
		AlertResult results = new AlertResult();
		results.addAlerts(alerts);

		assertEquals("Checking high risk", 0, results.getRiskNumber(Risk.High));
		assertEquals("Checking medium risk", 1,
				results.getRiskNumber(Risk.Medium));
		assertEquals("Checking low risk", 2, results.getRiskNumber(Risk.Low));
		assertEquals("Checking informational risk", 1,
				results.getRiskNumber(Risk.Informational));
		assertEquals("Checking false positives", 0,
				results.getRiskNumber(Risk.FalsePositive));

		assertEquals("Checking reliability warnings", 4,
				results.getReliabilityNumber(Reliability.Warning));
	}

}
