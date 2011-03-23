package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;


public class CrudTest extends TestCase {

  private static Timestamp createTimestamp(){
    final Calendar cal = Calendar.getInstance();
    return new Timestamp(cal.getTimeInMillis());
  }


	private DatabaseImpl db = null;

	public void testCreate() {

		db.update("DROP Table foo IF EXISTS", null);
		db.update("create table foo (color varchar(12), test varchar(20))", null);

	}

	public void testDelete(){
		final String colorParam = "Yellow";

    final String[] params = {colorParam};

		db.update("DELETE FROM foo WHERE color = ?", params);
		assertSelect(null, colorParam);
	}

	public void testInsert() {
		final String sql = "INSERT INTO foo (color, test) Values (?, ?)";

		final List<String> colors = new ArrayList<String>();

		colors.add("Red");
		colors.add("Blue");
		colors.add("Yellow");
		colors.add("Green");

		for(final String item : colors){
			final Object[] params = {item, null};
			db.update(sql, params);
		}

		assertSelect("Red", null);
		System.out.println("\nTable Populated");
	}


	public void testTimestamp(){
		db.update("DROP TABLE ts_test IF EXISTS", null);
		db.update("CREATE TABLE ts_test (ts timestamp)", null);

		final List<Timestamp> lstTs = new ArrayList<Timestamp>();

		for(int i = 0; i < 4; i++){
			final Timestamp ts = createTimestamp();
			final Object[] params = {ts};
			lstTs.add(ts);

			db.update("INSERT INTO ts_test (ts) VALUES (?)", params);
		}

		final ResultSetHandler<Timestamp> resultset = new ResultSetHandler<Timestamp>() {

			public Timestamp handle(final ResultSet resultSet) throws SQLException {
				return resultSet.getTimestamp("ts");
			}
		};

		final List<Timestamp> result = db.query("SELECT ts FROM ts_test", resultset);

		for(final Timestamp row : result){
			assertTrue(lstTs.contains(row));
		}

	}
	
	public void testRollback() throws SQLException{
		db.runTransaction(new Transaction() {
			
			public void doInTransaction(TransactionDatabase database) throws SQLException {
				database.startTransaction();
				String color = "Green";
				database.update("DELETE FROM foo WHERE color = ?", new String[]{color});
				
				throw new SQLException("Rollback Test Exception");
			}
		});
		assertSelect("Green", null);
	}

	public void testUpdate(){
		final String[] params = {"Orange", "Red"};
		int test1 = db.update("UPDATE foo SET color = ? WHERE color = ?", params);
                final String[] newParams = {"Color", "Not a Color"};
                int test2 = db.update("UPDATE foo SET color = ? WHERE color = ?", newParams);
		assertSelect("Orange", "Red");
	}

	@Override
	protected void setUp() throws Exception {

		if(db == null) {
			db = new DatabaseImpl(DataSourceFactory.createDataSource());
		}
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private void assertSelect(final String contains, final String notContains){

		final ResultSetHandler<String> color = new ResultSetHandler<String>() {

			public String handle(final ResultSet resultSet) throws SQLException {
				return resultSet.getString("color");
			}
		};

		final List<String> lstColor = db.query("SELECT color FROM foo", color);


		if(contains != null) {
      assertTrue(lstColor.contains(contains));
    }

		if(notContains != null) {
      assertFalse(lstColor.contains(notContains));
    }
	}


}
