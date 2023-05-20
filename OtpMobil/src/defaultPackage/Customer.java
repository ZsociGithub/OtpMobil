package defaultPackage;

import java.util.HashMap;

/**
 * Egy ügyfelet a neve és címe alapján azonosítunk be.
 * @author Zsolt
 *
 */
public class Customer {
	
	//key: webshop Id, value: customer Id, mindkettő maximum 20 karakter hosszú lehet
	private HashMap<String, String> webshopAndCustomerIds;

	//max 50 karakter hosszú lehet
	private String name;
	
	//max 100 karakter hosszú lehet
	private String address;
	

	public Customer(HashMap<String, String> webshopAndCustomerIds, String name, String address) {
		
		this.webshopAndCustomerIds = webshopAndCustomerIds;
		this.name = name;
		this.address = address;
	}

	public HashMap<String, String> getWebshopAndCustomerIds() {
		return webshopAndCustomerIds;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

}
