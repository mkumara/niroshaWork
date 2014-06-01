package tools;


import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import view.PodFrame;

public class PODAnalysis {

    public static void main(String args[]) throws ClassNotFoundException,
            MalformedURLException {
        try {
            System.out.println("args.length"+args.length+"\n");
            String user = args[0];
            String scenarioName = args[1];
            Properties jdbc = new Properties();
            DBInfo dbInf = new DBInfo();
            Map<String, String> m = dbInf.getParams();
            Map<String,String> tables = dbInf.getTableParams();
            jdbc.put("user", m.get("user"));
            jdbc.put("password", m.get("password"));
            jdbc.put("role", m.get("role"));
            jdbc.put("schema", user);
            String url =
                    "jdbc:postgresql://" + m.get("host") + ":" + m.get("port") +
                    "/" + m.get("database");
            Class.forName("org.postgresql.Driver");


            Connection c = DriverManager.getConnection(url, jdbc);
            new PodFrame(user + ".workingcpy_" + scenarioName, c,tables);
        } catch (SQLException ex) {
            Logger.getLogger(PODAnalysis.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.err.println("Error with Connection in PODAnalyzer");
        }
    }

    public void startAnalyzer(String [] args) throws ClassNotFoundException,
            MalformedURLException {
        try {
            System.out.println("args.length"+args.length+"\n");
            String user = args[0];
            String scenarioName = args[1];
            Properties jdbc = new Properties();
            DBInfo dbInf = new DBInfo();
            Map<String, String> m = dbInf.getParams();
            Map<String,String> tables = dbInf.getTableParams();
            jdbc.put("user", m.get("user"));
            jdbc.put("password", m.get("password"));
            jdbc.put("role", m.get("role"));
            jdbc.put("schema", user);
            String url =
                    "jdbc:postgresql://" + m.get("host") + ":" + m.get("port") +
                    "/" + m.get("database");
            Class.forName("org.postgresql.Driver");


            Connection c = DriverManager.getConnection(url, jdbc);
            new PodFrame(user + ".workingcpy_" + scenarioName, c,tables);
        } catch (SQLException ex) {
            Logger.getLogger(PODAnalysis.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.err.println("Error with Connection in PODAnalyzer");
        }
    }
}
