import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import junit.framework.Assert;

public class SynchroniseUnit {

	@Test
	
	public void findGathering1() {
		Government g = new Government("config_fileforGovernment.txt");
		g.resetTables();
		MobileDevice A = new MobileDevice("MobileDevice1.txt", g);
		MobileDevice B = new MobileDevice("MobileDevice2.txt", g);
		MobileDevice C = new MobileDevice("MobileDevice3.txt", g);
		MobileDevice D = new MobileDevice("MobileDevice4.txt", g);
		MobileDevice E = new MobileDevice("MobileDevice5.txt", g);
		g.recordTestResult("CovidTest1", 6, false);
		g.recordTestResult("CovidTest2", 4, false);
		g.recordTestResult("CovidTest3", 23, false);
		g.recordTestResult("CovidTest4", 61, false);
		g.recordTestResult("CovidTest5", 72, true);
		g.recordTestResult("CovidTest6", 76, false);
		g.recordTestResult("CovidTest7", 64, false);
		g.recordTestResult("CovidTest8", 42, false);
		g.recordTestResult("CovidTest9", 18, false);
		g.recordTestResult("CovidTest10", 12, false);
		g.recordTestResult("CovidTest11",80,true);
		A.recordContact("346b504c2c4b4b3715b4617369f44562f0d68f184d988e3a0e9f0288339bed39", 70, 30); // b
		A.recordContact("7e203cf9025264c556020d6fb05fa9f48d49b543be7e34612609b1a7a7945ed2", 71, 35); // c
		A.recordContact("f577316d9b159402b2504b46b0b63736b1e82c5f07a58f5583b2de65ca7e0cf9", 69, 37); // d
		B.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5", 70, 30); // a
		C.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5", 71, 35); // a
		D.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5", 69, 37); // a
		A.positiveTest("CovidTest1");
		A.positiveTest("CovidTest2");
		A.positiveTest("CovidTest3");
		A.positiveTest("CovidTest4");
		A.positiveTest("CovidTest5");
		B.positiveTest("CovidTest11");
		assertEquals(false, A.synchronizeData());
		assertEquals(true, B.synchronizeData());
		assertEquals(true, C.synchronizeData());
		assertEquals(true, D.synchronizeData());
		assertEquals(false, E.synchronizeData());
		assertEquals(false, D.synchronizeData());
		assertEquals(false, B.synchronizeData());
	
	}
	  String governmentconfig = "config_fileforGovernment.txt";
      String mobileconfig = "MobileDevice1.txt";
      String mobileconfig2= "MobileDevice2.txt";
      String mobileconfig3= "MobileDevice3.txt";
    @Test
   // @DisplayName("Input Validation Tests")
		    public void inputValidationTest() throws Exception {
		        String governmentconfig = "config_fileforGovernment.txt";
		        String mobileconfig = "MobileDevice1.txt";

		        Government g = new Government(governmentconfig);
		        MobileDevice mb = new MobileDevice(mobileconfig,g);
		        mb.recordContact("",0,0);
		        mb.positiveTest("");

		        g.recordTestResult("",0,false);
		    }

		    @Test
	//	    @DisplayName("Mobile contact test")
		    public void mobileContact() throws Exception {
		     
		        Government g = new Government(governmentconfig);
		        MobileDevice mb = new MobileDevice(mobileconfig,g);

		        mb.recordContact("7e203cf9025264c556020d6fb05fa9f48d49b543be7e34612609b1a7a7945ed2",3,3);//c
		        mb.recordContact("7e203cf9025264c556020d6fb05fa9f48d49b543be7e34612609b1a7a7945ed2",3,4);//c
                
		        assertEquals(false, mb.synchronizeData());
		       
		    }

		    @Test
		//    @DisplayName("Mobile contact test 2")
		    public void mobileContact2() throws Exception {
		      //contact same as initiator

		        Government gov = new Government(governmentconfig);
		        MobileDevice mobile1 = new MobileDevice(mobileconfig,gov);
		        MobileDevice mobile2 = new MobileDevice(mobileconfig2,gov);

		        mobile1.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5",33,33); //a
		        mobile1.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5",33,45); //a
		        mobile2.recordContact("346b504c2c4b4b3715b4617369f44562f0d68f184d988e3a0e9f0288339bed39",45,50); //b

		        mobile2.positiveTest("CovidTest1");
		        gov.recordTestResult("CovidTest1",33,true);
		      
                
		        assertEquals(false, mobile1.synchronizeData());
		        assertEquals(false, mobile2.synchronizeData());
		        assertEquals(false, mobile2.synchronizeData());
		  


		    }

		    @Test
		  //  @DisplayName("Find Gathering 3")
		    public void findGathering2() throws Exception {
		       
		        String Mob1 = "MobileDevice1.txt";
		        String Mob2 = "MobileDevice2.txt";
		        String Mob3 = "MobileDevice3.txt";
		        String Mob4 = "MobileDevice4.txt";
		        String Mob5 = "MobileDevice5.txt";
		        String Mob6 = "MobileDevice6.txt";
		       

		        Government gov = new Government(governmentconfig);
		        MobileDevice mobile1 = new MobileDevice(Mob1,gov);
		        MobileDevice mobile2 = new MobileDevice(Mob2,gov);
		        MobileDevice mobile3 = new MobileDevice(Mob3,gov);
		        MobileDevice mobile4 = new MobileDevice(Mob4,gov);
		        MobileDevice mobile5 = new MobileDevice(Mob5,gov);
		        MobileDevice mobile6 = new MobileDevice(Mob6,gov);
		      
		        mobile1.recordContact("346b504c2c4b4b3715b4617369f44562f0d68f184d988e3a0e9f0288339bed39",50,13);
		        mobile1.recordContact("7e203cf9025264c556020d6fb05fa9f48d49b543be7e34612609b1a7a7945ed2",50,13);
		        mobile1.recordContact("d9cb05516fb41b966b7fea5752e8eb4b46aab1602ffbaeb6230bcc17150648da",50,54);
		        mobile2.recordContact("fd8794c73c011cdf4f4bf9f9e4d7ff954b625229817bea5c316b43bfa618d10f",50,33);//f
		        mobile3.recordContact("d9cb05516fb41b966b7fea5752e8eb4b46aab1602ffbaeb6230bcc17150648da",50,32);
		        mobile4.recordContact("1782d4abd06546ef7266d9ee52a5a3fe94f764025bf28814274d95abf38f16e5",50,54);
		        mobile4.recordContact("fd8794c73c011cdf4f4bf9f9e4d7ff954b625229817bea5c316b43bfa618d10f",50,63);//f
		        mobile5.recordContact("346b504c2c4b4b3715b4617369f44562f0d68f184d988e3a0e9f0288339bed39",50,65);
		        mobile5.recordContact("fd8794c73c011cdf4f4bf9f9e4d7ff954b625229817bea5c316b43bfa618d10f",50,64);//f
		        mobile6.recordContact("7e203cf9025264c556020d6fb05fa9f48d49b543be7e34612609b1a7a7945ed2",50,62);
		        mobile6.recordContact("fd8794c73c011cdf4f4bf9f9e4d7ff954b625229817bea5c316b43bfa618d10f",50,16);//f

		        mobile1.synchronizeData();
		        mobile2.synchronizeData();
		        mobile3.synchronizeData();
		        mobile4.synchronizeData();
		        mobile5.synchronizeData();
		        mobile6.synchronizeData();

		        System.out.println(gov.findGatherings(50,13,20,0.66f));

		    }
		}
	


