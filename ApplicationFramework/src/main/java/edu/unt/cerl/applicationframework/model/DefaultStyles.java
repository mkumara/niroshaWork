
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.applicationframework.model;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class DefaultStyles {

    static StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

    public static Style createDefaultBlockStyle(Color fillColor) {

        Stroke stroke = sf.createStroke(ff.literal(
                Color.BLACK), ff.literal(1.0f), ff.literal(1.0f));

        Fill fill = sf.createFill(
                ff.literal(fillColor),
                ff.literal(0.5));

        Symbolizer sym;
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        Style style = SLD.wrapSymbolizers(sym);
        return style;
    }

    /**
     * This static method is used to create the Color[] array to be passed into
     * the methods such as createClassifiedPointStyle(...). An alternate version
     * of this method should be created which allows color classifications to be
     * loaded from parameter files.
     *
     * @author martyo
     * @param numClasses the number of classes
     * @return an array of Color objects
     */
    public static Color[] getClassFillColors(int numClasses) {
        Color fillColor[];
        fillColor = new Color[numClasses];
        fillColor[0] = new Color(0xF0F9E8);
        fillColor[1] = new Color(0xBAE4BC);
        fillColor[2] = new Color(0x7BCCC4);
        fillColor[3] = new Color(0x43A2CA);
        fillColor[4] = new Color(0x0868AC);
        //      fillColor[5] = new Color(0x00FF00);

        return fillColor;

    }

    /**
     * This static method can be used to create a Style object for a single
     * symbol polygon layer.
     *
     * @author martyo
     * @param outlineOpacity opacity of the line
     * @param outlineColor color of the line
     * @param width width of th line
     * @return a Style object for lines using a single symbol
     */
    public static Style createSingleSymbolPolygonStyle(double outlineOpacity, Color outlineColor, double fillOpacity, Color fillColor, float outlineWidth) {
        //Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(outlineWidth), ff.literal(1.0f));

        Stroke stroke = sf.createStroke(
                ff.literal(outlineColor),
                ff.literal(outlineWidth),
                ff.literal(outlineOpacity));

        Symbolizer sym = sf.createPolygonSymbolizer(stroke, sf.createFill(ff.literal(fillColor), ff.literal(fillOpacity)), null);
        Style style = SLD.wrapSymbolizers(sym);

        return style;
    }

    /**
     * This static method can be used to create a Style object for a single
     * symbol line layer.
     *
     * @author martyo
     * @param outlineOpacity opacity of the line
     * @param outlineColor color of the line
     * @param width width of th line
     * @return a Style object for lines using a single symbol
     */
    public static Style createSingleSymbolLineStyle(double outlineOpacity, Color outlineColor, float width) {
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(width), ff.literal(1.0f));
        Symbolizer sym = sf.createLineSymbolizer(stroke, null);
        Style style = SLD.wrapSymbolizers(sym);

        return style;
    }

    public static Style createSingleSymbolPointStyle(double outlineOpacity, Color outlineColor, double fillOpacity, Color fillColor, float width, float size) {
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(width), ff.literal(1.0f));
        Fill fill = sf.createFill(ff.literal(fillColor), ff.literal(fillOpacity));
        //  Symbolizer sym = sf.createPointSymbolizer(stroke, null);
        // Style style = SLD.wrapSymbolizers(sym);

        Graphic gr = sf.createDefaultGraphic();
        Mark mark = sf.getSquareMark();
        mark.setStroke(stroke);
        mark.setFill(fill);
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(ff.literal(size));

        PointSymbolizer sym = sf.createPointSymbolizer(gr, null);
        Style style = SLD.wrapSymbolizers(sym);

        return style;
    }

    /**
     * This static method can be used to create a Style object for lines
     * classified according to the value in the "class" attribute of each line
     * record. This method was designed to create a reusable way of creating
     * classified line styles for GeoTools
     *
     * @author martyo
     * @param opacity the opacity of the lines
     * @param fillColor an array of type Color[] with the colors for each class
     * of line. The number of elements in this array sets the number of classes.
     * @param width an array of type float[] which sets the width of each class
     * of line. The number of elements in this array must be equal to the number
     * in the fillColor[] array.
     * @param field field is what is used to determine the class each polygon
     * was assigned to. Classes must be 0 through (n-1) for the case of n
     * different classes.
     * @return a Style object for lines classified according to the value in the
     * attribute specified by field
     * @throws CQLException
     */
    public static Style createClassifiedLineStyle(double opacity, Color[] fillColor, float[] width, String field) throws CQLException {
        int numClasses = fillColor.length;

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);

        Stroke stroke[] = new Stroke[numClasses];
        Symbolizer sym[] = new Symbolizer[numClasses];
        Rule rule[] = new Rule[numClasses];

        for (int i = 0; i < numClasses; i++) {
            stroke[i] = sf.createStroke(ff.literal(fillColor[i]), ff.literal(width[i]), ff.literal(1.0f));
            sym[i] = sf.createLineSymbolizer(stroke[i], null);
            rule[i] = styleFactory.createRule();
            rule[i].setFilter(CQL.toFilter(field + " = " + i));
            rule[i].symbolizers().add(sym[i]);
        }
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * This static method can be used to create a Style object for polygons
     * classified according to the value in the "class" attribute of each
     * polygon record. This method was designed to create a reusable way of
     * creating classified polygon styles for GeoTools
     *
     * @author martyo
     * @param opacity the opacity of the polygons
     * @param fillColor an array of type Color[] with the colors for each class
     * of polygon. The number of elements in this array sets the number of
     * classes.
     * @param field is what is used to determine the class each polygon was
     * assigned to. Classes must be 0 through (n-1) for the case of n different
     * classes.
     * @return a Style object for polygons classified according to the value in
     * the attribute specified by field
     */
    public static Style createClassifiedPolygonStyle(double opacity, Color[] fillColor, String field) throws CQLException {
        int numClasses = fillColor.length;

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.black),
                filterFactory.literal(1),
                filterFactory.literal(1));
        Fill fill[] = new Fill[numClasses];
        PolygonSymbolizer sym[] = new PolygonSymbolizer[numClasses];
        Rule rule[] = new Rule[numClasses];

        for (int i = 0; i < numClasses; i++) {
            fill[i] = styleFactory.createFill(
                    filterFactory.literal(fillColor[i]),
                    filterFactory.literal(opacity));
            sym[i] = styleFactory.createPolygonSymbolizer(stroke,
                    fill[i], null);
            rule[i] = styleFactory.createRule();
            rule[i].setFilter(CQL.toFilter(field + " = " + i));
            rule[i].symbolizers().add(sym[i]);
        }

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * This static method can be used to create a Style object for points
     * classified according to the value in the "class" attribute of each point
     * record. This method was designed to create a reusable way of creating
     * classified point styles for GeoTools
     *
     * @author martyo
     * @param opacity the opacity of the points
     * @param fillColor an array of type Color[] with the colors for each class
     * of point. The number of elements in this array sets the number of
     * classes.
     * @param field is what is used to determine the class each point was
     * assigned to. Classes must be 0 through (n-1) for the case of n different
     * classes.
     * @return a Style object for points classified according to the value in
     * the attribute specified by field
     */
    public static Style createClassifiedPointStyle(double opacity, Color[] fillColor, String field) {
        //Color fillColor[] = getClassFillColors(numClasses);

        int numClasses = fillColor.length;

        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));          // opacity

        Fill fill[] = new Fill[numClasses];
        Graphic gr[] = new Graphic[numClasses];
        Mark mark[] = new Mark[numClasses];
        Symbolizer sym[] = new Symbolizer[numClasses];
        Rule rule[] = new Rule[numClasses];

        for (int i = 0; i < numClasses; i++) {
            fill[i] = styleFactory.createFill(
                    filterFactory.literal(fillColor[i]), // color
                    filterFactory.literal(opacity));     // opacity

            gr[i] = styleFactory.createDefaultGraphic();
            gr[i].graphicalSymbols().clear();
            mark[i] = styleFactory.getCircleMark();
            mark[i].setFill(fill[i]);
            mark[i].setStroke(stroke);
            gr[i].graphicalSymbols().add(mark[i]);
            gr[i].setSize(filterFactory.literal(10.0f));
            sym[i] = styleFactory.createPointSymbolizer(gr[i], null);

            rule[i] = styleFactory.createRule();
            try {
                rule[i].setFilter(CQL.toFilter(field + " = " + i));
            } catch (CQLException ex) {
                Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
            }
            rule[i].symbolizers().add(sym[i]);
        }

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public static Style createPointStyle() {
        Graphic gr = sf.createDefaultGraphic();

        Mark mark = sf.getCircleMark();

        mark.setStroke(sf.createStroke(
                ff.literal(Color.BLUE), ff.literal(1)));

        mark.setFill(sf.createFill(ff.literal(Color.CYAN)));

        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(ff.literal(5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PointSymbolizer sym = sf.createPointSymbolizer(gr, null);

        Rule rule = sf.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = sf.createFeatureTypeStyle(new Rule[]{rule});
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public static Style createDefaultPointStyle() {

        Stroke stroke = sf.createStroke(ff.literal(
                Color.RED), ff.literal(1.0f), ff.literal(1.0f));
        Symbolizer sym;
        sym = sf.createPointSymbolizer();
        Style style = SLD.wrapSymbolizers(sym);
        return style;
    }

    public static Style createDefaultBlockStyle() {

        Stroke stroke = sf.createStroke(ff.literal(
                Color.BLACK), ff.literal(1.0f), ff.literal(1.0f));
        Symbolizer sym;
        sym = sf.createLineSymbolizer(stroke, null);
        Style style = SLD.wrapSymbolizers(sym);
        return style;
    }

    public static Style createDefaultRoadStyle() {

        Stroke stroke = sf.createStroke(ff.literal(
                Color.RED), ff.literal(2.0f), ff.literal(1.0f));
        Symbolizer sym;
        sym = sf.createLineSymbolizer(stroke, null);
        Style style = SLD.wrapSymbolizers(sym);
        return style;
    }

    public static Style createDefaultAreaStyle() {

        Stroke stroke = sf.createStroke(ff.literal(
                Colors.BLUE_DARK), ff.literal(2.0f), ff.literal(1.0f));
        Symbolizer sym;
        sym = sf.createLineSymbolizer(stroke, null);
        Style style = SLD.wrapSymbolizers(sym);
        return style;
    }

    public static Style createDefaultHeatMapStyle() {

        // create an outline stroke
        Stroke stroke = getStroke(Color.BLACK, 1, 1);

        // create a partial opaque fill with different intensities
        Rule[] rules = new Rule[5];

        Fill fill = getFill(Colors.BROWN_I1, 1);
        PolygonSymbolizer sym = sf.createPolygonSymbolizer(stroke, fill, null);
        Rule r = getRule("rate < 0.000001", sym);
        rules[0] = r;
        fill = getFill(Colors.BROWN_I2, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.000001 AND rate < 0.011365", sym);
        rules[1] = r;
        fill = getFill(Colors.BROWN_I3, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.011365 AND rate < 0.022858", sym);
        rules[2] = r;
        fill = getFill(Colors.BROWN_I4, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.022858 AND rate < 0.050847", sym);
        rules[3] = r;
        fill = getFill(Colors.BROWN_I5, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.050847", sym);
        rules[4] = r;

        FeatureTypeStyle fts = sf.createFeatureTypeStyle(rules);
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public static Style createDefaultHeatMapStyleWithoutLines() {

        // create an outline stroke
        Stroke stroke = getStroke(Color.BLACK, 1, 1);
        stroke = null;

        // create a partial opaque fill with different intensities
        Rule[] rules = new Rule[5];

        Fill fill = getFill(Colors.BROWN_I1, 1);
        PolygonSymbolizer sym = sf.createPolygonSymbolizer(stroke, fill, null);
        Rule r = getRule("rate < 0.000001", sym);
        rules[0] = r;
        fill = getFill(Colors.BROWN_I2, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.000001 AND rate < 0.011365", sym);
        rules[1] = r;
        fill = getFill(Colors.BROWN_I3, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.011365 AND rate < 0.022858", sym);
        rules[2] = r;
        fill = getFill(Colors.BROWN_I4, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.022858 AND rate < 0.050847", sym);
        rules[3] = r;
        fill = getFill(Colors.BROWN_I5, 1);
        sym = sf.createPolygonSymbolizer(stroke, fill, null);
        r = getRule("rate >= 0.050847", sym);
        rules[4] = r;

        FeatureTypeStyle fts = sf.createFeatureTypeStyle(rules);
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public static Style createPODStyle() throws CQLException {
        System.out.println("In AppFramwork default styles, createpodstyle\n");
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        //  StyleBuilder sb = new StyleBuilder();
        TextSymbolizer textsym = styleFactory.createTextSymbolizer();
        textsym.setFill(styleFactory.createFill(filterFactory.literal(
                Color.BLACK), filterFactory.literal(1)));
        //textsym.setFont(new Font[] {styleFactory.createFont("Arial",12);
        textsym.setLabel(CQL.toExpression("id"));
        textsym.setFont(styleFactory.createFont(filterFactory.literal("Arial"),
                filterFactory.literal("italic"), filterFactory.literal(10),
                filterFactory.literal(10)));
        textsym.setHalo(styleFactory.createHalo(getFill(Color.WHITE, 1), filterFactory.literal(2)));

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));           // opacity

        Fill fill1 = styleFactory.createFill(
                filterFactory.literal(Color.RED), // color
                filterFactory.literal(1));     // opacity

        Graphic gr1 = styleFactory.createDefaultGraphic();
        gr1.graphicalSymbols().clear();
        Mark mark1 = styleFactory.getTriangleMark();
        mark1.setFill(fill1);
        mark1.setStroke(stroke);
        gr1.graphicalSymbols().add(mark1);
        gr1.setSize(filterFactory.literal(17.0f));
        Symbolizer sym1 = styleFactory.createPointSymbolizer(gr1, null);

        Fill fill2 = styleFactory.createFill(
                filterFactory.literal(Color.BLUE), // color
                filterFactory.literal(1));     // opacity

        Graphic gr2 = styleFactory.createDefaultGraphic();
        gr2.graphicalSymbols().clear();
        Mark mark2 = styleFactory.getTriangleMark();
        mark2.setFill(fill2);
        mark2.setStroke(stroke);
        gr2.graphicalSymbols().add(mark2);
        gr2.setSize(filterFactory.literal(17.0f));
        Symbolizer sym2 = styleFactory.createPointSymbolizer(gr2, null);

        Fill fill3 = styleFactory.createFill(
                filterFactory.literal(Color.GRAY), // color
                filterFactory.literal(1));     // opacity

        Graphic gr3 = styleFactory.createDefaultGraphic();
        gr3.graphicalSymbols().clear();
        Mark mark3 = styleFactory.getTriangleMark();
        mark3.setFill(fill3);
        mark3.setStroke(stroke);
        gr3.graphicalSymbols().add(mark3);
        gr3.setSize(filterFactory.literal(17.0f));
        Symbolizer sym3 = styleFactory.createPointSymbolizer(gr3, null);

        Rule rule1 = styleFactory.createRule();
        //rule1.setFilter(CQL.toFilter("(status = 'public' or status = 'Public') and onoff = true"));
        //On and Public = red
        rule1.setFilter(CQL.toFilter("type = 'true' and status = 'true'"));
        rule1.symbolizers().add(sym1);
        rule1.symbolizers().add(textsym);

        Rule rule2 = styleFactory.createRule();
        //On and private = blue
        rule2.setFilter(CQL.toFilter("type = 'false' and status = 'true'"));
        rule2.symbolizers().add(sym2);
        rule2.symbolizers().add(textsym);

        //Off = Grey
        Rule rule3 = styleFactory.createRule();
        rule3.setFilter(CQL.toFilter("status = 'false'"));
        rule3.symbolizers().add(sym3);
        rule3.symbolizers().add(textsym);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
            rule1, rule2, rule3});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    public static Style createCrossingStyle() {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));           // opacity

        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.WHITE), // color
                filterFactory.literal(1));     // opacity

        Graphic gr = styleFactory.createDefaultGraphic();
        gr.graphicalSymbols().clear();
        Mark mark = styleFactory.getCircleMark();
        mark.setFill(fill);
        mark.setStroke(stroke);
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10.0f));
        Symbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        return SLD.wrapSymbolizers(sym);
    }

    public static Style createCrossingStyleWithTraffic() {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

        // one color for each traffic class
        Color c1 = new Color(26, 152, 80);
        Color c2 = new Color(145, 207, 96);
        Color c3 = new Color(217, 239, 139);
        Color c4 = new Color(254, 224, 139);
        Color c5 = new Color(252, 141, 89);
        Color c6 = new Color(215, 48, 39);

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));           // opacity

        // one fill for each traffic class
        Fill fill1 = styleFactory.createFill(
                filterFactory.literal(c1), // color
                filterFactory.literal(1));     // opacity
        Fill fill2 = styleFactory.createFill(
                filterFactory.literal(c2), // color
                filterFactory.literal(1));     // opacity
        Fill fill3 = styleFactory.createFill(
                filterFactory.literal(c3), // color
                filterFactory.literal(1));     // opacity
        Fill fill4 = styleFactory.createFill(
                filterFactory.literal(c4), // color
                filterFactory.literal(1));     // opacity
        Fill fill5 = styleFactory.createFill(
                filterFactory.literal(c5), // color
                filterFactory.literal(1));     // opacity
        Fill fill6 = styleFactory.createFill(
                filterFactory.literal(c6), // color
                filterFactory.literal(1));     // opacity

        // for each class
        Graphic gr1 = styleFactory.createDefaultGraphic();
        gr1.graphicalSymbols().clear();
        Mark mark1 = styleFactory.getCircleMark();
        mark1.setFill(fill1);
        mark1.setStroke(stroke);
        gr1.graphicalSymbols().add(mark1);
        gr1.setSize(filterFactory.literal(10.0f));

        Symbolizer sym1 = styleFactory.createPointSymbolizer(gr1, null);

        Graphic gr2 = styleFactory.createDefaultGraphic();
        gr2.graphicalSymbols().clear();
        Mark mark2 = styleFactory.getCircleMark();
        mark2.setFill(fill2);
        mark2.setStroke(stroke);
        gr2.graphicalSymbols().add(mark2);
        gr2.setSize(filterFactory.literal(10.0f));

        Symbolizer sym2 = styleFactory.createPointSymbolizer(gr2, null);

        Graphic gr3 = styleFactory.createDefaultGraphic();
        gr3.graphicalSymbols().clear();
        Mark mark3 = styleFactory.getCircleMark();
        mark3.setFill(fill3);
        mark3.setStroke(stroke);
        gr3.graphicalSymbols().add(mark3);
        gr3.setSize(filterFactory.literal(10.0f));

        Symbolizer sym3 = styleFactory.createPointSymbolizer(gr3, null);

        Graphic gr4 = styleFactory.createDefaultGraphic();
        gr4.graphicalSymbols().clear();
        Mark mark4 = styleFactory.getCircleMark();
        mark4.setFill(fill4);
        mark4.setStroke(stroke);
        gr4.graphicalSymbols().add(mark4);
        gr4.setSize(filterFactory.literal(10.0f));

        Symbolizer sym4 = styleFactory.createPointSymbolizer(gr4, null);

        Graphic gr5 = styleFactory.createDefaultGraphic();
        gr5.graphicalSymbols().clear();
        Mark mark5 = styleFactory.getCircleMark();
        mark5.setFill(fill5);
        mark5.setStroke(stroke);
        gr5.graphicalSymbols().add(mark5);
        gr5.setSize(filterFactory.literal(10.0f));

        Symbolizer sym5 = styleFactory.createPointSymbolizer(gr5, null);

        Graphic gr6 = styleFactory.createDefaultGraphic();
        gr6.graphicalSymbols().clear();
        Mark mark6 = styleFactory.getCircleMark();
        mark6.setFill(fill6);
        mark6.setStroke(stroke);
        gr6.graphicalSymbols().add(mark6);
        gr6.setSize(filterFactory.literal(10.0f));

        Symbolizer sym6 = styleFactory.createPointSymbolizer(gr6, null);

        Rule rule1 = styleFactory.createRule();
        try {
            rule1.setFilter(CQL.toFilter("class = 0"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule1.symbolizers().add(sym1);

        Rule rule2 = styleFactory.createRule();
        try {
            rule2.setFilter(CQL.toFilter("class = 1"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule2.symbolizers().add(sym2);

        Rule rule3 = styleFactory.createRule();
        try {
            rule3.setFilter(CQL.toFilter("class = 2"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule3.symbolizers().add(sym3);

        Rule rule4 = styleFactory.createRule();
        try {
            rule4.setFilter(CQL.toFilter("class = 3"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule4.symbolizers().add(sym4);

        Rule rule5 = styleFactory.createRule();
        try {
            rule5.setFilter(CQL.toFilter("class = 4"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule5.symbolizers().add(sym5);

        Rule rule6 = styleFactory.createRule();
        try {
            rule6.setFilter(CQL.toFilter("class = 5"));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).log(Level.SEVERE, null, ex);
        }
        rule6.symbolizers().add(sym6);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
            rule1, rule2, rule3, rule4, rule5, rule6});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

    public static Style createRingStyle() throws CQLException,
            IOException {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Color c1 = new Color(224, 236, 244);
        Color c2 = new Color(158, 188, 218);
        Color c3 = new Color(136, 86, 167);

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.black),
                filterFactory.literal(1),
                filterFactory.literal(1));

        //  create a partial opaque fill
        Fill fill1 = styleFactory.createFill(
                filterFactory.literal(c1),
                filterFactory.literal(1));

        Fill fill2 = styleFactory.createFill(
                filterFactory.literal(c2),
                filterFactory.literal(1));

        Fill fill3 = styleFactory.createFill(
                filterFactory.literal(c3),
                filterFactory.literal(1));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym1 = styleFactory.createPolygonSymbolizer(stroke,
                fill1, null);
        PolygonSymbolizer sym2 = styleFactory.createPolygonSymbolizer(stroke,
                fill2, null);
        PolygonSymbolizer sym3 = styleFactory.createPolygonSymbolizer(stroke,
                fill3, null);

        Rule rule1 = styleFactory.createRule();
        rule1.setFilter(CQL.toFilter("ring = 1"));
        rule1.symbolizers().add(sym1);

        Rule rule2 = styleFactory.createRule();
        rule2.setFilter(CQL.toFilter("ring = 2"));
        rule2.symbolizers().add(sym2);

        Rule rule3 = styleFactory.createRule();
        rule3.setFilter(CQL.toFilter("ring = 3"));
        rule3.symbolizers().add(sym3);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
            rule1, rule2, rule3});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public static Style createCoverageStyle() throws CQLException,
            IOException {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Color color[] = {Color.YELLOW, Color.GREEN};

        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.black),
                filterFactory.literal(1),
                filterFactory.literal(1));

        Fill fill[] = new Fill[color.length];
        PolygonSymbolizer sym[] = new PolygonSymbolizer[color.length];
        Rule rule[] = new Rule[color.length];
        String cond[] = new String[color.length];

//        for( int i=0; i<color.length; i++ )
        {
            // This code has been unrolled as condition one is equal and
            // condition two is greater than.

            // Condition one (coverage = 0)
            //  create a partial opaque fill
            int i = 0;
            fill[i] = styleFactory.createFill(
                    filterFactory.literal(color[i]),
                    filterFactory.literal(1));

            /*
             * Setting the geometryPropertyName arg to null signals that we want to
             * draw the default geomettry of features
             */
            sym[i] = styleFactory.createPolygonSymbolizer(stroke,
                    fill[i], null);

            cond[i] = "coverage = 0";

            rule[i] = styleFactory.createRule();
            rule[i].setFilter(CQL.toFilter(cond[i]));
            rule[i].symbolizers().add(sym[i]);

            // Condition two (coverage > 0)
            //  create a partial opaque fill
            i = 1;
            fill[i] = styleFactory.createFill(
                    filterFactory.literal(color[i]),
                    filterFactory.literal(1));

            /*
             * Setting the geometryPropertyName arg to null signals that we want to
             * draw the default geomettry of features
             */
            sym[i] = styleFactory.createPolygonSymbolizer(stroke,
                    fill[i], null);

            cond[i] = "coverage > 0";

            rule[i] = styleFactory.createRule();
            rule[i].setFilter(CQL.toFilter(cond[i]));
            rule[i].symbolizers().add(sym[i]);
        }

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
    
    public static Style createResourceStyle() throws CQLException {
        return createPointStyle();
    }


    public static Style createtype2VulnStyle(int numColors) throws CQLException {
        // one color for each traffic class
        /*
         fillColor[0] = new Color(26, 152, 80);
         fillColor[1] = new Color(145, 207, 96);
         fillColor[2] = new Color(217, 239, 139);
         fillColor[3] = new Color(254, 224, 139);
         fillColor[4] = new Color(252, 141, 89);
         */
        /*
         c8dbed	200,219,237
         b3cce4	179,204,228
         9dbdcb	157,189,203
         88add2	136,173,210
         739ec9	115,158,201
         5d8ec0	93,142,192
         487fb7	72,127,183
         326fae	50,111,174
         1d60a5	29,96,165
         08519c	8,81,156    
         */
        Color[] fillColor = new Color[10];
        fillColor[0] = new Color(200,219,237);
        fillColor[1] = new Color(179,204,228);
        fillColor[2] = new Color(157,189,203);
        fillColor[3] = new Color(136,173,210);
        fillColor[4] = new Color(115,158,201);
        fillColor[5] = new Color(93,142,192);
        fillColor[6] = new Color(72,127,183);
        fillColor[7] = new Color(50,111,174);
        fillColor[8] = new Color(29,96,165);
        fillColor[9] = new Color(8,81,156);

        Color[] fillColorGrad = new Color[numColors];
        Color color1 = new Color(200, 219, 237);
        Color color2 = new Color(8, 81, 156);
        
        for(int i=0; i<numColors; i++) {
            int r = (i*1/numColors)*color1.getRed() + ((numColors-i)*1/numColors)*color2.getRed();
            int b = (i*1/numColors)*color1.getBlue() + ((numColors-i)*1/numColors)*color2.getBlue();;
            int g = (i*1/numColors)*color1.getGreen() + ((numColors-i)*1/numColors)*color2.getGreen();;
            fillColorGrad[i] = new Color(r,g,b);
        }

        return createClassifiedPolygonStyle(1, fillColor, "class");

    }

    public static Stroke getStroke(Color c, double thickness, double intensity) {
        System.out.println("DefaultStyles : getStroke");
        Stroke stroke = sf.createStroke(
                ff.literal(c),
                ff.literal(thickness),
                ff.literal(intensity));
        return stroke;
    }

    public static Fill getFill(Color c, double intensity) {
        System.out.println("DefaultStyles : getFill");
        Fill fill = sf.createFill(
                ff.literal(c),
                ff.literal(intensity));
        return fill;
    }

    public static Rule getRule(String cql, Symbolizer sym) {
        System.out.println("DefaultStyles : getRule " + cql);
        Rule rule = sf.createRule();
        try {
            rule.setFilter(CQL.toFilter(cql));
        } catch (CQLException ex) {
            Logger.getLogger(DefaultStyles.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        rule.symbolizers().add(sym);
        return rule;
    }
}
