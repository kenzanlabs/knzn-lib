package knzn.ldap;

/**
 * The {@code QuerySanitizer} class is designed to prevent denial-of-service attacks on an LDAP read by escaping metacharacters from LDAP search filters.
 * The {@code QuerySanitizer} is thread-safe for access by multiple instances.
 * @author tgolden
 */
public class QuerySanitzer {

  /**
   * Replaces the following metacharacters with their ASCII code equivalents. This method should be used on the user-input portions of LDAP search filters.
   * <ul>
   * <li>{@literal *}</li>
   * <li>{@literal \}</li>
   * <li>{@literal (}</li>
   * <li>{@literal )}</li>
   * <li>The null terminator character, \u0000</li>
   * @param unsafeQuery A {@code String} to escape for metacharacters.
   * @return A safe {@code String} which does not contain LDAP metacharacters.
   */
  public String escapeQuery(String unsafeQuery) {
    return unsafeQuery
        .replace("\\", "\\5C")
        .replace("*", "\\2A")
        .replace("(", "\\28")
        .replace(")", "\\29")
        .replace("\u0000", "\\00");
  }
  
}
