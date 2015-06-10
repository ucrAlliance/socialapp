/*
	Database Management Systems
	Department of Computer Science and Engineering
	University of California - Riverside
	Target DBMS: 'Postgres'

	Group Members:

Name: Ikk Anmol Singh Hundal
SID: 861134450

Name: Austin Macciola
SID: 860967151

We certify that this project is our own work, and except for the template
no help was received from anyone.
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

	// reference to physical database connection.
	private Connection _connection = null;

	// handling the keyboard inputs through a BufferedReader
	// This variable can be global for convenience.
	static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	/**
	 * Creates a new instance of Messenger
	 *
	 * @param hostname the MySQL or PostgreSQL server hostname
	 * @param database the name of the database
	 * @param username the user name used to login to the database
	 * @param password the user login password
	 * @throws java.sql.SQLException when failed to make a connection.
	 */
	public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

		/* Debugging
			System.out.print("Connecting to database..."); */
		try{

			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;

			/* Debugging
				System.out.println ("Connection URL: " + url + "\n");
			 */

			// obtain a physical connection
			this._connection = DriverManager.getConnection(url, user, passwd);
			//System.out.println("Done");
		} catch (Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
			System.out.println("Make sure you started postgres on this machine");
			System.exit(-1);
		}//end catch
	}//end ProfNetwork

	public void execute(String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.execute (sql);

		// close the instruction
		stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 *
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
		stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 ** obtains the metadata object for the returned result set.  The metadata
		 ** contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		// iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}//end executeQuery

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 *
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 ** obtains the metadata object for the returned result set.  The metadata
		 ** contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		// iterates through the result set and saves the data returned by the query.
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		// iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current
	 * value of sequence used for autogenerated keys
	 *
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next())
			return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
			// ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
					"Usage: " +
					"java [-classpath <classpath>] " +
					ProfNetwork.class.getName () +
					" <dbname> <port> <user>");
			return;
		}//end if

		Greeting();
		ProfNetwork esql = null;
		try{
			// use postgres JDBC driver.
			Class.forName ("org.postgresql.Driver").newInstance ();
			// instantiate the ProfNetwork object and creates a physical
			// connection.
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			esql = new ProfNetwork (dbname, dbport, user, "");

			boolean keepon = true;
			while(keepon) {
				// These are sample SQL statements
				System.out.println("\nMAIN MENU");
				System.out.println("1. Create user");
				System.out.println("2. Log in");
				System.out.println("9. < EXIT");
				String authorisedUser = null;
				switch (readChoice()){
					case 1: 
						CreateUser(esql); 
						break;
					case 2: 
						authorisedUser = LogIn(esql); 
						break;
					case 9: 
						keepon = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}//end switch
				
				if (authorisedUser != null) {
					boolean usermenu = true;
					while(usermenu) {
						PrintProfile(esql,authorisedUser,0);	
						System.out.println("\n1. View Connection Requests");
						System.out.println("2. View Friend List");
						System.out.println("3. Update Profile");
						System.out.println("4. Write a new message");
						System.out.println("5. View/Delete messages");
						System.out.println("6. Change password");
						System.out.println("7. Search people");
						System.out.println("9. Log out");

						switch (readChoice()){
							case 1: 
								ConnectionRequests(esql, authorisedUser);
								break;
							case 2: 
								myFriendList(esql, authorisedUser); 
								break;
							case 3: 
								UpdateProfile(esql, authorisedUser); 
								break;
							case 4: 
								sendMessage(esql, authorisedUser); 
								break;
							case 5: 
								ViewMessages(esql, authorisedUser); 
								break;
							case 6: 
								ChangePassword(esql, authorisedUser); 
								break;
							case 7: 
								Search(esql, authorisedUser); 
								break;
							case 9: 
								usermenu = false; 
								break;
							default : 
								System.out.println("Unrecognized choice!"); 
								break;
						}
					}
				}
			}//end while
		}catch(Exception e) {
			System.err.println (e.getMessage ());
		}finally{
			// make sure to cleanup the created table and close the connection.
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch (Exception e) {
				// ignored.
			}//end try
		}//end try
	}//end main

	public static void Greeting(){
		System.out.println(
				"\n*******************************************************\n" +
				"              Welcome to RLinkedin      	               \n" +
				"*******************************************************");
	}//end Greeting

	/*
	 * Reads the users choice given from the keyboard
	 * @int
	 **/
	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	/*
	 * Creates a new user with privided login, passowrd and phoneNum
	 * An empty block and contact list would be generated and associated with a user
	 **/
	public static void CreateUser(ProfNetwork esql){
		try{
			System.out.println("\n\tCREATE USER MENU");
			System.out.print("\tPlease provide the following information\n");
			System.out.print("\tlogin: ");
			String login = in.readLine();
			System.out.print("\tpassword: ");
			String password = in.readLine();
			System.out.print("\temail: ");
			String email = in.readLine();
			System.out.print("\tName (Optional): ");
			String name = in.readLine();
			System.out.print("\tDate of Birth (Optional): ");
			String dob = in.readLine();

			// Make Query
			String query;
			if(name.length()==0 && dob.length()!=0) {
				query = String.format("INSERT INTO USR (userid, password, email, dateofbirth) VALUES ('%s','%s','%s','%s')", login, password, email, dob);
			} else if(name.length()!=0 && dob.length()==0) {
				query = String.format("INSERT INTO USR (userid, password, email, name) VALUES ('%s','%s','%s','%s')", login, password, email, name);
			} else if(name.length()==0 && dob.length()==0) {
				query = String.format("INSERT INTO USR (userid, password, email) VALUES ('%s','%s','%s')", login, password, email);
			} else {
				query = String.format("INSERT INTO USR (userid, password, email, name, dateofbirth) VALUES ('%s','%s','%s','%s','%s')", login, password, email, name, dob);
			}

			esql.executeUpdate(query);
			System.out.println ("User successfully created!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static void UpdateProfileHelper(ProfNetwork esql, String current_usr, int callnum){
		try{
			if(callnum==0){
				System.out.println("\n\tEducation detail");
				System.out.print("\tInstitute Name: ");
				String iname = in.readLine();
				System.out.print("\tMajor: ");
				String major = in.readLine();
				System.out.print("\tDegree: ");
				String degree = in.readLine();
				System.out.print("\tStart Date (Optional): ");
				String sdate = in.readLine();
				System.out.print("\tEnd Date (Optional): ");
				String edate = in.readLine();

				// Make query
				String query;
				if(sdate.trim().length()==0){
					query = String.format("INSERT INTO educational_details (userid, instituitionname, major, degree) VALUES ('%s', '%s', '%s', '%s')", current_usr, iname, major, degree);
				}else if (edate.trim().length()==0){
					query = String.format("INSERT INTO educational_details (userid, instituitionname, major, degree, startdate) VALUES ('%s', '%s', '%s', '%s', '%s')", current_usr, iname, major, degree, sdate);
				}else{
					query = String.format("INSERT INTO educational_details (userid, instituitionname, major, degree, startdate, enddate) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", current_usr, iname, major, degree, sdate, edate);
				}
				esql.executeUpdate(query);
			} else if(callnum==1){
				System.out.println("\n\tWork Experience");
				System.out.print("\tCompany: ");
				String company = in.readLine();
				System.out.print("\tRole: ");
				String role = in.readLine();
				System.out.print("\tLocation (Optional): ");
				String location = in.readLine();
				System.out.print("\tStart Date (Mandatory): ");
				String sdate = in.readLine();
				System.out.print("\tEnd Date (Optional): ");
				String edate = in.readLine();
				String query;
				if(edate.trim().length()==0){
					query = String.format("INSERT INTO work_expr (userid, company, role, location, startdate) VALUES ('%s', '%s', '%s', '%s', '%s')", current_usr, company, role, location, sdate);
				} else {
					query = String.format("INSERT INTO work_expr (userid, company, role, location, startdate, enddate) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", current_usr, company, role, location, sdate, edate);
				}
				esql.executeUpdate(query);
			}
			System.out.println("Updated Sucessfully");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static void UpdateProfile(ProfNetwork esql, String current_user){
		try{
			boolean updateMenu = true;
			while(updateMenu) {
				String searchkey;
				System.out.println("\nUPDATE MENU");
				System.out.println("1. Add Educational detail");
				System.out.println("2. Add Work Experience");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						UpdateProfileHelper(esql,current_user, 0);
						break;
					case 2: 
						UpdateProfileHelper(esql,current_user, 1);
						break;
					case 9: 
						updateMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static Set<String> GetConnections(ProfNetwork esql, String usr){
		try{
			String connections = String.format("SELECT userid FROM connection_usr where connectionid='%s' and status='Request'", usr);
			List<List<String>> conlist= esql.executeQueryAndReturnResult(connections);
		
			Set<String> conset = new TreeSet<String>();
			for(List<String> conrows : conlist){	
				conset.add(conrows.get(0).trim());
			}

			return conset;
		}catch(Exception e){
			System.err.println (e.getMessage ());
			return null;
		}
	}

	public static void AcceptConnection(ProfNetwork esql, String cur_usr, String reqsender){
		try{
			String accept = String.format("update connection_usr set status='Accept' where userid='%s' and connectionid='%s' and status='Request'", reqsender, cur_usr);
			esql.execute(accept);
			System.out.println("Request Accepted Successfully");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void ConnectionRequests(ProfNetwork esql, String current_user){
		try{
			// Get Connections
			Set<String> conset=GetConnections(esql, current_user);
			
			if(conset.size()==0){
				System.out.println("\nYou have no incoming connection request. Try again later");
				return;
			}
			
			// Print 		
			System.out.println("\tREQUESTS");
			System.out.println("\t---------");
			for(String connec : conset){	
				System.out.println("\t"+connec);
			}
			System.out.println("\t---------");

			boolean updateMenu = true;
			while(updateMenu) {
				String searchkey;
				System.out.println("\nCONNECTION MENU");
				System.out.println("1. Accept Connection Request");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						System.out.print("\nEnter userid of the person whose connection you want to accept: ");
						String conid=in.readLine();
						if(!conset.contains(conid)){
							System.out.println("You have no connection request from the userid you entered");
							break;
						}
						AcceptConnection(esql,current_user,conid);
						break;
					case 9: 
						updateMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	/*
	 * Check log in credentials for an existing user
	 * @return User login or null is the user does not exist
	 **/
	public static String LogIn(ProfNetwork esql){
		try{
			System.out.println("\n\tLOGIN MENU");
			System.out.print("\tlogin: ");
			String login = in.readLine();
			System.out.print("\tpassword: ");
			String password = in.readLine();

			String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
			int userNum = esql.executeQuery(query);
			if (userNum > 0)
				return login;
			System.out.println("\tERROR:Invalid login and/or password. Please Try Again");
			return null;
		}catch(Exception e){
			System.err.println (e.getMessage ());
			return null;
		}
	}

	public static void SendMessageTo(ProfNetwork esql, String current_usr, String receiver_id){
		try{
			String query2 = String.format("SELECT max(msgId) FROM MESSAGE");
			List<List<String>> result= esql.executeQueryAndReturnResult(query2);

			// Newmsgid is 1+Last Message ID
			int newmsgid=1+Integer.parseInt(result.get(0).get(0));		

			System.out.print("\tPlease type in the contents of the message and press ENTER: ");
			String contents = in.readLine();

			// For Timestamp
			java.util.Date date = new java.util.Date();
			Timestamp myTimestamp = new Timestamp(date.getTime());
			String timest = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(myTimestamp);

			// Actual query
			String query = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '0', 'Sent')", newmsgid, current_usr, receiver_id, contents, timest);
			esql.executeUpdate(query);
			System.out.println("Message Sent!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void SendConnectionTo(ProfNetwork esql, String current_usr, String receiver_id, int clevel){
		try{
			if(clevel>3)
			{
				// PreChecking
				String pc = String.format("SELECT * FROM connection_usr WHERE userid = '%s'", current_usr);
				int numpc = esql.executeQuery(pc);
				if(numpc>5){
					System.out.println("This person is more than 3 levels apart form you. And you have already used your 5 free connections. Try sending a request to someone within 3 connections");
					return;
				}
			}
			
			// Checking			
			// Case1 - If you are already friends with that person
			Set<String> friends=FriendSet(esql,current_usr);
			if(friends.contains(receiver_id.trim())){
				System.out.println("You are already friends with that person");
				return;
			}			

			// Case2 - If you have already sent request to that person
			String case2 = String.format("SELECT * FROM connection_usr WHERE userid = '%s' and connectionid = '%s'", current_usr, receiver_id);
			int numc2 = esql.executeQuery(case2);
			if(numc2>0){
				System.out.println("You have already sent a connection request to that person.");
				return;
			}
			
			// Case3 - The other person has sent a connection request to you	
			String case3 = String.format("SELECT * FROM connection_usr WHERE userid = '%s' and connectionid = '%s'", receiver_id, current_usr);
			int numc3 = esql.executeQuery(case3);
			if(numc3>0){
				System.out.println("The other person has sent a connection request to you. Accept that connection.");
				return;
			}

			// If you made it this far, you're good. Go send a connection request.
			String query = String.format("INSERT INTO connection_usr (userid, connectionid, status) VALUES ('%s', '%s', 'Request')", current_usr, receiver_id);
			esql.executeUpdate(query);
			System.out.println("Connection Requested!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void PrintProfile(ProfNetwork esql, String usr, int clevel){
		try{
			System.out.println("--------------------");
			String query_basic = String.format("SELECT name,email,dateofbirth FROM usr where userid='%s'", usr);
			List<List<String>> basic= esql.executeQueryAndReturnResult(query_basic);
			
			// Print Basic
			List<String> info=basic.get(0);
			String name=info.get(0);
			String email=info.get(1);
			String dob=info.get(2);
			System.out.println("userid: " + usr);
			if(name!=null){
				System.out.println("Name: "+name);
			}
			System.out.println("Email: "+email);
			if(dob!=null  && clevel<=1){
				System.out.println("Date of Birth: "+dob);
			}

			// Print Educational Details
			String query_edu = String.format("SELECT instituitionname, major, degree, startdate, enddate FROM educational_details where userid='%s'", usr);
			List<List<String>> edu = esql.executeQueryAndReturnResult(query_edu);
			if(edu.size()==0){	
				System.out.println("\nNo Educational Details on record");
			}else{	
				for(List<String> edul : edu){	
					String iname=edul.get(0).trim();
					String major=edul.get(1).trim();
					String degree=edul.get(2).trim();
					String sdate=edul.get(3);
					String edate=edul.get(4);
					System.out.println("\nE: " + degree + " in " + major + " from " + iname);
					System.out.println("from " + sdate + " to " + edate);
				}
			}

			// Print Work Details
			String query_work = String.format("SELECT company, role, startdate, enddate FROM work_expr where userid='%s'", usr);
			List<List<String>> work = esql.executeQueryAndReturnResult(query_work);
			if(edu.size()==0){	
				System.out.println("\nNo Work Experience on record");
			}else{	
				for(List<String> workxp : work){	
					String company=workxp.get(0).trim();
					String role=workxp.get(1).trim();
					String sdate=workxp.get(2).trim();
					String edate=workxp.get(3);
					System.out.println("\nW: " + role + " at " + company);
					System.out.println("from " + sdate + " to " + edate);
				}
			}
			System.out.println("--------------------");

		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void ViewProfile(ProfNetwork esql, String current_usr, String profile_id, int clevel){
		try{
			PrintProfile(esql, profile_id, clevel);

			boolean optionsMenu = true;
			while(optionsMenu) {
				System.out.println("\n1. Send Message");
				System.out.println("2. Send Request");
				System.out.println("3. View Friend List");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						SendMessageTo(esql, current_usr, profile_id); 
						break;
					case 2: 
						SendConnectionTo(esql, current_usr, profile_id, clevel); 
						break;
					case 3: 
						FriendList(esql, current_usr, profile_id, clevel+1); 
						break;
					case 9: 
						optionsMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public static Set<String> FriendSet(ProfNetwork esql, String usr){
		try{
			String query = String.format("SELECT connectionId FROM CONNECTION_USR WHERE (userId = '%s') AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE (connectionId = '%s') AND status = 'Accept'", usr, usr);
			List<List<String>> result= esql.executeQueryAndReturnResult(query);

			Set<String> friends = new TreeSet<String>();

			for(List<String> list : result){
				for(String attr : list){
					friends.add(attr.trim());	
				}
			}
			return friends;
		}catch(Exception e){
			System.err.println(e.getMessage());
			return null;
		}
	}

	public static void PrintFriendSet(Set<String> friends){
		try{
			System.out.println("\tFRIENDS LIST");
			System.out.println("\t------------");

			// Print
			for(String friendname : friends){
				System.out.println("\t"+ friendname);	
			}

			System.out.println("\t------------");

		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void FriendList(ProfNetwork esql, String current_usr, String profile_id, int clevel){
		try {
			Set<String> friends=FriendSet(esql, profile_id);
			PrintFriendSet(friends);

			boolean optionsMenu = true;
			while(optionsMenu) {
				System.out.println("\nFRIENDS LIST MENU");
				System.out.println("1. View Profile");
				System.out.println("9. Go Back");
				switch (readChoice()){
					case 1: 
						System.out.print("Enter the name of the person whose profile you want to see: ");
						String friendname = in.readLine();
						if(!friends.contains(friendname))
						{
							System.out.print("That username does not exist in this friendlist");
							break;
						}
						ViewProfile(esql, current_usr, friendname, clevel);
						break;
					case 9: 
						optionsMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		} catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end

	public static void myFriendList(ProfNetwork esql, String current_usr){
		try {
			FriendList(esql, current_usr, current_usr, 0);
		} catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end

	public static void sendMessage(ProfNetwork esql, String current_usr){
		try{
			System.out.print("\tEnter recipient userid: ");
			String receiver_id = in.readLine();

			SendMessageTo(esql, current_usr, receiver_id);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end

	public static void deleteMessage(ProfNetwork esql, String msgid, int target){
		try{	
			if (target==1){
				String msgquery = String.format("SELECT deletestatus FROM MESSAGE WHERE msgid='%s'", msgid);
				List<List<String>> msgstatus = esql.executeQueryAndReturnResult(msgquery);
				int deletestatus=Integer.parseInt(msgstatus.get(0).get(0).trim());
				String updatequery;
				if(deletestatus==0){
					updatequery = String.format("update message set deletestatus=1 WHERE msgid='%s'", msgid);
				}else{
					updatequery = String.format("delete from message WHERE msgid='%s'", msgid);
				}
				esql.execute(updatequery);
				System.out.println("Message deleted from outbox");
			}
			else if(target==2){
				String msgquery = String.format("SELECT deletestatus FROM MESSAGE WHERE msgid='%s'", msgid);
				List<List<String>> msgstatus = esql.executeQueryAndReturnResult(msgquery);
				int deletestatus=Integer.parseInt(msgstatus.get(0).get(0).trim());
				String updatequery;
				if(deletestatus==0){
					updatequery = String.format("update message set deletestatus=2 WHERE msgid='%s'", msgid);
				}else{
					updatequery = String.format("delete from message WHERE msgid='%s'", msgid);
				}
				esql.execute(updatequery);
				System.out.println("Message deleted from inbox");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end

	public static void ViewMessages(ProfNetwork esql, String current_usr){
		try{
			// Recieved Messages
			System.out.println("\nRECEIVED MESSAGES");
			String queryr = String.format("SELECT msgId, contents, sendtime FROM MESSAGE WHERE receiverId = '%s' AND deleteStatus !=2", current_usr);
			List<List<String>> received= esql.executeQueryAndReturnResult(queryr);

			Set<String> inmessages = new TreeSet<String>();		
	
			// Print Here
			for(List<String> list : received){
				String msgid=list.get(0).trim();
				String contents=list.get(1).trim();
				String time=list.get(2).trim();
				
				// Add to set
				inmessages.add(msgid);
				
				System.out.println("\n\tmsgid: " + msgid);
				System.out.println("\tcontents: " + contents);
				System.out.println("\ttime: " + time);
			}

			// Sent Messages
			System.out.println("\nSENT MESSAGES");
			String querys = String.format("SELECT msgId, contents, sendtime FROM MESSAGE WHERE senderId = '%s' AND deleteStatus != 1", current_usr);
			List<List<String>> sent= esql.executeQueryAndReturnResult(querys);

			Set<String> outmessages = new TreeSet<String>();		

			// Print Here
			for(List<String> list : sent){
				String msgid=list.get(0).trim();
				String contents=list.get(1).trim();
				String time=list.get(2).trim();
				
				// Add to set
				outmessages.add(msgid);
				
				System.out.println("\n\tmsgid: " + msgid);
				System.out.println("\tcontents: " + contents);
				System.out.println("\ttime: " + time);
			}
			
			boolean messagesMenu = true;
			while(messagesMenu) {
				System.out.println("\nMessage Menu");
				System.out.println("1. Delete a Message");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						System.out.print("Enter the msgid of the message you want to delete: ");
						String msgid=in.readLine(); 
						if(inmessages.contains(msgid)){
							deleteMessage(esql, msgid, 2);
						}else if (outmessages.contains(msgid)){
							deleteMessage(esql, msgid, 1);
						}else{
							System.out.println("The message for the msgid you provided does not exist. Please try again");
						}
						break;
					case 9: 
						messagesMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end

	public static void ChangePassword(ProfNetwork esql, String current_usr){
		try{
			String new_password_A;
			String new_password_B;
			do{
				System.out.print("\tEnter new password: ");
				new_password_A = in.readLine();
				System.out.print("\tRe-enter new password: ");
				new_password_B = in.readLine();
				if(!new_password_A.equals(new_password_B))
				{
					System.out.println("Passwords did not match. Try again. ");
				}
			}while(!new_password_A.equals(new_password_B));

			String query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s'", new_password_A, current_usr);
			esql.executeUpdate(query);
			System.out.println("Changed password successfully!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static int getConnectionLevel(ProfNetwork esql, String usr, String prospect){
		try{	
			
			// Level 0
			Set<String> friends = FriendSet(esql, usr);
			if(friends.contains(prospect)){
				return 0;
			}

			// Level 1
			Set<String> friends1 = new TreeSet<String>();
			for(String cur_id : friends){
				friends1.addAll(FriendSet(esql, cur_id));
			}
			if(friends1.contains(prospect)){
				return 1;
			}

			// Level 2
			Set<String> friends2 = new TreeSet<String>();
			for(String cur_id : friends1){
				friends2.addAll(FriendSet(esql, cur_id));
			}
			if(friends2.contains(prospect)){
				return 2;
			}

			// Level 3
			Set<String> friends3 = new TreeSet<String>();
			for(String cur_id : friends2){
				friends3.addAll(FriendSet(esql, cur_id));
			}
			if(friends3.contains(prospect)){
				return 3;
			}

			// If you make it here, then you are level 4.
			return 4;

		}catch(Exception e){
			System.err.println (e.getMessage ());
			return -1;
		}
	}//end

	public static void search_helper(ProfNetwork esql, String cur_usr, String searchkey, int attr){
		try{
			String searchquery;
			if(attr==1){
				searchquery = String.format("select userid from usr where userid='%s'", searchkey);
			} else if (attr==2) {
				searchquery = String.format("select userid from usr where email='%s'", searchkey);
			} else {
				searchquery = String.format("select userid from usr where name='%s'", searchkey);
			}
	
			List<List<String>> searchresults= esql.executeQueryAndReturnResult(searchquery);
			if(searchresults.size()==0)
			{
				System.out.println("No result found");
				return;
			}

			Set<String> friends = FriendSet(esql, cur_usr);
			Set<String> searchset = new TreeSet<String>();
			for(List<String> searchlist : searchresults){
				String cursearch=searchlist.get(0).trim();
				searchset.add(cursearch);
			}
				
			System.out.println("\nSearch Results");
	
			for(String cur_id : searchset){
				System.out.println("\nUserid: " + cur_id);
				if(friends.contains(cur_id)){
					System.out.println("Status: Friends");
					//PrintProfile(esql, cur_id,1);
				}else{
					System.out.println("Status: Not Friends");
					//PrintProfile(esql, cur_id,2);
				}
			}

			boolean searchResultMenu = true;
			while(searchResultMenu) {
				System.out.println("\nSEARCH RESULT MENU");
				System.out.println("1. View Profile");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						System.out.print("Enter the userid of the person whose profile you want to see: ");
						String profile_id=in.readLine(); 
						if(!searchset.contains(profile_id)){
							System.out.println("The userid you entered does not exist in the search results. Please try again");
							break;
						}
						int clevel = getConnectionLevel(esql, cur_usr, profile_id);
						ViewProfile(esql, cur_usr, profile_id, clevel);
						break;
					case 9: 
						searchResultMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	public static void Search(ProfNetwork esql, String cur_usr){
		try{
			boolean searchMenu = true;
			while(searchMenu) {
				String searchkey;
				System.out.println("\nSEARCH MENU");
				System.out.println("1. Search by userid");
				System.out.println("2. Search by email");
				System.out.println("3. Search by Name");
				System.out.println("9. Go back");
				switch (readChoice()){
					case 1: 
						System.out.print("Enter userid: ");
						searchkey=in.readLine(); 
						search_helper(esql,cur_usr,searchkey,1);
						break;
					case 2: 
						System.out.print("Enter email: ");
						searchkey=in.readLine(); 
						search_helper(esql,cur_usr,searchkey,2);
						break;
					case 3: 
						System.out.print("Enter Name: ");
						searchkey=in.readLine(); 
						search_helper(esql,cur_usr,searchkey,3);
						break;
					case 9: 
						searchMenu = false; 
						break;
					default : 
						System.out.println("Unrecognized choice!"); 
						break;
				}
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end	

}//end ProfNetwork
