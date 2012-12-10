package knzn.ldap;

import java.util.Hashtable;

import javax.naming.Context;

public final class InitialDirectoryContextFactory {

  private InitialDirectoryContextFactory() {
  }
 
  public static Hashtable<String, String> createBaseEnv(final String principle,
          final String credentials, final String host, final String authentication) {
    Hashtable<String, String> env = new Hashtable<String, String>();

    // basic settings
    env.put(Context.PROVIDER_URL, host);
    env.put(Context.SECURITY_PRINCIPAL, principle);
    env.put(Context.SECURITY_CREDENTIALS, credentials);
    env.put(Context.SECURITY_AUTHENTICATION, authentication);
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    return env;
  }

  public static Hashtable<String, String> createPooledEnv(
          final String principle, final String credentials, final String host, final String authentication,
          final int connectTimeout, final int poolSize, final int poolTimeout) {
    Hashtable<String, String> env = createBaseEnv(principle, credentials, host, authentication);
    env.put("com.sun.jndi.ldap.connect.pool", "true");
    env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectTimeout));
    env.put("com.sun.jndi.ldap.connect.pool.maxsize", String.valueOf(poolSize));
    env.put("com.sun.jndi.ldap.connect.pool.timeout", String.valueOf(poolTimeout));
    return env;
  }
}