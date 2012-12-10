package knzn.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public final class ContextFactory {
  
  private ContextFactory() {}
  
	private static Hashtable<String, String> env = InitialDirectoryContextFactory.createBaseEnv("uid=admin, ou=system", "secret", "o=sevenSeas", "simple");
	
	public static InitialContext create() throws NamingException {
	  env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.jndi.ServerContextFactory");
	  return new InitialContext(env);
	}
	
	public static InitialDirContext createDir() throws NamingException {
	  env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.jndi.ServerContextFactory");
		return new InitialDirContext(env);
	}
}
