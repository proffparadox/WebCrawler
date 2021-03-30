import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageGet implements Callable<PageGet> {
  static final int TIMEOUT = 60000;

  private String url;
  private int depth;
  private Set<String> urlList = new HashSet<>();

  public PageGet(String url, int depth){
    this.url = url;
    this.depth = depth;
  }

  @Override
  public PageGet call() throws Exception {
    System.out.println(">> Depth: " + depth + " [" + url + "]");
    Document document = Jsoup.connect(url).timeout(TIMEOUT).get();
    processLinks(document.select("a[href^=\"/wiki/\"]"));
    return this;
  }
  private void processLinks(Elements links){
    for (Element link : links) {
      urlList.add(link.attr("abs:href"));
    }
  }
  public int getDepth() {
    return depth;
  }

  public Set<String> getUrlList() {
    return urlList;
  }
}
