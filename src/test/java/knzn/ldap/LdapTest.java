package knzn.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.unit.AbstractServerTest;
import org.junit.Before;
import org.junit.Test;

public class LdapTest extends AbstractServerTest {

  @Before
  public void setUp() throws Exception {

    setupPartition();

    super.setUp();

    final InputStream inStream = new FileInputStream(new File(
            "src/test/resources/demo.ldif"));
    importLdif(inStream);
  }

  @Test
  public void testImport() throws NamingException {
    final Set<String> result = searchDNs("(ObjectClass=*)", "",
            SearchControls.ONELEVEL_SCOPE);

    assertTrue("testImport failed", result.contains("ou=groups,o=sevenSeas"));
    assertTrue("testImport failed", result.contains("ou=people,o=sevenSeas"));
  }

  @Test
  public void testPartition() throws NamingException {

    final InitialContext ctx = ContextFactory.create();

    final DirContext dir = (DirContext) ctx.lookup("");

    assertNotNull("dir is null", dir);

    final Attributes attributes = dir.getAttributes("");

    assertNotNull("attributes not found", attributes);
    assertEquals("attribute did not match", "sevenseas", attributes.get("o").get());

    final Attribute attribute = attributes.get("objectClass");

    assertNotNull("attribute not null", attribute);
    assertTrue("attribute does not contain top", attribute.contains("top"));
    assertTrue("attribute does not contain organization", attribute.contains("organization"));

  }

  @Test
  public void testSearch() {
    Hashtable<String, String> env = InitialDirectoryContextFactory.createBaseEnv("uid=admin, ou=system", "secret", "o=sevenSeas", "simple");
    env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.jndi.ServerContextFactory");

    final LdapTemplate temp = new LdapTemplate(env);

    final SearchResultHandler<Object> result = new SearchResultHandler<Object>() {

      public Object handle(final SearchResultWrapper searchResult)
              throws NamingException {
        return searchResult.getAttribute("ou");
      }
    };
    final SearchControls search = new SearchControls();
    search.setSearchScope(SearchControls.ONELEVEL_SCOPE);

    final List<Object> list = temp.search("", "(ou=*)", result, search);

    assertTrue("list does not contain people", list.contains("people"));
    assertTrue("list does not contain groups", list.contains("groups"));
  }

  private Set<String> searchDNs(final String filter, final String base,  final int scope) throws NamingException {
    final DirContext appRoot = ContextFactory.createDir();

    final SearchControls controls = new SearchControls();
    controls.setSearchScope(scope);
    final NamingEnumeration<SearchResult> result = appRoot.search(base, filter, controls);

    // collect all results
    final HashSet<String> entries = new HashSet<String>();

    while (result.hasMore()) {
      final SearchResult entry = result.next();
      entries.add(entry.getName());
    }

    return entries;
  }

  private void setupPartition() throws NamingException {

    final MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
    pcfg.setName("sevenSeas");
    pcfg.setSuffix("o=sevenseas");

    final Set<String> indexedAttrs = new HashSet<String>();
    indexedAttrs.add("objectClass");
    indexedAttrs.add("o");
    pcfg.setIndexedAttributes(indexedAttrs);

    final Attributes attrs = new BasicAttributes(true);

    Attribute attr = new BasicAttribute("objectClass");
    attr.add("top");
    attr.add("organization");
    attrs.put(attr);

    attr = new BasicAttribute("o");
    attr.add("sevenseas");
    attrs.put(attr);

    pcfg.setContextEntry(attrs);

    final Set<MutablePartitionConfiguration> pcfgs = new HashSet<MutablePartitionConfiguration>();
    pcfgs.add(pcfg);

    configuration.setContextPartitionConfigurations(pcfgs);

    final File workingDirectory = new File("server-work");
    configuration.setWorkingDirectory(workingDirectory);

  }
}
