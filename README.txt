Project Name: knzn-lib
Last Modified: 2/9/2011
Version 0.1

-----------------------------------------------------------------------------
Database Library: src/main/java/knzn/db
Database.java
DatabaseImpl.java
ResultSetHandler.java
Transaction.java
TransactionalDatabase.java
TransactionDatabase.java

Documentation:

Constructors
DatabaseImpl(final String dataSourceName)
Constructor attempts to lookup data source with the specified name

DatabaseImpl(final DataSource dataSource)
Constructor expects a provided data source

Methods
query(final String sql, final Objec[] params, final ResultSetHandler result) : List
RETURNS: List of results for a column specified in the Sql statement
Parameters:
sql - Sql state to query the database
SELECT * FROM user WHERE username = ? AND password = ?
params - Array of parameters to add to the Sql statement
Object[] params = {"username", "password"};
result - Interface used to determine what column to select and what data type to return
final ResultSetHandler<String> color = new ResultSetHandler<String>() { 
public String handle(final ResultSet resultSet) throws SQLException { 
return resultSet.getString("username");}};


update(final String sql, final Object[] params) : void
Runs an update sql update statement
Parameters:
sql - Sql statement used to update the database
params - Array of parameters to add to the Sql statement. Can be Null

runTransaction(final Transaction transaction) : void
Runs a sql command with provided functionality such as Rollback and Commit
If an exception is thrown in runTransaction then a rollback is executed
(see below for more on runTransaction) 

TransactionDatabase Class
Provides an interface for executing transactional commands using the DatabaseImpl Class

DatabaseImpl db = new DatabaseIml(dataSource); 
db.runTransaction(new Transaction() { 
public void doInTransaction(TransactionDatabase database) throws SQLException { 
database.startTransaction(); 
String color = "Green"; 
database.update("DELETE FROM foo WHERE color = ?", new String[]{color}); }});
The result is the update statement is executed as a transactional database

------------------------------------------------------------------------------
Ldap Library: src/main/java/knzn/ldap
InitialDirectionContextFactory.java
LdapTemplate.java
SearchResultHandler.java
SearchResultWrapper.java

Documentation:
InitialContextFactory Class
Provides static methods for building an initial context used to communicate with an ldap server


createBaseEnv(String principle, String credentials, String host, String authentication) : Hashtable
Parameters:
principle - Security Principle
credentials - Security Credentials
host - Provider Url
authentication - Security Authentication type


createPooledEnv(String principle, String credentials, String host, String authentication, int connectTimeout, int poolSize, int poolTimeout) : Hashtable
Parameters:
principle - Security Principle
credentials - Security Credentials
host - Provider Url
authentication - Security Authentication type
connectionTimeout - How long to stay connected
poolSize - Maximum pool size
poolTimout - How long before the poot times out


LdapTemplate Class

Constructor

LdapTemplate(Hashtable env)
the Initial context must be provided


Method

search(String name, String filter, SearchResultHandler srMapper, SearchControls constraints) : List
searches an ldap server for specific attributes
RETURNS: List of search results


Parameters:
name - the name of the context to search
filter - search filter provided
Filter syntax example: "(cn=*)" * means all
srMapper - interface that defines the what attributes to get and what data type to return
SearchResultHandler<Object> result = new SearchResultHandler<Object>() {
public Object handle(SearchResultWrapper searchResult) throws NamingException {
return searchResult.getAttribute("ou"); }};
constraints - the search controls that control the search. Can be Null

 ## LICENSE
Copyright 2017 Kenzan, LLC <http://kenzan.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.