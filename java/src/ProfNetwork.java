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
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\nDASHBOARD");
                System.out.println("---------");
                System.out.println("1. View My Profile");
                System.out.println("2. View Friend List");
                System.out.println("3. Update Profile");//FIXME
                System.out.println("4. Write a new message");
                System.out.println("5. Send Friend Request");//FIXME
                System.out.println("6. View/Delete messages");
                System.out.println("7. Change password");
                System.out.println("8. Search people");//FIXME
                System.out.println("9. Log out");

                switch (readChoice()){
                   case 1: 
							myFriendList(esql, authorisedUser); 
							break;
                   case 2: 
							UpdateProfile(esql, authorisedUser); 
							break;
                   case 3: 
							sendMessage(esql, authorisedUser); 
							break;
                   //case 4: SendRequest(esql); break;
                   case 5: 
							ViewMessages(esql, authorisedUser); 
							break;
                   case 6: 
							ChangePassword(esql, authorisedUser); 
							break;
                   case 7: 
							Search(esql); 
							break;
                   case 8: 
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
         System.out.println("\t------------------");
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

	public static void UpdateProfile(ProfNetwork esql, String current_user){
      try{
			System.out.println("\n\tUPDATE PROFILE");
         System.out.println("\t------------------");
			System.out.print("\tPlease provide the new information\n");
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
	 			query = String.format("UPDATE USR SET userid='%s', password='%s', email='%s', dateofbirth='%s' WHERE userid='%s'", login, password, email, dob, current_user);
			} else if(name.length()!=0 && dob.length()==0) {
	 			query = String.format("UPDATE USR SET userid='%s', password='%s', email='%s', name='%s' WHERE userid='%s'", login, password, email, name, current_user);
			} else if(name.length()==0 && dob.length()==0) {
	 			query = String.format("UPDATE USR SET userid='%s', password='%s', email='%s' WHERE userid='%s'", login, password, email, current_user);
			} else {
	 			query = String.format("UPDATE USR SET userid='%s', password='%s', email='%s', name='%s', dateofbirth='%s' WHERE userid='%s'", login, password, email, name, dob, current_user);
			}

         esql.executeUpdate(query);
         System.out.println ("Profile successfully updated!");
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
         System.out.println("\t----------");
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

	public static void SendConnectionTo(ProfNetwork esql, String current_usr, String receiver_id){
		try{
			//Pre Checking FIXME
      	String query3 = String.format("SELECT * FROM connection_usr WHERE userid = '%s' and connectionid = '%s' OR userid = '%s' and connectionid = '%s'", current_usr, receiver_id, receiver_id, current_usr);
         int num = esql.executeQuery(query3);
			if(num>0){
            System.out.println("A connection already exists. Please accept connection request, or wait for the connection to accept it.");
				return;
			}

			// Actual query
			String query = String.format("INSERT INTO connection_usr (userid, connectionid, status) VALUES ('%s', '%s', 'Request')", current_usr, receiver_id);
			esql.executeUpdate(query);
			System.out.println("Connection Requested!");
		}catch(Exception e){
         System.err.println (e.getMessage ());
		}
	}

	public static void ViewProfile(ProfNetwork esql, String current_usr, String profile_id, int clevel){
		try{
			//FIXME
			//Print Profile Here
			//PrintFriendList(esql, current_usr);
		
			boolean optionsMenu = true;
			while(optionsMenu) {
            System.out.println("1. Send Message");
            System.out.println("2. Send Request");
            System.out.println("3. View Friend List");
            System.out.println("4. Go back");
            switch (readChoice()){
            	case 1: 
                	SendMessageTo(esql, current_usr, profile_id); 
                	break;
               case 2: 
						SendConnectionTo(esql, current_usr, profile_id); 
						break;
               case 3: 
						FriendList(esql, current_usr, profile_id, clevel+1); 
						break;
               case 4: 
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
            System.out.println("---------");
            System.out.println("1. View Profile");//FIXME DONE(shitty user interface)
            System.out.println("2. Go Back");
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
               case 2: 
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

	public static void ViewMessages(ProfNetwork esql, String current_usr){
		try{
			// Recieved Messages
			System.out.println("RECEIVED MESSAGES");
			System.out.println("-----------------\n");
			String queryr = String.format("SELECT msgId, contents, deleteStatus, sendtime FROM MESSAGE WHERE receiverId = '%s' AND deleteStatus !=2", current_usr);
			List<List<String>> received= esql.executeQueryAndReturnResult(queryr);

			// Print Here
			for(List<String> list : received){
				System.out.println("\tmsgid: " + list.get(0));
				System.out.println("\tcontents: " + list.get(1).trim());
				System.out.println("\tdeleteStatus: " + list.get(2));
				System.out.println("\ttime: " + list.get(3));
				System.out.println("\n");
			}

			// Sent Messages
			System.out.println("SENT MESSAGES");
			System.out.println("-------------");
			String querys = String.format("SELECT msgId, contents, deleteStatus, sendtime FROM MESSAGE WHERE senderId = '%s' AND deleteStatus != 1", current_usr);
			List<List<String>> sent= esql.executeQueryAndReturnResult(querys);

			// Print Here
			for(List<String> list : sent){
				System.out.println("\tmsgid: " + list.get(0));
				System.out.println("\tcontents: " + list.get(1).trim());
				System.out.println("\tdeleteStatus: " + list.get(2));
				System.out.println("\ttime: " + list.get(3));
				System.out.println("\n");
			}

		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		try{
			String delete_message = "";
			int num = 0;
			do{
				System.out.print("\tWould you like to delete any messages? y/n: ");
				delete_message = in.readLine();
				if(delete_message.equals("y"))
				{
					System.out.print("\tEnter messageId of message you would like to delete: ");
					String message_id = in.readLine();
					String query = String.format("Select msgId FROM MESSAGES_RECEIVED WHERE msgId = '%s'", message_id);
					num = esql.executeQuery(query);
					if(num > 0)
					{
						System.out.println("message id found in MESSAGES_RECEIVED");
						String query2 = String.format("UPDATE MESSAGE SET deleteStatus = 2 WHERE receiverId = '%s'", current_usr);
						esql.executeUpdate(query2);
						System.out.println("Message removed from inbox!");
					}
					String query3 = String.format("Select msgId FROM MESSAGES_SENT WHERE msgId = '%s'", message_id);
					num = esql.executeQuery(query3);
					if(num > 0)
					{
						System.out.println("message id found in MESSAGES_SENT");
						String query4 = String.format("UPDATE MESSAGE SET deleteStatus = 1 WHERE senderId = '%s'", current_usr);
						esql.executeUpdate(query4);
						System.out.println("Message removed from outbox!");
					}
				}
				else if(delete_message.equals("n"))
				{
					System.out.println("No messages deleted from inbox/outbox");
				}
				else
				{
					System.out.println("Incorrect input!");
				}
			}while(!delete_message.equals("y") && !delete_message.equals("n"));
		}catch(Exception e){
			System.err.println(e.getMessage());
		}

		//FIXME NEED THINK OF A WAY TO FIND OUT WHEN WE ARE ABLE TO SET THE DELETESTATUS EQUAL TO 3 SO IT CAN BE REMOVED FROM DATABASE
		try{
			String query = String.format("DELETE FROM MESSAGE WHERE deleteStatus = 3");
			int num = esql.executeQuery(query);
			if(num > 0)
			{
				System.out.print(num);
				System.out.print(" Messages were deleted from the datatable.");
			}
			}catch(Exception e){
				System.out.println("Message remains in database because sender/receiver still holds onto it");
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

	public static void Search(ProfNetwork esql){
		try{
			System.out.print("\tEnter name to search: ");
			String usr_name = in.readLine();
			
			String query = String.format("SELECT U.email, E.instituitionName, E.major, E.degree, W.company, W.role FROM USR U, EDUCATIONAL_DETAILS E, WORK_EXPR W WHERE  U.name = '%s' AND U.userId = W.userId AND U.userId = E.userId", usr_name);
			esql.executeQueryAndPrintResult(query);
			System.out.println("Found USER successfully!");
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}//end	

}//end ProfNetwork
