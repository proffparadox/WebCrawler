import java.util.Objects;

public class UrlObject {
  String url;
  int depth;

  public UrlObject(String url, int depth) {
    this.url = url;
    this.depth = depth;
  }

  @Override
  public String toString() {
    return "{\"UrlObject\":{"
        + "\"url\":\"" + url + "\""
        + ",\"depth\":\"" + depth + "\""
        + "}}";
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UrlObject)) {
      return false;
    }
    UrlObject urlObject = (UrlObject) o;
    return getDepth() == urlObject.getDepth() && getUrl().equals(urlObject.getUrl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUrl(), getDepth());
  }
}
