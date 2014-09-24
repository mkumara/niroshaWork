
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package censusBlocks;

public class Address {
	
	public String street;
	public String city;
	public int zip;

	
	public Address(String street, String city, int zip) {
		this.street = street;
		this.city = city;
		this.zip = zip;
	}
}
