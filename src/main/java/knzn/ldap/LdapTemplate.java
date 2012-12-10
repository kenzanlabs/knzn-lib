package knzn.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * The {@code LdapTemplate} class provides convenience methods for searching LDAP {@link DirContext} objects with the LDAP query syntax. When searching, a
 * {@link SearchResultHandler} is provided as a callback, which will be invoked on each search result. The output of the {@link SearchResultHandler#handle(SearchResultWrapper)}
 * method is added to the list of objects to return from the search function, as specified by the generic type argument of that handler.
 */
public class LdapTemplate{

  private static final Logger LOGGER = Logger.getLogger(LdapTemplate.class.getSimpleName());
  
  private final Hashtable<String, String> env;

  /**
   * Creates a new {@link LdapTemplate} with the given configuration.
   * @param env A {@link Hashtable} of properties to configure the {@link InitialDirContext}.
   */
  public LdapTemplate(final Hashtable<String, String> env) {
    this.env = env;
  }

  /**
   * Searches the given context using the specified filter, and uses the provided {@link SearchResultHandler} to map the {@link SearchResult results} to
   * a Java object.
   * @param ldapTemplate An {@link LdapTemplate} to {@link #search(String, String, SearchResultHandler, SearchControls) search}.
   * @param name A {@code String} representing the context root to search, including all subtrees.
   * @param filter A {@code String} representing the LDAP query.
   * @param srMapper A {@code SearchResultHandler} to map {@link SearchResult} objects to domain objects.
   * @return A {@code List} of domain objects, as specified by the generic type arguments for the mapper.
   * @deprecated Clients should prefer searching on the {@code LdapTemplate} directly, and providing their own {@link SearchControls}.
   * @see {@link #search(String, String, SearchResultHandler, SearchControls)}
   */
  @Deprecated
  public static <E> List<E> search(final LdapTemplate ldapTemplate, final String name, final String filter, final SearchResultHandler<E> srMapper) {      
  	final SearchControls searchControls = new SearchControls();
  	searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      
  	return ldapTemplate.search(name, filter, srMapper, searchControls);
  }

  /**
   * Searches the given context using the specified filter. Maps results with the given handler, and applies any custom search constraints.
   * @param name A {@code String} representing the context to search.
   * @param filter A {@code String} representing the LDAP search filter.
   * @param srMapper A {@link SearchResultHandler} that maps {@link SearchResult results} to a specified domain object.
   * @param constraints Any custom {@link SearchControls} to modify the search operation, such as cascading onto subtrees.
   * @return A {@code List} of domain objects specified by the generic type argument.
   */
  public <E> List<E> search(final String name, final String filter, final SearchResultHandler<E> srMapper, final SearchControls constraints) {
    DirContext ctx = null;
    final List<E> results = new ArrayList<E>();
    try {
      ctx = new InitialDirContext(env);

      LOGGER.info(" - search: " + name);
      LOGGER.info(" - filter: " + filter);
      
      final NamingEnumeration<SearchResult> answer = ctx.search(name, filter, constraints);

      while (answer.hasMoreElements()) {
        results.add(srMapper.handle(new SearchResultWrapper(answer.nextElement())));
      }
      LOGGER.info(" - count: " + results.size());

    } catch (final NamingException e) {
      LOGGER.log(Level.SEVERE, "Could not search ldap", e);
    }finally{
      try {
        ctx.close();
      } catch (final NamingException e) {
        LOGGER.log(Level.SEVERE, "Could not close context", e);
      }
    }

    return results;
  }
}