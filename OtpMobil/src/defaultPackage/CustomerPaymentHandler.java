package defaultPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class CustomerPaymentHandler {
	
	private static LinkedList<Customer> customers = new LinkedList<Customer>();
	private static LinkedList<Payment> payments = new LinkedList<Payment>();

	static Logger logger = Logger.getLogger(CustomerPaymentHandler.class.getName());
	
	private static int fieldsNumInCustomerCsv = 4;
	private static int fieldsNumInPaymentsCsv = 7;
	
	public static void main(String[] args) {

		String fileSeparator = File.separator;

		final String logFile = System.getProperty("user.dir") + fileSeparator + "application.log";
		final String customerCsvFile = new File(System.getProperty("user.dir") + fileSeparator + "customer.csv").toString();
		final String paymentsCsvFile = new File(System.getProperty("user.dir") + fileSeparator + "payments.csv").toString();
		final String report01CsvFile = System.getProperty("user.dir") + fileSeparator + "report01.csv";
		final String report02CsvFile = System.getProperty("user.dir") + fileSeparator + "report02.csv";
		final String topCsvFile = System.getProperty("user.dir") + fileSeparator + "top.csv";

		//Ha léteznek a file-ok, töröljük
		try {
			
			Path logPath = Paths.get(logFile);
			Path report01Path = Paths.get(report01CsvFile);
			Path report02Path = Paths.get(report02CsvFile);
			Path topPath = Paths.get(topCsvFile);
			
			Files.deleteIfExists(logPath);
			Files.deleteIfExists(report01Path);
			Files.deleteIfExists(report02Path);
			Files.deleteIfExists(topPath);
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
			
		}

		//Log file létrehozása
		try {
			
			FileHandler fileHandler = new FileHandler(logFile, true);
			
			logger.addHandler(fileHandler);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

		CustomerPaymentHandler cph = new CustomerPaymentHandler();

		//Ha üres a customer.csv file
		if (new File(customerCsvFile).length() == 0)
			logger.warning("Üres a customer.csv file! Nem lett elmentve egy ügyfél sem!");
		else 
			cph.readCustomerCsv(customerCsvFile);
		
		//Ha üres a payments.csv file
		if (new File(paymentsCsvFile).length() == 0)
			logger.warning("Üres a payments.csv file! Nem lett elmentve egy fizetés sem sem!");
		else
			cph.readPaymentsCsv(paymentsCsvFile);

		cph.writeReport01AndTopCsv(report01CsvFile, topCsvFile);
		
		cph.writeReport02Csv( report02CsvFile);

	}
	
	private void readCustomerCsv(String customerCsvFile) {
		
		String line = "";  
		String splitBy = ";"; 
		String customerStr;
		
		BufferedReader br = null;

		try {
		
			br = new BufferedReader(new FileReader(customerCsvFile));
		
			while ((line = br.readLine()) != null) {
				
				String[] customer = line.split(splitBy);
				
				//Ha nincs meg az összes adat a customer.csv file-ban
				if(customer.length != fieldsNumInCustomerCsv) {
					
					String cust = "";
					
					for(String data : customer) {
						
						cust += " '" + data + "'";
						
					}
					
					logger.warning("Nincs meg az összes adat a customer.csv file-ban a következő ügyfélhez. Az ügyfél nem lett elmentve a memóriába:\n"
							+ cust);
					
					continue;
				}
				
				//A vezérlés csak akkor jut ide, ha customer.length != fieldsNumInCustomerCsv
				customerStr = "A következő ügyfél nem lett elmentve a memóriába: \n "
								+ "Webshop azonosító: " + customer[0] + ", Ügyfél azonosító: " + customer[1] + ", Név: " + customer[2] + ", Cím: " + customer[3];
				
				//webshop id ellenőrzés
				if(customer[0].trim().isEmpty() || "".equals(customer[0]) || customer[0].length() > 20) {
	
					logger.warning("Hibás a webshop azonosító! Nem lehet üres és nem lehet hosszabb 20 karakternél.\n"
									+ customerStr);
					
					continue;
					
				}
				
				//customer id ellenőrzés
				if(customer[1].trim().isEmpty() || "".equals(customer[1]) || customer[1].length() > 20) {
	
					logger.warning("Hibás az ügyfél azonosító! Nem lehet üres és nem lehet hosszabb 20 karakternél.\n"
									+ customerStr);
					
					continue;
	
				}
				
				//Ügyfél név ellenőrzés
				if(customer[2].trim().isEmpty() || "".equals(customer[2]) || customer[2].length() > 50) {
	
					logger.warning("Hibás az ügyfél név! Nem lehet üres és nem lehet hosszabb 50 karakternél.\n"
									+ customerStr);
					
					continue;
	
				}
				
				//Ügyfél cím ellenőrzés
				if(customer[3].trim().isEmpty() || "".equals(customer[3]) || customer[3].length() > 100) {
	
					logger.warning("Hibás az ügyfél cím! Nem lehet üres és nem lehet hosszabb 100 karakternél.\n"
									+ customerStr);
					
					continue;
	
				}
	
				//Megnézzük, hogy léteznek-e már az ügyfelek a memóriában. Egy ügyfelet a neve és címe alapján azonosítunk be. Ha nem létezik, akkor ellenőrizzük és ha lehet, felvisszük a memóriába.
				if(customers.size() > 0) {
					
					boolean exists = false;
					
					//Megnézzük, hogy létezik-e már a webshop-ügyfél azonosító pár
					for(Customer c : customers) {
						
						//Ha létezik a webshop-ügyfél azonosító pár
						if(c.getWebshopAndCustomerIds().get(customer[0]) != null && c.getWebshopAndCustomerIds().get(customer[0]).contains(customer[1])) {
	
							exists = true;
							break;
						}
						
					}
					
					if(exists) {
						
						logger.warning("A webshop azonosító és ügyfélazonosító pár már létezik: "+ customer[0] + "/" + customer[1] + "\n"
								+ customerStr);
						
						continue;
					}
					
					Customer cust = null;
					
					for(Customer c : customers) {
						
						//Ha létezik az ügyfél
						if(customer[2].trim().equals(c.getName()) && customer[3].trim().equals(c.getAddress())) {
							
							cust = c;
							
							break;
						}
						
					}
					
					//Létezik az ügyfél a memóriában?
					if(cust != null) {
						
						//Azt már fent ellenőriztük, hogy létezik-e a webshop-ügyfél azonosító pár. Csak akkor juthattunk ide, ha nem létezik, ezért ezt már nem ellenőrizzük.
						
						//Ellenőrizzük a webshop és ügyfél azonosítókat és ha lehet, hozzáadjuk a létező ügyfélhez
						
						//Ha létezik a webshop azonosító az ügyfélnél, akkor ugyanolyan webshop azonosítót nem vihetünk fel 
						if(cust.getWebshopAndCustomerIds().get(customer[0]) != null) {
	
							logger.warning("Ez a webshop azonosító az ügyfélnél már létezik. Kétszer ugyanezt nem mentjük el: "+ customer[0] + "\n"
									+ customerStr);
							
							continue;
						
						//Ha nem létezik a webshop azonosító az ügyfélnél	
						}else {
						
							cust.getWebshopAndCustomerIds().put(customer[0], customer[1]);
						}
						
					//Ha nem létezik az ügyfél a memóriában, akkor rögzítjük	
					} else {
						
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put(customer[0], customer[1]);
						
						customers.add(new Customer(hm, customer[2].trim(), customer[3].trim()));
						
					}
					
				
				//Ha még egyáltalán nincs ügyfél a memóriában, további ellenőrzés nélkül rögzítjük
				} else {
					
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put(customer[0], customer[1]);
					
					customers.add(new Customer(hm, customer[2].trim(), customer[3].trim()));
					
				}
				
			}
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
		
			if(br != null) {
				try {
				
					br.close();
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}	

	}
	
	private void readPaymentsCsv(String paymentsCsvFile) {

		String line = "";  
		String splitBy = ";"; 
		String paymentStr;
		
		BufferedReader br = null;

		try {
		
			br = new BufferedReader(new FileReader(paymentsCsvFile));
		
			while ((line = br.readLine()) != null) {
				
				String[] payment = line.split(splitBy);
				
				//Ha nincs meg az összes adat a payments.csv file-ban
				if(payment.length != fieldsNumInPaymentsCsv) {
					
					String pay = "";
					
					for(String data : payment) {
						
						pay += " '" + data + "'";
						
					}
					
					logger.warning("Nincs meg az összes adat a payments.csv file-ban a következő fizetéshez. A fizetés nem lett elmentve a memóriába:\n"
							+ pay);
					
					continue;
				}
				
				//A vezérlés csak akkor jut ide, ha payment.length != fieldsNumInPaymentsCsv
				paymentStr = "A következő fizetés nem lett elmentve a memóriába: \n "
								+ "Webshop azonosító: " + payment[0] + ", Ügyfél azonosító: " + payment[1] + ", Fizetés módja: " + payment[2] + ", Összeg: " + payment[3]
										+ ", Bankszámlaszám: " + payment[4]+ ", Kártyaszám: " + payment[5]+ ", Fizetés dátuma: " + payment[6];
				
				//webshop id ellenőrzés
				if(payment[0].trim().isEmpty() || "".equals(payment[0]) || payment[0].length() > 20) {
	
					logger.warning("Hibás a webshop azonosító! Nem lehet üres és nem lehet hosszabb 20 karakternél.\n"
									+ paymentStr);
					
					continue;
					
				}
				
				//customer id ellenőrzés
				if(payment[1].trim().isEmpty() || "".equals(payment[1]) || payment[1].length() > 20) {
	
					logger.warning("Hibás az ügyfél azonosító! Nem lehet üres és nem lehet hosszabb 20 karakternél.\n"
									+ paymentStr);
					
					continue;
	
				}
				
				//Fizetés módja ellenőrzés
				if(!"card".equals(payment[2]) && !"transfer".equals(payment[2])) {
	
					logger.warning("Hibás a fizetés módja! Card-nak vagy transfer-nek kell lennie.\n"
									+ paymentStr);
					
					continue;
	
				}
				
				//Összeg ellenőrzés
				try {
					
					if(payment[3].trim().isEmpty() || "".equals(payment[3]) || payment[3].length() > 10) {
		
						logger.warning("Hibás az Összeg! Nem lehet üres és nem lehet hosszabb 10 karakternél.\n"
										+ paymentStr);
						
						continue;
		
					}
					
				} catch (NumberFormatException e) {
	
					logger.warning("Hibás az Összeg! Nem szám.\n"
							+ paymentStr);
			
					continue;
	
				}
				
				//Bankszámlaszám ellenőrzés
				if(("transfer".equals(payment[2]) && payment[4].trim().isEmpty() ) || ("transfer".equals(payment[2]) && "".equals(payment[4]) )|| payment[4].length() > 16) {
	
					logger.warning("Hibás, vagy nincs Bankszámlaszám! Ha a fizetés módja 'transfer', nem lehet üres. Nem lehet hosszabb 16 karakternél.\n"
									+ paymentStr);
					
					continue;
	
				}
				
				//Kártyaszám ellenőrzés
				if(("card".equals(payment[2]) && payment[5].trim().isEmpty() ) || ("card".equals(payment[2]) && "".equals(payment[5]) )|| payment[5].length() > 16) {
	
					logger.warning("Hibás, vagy nincs Kártyaszám! Ha a fizetés módja 'card', nem lehet üres. Nem lehet hosszabb 16 karakternél.\n"
									+ paymentStr);
					
					continue;
	
				}
				
				//Dátum ellenőrzés
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
				dateFormat.setLenient(false);
				
				try {
					
					dateFormat.parse(payment[6].trim());
					
				} catch (ParseException pe) {
				 
					logger.warning("Hibás dátum!\n"
							+ paymentStr);
			
					continue;
					
				}
				
				
				boolean exists = false;
				
				//Megnézzük, hogy létezik-e már a webshop-ügyfél azonosító pár az ügyfeleknél. Ha nem létezik, nem rögzítjük a fizetést.
				for(Customer c : customers) {
					
					//Ha létezik a webshop-ügyfél azonosító pár
					if(c.getWebshopAndCustomerIds().get(payment[0]) != null && c.getWebshopAndCustomerIds().get(payment[0]).contains(payment[1])) {
	
						exists = true;
						break;
					}
					
				}
				
				//Ha létezik a webshop-ügyfél azonosító pár az ügyfeleknél, akkor rögzítjük a fizetést.
				if(exists) {
	
					//Fizetések ismétlődhetnek ugyanazokkal az adatokkal, ezért nem ellenőrizzük, hogy létezik-e már fizetés ugyanazokkal az adatokkal.
					payments.add(new Payment(payment[0], payment[1], payment[2], payment[3], payment[4], payment[5], payment[6]));
				
				//Ha nem létezik a webshop-ügyfél azonosító pár az ügyfeleknél, akkor nem rögzítjük a fizetést.	
				}else {
					
					logger.warning("Nem létezik a webshop-ügyfél azonosító pár az ügyfeleknél!\n"
							+ paymentStr);
			
					continue;
	
				}
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
		
			if(br != null) {
				try {
				
					br.close();
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}	

	}
	
	private void writeReport01AndTopCsv(String report01CsvFile, String topCsvFile) {

			ArrayList<String> rows = new ArrayList<String>();
			ArrayList<String> firstTwo = new ArrayList<String>();
			int sum;
			int first = 0;
			int second = 0;
			String firstStr = "";
			String secondStr = "";
			String webId;
			String custId;

			if(customers != null && payments != null) {
			
				for(Customer c : customers) {
					
					sum = 0;
					
					for(Map.Entry<String, String> map : c.getWebshopAndCustomerIds().entrySet()) {
						
						webId = map.getKey();
						custId = map.getValue();
						
						for(Payment p : payments) {
							
							if(webId.equals(p.getWebshopId()) && custId.equals(p.getCustomerId())) {
								
								sum += Integer.parseInt(p.getAmount());
								
							}
							
						}
						
					}
	
					if(sum > 0) {
						
						rows.add(c.getName() + ";" + c.getAddress() + ";" + sum);
						
					}
					
					if(sum > first) {
						
						first = sum;
						
						firstStr = c.getName() + ";" + c.getAddress() + ";" + first;
						
					} else if(sum == first) {
						
						if(!"".equals(firstStr)) 
							firstStr += "\n";
							
						firstStr += c.getName() + ";" + c.getAddress() + ";" + first;	

					} else if(sum > second) {
						
						second = sum;
						
						secondStr = c.getName() + ";" + c.getAddress() + ";" + second;
					
					}else if(sum == second) {
						
						if(!"".equals(secondStr)) 
							secondStr += "\n";
							
						secondStr += c.getName() + ";" + c.getAddress() + ";" + second;	

					}
					
				}
				
				firstTwo.add(firstStr);
				firstTwo.add(secondStr);
				
				if(rows.size() > 0) {
	
					try {
						
						FileWriter csvWriter = new FileWriter(report01CsvFile);
						FileWriter csvWriter2 = new FileWriter(topCsvFile);
			
						for (String rowData : rows) {
							csvWriter.append(rowData);
							csvWriter.append("\n");
						}
						
						for (String rowData : firstTwo) {
							csvWriter2.append(rowData);
							csvWriter2.append("\n");
						}
		
						csvWriter.flush();
						csvWriter.close();
						csvWriter2.flush();
						csvWriter2.close();
						
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}	
			}
	}
	
	private void writeReport02Csv(String report02CsvFile) {
		
		//Integer [0]: card, Integer [1]: transfer
		HashMap<String, Integer[]> map = new HashMap<String, Integer[]>();

		for(Payment pm : payments) {
			
			//Ha a WebshopId létezik már a map-ben
			if(map.get(pm.getWebshopId()) != null) {
				
				//Integer [0]: card, Integer [1]: transfer
				if("card".equals(pm.getPaymentType())) {
					
					Integer[] ia = {map.get(pm.getWebshopId())[0] + Integer.parseInt(pm.getAmount()), map.get(pm.getWebshopId())[1]};
					
					map.replace(pm.getWebshopId(), ia);
				
				//Integer [0]: card, Integer [1]: transfer
				}else {
					
					Integer[] ia = {map.get(pm.getWebshopId())[0], map.get(pm.getWebshopId())[1] + Integer.parseInt(pm.getAmount())};
					
					map.replace(pm.getWebshopId(), ia);
					
				}

			//Ha a WebshopId nem létezik a map-ben
			}else {
				
				//Integer [0]: card, Integer [1]: transfer
				if("card".equals(pm.getPaymentType())) {
				
					Integer[] ia = {Integer.parseInt(pm.getAmount()), 0};
					map.put(pm.getWebshopId(), ia);
				
				//Integer [0]: card, Integer [1]: transfer
				}else {
					
					Integer[] ia = {0, Integer.parseInt(pm.getAmount())};
					map.put(pm.getWebshopId(), ia);
					
				}
			}
			
		}

		if(map.size() > 0) {
			
			try {
				
				FileWriter csvWriter = new FileWriter(report02CsvFile);

				for (Map.Entry<String, Integer[]> m : map.entrySet()) {
					csvWriter.append(m.getKey() + ";" + m.getValue()[0] + ";" + m.getValue()[1]);
					csvWriter.append("\n");
				}

				csvWriter.flush();
				csvWriter.close();

				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}	
			
	}

}
