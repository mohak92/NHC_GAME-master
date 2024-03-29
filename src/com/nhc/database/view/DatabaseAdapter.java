package com.nhc.database.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
	//Table name
	private static final String LOGIN_TABLE = "user";
	//Table unique id
	public static final String COL_ID = "id";
	//Table username and password columns 
	public static final String COL_USERNAME = "username";
	public static final String COL_PASSWORD = "password";
	public static final String COL_FULLNAME = "fullname";
	public static final String COL_AGE = "age";
	public static final String COL_SEX = "sex";
	public static final String COL_STATE = "state";
	
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	/**
	 * The adapter constructor. 
	 * @param context
	 */
	public DatabaseAdapter(Context context) {
		this.context = context;
	}
	
	/**
	 * Creates the database helper and gets the database. 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Closes the database.
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Creates the user name and password.
	 * 
	 * @param username The username.
	 * @param password The password.
	 * @return
	 */
	public long createUser(String username, String password, String fullname, String age, String sex, String state) {
		System.out.println("username is " + username);
		System.out.println("password is " + password);
		System.out.println("fullname is " + fullname);
		System.out.println("age is " + age);
		System.out.println("sex is " + sex);
		System.out.println("state is " + state);
		
		ContentValues initialValues = createUserTableContentValues(username, password, fullname, age, sex, state);
		return database.insert(LOGIN_TABLE, null, initialValues);
	}
	
	/**
	 * Removes a user's details given an id.
	 * 
	 * @param rowId Column id. 
	 * @return
	 */
	public boolean deleteUser(long rowId) {
		return database.delete(LOGIN_TABLE, COL_ID + "=" + rowId, null) > 0;
	}
	
	public boolean updateUserTable(long rowId, String username, String password, String fullname, String age, String sex, String state) {
		ContentValues updateValues = createUserTableContentValues(username, password, fullname, age, sex, state);
		return database.update(LOGIN_TABLE, updateValues, COL_ID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Retrieves the details of all the users stored in the login table.
	 * 
	 * @return
	 */
	public Cursor fetchAllUsers() {
		return database.query(LOGIN_TABLE, new String[] { COL_ID, COL_USERNAME, 
				COL_PASSWORD, COL_FULLNAME, COL_AGE, COL_SEX, COL_STATE }, null, null, null, null, null);
	}
	
	/**
	 * Retrieves the details of a specific user, given a username and password.
	 * 
	 * @return
	 */
	public Cursor fetchUser(String username, String password) {
		 Cursor myCursor = database.query(LOGIN_TABLE, 
				new String[] { COL_ID, COL_USERNAME, COL_PASSWORD }, 
				COL_USERNAME + "='" + username + "' AND " + 
				COL_PASSWORD + "='" + password + "'", null, null, null, null);
		
		
		
		
		/*Cursor myCursor = database.query(LOGIN_TABLE, 
				new String[] { COL_ID, COL_USERNAME, COL_PASSWORD , COL_FULLNAME}, 
				COL_USERNAME + "='" + username + "' AND " + 
				COL_PASSWORD + "='" + password + "' AND "  + COL_FULLNAME + "='" + fullname + "'", null, null, null, null);
		*/
		
		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		return myCursor;
	}
	
	/**
	 * Returns the table details given a row id.
	 * @param rowId The table row id. 
	 * @return
	 * @throws SQLException
	 */
	public Cursor fetchUserById(long rowId) throws SQLException {
		Cursor myCursor = database.query(LOGIN_TABLE, 
				new String[] { COL_ID, COL_USERNAME, COL_PASSWORD , COL_FULLNAME}, 
				COL_ID + "=" + rowId, null, null, null, null);
		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		return myCursor;
	}
	
	public Cursor fetchUserByUname(String uname) throws SQLException {
		Cursor myCursor = database.query(LOGIN_TABLE, 
				new String[] { COL_ID, COL_USERNAME, COL_PASSWORD , COL_FULLNAME, COL_AGE, COL_SEX, COL_STATE}, 
				COL_USERNAME + "=" +  '"' + uname + '"' , null, null, null, null, null);
		
		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		
		return myCursor;
	}
	
	/**
	 * Stores the username and password upon creation of new login details.
	 * @param username The user name.
	 * @param password The password.
	 * @return The entered values. 
	 */
	private ContentValues createUserTableContentValues(String username, String password, String fullname, String age, String sex, String state) {
		ContentValues values = new ContentValues();
		values.put(COL_USERNAME, username);
		values.put(COL_PASSWORD, password);
		values.put(COL_FULLNAME, fullname);
		values.put(COL_AGE, age);
		values.put(COL_SEX, sex);
		values.put(COL_STATE, state);
		return values;
	}
}
