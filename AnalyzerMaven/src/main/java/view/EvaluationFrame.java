
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;

import data.Settings;

import view.PodFrame.PopVsDistResults;

/**
 * contains a window that displays all the PODs and allows for changing the
 * settings
 * 
 * @author tamara
 */

public class EvaluationFrame extends JFrame {

	private int numberOfPods;
	private TabbedResult tabber;
	private Settings set;
	
	public  EvaluationFrame(int numberOfPods, Settings set, String prefix) {
		super("Evaluation Results: "+ prefix);
		this.set = set;
		this.numberOfPods = numberOfPods;
		tabber = new TabbedResult(set);
	}

	
	public void init() {

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(new Dimension(800, 650));
		this.setResizable(true);
		Container content = this.getContentPane();
		content.setLayout(new BorderLayout());
		tabber.init();
		content.add(tabber);
		content.setVisible(true);
		this.setVisible(true);

	}
	public void setText(String text){
		tabber.setText(text);
	}

        public void setPODs(int [] pods){
            tabber.setPODs(pods);
        }

	public void setPopulation(int [] population){
		tabber.setPopulation(population);
	}
	public void setHours(double[] hours){
		tabber.setHours(hours);
	}
	public void setBooths(double[] booths){
		tabber.setBooths(booths);
	}
	public void setRate_48(double[] rate){
		tabber.setRate_48(rate);
	}
	
	public void setRate_servingTime(double[] rate) {
		tabber.setRate_servingTime(rate);
	}
	public void setCars(double[] cars){
		tabber.setCars(cars);
	}

        public void setDistanceRange(double [] distRange) {
            tabber.setDistRange(distRange);
        }

        public void setPopVsDistResultSet(PopVsDistResults popVsDist) {
            tabber.setPopVsDist(popVsDist);
        }
        
        public void setPodFrame(PodFrame podFrame) {
            tabber.setPodFrame(podFrame);
        }
}





