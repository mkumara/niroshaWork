package edu.unt.cerl.replan.view;

import edu.unt.cerl.replan.REPLAN;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.locale.LocaleUtils;
import org.geotools.swing.tool.CursorTool;

/**
 * Class LocationSelector
 */
public class LocationSelector extends CursorTool {

    //
    // Fields
    //
    /** The tool name */
    public static final String TOOL_NAME = LocaleUtils.getValue("CursorTool", "Info");
    /** Tool tip text */
    public static final String TOOL_TIP = LocaleUtils.getValue("CursorTool", "InfoTooltip");
    /** Cursor */
    public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    /** Cursor hotspot coordinates */
    public static final Point CURSOR_HOTSPOT = new Point(0, 0);
    /** Icon for the control */
    public static final String ICON_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    private Cursor cursor;
    private SelectedCoords selCoords;
    private Boolean transportationVuln;
  
    //
    // Constructors
    //
    public class SelectedCoords extends Observable {

        public DirectPosition2D locationSelected;

//        public SelectedCoords() {
//            this.addObserver(REPLAN.getMainFrame().getTabs().getSelectedScenario());
//        }

        public void change() {
            System.out.println("Setting changed!");
            setChanged();
        }

        public void clearChange() {
            clearChanged();
        }
    }

    public LocationSelector(int which) {
        this();
        if (which == 1) {
            transportationVuln = true;
        } else {
            transportationVuln = false;
        }
        }

    public Boolean isTransportationVuln() {
        return transportationVuln;
    }
    
    
    public LocationSelector() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        ImageIcon cursorIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
        cursor = tk.createCustomCursor(cursorIcon.getImage(), CURSOR_HOTSPOT, TOOL_TIP);
        selCoords = new SelectedCoords();
    }

    //
    // Methods
    //
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        DirectPosition2D pos = ev.getWorldPos();
        setLocationSelected(pos);
        REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().toFront();
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor updatedCursor) {
        this.cursor = updatedCursor;
    }

    private void setLocationSelected(DirectPosition2D pos) {
        System.out.println("x = " + pos.x + " and y = " + pos.y);
        selCoords.locationSelected = new DirectPosition2D(pos.x, pos.y);
        selCoords.change();

        selCoords.notifyObservers();
        selCoords.deleteObservers();
        //this.notify();
    }

    //
    // Accessor methods
    //
    public SelectedCoords getSelCoords() {
        return selCoords;
    }

    public DirectPosition2D getLocationSelected() {

        return selCoords.locationSelected;

    }
    //
    // Other methods
    //
}
