package org.jenkinsci.plugins.zap.util;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import hudson.FilePath;
import org.jenkinsci.plugins.zap.model.Alert;

import org.junit.Test;

public class ZapParserTest {

	private static ZapParser parser = new ZapParser();
	private static File path = new File(
			"./src/test/resources/test-job/workspace/test/target/zap/");
	private static FilePath fp = new FilePath(path);
	private static FilePath file = new FilePath(fp, "zap-report.xml");

	@Test
	public void testParse() throws InvocationTargetException {
		Collection<Alert> alerts = parser.parse(file);
		assertEquals("Should have 4 alerts", 4, alerts.size());

		System.out.println(alerts);
		// check the URLs
		for (Alert a : alerts) {
			if ("Cross Site Request Forgery".equals(a.getType())) {
				assertEquals(1, a.getUrls().size());
			} else if ("X-Frame-Options header not set".equals(a.getType())) {
				assertEquals(40, a.getUrls().size());

			} else if ("X-Content-Type-Options header missing".equals(a
					.getType())) {
				assertEquals(40, a.getUrls().size());
			} else if ("Cookie set without HttpOnly flag".equals(a.getType())) {
				assertEquals(1, a.getUrls().size());
			}
		}
	}
	
	public static Collection<Alert> getAlerts() throws InvocationTargetException {
		return parser.parse(file);
	}
}
