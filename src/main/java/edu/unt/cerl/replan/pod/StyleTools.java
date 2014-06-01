package edu.unt.cerl.replan.pod;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

public class StyleTools {

    public Style createPODStyle() throws CQLException {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        StyleBuilder sb = new StyleBuilder();
//http://osgeo-org.1803224.n2.nabble.com/how-to-get-geotools-to-draw-overlapping-features-td4899752.html
        //create label
        // http://www.mail-archive.com/geotools-gt2-users@lists.sourceforge.net/msg09762.html
        //  TextSymbolizer textsym = (TextSymbolizer) styleFactory.createTextSymbolizer(styleFactory.
        //          createFill((Expression) Color.BLACK),(org.geotools.styling.Font[]) new Font[]{styleFactory.createFont("Lucida Sans",
        //              10), styleFactory.createFont("Arial", 10)}, styleFactory.createHalo(), styleFactory.
        //          literalExpression("X"), new PointPlacementImpl(), "GEOMETRY");
        TextSymbolizer textsym = styleFactory.createTextSymbolizer();
        textsym.setFill(styleFactory.createFill(filterFactory.literal(
                Color.BLACK), filterFactory.literal(1)));
        //textsym.setFont(new Font[] {styleFactory.createFont("Arial",12);
        textsym.setLabel(CQL.toExpression("id"));
        textsym.setFont(sb.createFont(new Font("Arial", Font.BOLD, 14)));
        textsym.setHalo(sb.createHalo(Color.white, 2));

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

        Rule rule1 = styleFactory.createRule();
        rule1.setFilter(CQL.toFilter("(type = 'public' or type = 'Public') and onoff = true"));
        rule1.symbolizers().add(sym1);
        rule1.symbolizers().add(textsym);

        Rule rule2 = styleFactory.createRule();
        rule2.setFilter(CQL.toFilter("(type = 'Corporate' or type = 'corporate') and onoff = true"));
        rule2.symbolizers().add(sym2);
        rule2.symbolizers().add(textsym);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule1,rule2});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Create a style for displaying PODs (red triangle)
     *
     * @return
     */
    public Style createPODStyleOld() throws CQLException {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        StyleBuilder sb = new StyleBuilder();
//http://osgeo-org.1803224.n2.nabble.com/how-to-get-geotools-to-draw-overlapping-features-td4899752.html
        //create label
        // http://www.mail-archive.com/geotools-gt2-users@lists.sourceforge.net/msg09762.html
        //  TextSymbolizer textsym = (TextSymbolizer) styleFactory.createTextSymbolizer(styleFactory.
        //          createFill((Expression) Color.BLACK),(org.geotools.styling.Font[]) new Font[]{styleFactory.createFont("Lucida Sans",
        //              10), styleFactory.createFont("Arial", 10)}, styleFactory.createHalo(), styleFactory.
        //          literalExpression("X"), new PointPlacementImpl(), "GEOMETRY");
        TextSymbolizer textsym = styleFactory.createTextSymbolizer();
        textsym.setFill(styleFactory.createFill(filterFactory.literal(
                Color.BLACK), filterFactory.literal(1)));
        //textsym.setFont(new Font[] {styleFactory.createFont("Arial",12);
        textsym.setLabel(CQL.toExpression("id"));
        textsym.setFont(sb.createFont(new Font("Arial", Font.BOLD, 16)));
        textsym.setHalo(sb.createHalo(Color.white, 2));



        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));           // opacity

        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.RED), // color
                filterFactory.literal(1));     // opacity

        Graphic gr = styleFactory.createDefaultGraphic();
        gr.graphicalSymbols().clear();
        Mark mark = styleFactory.getTriangleMark();
        mark.setFill(fill);
        mark.setStroke(stroke);
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(15.0f));
        Symbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        return SLD.wrapSymbolizers(sym, textsym);
    }

    public Style createCrossingStyleWithTraffic() throws CQLException {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

        // one color for each traffic class
        Color c1 = new Color(26, 152, 80);
        Color c2 = new Color(106, 229, 106);
        Color c3 = new Color(243, 253, 37);
        Color c4 = new Color(253, 164, 37);
        Color c5 = new Color(253, 111, 37);
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
        rule1.setFilter(CQL.toFilter("class = 0"));
        rule1.symbolizers().add(sym1);

        Rule rule2 = styleFactory.createRule();
        rule2.setFilter(CQL.toFilter("class = 1"));
        rule2.symbolizers().add(sym2);

        Rule rule3 = styleFactory.createRule();
        rule3.setFilter(CQL.toFilter("class = 2"));
        rule3.symbolizers().add(sym3);

        Rule rule4 = styleFactory.createRule();
        rule4.setFilter(CQL.toFilter("class = 3"));
        rule4.symbolizers().add(sym4);

        Rule rule5 = styleFactory.createRule();
        rule5.setFilter(CQL.toFilter("class = 4"));
        rule5.symbolizers().add(sym5);

        Rule rule6 = styleFactory.createRule();
        rule6.setFilter(CQL.toFilter("class = 5"));
        rule6.symbolizers().add(sym6);


        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule1, rule2, rule3, rule4, rule5, rule6});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

    public Style createCrossingStyle() {
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

    public Style createCatchmentStyle() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.black),
                filterFactory.literal(3),
                filterFactory.literal(1));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.lightGray),
                filterFactory.literal(0.5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
                fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

        public Style createCatchmentStyle2() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK),
                filterFactory.literal(3),
                filterFactory.literal(1));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.lightGray),
                filterFactory.literal(0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
                fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

            public Style createCatchmentStyle3() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLUE),
                filterFactory.literal(3),
                filterFactory.literal(1));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.lightGray),
                filterFactory.literal(0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
                fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

    public Style createBlockStyle() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.black),
                filterFactory.literal(1),
                filterFactory.literal(1));

        // create a partial opaque fill
        //  Fill fill = styleFactory.createFill(
        //        filterFactory.literal(Color.CYAN),
        //      filterFactory.literal(0.5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
                null, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;

    }

    public Style createRoadStyle() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        //outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.red),
                filterFactory.literal(1), // width of displayed roads
                filterFactory.literal(1));

        // create a partial opaque fill
        // Fill fill = styleFactory.createFill(
        //       filterFactory.literal(Color.CYAN),
        //     filterFactory.literal(0.5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
                null, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{
                    rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public Style createRingStyle(FeatureSource fs) throws CQLException,
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
}
