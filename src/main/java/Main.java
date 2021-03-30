import java.io.File;
import java.io.IOException;
import org.json.simple.parser.ParseException;

public class Main {
  public static void main(String[] args) throws InterruptedException, IOException, ParseException {
    WebCrawler webCrawler = new WebCrawler(2);
    File file = new File("urllist.json");
    webCrawler.read(file);
    webCrawler.crawl("https://en.wikipedia.org/wiki/DisplayPort");
    webCrawler.write("urlList.json");
  }
}
