package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgis.MultiPolygon;
import org.postgis.Point;

public class Partitioner {

    private int tempPod = -1;
    private int pod = -1;
    private double distance;
    private DBInteractions db;
    private Map<Integer, CensusBlock> blockMap = null;
    private MultiPolygon[] podsArray = null;
    private ScenarioState s;
    int returnValue = 0;

    public Partitioner(DBInteractions db, Map m, ScenarioState s) {
        this.s = s;
        this.db = db;
        this.tempPod = new Integer((String) m.get(DefaultConstants.NUM_PODS)) + 1;
        this.pod = 1;
        this.podsArray =
                new MultiPolygon[new Integer((String) m.get(DefaultConstants.NUM_PODS))];
        //System.out.println("Partitioner 34");
        MultiPolygon outline = db.joinCensusBlocks();
        //System.out.println("Partitioner 36");
        db.initPodsTo(tempPod++);
        //System.out.println("Partitioner 38");
        CensusBlock[] blocks = this.createBlockList(db.getCentroids(),
                db.getPopulationAsMap());
        //System.out.println("Partitioner 41");
        this.blockMap = this.createBlockMap(blocks);
        //System.out.println("Partitioner 43");
        this.recursiveStep(new Integer((String) m.get(DefaultConstants.NUM_PODS)),
                blocks, outline);
        //System.out.println("Partitioner 46");
        db.createAndWriteCatchmentTable(this.podsArray, s.getWorkingCopyName());
        //System.out.println("Partitioner 48");
        db.createAndInitPodTable(s.getWorkingCopyName());
        //System.out.println("Partitioner 50");

        s.setCatchmentAreasGiven(true);
        //System.out.println("Partitioner 53");

        returnValue = 1;

    }

    private Map<Integer, CensusBlock> createBlockMap(CensusBlock[] a) {
        Map<Integer, CensusBlock> tempMap = new HashMap();
        for (int i = 0; i < a.length; i++) {
            tempMap.put(new Integer(a[i].getId()), a[i]);
        }
        return tempMap;
    }

    private List<CensusBlock> createList(CensusBlock[] arr) {
        List<CensusBlock> l = new LinkedList();
        for (int i = 0; i < arr.length; i++) {
            l.add(arr[i]);
        }
        return l;
    }

    private CensusBlock[] createArray(List<CensusBlock> l) {
        CensusBlock[] a = new CensusBlock[l.size()];
        Iterator<CensusBlock> it = l.iterator();
        for (int i = 0; i < a.length; i++) {
            a[i] = it.next();
        }
        return a;
    }

    private CensusBlock[] createBlockList(ResultSet centroids,
            Map<Integer, Integer> population) {
        CensusBlock[] blocks = null;
        try {
            centroids.last();
            blocks = new CensusBlock[centroids.getRow()];
            centroids.beforeFirst();
            while (centroids.next()) {
                Integer id = new Integer(centroids.getInt("logrecno"));
                Point p = new Point(centroids.getString("centroid"));
                int num_pop = population.get(id);
                blocks[centroids.getRow() - 1] = new CensusBlock(id, p, num_pop);
                //   System.out.println(centroids.getRow() + " " + blocks[centroids.getRow()]);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Partitioner.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        // System.out.println("in create block list " + blocks[0]);
        return blocks;
    }

    /*
     * Framework for the recursive algorithm. Performs the necessary recursion
     * calling appropriate functions at each recursive step
     */
    private void recursiveStep(int podsToPlace, CensusBlock[] b,
            MultiPolygon outline) {

        // get the points of the polygon outline and find the two most distant points
        Point[] points = this.retrievePointsFromPolygon(outline);
        /*temporary function call to shift starting points*/
        //Point[] result = this.shiftTwoFurthestPoints(points);
        Point[] result = this.findTwoFurthestPoints(points);
        System.out.println(result[0]);
        System.out.println(result[1]);

        Point p1 = result[0];
        Point p2 = result[1];

        List<CensusBlock> l1 = this.createList(
                this.distanceBasedQuicksort(p1, b));
        List<CensusBlock> l2 = this.createList(
                this.distanceBasedQuicksort(p2, b));
        this.checkListsIfReverted(this.createList(this.distanceBasedQuicksort(p1,
                b)), this.createList(this.distanceBasedQuicksort(p2, b)));

        int population1 = 0;
        int population2 = 0;

        List<CensusBlock> result1 = new LinkedList();
        List<CensusBlock> result2 = new LinkedList();

        int part1 = podsToPlace / 2;
        int part2 = podsToPlace - part1;
        int pod1 = - 1, pod2 = -1;
        if (part1 == 1) {
            pod1 = this.pod++;
        } else {
            pod1 = this.tempPod++;
        }
        if (part2 == 1) {
            pod2 = this.pod++;
        } else {
            pod2 = this.tempPod++;
        }
        double ratio = (double) part1 / (double) part2;


        while (l1.size() > 0) {
            //    System.out.println("size = " + l1.size() + " and " + l2.size());
            //   System.out.println("population = " + population1 + " and " + population2);
            //   System.out.println("---");
            if (part2 * population1 < part1 * population2) {
                CensusBlock nextBlock = l1.get(0);
                population1 += nextBlock.getPopulation();
                nextBlock.setPod(pod1);
                l1.remove(nextBlock);
                l2.remove(nextBlock);
                result1.add(nextBlock);
            } else {
                CensusBlock nextBlock = l2.get(0);
                population2 += nextBlock.getPopulation();
                nextBlock.setPod(pod2);
                l1.remove(nextBlock);
                l2.remove(nextBlock);
                result2.add(nextBlock);
            }
        }
        System.out.println(population1 + " and " + population2);

        db.writePodsToDBFaster(blockMap, pod1, pod2, s.getWorkingCopyName());

        System.out.println("done writing pods to db");

        MultiPolygon outline1 = db.joinCensusBlocks(pod1, s.getWorkingCopyName());
        MultiPolygon outline2 = db.joinCensusBlocks(pod2, s.getWorkingCopyName());

        if (part1 > 1) {
            System.out.println("size of l1 = " + result1.size());
            this.recursiveStep(part1, this.createArray(result1), outline1);
        } else {
            this.podsArray[pod1 - 1] = outline1;
        }
        if (part2 > 1) {
            System.out.println("size of result2 = " + result2.size());
            CensusBlock[] test = this.createArray(result2);
            System.out.println("size of array = " + test.length);
            this.recursiveStep(part2, this.createArray(result2), outline2);
        } else {
            this.podsArray[pod2 - 1] = outline2;
        }
    }

    private Point[] retrievePointsFromPolygon(MultiPolygon m) {
        Point[] points = new Point[m.numPoints()];
        System.out.println(m.numPoints());
        for (int i = 0; i < m.numPoints(); i++) {
            points[i] = m.getPoint(i);
        }
        return points;
    }

    private Point[] shiftTwoFurthestPoints(Point[] points) {
        Point[] result = new Point[2];
        double maxDist = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                double newDist = this.getDistance(points[i], points[j]);
                if (newDist > maxDist) {
                    maxDist = newDist;
                    result[0] = points[i];
                    result[1] = points[j];
                }
            }
        }
        //find two farthest points p1, p2
        //ST_MakeLine(p1, p2);
        // p1= SETSRID(MAKEPOINT(result1.x, reslult1.y), 4326)
        //Find mid point of line (p1, p2)
        //ST_Line_Interpolate_Point(ST_MakeLine(p1, p2), 0.5)
        //ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), 0.5) )
        //find endpoints of line
        // float newPStartX = ST_X(ST_StartPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), 0.5) )));
        // float newPStartY = ST_Y(ST_StartPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), 0.5) )));
        //float newPEndX = ST_X(ST_EndPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), 0.5) )));
        //float newPEndY = ST_Y(ST_EndPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT(result[0].x, reslult[0].y), 4326), SETSRID(MAKEPOINT(result[1].x, reslult[1].y), 4326)), 0.5) )));
        System.out.println("Points before shifting: X: "+ result[0].getX() + " Y: "+ result[0].getY() + "\n" );
        System.out.println("Points before shifting: X: "+ result[1].getX() + " Y: "+ result[1].getY() + "\n" );
        float newPStartX = 0;
        float newPStartY = 0;
        float newPEndX = 0;
        float newPEndY = 0;

        try {
            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String q1 = "SELECT ST_X(ST_StartPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 3*pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 0.5) ))) AS res;";
            System.out.println(q1+"\n");
            ResultSet rs1 = stmt.executeQuery(q1);
            while (rs1.next()) {
                newPStartX = rs1.getFloat("res");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String q1 = "SELECT ST_Y(ST_StartPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 3*pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 0.5) ))) AS res;";
            ResultSet rs1 = stmt.executeQuery(q1);
            while (rs1.next()) {
                newPStartY = rs1.getFloat("res");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String q1 = "SELECT ST_X(ST_EndPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 3*pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 0.5) ))) AS res;";
            ResultSet rs1 = stmt.executeQuery(q1);
            while (rs1.next()) {
                newPEndX = rs1.getFloat("res");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String q1 = "SELECT ST_Y(ST_EndPoint(ST_Rotate(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 3*pi()/4, ST_Line_Interpolate_Point(ST_MakeLine(SETSRID(MAKEPOINT("+result[0].x+", "+result[0].y+"), 4326), SETSRID(MAKEPOINT("+result[1].x+", "+result[1].y+"), 4326)), 0.5) ))) AS res;";
            ResultSet rs1 = stmt.executeQuery(q1);
            while (rs1.next()) {
                newPEndY = rs1.getFloat("res");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }





        Point newPStart = new Point(newPStartX, newPStartY);
        Point newPEnd = new Point(newPEndX, newPEndY);
        result[0] = newPStart;
        result[1] = newPEnd;

        System.out.println("Points after shifting: X: "+ result[0].getX() + " Y: "+ result[0].getY() + "\n" );
        System.out.println("Points after shifting: X: "+ result[1].getX() + " Y: "+ result[1].getY() + "\n" );

        System.out.println("maxDist = " + maxDist);
        this.distance = maxDist;
        return result;
    }

    private Point[] findTwoFurthestPoints(Point[] points) {
        Point[] result = new Point[2];
        double maxDist = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                double newDist = this.getDistance(points[i], points[j]);
                if (newDist > maxDist) {
                    maxDist = newDist;
                    result[0] = points[i];
                    result[1] = points[j];
                }
            }
        }
        System.out.println("maxDist = " + maxDist);
        this.distance = maxDist;
        return result;
    }

    public double getDistance(Point p1, Point p2) {
        double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y,
                2));
        return dist;
    }

    private CensusBlock[] distanceBasedQuicksort(Point p, CensusBlock[] arr) {
        distanceBasedQuickSort(p, arr, 0, arr.length - 1);
        return arr;
    }

    private void distanceBasedQuickSort(Point p, CensusBlock[] arr, int left,
            int right) {

        int index = quicksortPartition(p, arr, left, right);

        if (left < index - 1) {
            distanceBasedQuickSort(p, arr, left, index - 1);
        }

        if (index < right) {
            distanceBasedQuickSort(p, arr, index, right);
        }

    }

    private int quicksortPartition(Point p, CensusBlock[] arr, int left,
            int right) {

        int i = left, j = right;
        CensusBlock tmp;
        Point pivot = arr[(left + right) / 2].getCentroid();

        while (i <= j) {
            while (getDistance(p, arr[i].getCentroid()) < getDistance(p, pivot)) {
                i++;
            }

            while (getDistance(p, arr[j].getCentroid()) > getDistance(p, pivot)) {
                j--;
            }

            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        ;
        return i;
    }

    private void checkListsIfReverted(List<CensusBlock> l1, List<CensusBlock> l2) {
        System.out.println("CHECKING FOR REVERTED LISTS");
        for (int i = 0; i < 10; i++) {
            System.out.println("===================");
            System.out.println(l1.get(i));
            System.out.println("---");
            System.out.println(l2.get(l1.size() - i - 1));
            System.out.println("===================");
        }
    }

    public int getReturnValue() {
        return returnValue;
    }
}
