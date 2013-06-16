package org.jenkinsci.plugins.zap;

import static org.junit.Assert.*;

import org.junit.Test;

public class ZapHealthReportThresholdsTest {

	@Test
	public void testZaproxyHealthReportThresholds_AllPositiveAndValid() {
		String maxHigh = "10";
		String maxMedium = "3";
		String maxLow = "27";
		String minHigh = "5";
		String minMedium = "2";
		String minLow = "17";

		ZapHealthReportThresholds th = new ZapHealthReportThresholds(maxHigh,
				maxMedium, maxLow, null, minHigh, minMedium, minLow, null);

		assertEquals("Check max high risk threshold", Integer.valueOf(maxHigh).intValue(),
				th.getMaxHighRisk());
		assertEquals("Check max medium risk threshold",
				Integer.valueOf(maxMedium).intValue(), th.getMaxMediumRisk());
		assertEquals("Check max low risk threshold", Integer.valueOf(maxLow).intValue(),
				th.getMaxLowRisk());
		assertEquals("Check min high risk threshold", Integer.valueOf(minHigh).intValue(),
				th.getMinHighRisk());
		assertEquals("Check min medium risk threshold",
				Integer.valueOf(minMedium).intValue(), th.getMinMediumRisk());
		assertEquals("Check min low risk threshold", Integer.valueOf(minLow).intValue(),
				th.getMinLowRisk());
		assertTrue("This threshold should  be valid", th.isValid());
	}

	@Test
	public void testZaproxyHealthReportThresholds_AllPositiveAndInvalid() {
		String maxHigh = "10";
		String maxMedium = "3";
		String maxLow = "27";
		String minHigh = "15";
		String minMedium = "5";
		String minLow = "37";

		ZapHealthReportThresholds th = new ZapHealthReportThresholds(maxHigh,
				maxMedium, maxLow, null, minHigh, minMedium, minLow, null);

		assertEquals("Check max high risk threshold", Integer.valueOf(maxHigh).intValue(),
				th.getMaxHighRisk());
		assertEquals("Check max medium risk threshold", Integer.valueOf(maxMedium).intValue(),
				th.getMaxMediumRisk());
		assertEquals("Check max low risk threshold",Integer.valueOf(maxLow).intValue(), th.getMaxLowRisk());
		assertEquals("Check min high risk threshold", Integer.valueOf(minHigh).intValue(),
				th.getMinHighRisk());
		assertEquals("Check min medium risk threshold", Integer.valueOf(minMedium).intValue(),
				th.getMinMediumRisk());
		assertEquals("Check min low risk threshold", Integer.valueOf(minLow).intValue(), th.getMinLowRisk());
		assertFalse("This threshold should  not be valid", th.isValid());
	}

	@Test
	public void testZaproxyHealthReportThresholds_AllNegative() {
		String maxHigh = "-10";
		String maxMedium = "-3";
		String maxLow = "-27";
		String minHigh = "-5";
		String minMedium = "-2";
		String minLow = "-17";

		ZapHealthReportThresholds th = new ZapHealthReportThresholds(maxHigh,
				maxMedium, maxLow, null, minHigh, minMedium, minLow, null);

		assertEquals("Check max high risk threshold", Integer.valueOf(maxHigh).intValue(),
				th.getMaxHighRisk());
		assertEquals("Check max medium risk threshold", Integer.valueOf(maxMedium).intValue(),
				th.getMaxMediumRisk());
		assertEquals("Check max low risk threshold", Integer.valueOf(maxLow).intValue(), th.getMaxLowRisk());
		assertEquals("Check min high risk threshold", Integer.valueOf(minHigh).intValue(),
				th.getMinHighRisk());
		assertEquals("Check min medium risk threshold", Integer.valueOf(minMedium).intValue(),
				th.getMinMediumRisk());
		assertEquals("Check min low risk threshold", Integer.valueOf(minLow).intValue(), th.getMinLowRisk());
		assertFalse("This threshold should  not be valid", th.isValid());
	}

}
