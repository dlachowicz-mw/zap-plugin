package org.jenkinsci.plugins.zap.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class RiskTest {

	@Test
	public void testNotEquals() {
		Risk r1 = Risk.Medium;
		Risk r2 = Risk.Low;

		assertFalse(r1.equals(r2));
		assertFalse(r2.equals(r1));
	}

	@Test
	public void testEquals() {
		Risk r1 = Risk.High;
		Risk r2 = Risk.High;

		assertTrue(r1.equals(r2));
	}

}
