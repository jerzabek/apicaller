package ga.jarza.apicaller;

public class Header {

  private String key, value;

  /**
   * Header object used to hold data about the user defined key-value objects stored in the TableView.
   * @param key Header key
   * @param value Header value
   */
  public Header(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
