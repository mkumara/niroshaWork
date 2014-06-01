package action;

import java.awt.event.ActionEvent;
import java.awt.Desktop;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import view.EvaluationFrame;
import view.PodFrame;
import view.PodPanel;
import view.SettingsFrame;
import action.ExtensionFileFilter;
public class MenuListener implements ActionListener {

	private PodFrame owner;

	public MenuListener(PodFrame owner) {
		super();
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("evaluate")) {
			PodPanel[] panels = owner.getPanels();
			double[] cars = new double[panels.length];
			double[] booths = new double[panels.length];
			double[] hours = new double[panels.length];
                        int[] pods = new int[panels.length];
			double rate_48[] = new double[panels.length];
			double rate_servingTime[] = new double[panels.length];
			String result = "";
			for (int i = 0; i < panels.length; i++) {
				result += panels[i].calculateEstimate();
				cars[i] = panels[i].getCars();
				booths[i] = panels[i].getBooths();
				hours[i] = panels[i].getHours();
				rate_48[i] = panels[i].getRate_48();
				rate_servingTime[i] = panels[i].getRate_servingTime();
                                pods[i] = panels[i].getId();
			}
			EvaluationFrame evaluation = new EvaluationFrame(5, this.owner
					.getSettings(), owner.getPrefix());
			evaluation.setText(result);
			evaluation.setPopulation(owner.getPopulation());
			evaluation.setHours(hours);
			evaluation.setBooths(booths);
			evaluation.setRate_48(rate_48);
			evaluation.setRate_servingTime(rate_servingTime);
			evaluation.setCars(cars);
                        evaluation.setPODs(pods);
                        evaluation.setDistanceRange(owner.getDistanceRange());
                        //evaluation.setPopVsDistResultSet(owner.getPopVsDistResultSet());
                        evaluation.setPopVsDistResultSet(owner.getPopVsDistResults());
                        evaluation.setPodFrame(owner);
			evaluation.init();

		} else if (e.getActionCommand().equals("open")) {
			JFileChooser c = new JFileChooser();
			FileFilter filter = new ExtensionFileFilter("POD data files","pdat");
			c.setFileFilter(filter);
			int returnVal = c.showOpenDialog(owner);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				owner.readValues(c.getSelectedFile());
			}
		} else if (e.getActionCommand().equals("save")) {
			JFileChooser c = new JFileChooser();
			FileFilter filter = new ExtensionFileFilter("POD data files","pdat");
			c.setFileFilter(filter);
			int returnVal = c.showSaveDialog(owner);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				owner.saveValues(c.getSelectedFile());
			}
		} else if (e.getActionCommand().equals("fill")) {
			owner.fill();
		} else if (e.getActionCommand().equals("settings")) {
			new SettingsFrame(owner);
		} else if (e.getActionCommand().equals("help")) {
			Desktop d = Desktop.getDesktop();
			try {
				File f = new File("help.html");
				try {
					d.browse(new URI("file://"+f.getAbsolutePath()));
				} catch (URISyntaxException e1) {
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this.owner,
						"Problem loading URL", "Help",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (e.getActionCommand().equals("info")) {
			String[] msg = new String[4];
			msg[0] = "PODAnalyzer 1.0";
			msg[1] = "2010";
			msg[2] = "CERL";
			msg[3] = "University of North Texas";
			JOptionPane.showMessageDialog(this.owner, msg, "Info",
					JOptionPane.INFORMATION_MESSAGE);
		} else if(e.getActionCommand().equals("quit")){
			System.exit(0);
		} else if(e.getActionCommand().equals("default")){
			File f = new File("default.pdat");
			owner.readValues(f);
		}

	}

}
