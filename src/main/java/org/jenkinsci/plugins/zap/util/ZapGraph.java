package org.jenkinsci.plugins.zap.util;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;

import org.jenkinsci.plugins.zap.ZapBuildAction;
import org.jenkinsci.plugins.zap.model.Risk;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Class drawing the alert trend graph.
 * 
 * @author Patrick Roth
 */
public class ZapGraph {

	private ZapBuildAction action = null;

	public ZapGraph(ZapBuildAction action) {
		this.action = action;
	}

	/**
	 * Generates the graph that shows the alert trend upto the given build.
	 */
	public void doGraph(StaplerRequest req, StaplerResponse rsp)
			throws IOException {
		if (ChartUtil.awtProblemCause != null) {
			// not available. send out error message
			rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
			return;
		}

		AbstractBuild<?, ?> build = action.getBuild();
		Calendar t = build.getTimestamp();

		String w = Util.fixEmptyAndTrim(req.getParameter("width"));
		String h = Util.fixEmptyAndTrim(req.getParameter("height"));
		int width = (w != null) ? Integer.valueOf(w) : 500;
		int height = (h != null) ? Integer.valueOf(h) : 200;

		new GraphImpl(action, t, width, height) {

			// @Override
			protected DataSetBuilder<Risk, NumberOnlyBuildLabel> createDataSet(
					ZapBuildAction action) {
				DataSetBuilder<Risk, NumberOnlyBuildLabel> dsb = new DataSetBuilder<Risk, NumberOnlyBuildLabel>();

				for (ZapBuildAction a = action; a != null; a = a
						.getPreviousResult()) {
					NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(
							a.getBuild());

					dsb.add(a.getRiskNumber(Risk.High),
							Risk.High, label);
					dsb.add(a.getRiskNumber(Risk.Medium),
							Risk.Medium, label);
					dsb.add(a.getRiskNumber(Risk.Low),
							Risk.Low, label);
					dsb.add(a.getRiskNumber(Risk.Informational),
							Risk.Informational, label);
					dsb.add(a.getRiskNumber(Risk.FalsePositive),
							Risk.FalsePositive, label);
					
				}

				return dsb;
			}
		}.doPng(req, rsp);
	}

	public Api getApi() {
		return new Api(this);
	}

	private abstract class GraphImpl extends Graph {

		private ZapBuildAction action;

		public GraphImpl(ZapBuildAction action, Calendar timestamp,
				int defaultW, int defaultH) {
			super(timestamp, defaultW, defaultH);
			this.action = action;
		}

		protected abstract DataSetBuilder<Risk, NumberOnlyBuildLabel> createDataSet(
				ZapBuildAction action);

		protected JFreeChart createGraph() {
			final CategoryDataset dataset = createDataSet(action).build();
			final JFreeChart chart = ChartFactory.createLineChart(null, // chart
																		// title
					null, // unused
					"", // range axis label
					dataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					true, // tooltips
					false // urls
					);

			// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

			final LegendTitle legend = chart.getLegend();
			legend.setPosition(RectangleEdge.RIGHT);

			chart.setBackgroundPaint(Color.white);

			final CategoryPlot plot = chart.getCategoryPlot();

			// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0,
			// 5.0));
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(null);
			plot.setRangeGridlinesVisible(true);
			plot.setRangeGridlinePaint(Color.black);

			CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
			plot.setDomainAxis(domainAxis);
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
			domainAxis.setLowerMargin(0.0);
			domainAxis.setUpperMargin(0.0);
			domainAxis.setCategoryMargin(0.0);

			final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			// if (line != null) {
			// rangeAxis
			// .setUpperBound(line.getCovered() > line.getMissed() ? line
			// .getCovered() + 5 : line.getMissed() + 5);
			// }
			rangeAxis.setLowerBound(0);

			final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
					.getRenderer();

			renderer.setSeriesPaint(0, Color.red);
			renderer.setSeriesPaint(1, Color.orange);
			renderer.setSeriesPaint(2, Color.yellow);
			renderer.setSeriesPaint(3, Color.blue);
			renderer.setSeriesPaint(4, Color.green);

			renderer.setSeriesItemLabelPaint(0, Color.red);
			renderer.setSeriesItemLabelPaint(1, Color.orange);
			renderer.setSeriesItemLabelPaint(2, Color.yellow);
			renderer.setSeriesItemLabelPaint(3, Color.blue);
			renderer.setSeriesItemLabelPaint(4, Color.green);

			renderer.setSeriesFillPaint(0, Color.red);
			renderer.setSeriesFillPaint(1, Color.orange);
			renderer.setSeriesFillPaint(2, Color.yellow);
			renderer.setSeriesFillPaint(3, Color.blue);
			renderer.setSeriesFillPaint(4, Color.green);

			renderer.setBaseStroke(new BasicStroke(4.0f));
			//ColorPalette.apply(renderer);

			// crop extra space around the graph
			plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

			return chart;
		}
	}
}
