/*
 *  The MIT License
 *
 *  Copyright 2010 Sony Ericsson Mobile Communications. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

/**
 * @author Allyn Pierre (Allyn.GreyDeAlmeidaLimaPierre@sonyericsson.com)
 * @author Eduardo Palazzo (Eduardo.Palazzo@sonyericsson.com)
 * @author Mauro Durante (Mauro.DuranteJunior@sonyericsson.com)
 */
package org.jenkinsci.plugins.zap.portlet;

import hudson.model.Job;
import hudson.model.Run;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jenkinsci.plugins.zap.ZapBuildAction;
import org.jenkinsci.plugins.zap.model.Risk;
import org.jenkinsci.plugins.zap.portlet.bean.ZapAlertResultSummary;
import org.jenkinsci.plugins.zap.portlet.utils.Utils;
import org.joda.time.LocalDate;

/**
 * Load data of ZAP alert results used by chart or grid.
 * 
 * @author Patrick Roth
 */
public final class ZapLoadData {

	/**
	 * Private constructor avoiding this class to be used in a non-static way.
	 */
	private ZapLoadData() {
	}

	/**
	 * Get ZAP alert results of all jobs and store into a sorted HashMap by
	 * date.
	 * 
	 * @param jobs
	 *            jobs of Dashboard view
	 * @param daysNumber
	 *            number of days
	 * @return Map The sorted summaries
	 */
	public static Map<LocalDate, ZapAlertResultSummary> loadChartDataWithinRange(
			List<Job> jobs, int daysNumber) {

		Map<LocalDate, ZapAlertResultSummary> summaries = new HashMap<LocalDate, ZapAlertResultSummary>();

		// Get the last build (last date) of the all jobs
		LocalDate lastDate = Utils.getLastDate(jobs);

		// No builds
		if (lastDate == null) {
			return null;
		}

		// Get the first date from last build date minus number of days
		LocalDate firstDate = lastDate.minusDays(daysNumber);

		// For each job, get ZAP alert results according with
		// date range (last build date minus number of days)
		for (Job job : jobs) {

			Run run = job.getLastBuild();

			if (null != run) {
				LocalDate runDate = new LocalDate(run.getTimestamp());

				while (runDate.isAfter(firstDate)) {

					summarize(summaries, run, runDate, job);

					run = run.getPreviousBuild();

					if (null == run) {
						break;
					}

					runDate = new LocalDate(run.getTimestamp());

				}
			}
		}

		// Sorting by date, ascending order
		Map<LocalDate, ZapAlertResultSummary> sortedSummaries = new TreeMap<LocalDate, ZapAlertResultSummary>(
				summaries);

		return sortedSummaries;

	}

	/**
	 * Summarize ZAP alert results.
	 * 
	 * @param summaries
	 *            a Map of ZapAlertResultSummary objects indexed by dates
	 * @param run
	 *            the build which will provide information about the alert
	 *            result
	 * @param runDate
	 *            the date on which the build was performed
	 * @param job
	 *            job from the DashBoard Portlet view
	 */
	private static void summarize(
			Map<LocalDate, ZapAlertResultSummary> summaries, Run run,
			LocalDate runDate, Job job) {

		ZapAlertResultSummary zapAlertResult = getResult(run);

		// Retrieve ZAP information for informed date
		ZapAlertResultSummary zapAlertResultSummary = summaries
				.get(runDate);

		// Consider the last result of each
		// job date (if there are many builds for the same date). If not
		// exists, the ZAP alert data must be added. If exists
		// ZAP alert data for the same date but it belongs to other
		// job, sum the values.
		if (zapAlertResultSummary == null) {
			zapAlertResultSummary = new ZapAlertResultSummary();
			zapAlertResultSummary.addZapResult(zapAlertResult);
			zapAlertResultSummary.setJob(job);
		} else {

			// Check if exists ZAP data for same date and job
			List<ZapAlertResultSummary> listResults = zapAlertResultSummary
					.getAlertResults();
			boolean found = false;

			for (ZapAlertResultSummary item : listResults) {
				if ((null != item.getJob())
						&& (null != item.getJob().getName()) && (null != job)) {
					if (item.getJob().getName().equals(job.getName())) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				zapAlertResultSummary.addZapResult(zapAlertResult);
				zapAlertResultSummary.setJob(job);
			}
		}

		summaries.put(runDate, zapAlertResultSummary);
	}

	/**
	 * Get the ZAP alert result for a specific run.
	 * 
	 * @param run
	 *            a job execution
	 * @return ZapAlertResultSummary the alert result
	 */
	private static ZapAlertResultSummary getResult(Run run) {
		ZapBuildAction zapAction = run
				.getAction(ZapBuildAction.class);

		int high = 0;
		int medium = 0;
		int low = 0;
		int informational = 0;
		int falsePositive = 0;

		if (zapAction != null) {
		}
		return new ZapAlertResultSummary(run.getParent(), high, medium,
				low, informational, falsePositive);
	}

	/**
	 * Summarize the last alert results of all jobs. If a job doesn't include
	 * any alert, add zero.
	 * 
	 * @param jobs
	 *            a final Collection of Job objects
	 * @return ZapAlertResultSummary the result summary
	 */
	public static ZapAlertResultSummary getResultSummary(
			final Collection<Job> jobs) {
		ZapAlertResultSummary summary = new ZapAlertResultSummary();

		for (Job job : jobs) {

			int high = 0;
			int medium = 0;
			int low = 0;
			int informational = 0;
			int falsePositive = 0;

			Run run = job.getLastSuccessfulBuild();

			if (run != null) {

				ZapBuildAction zapAction = job.getLastSuccessfulBuild()
						.getAction(ZapBuildAction.class);

				if (null != zapAction) {
					high += zapAction.getRiskNumber(Risk.High);
					medium += zapAction.getRiskNumber(Risk.Medium);
					low += zapAction.getRiskNumber(Risk.Low);
					informational += zapAction
							.getRiskNumber(Risk.Informational);
					falsePositive += zapAction
							.getRiskNumber(Risk.FalsePositive);
				}
			}
			summary.addZapResult(new ZapAlertResultSummary(job, high,
					medium, low, informational, falsePositive));
		}
		return summary;
	}
}
