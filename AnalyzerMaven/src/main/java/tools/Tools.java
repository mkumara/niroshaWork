package tools;

public class Tools {

	public static double Haversine(double lat1, double long1, double lat2,
			double long2) {

		double r = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLong = Math.toRadians(long2 - long1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
        Math.sin(dLong/2) * Math.sin(dLong/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = r * c;

		return d;
	}

}
