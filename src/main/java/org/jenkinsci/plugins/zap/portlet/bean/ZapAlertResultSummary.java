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
package org.jenkinsci.plugins.zap.portlet.bean;

import hudson.model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary of the ZAP alert result.
 */
public class ZapAlertResultSummary {

	/**
	 * The related job.
	 */
	private Job job;

	/**
	 * High risk alert number.
	 */
	private int highScore;

	/**
	 * Medium risk alert number.
	 */
	private int mediumScore;

	/**
	 * Low risk alert number.
	 */
	private int lowScore;

	/**
	 * Informational risk alert number.
	 */
	private int informationalScore;

	/**
	 * False positive risk alert number.
	 */
	private int falsePositiveScore;

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public int getMediumScore() {
		return mediumScore;
	}

	public void setMediumScore(int mediumScore) {
		this.mediumScore = mediumScore;
	}

	public int getLowScore() {
		return lowScore;
	}

	public void setLowScore(int lowScore) {
		this.lowScore = lowScore;
	}

	public int getInformationalScore() {
		return informationalScore;
	}

	public void setInformationalScore(int informationalScore) {
		this.informationalScore = informationalScore;
	}

	public int getFalsePositiveScore() {
		return falsePositiveScore;
	}

	public void setFalsePositiveScore(int falsePositiveScore) {
		this.falsePositiveScore = falsePositiveScore;
	}

	private List<ZapAlertResultSummary> alertResults = new ArrayList<ZapAlertResultSummary>();

	/**
	 * Default Constructor.
	 */
	public ZapAlertResultSummary() {
	}

	/**
	 * Constructor with parameters.
	 * 
	 */
	public ZapAlertResultSummary(Job job, int highScore, int mediumScore,
			int lowScore, int informationalScore, int falsePositiveScore) {
		this.job = job;
		this.highScore = highScore;
		this.mediumScore = mediumScore;
		this.lowScore = lowScore;
		this.informationalScore = informationalScore;
		this.falsePositiveScore = falsePositiveScore;
	}

	/**
	 * Add a alert result.
	 * 
	 * @param alertResult
	 *            a alert result
	 * @return ZapAlertResultSummary summary of the ZAP alert result
	 */
	public ZapAlertResultSummary addZapResult(
			ZapAlertResultSummary alertResult) {

		this.setHighScore(this.getHighScore() + alertResult.getHighScore());
		this.setMediumScore(this.getMediumScore()
				+ alertResult.getMediumScore());
		this.setLowScore(this.getLowScore() + alertResult.getLowScore());
		this.setInformationalScore(this.getInformationalScore()
				+ alertResult.getInformationalScore());
		this.setFalsePositiveScore(this.getFalsePositiveScore()
				+ alertResult.getFalsePositiveScore());

		getAlertResults().add(alertResult);

		return this;
	}

	/**
	 * Get list of ZapAlertResultSummary objects.
	 * 
	 * @return List a List of ZapCoverageResult objects
	 */
	public List<ZapAlertResultSummary> getZapResults() {
		return this.getAlertResults();
	}

	/**
	 * @return Job a job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * @param job
	 *            a job
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * @return a list of alert results
	 */
	public List<ZapAlertResultSummary> getAlertResults() {
		return alertResults;
	}

	/**
	 * @param alertResults
	 *            the list of alert results to set
	 */
	public void setCoverageResults(List<ZapAlertResultSummary> alertResults) {
		this.alertResults = alertResults;
	}
}
