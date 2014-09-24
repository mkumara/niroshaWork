
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package view;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

public class SufficientHoursBarRenderer extends BarRenderer {
	public SufficientHoursBarRenderer() {
		super();
	}

	public Paint getItemPaint(int x_row, int x_col) {
		CategoryDataset l_jfcDataset = getPlot().getDataset();
		String l_rowKey = (String) l_jfcDataset.getRowKey(x_row);
		String l_colKey = (String) l_jfcDataset.getColumnKey(x_col);
		double l_value = l_jfcDataset.getValue(l_rowKey, l_colKey)
				.doubleValue();
		if (l_value < 48) {
			return Color.green;
		} else {
			return Color.red;
		}
	}
}