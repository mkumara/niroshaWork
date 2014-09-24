
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

import javax.swing.JMenuBar;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import action.MenuListener;

public class PodMenu extends JMenuBar {

	private MenuListener listener;

	public PodMenu(MenuListener listener) {
		super();
		this.listener = listener;
		init();
	}

	private void init() {
		JMenu menu_file = new JMenu("File");
		JMenu menu_edit = new JMenu("Edit");
		JMenu menu_help = new JMenu("Help");

		JMenuItem item_open = new JMenuItem("Open...");
		JMenuItem item_save = new JMenuItem("Save...");
		JMenuItem item_evaluate = new JMenuItem("Evaluate!");
		JMenuItem item_fill = new JMenuItem("Fill");
		JMenuItem item_info = new JMenuItem("Info");
		JMenuItem item_settings = new JMenuItem("Settings...");
		JMenuItem item_help = new JMenuItem("Help");
		JMenuItem item_default = new JMenuItem("Default values");
		JMenuItem item_quit = new JMenuItem("Exit");

		item_open.setActionCommand("open");
		item_save.setActionCommand("save");
		item_evaluate.setActionCommand("evaluate");
		item_fill.setActionCommand("fill");
		item_info.setActionCommand("info");
		item_settings.setActionCommand("settings");
		item_help.setActionCommand("help");
		item_default.setActionCommand("default");
		item_quit.setActionCommand("quit");

		item_open.addActionListener(listener);
		item_save.addActionListener(listener);
		item_evaluate.addActionListener(listener);
		item_fill.addActionListener(listener);
		item_info.addActionListener(listener);
		item_settings.addActionListener(listener);
		item_help.addActionListener(listener);
		item_default.addActionListener(listener);
		item_quit.addActionListener(listener);

		this.add(menu_file);
		this.add(menu_edit);
		this.add(menu_help);

		//menu_file.add(item_open);
		//menu_file.add(item_save);
		//menu_file.add(item_default);
		//menu_file.addSeparator();
		menu_file.add(item_evaluate);
		menu_file.addSeparator();
		menu_file.add(item_quit);
		menu_help.add(item_help);
		menu_help.add(item_info);
		menu_edit.add(item_settings);
	}

}
