import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebCrawler {
  public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
  private static final long PAUSE_TIME = 1000;

  private HashSet<UrlObject> visited = new HashSet<>();
  private List<Future<PageGet>> futurelist = new ArrayList<>();
  private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

  private String rootURL;
  private final int maxDepth;

  public WebCrawler(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public void crawl(String rootURL) throws InterruptedException {
    this.rootURL = rootURL;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    submitNewURL(rootURL, 0);
    while (checkPageGrabs())
      ;
    stopWatch.stop();
    System.out.println("Found " + visited.size() + " urls");
    System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
  }

  private boolean checkPageGrabs() throws InterruptedException {
    Thread.sleep(PAUSE_TIME);
    HashSet<PageGet> pageGetHashSet = new HashSet<>();
    Iterator<Future<PageGet>> futureIterator = futurelist.iterator();
    while (futureIterator.hasNext()) {
      Future<PageGet> future = futureIterator.next();
      if (future.isDone()) {
        futureIterator.remove();
        try {
          pageGetHashSet.add(future.get());
        } catch (InterruptedException | ExecutionException ignored) {
        }
      }
    }
    for (PageGet pageGet : pageGetHashSet) {
      addNewUrls(pageGet);
    }
    return futurelist.size() > 0;
  }

  private void addNewUrls(PageGet pageGet) {
    for (String s : pageGet.getUrlList()) {
      submitNewURL(s, pageGet.getDepth() + 1);
    }
  }

  private void submitNewURL(String url, int depth) {
    if ((!visited.contains(url)) && (depth <= maxDepth)) {
      visited.add(new UrlObject(url, depth));
      PageGet pageGet = new PageGet(url, depth);
      Future<PageGet> pageGetFuture = executorService.submit(pageGet);
      futurelist.add(pageGetFuture);
    }
  }

  public void write(String path) throws IOException {
    StringWriter out = new StringWriter();
    JSONArray output = new JSONArray();
    output.addAll(visited);
    FileUtils.writeStringToFile(new File(path), output.toJSONString());
  }

  public void read(File path) throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    JSONArray obj = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream(path)));
    int urlsSubmitted = 0;
    int urlsPreviouslyVisited = 0;
    System.out.println(obj.size());
    for (Object o : obj) {
      JSONObject urlobject = (JSONObject) o;
      String depth = (String) urlobject.get("depth");
      String url = (String) urlobject.get("url");
      System.out.println(depth + url);
     /*   if (depth == maxDepth - 1){
          submitNewURL(url,depth);
          urlsSubmitted++;
        } else {
          visited.add(new UrlObject(url,depth));
          urlsPreviouslyVisited++;
        }
      }
    System.out.println("Submitted: " + urlsSubmitted);
    System.out.println("Previously Visited: " + urlsPreviouslyVisited);*/
  }
}}
