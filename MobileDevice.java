import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import java.io.*;

public class MobileDevice {

	String Configfile;
	Government ContactTracer; 

	MobileDevice(String Configfile, Government ContactTracer) { 
		/**Constructor for the class where configfile has the
		configfile location and ContactTracer is a object of the government class.*/
		this.Configfile = Configfile;
		this.ContactTracer = ContactTracer;
	}

	ArrayList<String> positivetest = new ArrayList<>(); //stores the positive testhashes for the user
	Map<String, String> userdevicedetails = new HashMap<String, String>(); //stores the users device details
    Map<String, ArrayList<Integer>> contacts = new HashMap<String, ArrayList<Integer>>(); //stores the details of the devices the user contacts
    
	private boolean read(String Configfile) {
		//This method is used to read the contents of the Configfile from the file path from the constructor of class
		File file = new File(Configfile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {

			System.out.println(e.getMessage());
			return false;/** catch exception if file is not found */
		}
		String st;
		try {
			while ((st = br.readLine()) != null) /**
											 * running a loop while there is a
											 * line in the input file
											 */
			{
				String words[] = st.trim().split("="); /**
								 * splitting each line into words separated by 1
								 * or more spaces/tabs trailing or leading, and
								 * storing it into array words.
								 */
				userdevicedetails.put(words[0], words[1]);
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	

	public void recordContact(String individual, int date, int duration) {
		if (individual == null || individual.isEmpty() || duration <= 0 || date < 0) {
			return;  //testing for some input validation
		}
		ArrayList<Integer> time = new ArrayList<>(); //ArrayList to store the date and duration of the contacts
		
		if(contacts.containsKey(individual)){ //if local storage of contacts already contains the key individual then we add the new contacted duration to the previous value
			//without creating a new individual key
			int existingdate=contacts.get(individual).get(0);
			int exisitingduration= contacts.get(individual).get(1);
			if(existingdate==date){  
				time.add(date);
				time.add(exisitingduration+duration);
				contacts.put(individual.toLowerCase(), time);
			}
		}
		else{ //else we create a new contact key with its time and duration
			time.add(date);
		    time.add(duration);
			contacts.put(individual.toLowerCase(), time);
		}

	}

	private String hash(String deviceaddress) {
		String sha256 = null;  /** create a Hash of the users device hash and name*/
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(deviceaddress.getBytes(StandardCharsets.UTF_8));
			sha256 = DatatypeConverter.printHexBinary(digest).toLowerCase();

		} catch (NoSuchAlgorithmException e) {
			e.getLocalizedMessage();
		}
		return sha256;
	}

	public void positiveTest(String testHash) { /** if the testHash is not null or empty then it reads the 
	users device hash and stores the positive teshHash corresponding to that in the positivetest*/
		if (testHash == null || testHash.isEmpty()) {
			return;
		}
		read(Configfile);
		positivetest.add(testHash);

	}

	public boolean synchronizeData() {
		if (read(Configfile) == false) { //if there is some error in the Configfile location then returns false
			return false;
		}
		read(Configfile);
		String initiator = ""; //stores the details of the users device address and name

		String xmlString = "@";
		for (Map.Entry<String, String> entry : userdevicedetails.entrySet()) {
			initiator += entry.getValue();
		}

		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance(); //creates a new instance of the document factory class
			DocumentBuilder documentBuilder;
			documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();

			Element contactSummary = doc.createElement("contacts_summary");
			doc.appendChild(contactSummary); // appends child node contact
												// summary to document doc
			if (!positivetest.isEmpty()) {  //if positivetest arrayList is not empty then append the data to the positiveHash in the XML
				Element positivehash = doc.createElement("positivehash");
				contactSummary.appendChild(positivehash);

				for (String positive : positivetest) {
					Element covidresult = doc.createElement("test_hash");
					covidresult.appendChild(doc.createTextNode(positive));
					positivehash.appendChild(covidresult);
				}

			}
			for (Map.Entry<String, ArrayList<Integer>> contacts : contacts.entrySet()) { 
				/** for all the contacts stored locally in the contacts Map get all the data and append into the XML
				 * file*/
				Element contact = doc.createElement("contact");
				contactSummary.appendChild(contact);

				Element individualname = doc.createElement("individual");
				individualname.appendChild(doc.createTextNode(contacts.getKey()));
				contact.appendChild(individualname);

				ArrayList<Integer> hold = new ArrayList<>(contacts.getValue());
				Element Date = doc.createElement("date");
				Date.appendChild(doc.createTextNode(String.valueOf(hold.get(0)))); 
																					
				contact.appendChild(Date);

				Element time = doc.createElement("time");
				time.appendChild(doc.createTextNode(String.valueOf(hold.get(1)))); 
																				
				contact.appendChild(time);

			}

			try {

				Transformer tr = TransformerFactory.newInstance().newTransformer(); // create
																					// a
																					// new
																					// instance
																					// of
																					// transformer
																					// class
				/** OuptputProperty sets the first line of the xml */
				tr.setOutputProperty(OutputKeys.INDENT, "yes"); // gives a
																// proper
																// structure to
																// the XML file
																// tags
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // indent
																						// of
																						// 4
																						// for
																						// tag
																						// nesting

				// send DOM (Document object model) to file

				StreamResult result = new StreamResult(new StringWriter()); 
				tr.transform(new DOMSource(doc), result); //transform the XML document and write it into a string
				xmlString = result.getWriter().toString();

			}

			catch (TransformerException te) {
				System.out.println(te.getMessage());
			}

		} catch (ParserConfigurationException e) {

			e.getLocalizedMessage();
		}
		if (initiator == null || initiator.isEmpty()) { //if the initiator data is null or empty then return false.
			return false;
		}
		if (ContactTracer.mobileContact(hash(initiator), xmlString) == true) { 
			/** pass the initiator hash and the xmlString created above to the governments mobile contact method
			 * using the Government class object ContactTracer*/
			return true;  
		}

		return false;
	}
}
