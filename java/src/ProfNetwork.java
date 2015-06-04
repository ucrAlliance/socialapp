/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
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

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
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
            System.out.println("MAIN MENU");
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
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");//DONE
                System.out.println("4. Send Friend Request");
                System.out.println("5. View/Delete messages");//DONE
                System.out.println("6. Change password");//DONE
                System.out.println("7. Search people");//DONE
                System.out.println("8. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser); break;
                   //case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: sendMessage(esql, authorisedUser); break;
                   //case 4: SendRequest(esql); break;
                   case 5: ViewMessages(esql, authorisedUser); break;
                   case 6: ChangePassword(esql, authorisedUser); break;
                   case 7: Search(esql); break;
                   case 8: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
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
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
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
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
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
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

public static void FriendList(ProfNetwork esql, String current_usr){
	try{
		String query4 = String.format("DROP TABLE IF EXISTS FRIENDS");
		esql.executeUpdate(query4);
		String query = String.format("CREATE TEMP TABLE FRIENDS AS SELECT connectionId FROM CONNECTION_USR WHERE (userId = '%s') AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE (connectionId = '%s') AND status = 'Accept'", current_usr, current_usr);
		esql.executeUpdate(query);
		String query2 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE (userId = '%s') AND status = 'Accept' UNION SELECT userId FROM CONNECTION_USR WHERE (connectionId = '%s') AND status = 'Accept'", current_usr, current_usr);
		esql.executeQueryAndPrintResult(query2);
		System.out.println("Friends table created!");
		
		boolean optionsMenu = true;
		while(optionsMenu) {
			System.out.println("OPTIONS MENU");
            System.out.println("---------");
            System.out.println("1. Send Message to friend");
            System.out.println("2. Send Request");
            System.out.println("3. View Profile");
            System.out.println("4. View a friends 'friend' list");
            System.out.println("5. Go back to Main Menu");
            switch (readChoice()){
                case 1: 
                	int num = 0;
                	String friend_id = "";
                	do{
                		try{
                			System.out.print("/tEnter userId of person to send message to: ");
                			friend_id = in.readLine();
                			String query3 = String.format("SELECT userId, connectionId FROM FRIENDS WHERE userId = '%s' OR connectionId = '%s'", friend_id, friend_id);
                			num = esql.executeQuery(query3);
                			System.out.println("User exists in FRIENDS!");
                			}catch(Exception e){
                				System.err.println(e.getMessage());
                			}
                	}while(num <= 0);
                	sendMessage(esql, current_usr); 
                	break;
                //case 2: sendRequest(esql, current_usr); break;
                case 3: Search(esql); break;
                case 4: 
                	
                	FriendList(esql, current_usr); break;
                case 5: optionsMenu = false; break;
                default : System.out.println("Unrecognized choice!"); break;
            }
        }
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
}//end


/**public static void SendRequest(ProfNetwork esql, String current_usr){
	try{
		System.out.print("\tEnter a userId to send a request to: ");
		String user_id = in.readLine();
		String query = String.format("SELECT userId FROM USR WHERE userId = '%s'", user_id);
		int valid = esql.executeQuery(query);
		if(valid > 0)
		{
			System.out.println("User is exists/is valid!");
		}
		//need to make a checkLevel() function that recursively finds how many friends you
		//have and then finds there friends up to three levels so you know if you can 
		//send them a request
}//end**/

public static void sendMessage(ProfNetwork esql, String current_usr){
	try{
		System.out.print("\tEnter in the userId of who to send a message too: ");
		String receiver_id = in.readLine();
		
		String query = String.format("SELECT msgId FROM MESSAGE ORDER BY msgId DESC LIMIT 1");
		System.out.println("This is the last messageId: ");
		esql.executeQueryAndPrintResult(query);
		
		System.out.print("\tEnter in this value +1 for a valid messageId: ");
		String message_id = in.readLine();
		System.out.print("\tEnter the contents of the message you would like to send. Hit enter when done: ");
		String contents = in.readLine();
		java.util.Date date = new java.util.Date();
		Timestamp myTimestamp = new Timestamp(date.getTime());
		String S = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(myTimestamp);
		System.out.println(S);
		String query3 = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '0', 'Sent')", message_id, current_usr, receiver_id, contents, S);
		esql.executeUpdate(query3);
		System.out.println("Message Sent!");
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
}//end

public static void ViewMessages(ProfNetwork esql, String current_usr){
	try{
		String query = String.format("SELECT M.msgId, M.contents FROM MESSAGE M, USR U WHERE M.receiverId = '%s'", current_usr);
		esql.executeQueryAndPrintResult(query);
		System.out.println("Messages Found!");
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	try{
		System.out.print("\tEnter a messageId to delete or hit enter to do nothing: ");
		String delete_message = in.readLine();
		if(delete_message != "")
		{
			String query = String.format("UPDATE MESSAGE SET deleteStatus = deleteStatus + 1 WHERE receiverId = '%s'", current_usr);
			esql.executeUpdate(query);
			System.out.println("Message deleted!");
		}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	/**try{
		String query = String.format("DELETE FROM MESSAGE WHERE deleteStatus = 3");
		esql.executeQuery(query);
		}catch(Exception e){
			System.out.println("Message remains in database because sender still holds onto it");
		}**/
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
