package edu.unt.cerl.replan.model;

import edu.unt.cerl.replan.controller.ChangeState;
import edu.unt.cerl.replan.controller.db.PODQueries;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class keeps the List of PODs and provides methods for accessing the POD
 * objects It also provides functionality for adding and deleting PODs used by
 * the controller.
 *
 * This class is concerned with maintaining the List of POD objects. The
 * controller is concerned with keeping this list up to date.
 */
public class PODList extends Observable {

    // private LinkedList pods2;
    //private LinkedList <Integer>pods_fid2id;
    private LinkedHashMap pods;
    private HashMap<Integer, Integer> podIdtoFid;
    private int next_fid; // managed by this class, a particular POD's fid never changes. This will result in gaps in fid numbers.
    private int which_pod_id_updated;
    private boolean firstPodEver;
    ChangeState change;
    PODChangedMessage message;
    
    public PODList() {
        pods = new LinkedHashMap(); //may need to call constructor specifying default capacity and load factor
        podIdtoFid = new HashMap<Integer, Integer>();
        next_fid = 0;

        firstPodEver = false;
        //pods_fid2id = new LinkedList<Integer>();
    }

    ;

    public POD access_POD_by_id(int id) {
        return (POD) pods.get(get_pod_fid(id));
    }

    public POD access_POD(int fid) {
        //System.out.println("access_POD pods = " + pods.toString());
        return (POD) pods.get(fid);
    }

    /*
     * public POD access_POD_by_id(int i){ return id; }
     */
    /**
     * This method accepts and integer id. It traverses the list of PODs looking
     * for this id. Once it has found it, it returns the associated fid. If the
     * id is not found, the method returns -1.
     *
     * @param id the id of the POD we are looking for
     * @return the fid of the POD whose id matches the parameter passed
     */
    public int get_pod_fid(int id) {

        if(podIdtoFid.get(id) == null) {
            return -1;
        } else {
            return podIdtoFid.get(id);
        }
//        int current_id = 0;
//        //System.out.println("unt.edu.cerl.replan.view.PODList: Do I make it here?");
//        Set set = pods.entrySet();
//        Iterator i = set.iterator();
//        while (i.hasNext()) {
//            Map.Entry me = (Map.Entry) i.next();
//            //System.out.println("searching for id:" + id + "   fid:" + me.getKey() + "   id:" + get_pod_id(((POD) me.getValue()).get_fid()));
//            // current_id = get_pod_id(((POD) me.getValue()).get_fid());
//            //if (current_id == id) {
//            //if (get_pod_id(((POD) me.getValue()).get_fid()) == id)
//            if (((POD) me.getValue()).get_id() == id) {
//                //System.out.println("Found it!    " + me.getKey());
//                //return Integer.parseInt((String) me.getKey());
//                return ((POD) me.getValue()).get_fid();
//            }
//        }
//
//        return -1;
    }

    public boolean isEmpty() {
        if (pods.size() == 0) {
            return true;
        }
        return false;

    }

    public int get_pod_id(int fid) {
        return ((POD) pods.get(fid)).get_id();
    }

    public int get_number_of_pods() {
        return pods.size();
    }

    public void set_next_fid(int value) {
        next_fid = value;
    }

    // This will move to PODList_Controller
    public void update_pod_test(int id, int fid, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) {

        ((POD) pods.get(fid)).update_POD(name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        which_pod_id_updated = fid;


        change = new ChangeState(2, fid);
    }

    public void update_pod(int id, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {

        ((POD) pods.get(get_pod_fid(id))).update_POD(name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        which_pod_id_updated = id;
        

        change = new ChangeState(2, get_pod_fid(id));
        PODQueries.update_POD(get_pod_fid(id), name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        message = new PODChangedMessage(true, false, false, true, false, get_pod_fid(id));
        this.setChanged();
        this.notifyObservers();

    }
    
    // needs to be rewritten to pass several messages
    public int getUpdatedPod() {
        return which_pod_id_updated;
    }

    public POD add_pod(ScenarioState state, String name, String address, String city, String zip,
            double longitude, double latitude, Boolean type, Boolean status,
            String comments, int numBooths) throws SQLException {
//pods_fid2id.add(next_fid);
        pods.put(next_fid, new POD((pods.size() + 1), next_fid, name, address,
                city, zip, longitude, latitude, type, status, comments, numBooths));
        podIdtoFid.put((pods.size()), next_fid);
        change = new ChangeState(1, next_fid);
        if (next_fid == 0) {
            firstPodEver = true;
        } else {
            firstPodEver = false;
        }
        next_fid++;

        // ChangeState message = new ChangeState(0,0);
        //   this.message = message;

        //PODQueries.addNewPOD("new POD name", "", "", "00000", 0.0, 0.0, true, true, "", 1);
        PODQueries.addNewPOD(state, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        message = new PODChangedMessage(true, firstPodEver, true, false, false, -999);
        this.setChanged();
        
        if (longitude != 0.0 && latitude != 0.0) {
            this.notifyObservers();
        }
        return (this.access_POD(next_fid - 1));

    }
    
    public POD add_pod_dontNotify(ScenarioState state, String name, String address, String city,
            String zip, double longitude, double latitude, Boolean type,
            Boolean status, String comments, int numBooths)
            throws SQLException {
//pods_fid2id.add(next_fid);
        pods.put(next_fid, new POD((pods.size() + 1), next_fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths));
        podIdtoFid.put((pods.size()), next_fid);
        change = new ChangeState(1, next_fid);
        if (next_fid == 0) {
            firstPodEver = true;
        } else {
            firstPodEver = false;
        }
        next_fid++;

        // ChangeState message = new ChangeState(0,0);
        //   this.message = message;
        
        //PODQueries.addNewPOD("new POD name", "", "", "00000", 0.0, 0.0, true, true, "", 1);
        PODQueries.addNewPOD(state, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        message = new PODChangedMessage(true, firstPodEver, true, false, false, -999);
//        this.setChanged();
//        this.notifyObservers();
        return (this.access_POD(next_fid - 1));

    }

    public POD delete_pod(int fid) throws SQLException {

        System.out.println("Deleting POD with fid=" + fid);
        int id_deleted = get_pod_id(fid);
        PODQueries.delete_POD(fid);
        message = new PODChangedMessage(true, false, false, false, true, fid);
        this.setChanged();
        this.notifyObservers();

        POD deletedPOD = (POD) pods.remove(fid);
        podIdtoFid.remove(id_deleted);

        POD temp = new POD(); // throw this object away
        /*
         * note: PODs will always be added at the end of the list. The list is
         * ordered by fid. This places the id's in order as well. Therefore, id
         * will be placement in list, starting at 1.
         */

        Collection c = pods.values();
        Iterator itr = c.iterator();


        while (itr.hasNext()) { // if there is another element
            temp = ((POD) itr.next());
            System.out.println("Traversing List: " + temp.get_fid());

            if (temp.get_fid() > fid) { // if this POD has a greater fid, then it must also have a greater id
                int id_toDecrement = temp.get_id();
                int temp_fid = temp.get_fid();
                temp.decrement_id();
                id_toDecrement--;
                podIdtoFid.put(id_toDecrement, temp_fid);

                System.out.println("Greater than " + temp.get_fid() + " " + temp.get_id());
                //System.out.println("decrementing id: "+((POD) itr.next()).decrement_id()); // decrement the POD's id
            }
        }


        change = new ChangeState(1, fid);
        return deletedPOD;
    }

    public PODChangedMessage getMessage() {
        return message;
    }

    public void setList(Map list) {
        pods = (LinkedHashMap) list;
        Iterator it = list.keySet().iterator();
        while(it.hasNext()) {
            int fid = (Integer)it.next();
            POD pod = (POD)list.get(fid);
            int id = pod.get_id();
            podIdtoFid.put(id, fid);
        }
        next_fid = list.size();
        firstPodEver = false;
    }

    public void setListChanged() {
        System.out.println("setListChanged");
        this.setChanged();
        this.notifyObservers();
    }
    
    
    
    public void update_pod_dontNotify_2 (int id, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
        ((POD) pods.get(get_pod_fid(id))).update_POD(name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        which_pod_id_updated = id;
        change = new ChangeState(2, get_pod_fid(id));
        PODQueries.update_POD(get_pod_fid(id), name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        message = new PODChangedMessage(true, false, false, true, false, get_pod_fid(id));
    }
    
    
    public POD update_pod_dontNotify(int fid, int id, String name, String address,
            String city, String zip, double longitude, double latitude, Boolean type,
            Boolean status, String comments, int numBooths)
            throws SQLException {
//pods_fid2id.add(next_fid);
        pods.put(fid, new POD((pods.size() + 1), fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths));
        podIdtoFid.put(pods.size(), fid);
        change = new ChangeState(1, fid);
        if (fid == 0) {
            firstPodEver = true;
        } else {
            firstPodEver = false;
        }

        // ChangeState message = new ChangeState(0,0);
        //   this.message = message;

        //PODQueries.addNewPOD("new POD name", "", "", "00000", 0.0, 0.0, true, true, "", 1);
/*
         * public static void update_POD(int fid, int id, String name, String
         * address, String city, String zip, double longitude, double latitude,
         * Boolean type, Boolean status, String comments, int numBooths) throws
         * SQLException {
         */
        PODQueries.update_POD(fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
        message = new PODChangedMessage(true, firstPodEver, true, false, false, -999);
//        this.setChanged();
//        this.notifyObservers();
        return (this.access_POD(next_fid - 1));

    }
}
