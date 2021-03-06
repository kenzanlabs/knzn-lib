package knzn.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class SearchResultWrapper{
  private final SearchResult searchResult;

  public SearchResultWrapper(final SearchResult searchResult) {
    this.searchResult = searchResult;
  }

  public String getAttribute(final String name) throws NamingException {
    String value = null;

    final Attributes attributes = searchResult.getAttributes();

    if(attributes != null){
      final Attribute attr = attributes.get(name);
      if (attr != null) {
        value = (String) attr.get();
      }
    }
    return value;
  }

  public Attributes getAttributes() {
    return this.searchResult.getAttributes();
  }

  public List<String> getAllAttributes(final String attributeName) throws NamingException {
    final List<String> attributeList = new ArrayList<String>();
    final Attribute attr = searchResult.getAttributes().get(attributeName);
    if (attr != null) {
      for (final NamingEnumeration<?> all = attr.getAll(); all.hasMore();) {
        final String attribute = (String) all.next();
        attributeList.add(attribute);
      }
    }
    return attributeList;
  }
}