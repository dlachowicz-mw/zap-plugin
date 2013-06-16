package org.jenkinsci.plugins.zap;

import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;

import org.apache.maven.project.MavenProject;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test for Project configuration.
 * 
 * @author Patrick Roth
 */
public class ZapConfigSubmitTest extends HudsonTestCase {

	@Test
	public void test() throws Exception {
		WebClient client = new WebClient();
		client.setThrowExceptionOnFailingStatusCode(false);

		MavenModuleSet mp = createMavenProject();
		HtmlPage p = client.goTo(mp.getUrl() + "/configure");
		HtmlForm f = p.getFormByName("config");

		System.out.println(p.asXml());

		HtmlButton addPostBuildActionButton = f.getButtonByCaption("Add post-build action");
		HtmlPage addPostBuildActionPage = addPostBuildActionButton.click();
		System.out.println("_______________________________________");
		System.out.println("***************************************");
		System.out.println(addPostBuildActionPage.asXml());
		
//		ZaproxyPublisher publisher = (ZaproxyPublisher) fp
//				.getPublisher(ZaproxyPublisher.DESCRIPTOR);
		Object postbuilders = mp.getPostbuilders();

		// assertEquals("", publisher.includes);

	}

	// @Test
	// @Ignore
	// public void testIncludeIsSet() throws Exception {
	/*
	 * WebClient client = new WebClient();
	 * client.setThrowExceptionOnFailingStatusCode(false);
	 * 
	 * FreeStyleProject fp = createFreeStyleProject(); HtmlPage p =
	 * client.goTo(fp.getUrl() + "/configure"); HtmlForm f =
	 * p.getFormByName("config");
	 * 
	 * f.getInputByName("hudson-plugins-Zaproxy-ZaproxyPublisher").setChecked
	 * (true); f.getInputByName("Zaproxy.includes").setValueAttribute("***");
	 * submit(f);
	 * 
	 * ZaproxyPublisher publisher = (ZaproxyPublisher)
	 * fp.getPublisher(ZaproxyPublisher.DESCRIPTOR);
	 * 
	 * assertEquals("***", publisher.includes);
	 */
	// }

	// @Test
	// @Ignore
	// public void testHealthReportDefaultMaxValue() throws Exception {
	/*
	 * WebClient client = new WebClient();
	 * client.setThrowExceptionOnFailingStatusCode(false);
	 * 
	 * FreeStyleProject fp = createFreeStyleProject(); HtmlPage p =
	 * client.goTo(fp.getUrl() + "/configure"); HtmlForm f =
	 * p.getFormByName("config");
	 * 
	 * f.getInputByName("hudson-plugins-Zaproxy-ZaproxyPublisher").setChecked
	 * (true);
	 * f.getInputByName("ZaproxyHealthReports.maxClass").setValueAttribute ("");
	 * f.getInputByName("ZaproxyHealthReports.maxMethod").setValueAttribute
	 * (""); f.getInputByName("ZaproxyHealthReports.maxLine").setValueAttribute
	 * ("");
	 * f.getInputByName("ZaproxyHealthReports.maxBranch").setValueAttribute
	 * ("");
	 * f.getInputByName("ZaproxyHealthReports.maxInstruction").setValueAttribute
	 * ("");
	 * f.getInputByName("ZaproxyHealthReports.maxComplexity").setValueAttribute
	 * (""); submit(f);
	 * 
	 * ZaproxyPublisher publisher = (ZaproxyPublisher)
	 * fp.getPublisher(ZaproxyPublisher.DESCRIPTOR);
	 * ZaproxyHealthReportThresholds thresholds = publisher.healthReports;
	 * 
	 * assertEquals(100, thresholds.getMaxClass()); assertEquals(70,
	 * thresholds.getMaxMethod()); assertEquals(70, thresholds.getMaxLine());
	 * assertEquals(70, thresholds.getMaxBranch()); assertEquals(70,
	 * thresholds.getMaxInstruction()); assertEquals(70,
	 * thresholds.getMaxComplexity());
	 */
	// assertTrue(true);
	// }

	// @Test
	// @Ignore
	// public void testHealthReportMaxValue() throws Exception {
	/*
	 * WebClient client = new WebClient();
	 * client.setThrowExceptionOnFailingStatusCode(false);
	 * 
	 * FreeStyleProject fp = createFreeStyleProject(); HtmlPage p =
	 * client.goTo(fp.getUrl() + "/configure"); HtmlForm f =
	 * p.getFormByName("config");
	 * 
	 * f.getInputByName("hudson-plugins-Zaproxy-ZaproxyPublisher").setChecked
	 * (true);
	 * f.getInputByName("ZaproxyHealthReports.maxClass").setValueAttribute
	 * ("8");
	 * f.getInputByName("ZaproxyHealthReports.maxMethod").setValueAttribute
	 * ("9"); f.getInputByName("ZaproxyHealthReports.maxLine").setValueAttribute
	 * ("10");
	 * f.getInputByName("ZaproxyHealthReports.maxBranch").setValueAttribute
	 * ("11");
	 * f.getInputByName("ZaproxyHealthReports.maxInstruction").setValueAttribute
	 * ("12");
	 * f.getInputByName("ZaproxyHealthReports.maxComplexity").setValueAttribute
	 * ("13"); submit(f);
	 * 
	 * ZaproxyPublisher publisher = (ZaproxyPublisher)
	 * fp.getPublisher(ZaproxyPublisher.DESCRIPTOR);
	 * ZaproxyHealthReportThresholds thresholds = publisher.healthReports;
	 * 
	 * assertEquals(8, thresholds.getMaxClass()); assertEquals(9,
	 * thresholds.getMaxMethod()); assertEquals(10, thresholds.getMaxLine());
	 * assertEquals(11, thresholds.getMaxBranch()); assertEquals(12,
	 * thresholds.getMaxInstruction()); assertEquals(13,
	 * thresholds.getMaxComplexity());
	 */
	// }

	// @Test
	// @Ignore
	// public void testHealthReportMinValue() throws Exception {
	/*
	 * WebClient client = new WebClient();
	 * client.setThrowExceptionOnFailingStatusCode(false);
	 * 
	 * FreeStyleProject fp = createFreeStyleProject(); HtmlPage p =
	 * client.goTo(fp.getUrl() + "/configure"); HtmlForm f =
	 * p.getFormByName("config");
	 * 
	 * f.getInputByName("hudson-plugins-Zaproxy-ZaproxyPublisher").setChecked
	 * (true);
	 * f.getInputByName("ZaproxyHealthReports.minClass").setValueAttribute
	 * ("1");
	 * f.getInputByName("ZaproxyHealthReports.minMethod").setValueAttribute
	 * ("2"); f.getInputByName("ZaproxyHealthReports.minLine").setValueAttribute
	 * ("3");
	 * f.getInputByName("ZaproxyHealthReports.minBranch").setValueAttribute
	 * ("11");
	 * f.getInputByName("ZaproxyHealthReports.minInstruction").setValueAttribute
	 * ("12");
	 * f.getInputByName("ZaproxyHealthReports.minComplexity").setValueAttribute
	 * ("13"); submit(f);
	 * 
	 * ZaproxyPublisher publisher = (ZaproxyPublisher)
	 * fp.getPublisher(ZaproxyPublisher.DESCRIPTOR);
	 * ZaproxyHealthReportThresholds thresholds = publisher.healthReports;
	 * 
	 * assertEquals(1, thresholds.getMinClass()); assertEquals(2,
	 * thresholds.getMinMethod()); assertEquals(3, thresholds.getMinLine());
	 * assertEquals(11, thresholds.getMinBranch()); assertEquals(12,
	 * thresholds.getMinInstruction()); assertEquals(13,
	 * thresholds.getMinComplexity());
	 */
	// }

}
