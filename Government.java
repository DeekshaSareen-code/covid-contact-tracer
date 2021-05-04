import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Government {

	String configFile;

	Government(String configFile) {
		this.configFile = configFile;
	}

	ArrayList<String> logindetails = new ArrayList<String>();
	static Statement statement = null;
	static ResultSet resultSet = null;
	static Connection connection = null;
	
	
	private boolean connection() {
		File file = new File(configFile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {

			System.out.println(e.getMessage());
			return false;/** catch exception if file is not found */
		}
		if (file.length() == 0) { /** if file contents are empty return 0 else continue */
									  
			return false;
		} else {
			String st;
			try {
				while ((st = br.readLine()) != null) /**
												 * running a loop while there is a
												 * line in the input file
												 */
				{
					String words[] = st.trim().split(
							"="); /**
									 * splitting each line into words separated by
									 * 1 or more spaces/tabs trailing or
									 * leading, and storing it into array words.
									 */
					logindetails.add(words[1]);
				}
				br.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); // create a new instance of the JDBC driver
		} catch (InstantiationException e) {
			e.getLocalizedMessage();
			return false;
		} catch (IllegalAccessException e) {
			e.getLocalizedMessage();
			return false;
		} catch (ClassNotFoundException e) {
			e.getLocalizedMessage();
			return false;
		}
		// Load a connection library between Java and the database
		// Connect to the Dal database
		try {

			connection = DriverManager.getConnection( logindetails.get(0),logindetails.get(1), logindetails.get(2));
			statement = connection.createStatement(); /** make a connection to the sql database hosted at dals server*/
		} catch (SQLException e) {
			e.getMessage(); // catch if an exception is thrown
			return false;
		}
		return true;

	}

	private static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild(); //this method converts the elements read from the xml string into a string form.
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData(); //if successfully converted returns the string else returns ? 
		}
		return "?";
	}

	public boolean mobileContact(String initiator, String contactInfo) {
		// Initiator has the device address and the device name.
		// contactinfo has the location of the xml file with all the contacts
		// with the device along with testhash if device owner has covid.
		if (connection() == false) {
			return false;
		}
		connection();
		ArrayList<String> contacts = new ArrayList<>();
		try {
			statement.execute("use "+logindetails.get(1)+")";  //sql to select the database.
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(contactInfo));
			Document doc1 = db.parse(is); //parsing the input from the xmlString into a Document object.

			if (doc1.getElementsByTagName("positivehash") != null) { 
				//if the positivehashes exists then it is inserted into the governments database.

				NodeList nodes = doc1.getElementsByTagName("test_hash");

				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);
					String sql = "insert into positiveResult (DeviceHash, Test_Hash) values ('" + initiator + "','"+ getCharacterDataFromElement(element) + "')";
					statement.executeUpdate(sql);

				}

			}

			NodeList nodes = doc1.getElementsByTagName("contact"); //reading tags in the contact parent tag

			for (int i = 0; i < nodes.getLength(); i++) {
				/** for the entire length of the nodes in the contact tag, all sub tags are read and data is stored
				 * in the Governments database table contacts.*/
				Element element = (Element) nodes.item(i);

				NodeList name = element.getElementsByTagName("individual");
				Element line = (Element) name.item(0);
				NodeList date = element.getElementsByTagName("date");
				Element line1 = (Element) date.item(0);
				NodeList time = element.getElementsByTagName("time");
				Element line2 = (Element) time.item(0);

				resultSet = statement.executeQuery("select * from contacts");
				String sqlupdate = null;
				while (resultSet.next()) {
					int Dateofcontact = resultSet.getInt("DateofContact");
					String DeviceHash = resultSet.getString("DeviceHash");
					String ContactDeviceHash = resultSet.getString("ContactdeviceHash");
					int Duration = resultSet.getInt("Duration");
				  
					if (Dateofcontact == Integer.parseInt(getCharacterDataFromElement(line1))&& DeviceHash.equalsIgnoreCase(initiator)&& ContactDeviceHash.equalsIgnoreCase(getCharacterDataFromElement(line))) {
       //if a contact already exists for the same day then its contents are updated with the total duration they met in the entire day.
						sqlupdate = "update contacts set Duration= "+(Duration+Integer.parseInt(getCharacterDataFromElement(line2)))+ " where DateofContact = " +Integer.parseInt(getCharacterDataFromElement(line1))+ " and DeviceHash = '" + initiator + "' and ContactdeviceHash ='"+ getCharacterDataFromElement(line)+"'";

					}

				}
				if (sqlupdate != null) { //if there was a duplicate found then execute the sql update query
					statement.executeUpdate(sqlupdate);
				}

				if (sqlupdate == null) {
/** if duplicate is not found then insert the contacts into the contacts table*/
					String sql = "insert into contacts(DeviceHash, DateofContact, Duration, ContactdeviceHash) values ('"+ initiator + "'," + getCharacterDataFromElement(line1) + ","+ getCharacterDataFromElement(line2) + ",'" + getCharacterDataFromElement(line) + "');";
					statement.executeUpdate(sql);
					contacts.add(getCharacterDataFromElement(line)); //store the contact in contacts
				}
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());

		}

		try {

			for (String a : contacts) { 
				//if the user hasnt been informed of a positive contact yet then return true if informed then return false.
				resultSet = statement.executeQuery("select contacts.contact_ID,contacts.DeviceHash, contacts.ContactdeviceHash,contacts.DateofContact, contacts.Duration,Testing.TestHash,Testing.TestDate, Testing.Result from contacts,positiveResult,Testing where contacts.ContactdeviceHash=positiveResult.DeviceHash and positiveResult.Test_Hash=Testing.TestHash and contacts.DateofContact between Testing.TestDate-14 and Testing.TestDate and Testing.Result='true' and contacts.DeviceHash='"+ initiator + "'");
				while (resultSet.next()) {
					if (resultSet.getString("ContactdeviceHash").equals(a)) {
						return true;
					}
				}
			}

		} catch (SQLException e) {

			e.getLocalizedMessage();
		}

		return false;
	}

	public void recordTestResult(String testHash, int date, boolean result) {
		/**this method stores the testhashes along with the date and the result into the database*/
		if (connection() == false) {
			return;
		}
		if (testHash == null || testHash.isEmpty() || date < 0 || result != false && result != true) {
			return; 
		}
		try {
			String sql = "insert into Testing(TestHash, TestDate, Result) values ('" + testHash + "'," + date + ",'"+ result + "')"; // contacts
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlEx) {
				}
				statement = null;
			}

		}

	}

	void resetTables() { //this is an additional utility that I added for the government to reset all the tables
		//that is delete the data from all the tables and get the date and time when the records were deleted
		connection();

		try {
			statement.execute("use dsareen;");
			String sql = "delete from contacts";
			statement.executeUpdate(sql);
			String sql1 = "delete from positiveResult";
			statement.executeUpdate(sql1);
			String sql2 = "delete from Testing";
			statement.executeUpdate(sql2);
			Date date = new Date();
			System.out.println("Records delete successfully on date : " + date);
		} catch (SQLException e) {
			e.getMessage();
		}

	}

	

	public int findGatherings(int date, int minSize, int minTime, float density) {
		if (connection() == false) {
			return -1; //if connection is false return -1
		}
		connection();
		if (date < 0 || minTime < 1 || density < 0 ||minSize<0) {
			return 0; //if any input validation return 0
		}
		int count = 0; //global gathering count is initialized to 0;

		Map<String, ArrayList<ArrayList<String>>> pairs = new HashMap<String, ArrayList<ArrayList<String>>>();
		Map<String, ArrayList<String>> pairsonly = new HashMap<String, ArrayList<String>>();
		
		try {
			resultSet = statement.executeQuery("select DeviceHash, ContactdeviceHash,Duration from contacts where DateofContact=" + date);

			// **get all the pairs from contacts along with minimum "minTime"
			// duration at a "date" and store them in a arraylist pairs.*/
			while (resultSet.next()) {
				String device1 = resultSet.getString("DeviceHash");
				String device2 = resultSet.getString("ContactdeviceHash");
				int duration = resultSet.getInt("Duration");

				if (pairs.containsKey(device1)) {

					ArrayList<String> pair = new ArrayList<>();
					pair.add(device2);
					pair.add("" + duration);
					pairs.get(device1).add(pair);

					pairsonly.get(device1).add(device2);
					//if the pair key already exists then just add the duration else create a new key for the pair
				} else {
					ArrayList<ArrayList<String>> values = new ArrayList<>();
					ArrayList<String> pair = new ArrayList<>();
					ArrayList<String> edgeonly = new ArrayList<>();
					pair.add(device2);
					pair.add("" + duration);
					values.add(pair);
					pairs.put(device1, values);
					edgeonly.add(device2);
					pairsonly.put(device1, edgeonly);

				}

			}
		} catch (SQLException e) {

			e.getLocalizedMessage();
		}
		ArrayList<String> individuals = new ArrayList<>(); //Stores the unique individuals
		for (Entry<String, ArrayList<ArrayList<String>>> ind : pairs.entrySet()) {
			individuals.add(ind.getKey());
		}

		Set<Set<String>> universalS=new HashSet<Set<String>>(); //this universalS set is created for storing all the unique gatherings formed

		for (int i = 0; i < individuals.size(); i++) { //for all the individuals form unique pairs
			for (int j = i + 1; j < individuals.size(); j++) {
/** for each pair formed and for indiviual A and B in that pair store the contacts that they both met respectively
 * in set pairA and pairB*/
				Set<String> pairA = new HashSet<>(pairsonly.get(individuals.get(i)));
				pairA.add(individuals.get(i));
				Set<String> pairB = new HashSet<>(pairsonly.get(individuals.get(j)));
				pairB.add(individuals.get(j));

				pairA.retainAll(pairB); //get the common contacts from pairA and pairB
				int n = pairA.size(); //n is the number of individuals in potential gathering in pairA
				if (n >= minSize && !universalS.contains(pairA)) { //if size of n is >= minSize and the gathering is unique

					float m = n * (n - 1) / 2; //find m 
					float count1 = 0; //counter to store the pairs formed from elements in set pairA

					List<String> list = new ArrayList<String>(pairA);
					if(list.size()>1){
						for (int k = 0; k < list.size(); k++) {
						for (int l = k + 1; l < list.size(); l++) {
							int z = 0;
							ArrayList<ArrayList<String>> sample = new ArrayList<ArrayList<String>>();

							sample = pairs.get(list.get(k)); //data is being fetched from map pairs
							for (ArrayList a : sample) {
								if (a.contains(list.get(l))) {
									z = Integer.parseInt((String) a.get(1));

								}
							}
 /**if for each pair formed the time for which they have met is greater than equal to minTime then count1++*/
							if (z >= minTime) {
								count1++;
							}

						}
					}
					}
					

					float density1 = 0; //density1 is used to find the ratio of count1 and m
					if(m>0){
						density1=count1 / m;
					}
					

					if (density1 >= density) { 
						universalS.add(pairA);
						count++;

					}
				}
			}
		}
		// iterate over all pairs one by one.
		// take first pair and check which all individuals they both of met. Get
		// all unique individuals n . Should be minsize atleast
		// S is the set of all pairs in contact with both A and B
		// check of m=n(n-1)/2 condition. ie there should be atmost M possible
		// pairs in S.

		// no. of pairs in S is c
		// if c/m is greater than density then worth reporting. and remove all
		// these individuals from S and go to next pair.

		return count;
	}

}
