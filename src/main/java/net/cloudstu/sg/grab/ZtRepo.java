package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockDao;
import net.cloudstu.sg.entity.StockModel;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.formatter.StringTrimFormatter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.*;

/**
 * 涨停预测获取
 *
 * @author zhiming.li
 * @date 2018/4/27
 */
@Slf4j
@TargetUrl("http://www.178448.com/fjzt-1.html\\?page=\\d+")
@ExtractBy(value = "//div[@class='datalist']/table/tbody/tr", multi = true)
public class ZtRepo implements AfterExtractor {

    private static Set<String> forecasterSet = new LinkedHashSet<>();

    public static Map<String, List<String>> stockWithForecasterMap = new LinkedHashMap<>();

    @Override
    public void afterProcess(Page page) {
        if (forecasterSet.contains(this.forecasterName)) {
            log.warn(String.format("%s-%s-%s", this.forecasterName,
                    getStockName(this.containStockName), getForecasterTime(this.containStockName)));
            if (StringUtils.isEmpty(stockWithForecasterMap.get(this.forecasterName))) {
                List<String> stocks = new ArrayList<>();
                stocks.add(getStockName(this.containStockName));
                stockWithForecasterMap.put(this.forecasterName, stocks);
            } else {
                stockWithForecasterMap.get(this.forecasterName).add(getStockName(this.containStockName));
            }
        }
    }

    public synchronized static void loadForecasters(List<String> forecasters) {
        forecasterSet.addAll(forecasters);
    }

    public synchronized static void clearForecasters(List<String> forecasters) {
        forecasterSet.clear();
    }

    private String getStockName(String containStockName) {
        if (StringUtils.isEmpty(containStockName)) {
            return "";
        }
        return containStockName.split("</a>")[1].split(" ")[3];
    }

    private String getForecasterTime(String containStockName) {
        if (StringUtils.isEmpty(containStockName)) {
            return "";
        }
        return String.format("%s %s", containStockName.split("</a>")[1].split(" ")[5], containStockName.split("</a>")[1].split(" ")[6]);
    }

    /**
     * 获取涨停预测
     *
     * @param startPage 抓取起始页
     * @param endPage   抓取结束页
     */
    public static void grab(int startPage, int endPage) {

        OOSpider ooSpider = null;

        try {
            ooSpider = buildOOSpider();
            for (int i = startPage; i <= endPage; i++) {
                try {
                    ZtRepo repo = ooSpider.get(getGrabUrl(i));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        } finally {
            if (ooSpider != null) {
                ooSpider.close();
            }
        }
    }


    public static String getGrabUrl(int page) {
        return String.format("http://www.178448.com/fjzt-1.html?page=%s", page);
    }

    /**
     * 初始化spider
     *
     * @return spider
     */
    private static OOSpider buildOOSpider() {

        // site公共信息设置
        Site site = Site.me()
                .setSleepTime(100)
                .setDomain(DOMAIN)
                .setUserAgent(
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

        OOSpider ooSpider = OOSpider.create(site,
                new ConsolePageModelPipeline(), ZtRepo.class);
        return ooSpider;
    }

    private static final String DOMAIN = "www.178448.com";

    @ExtractBy(value = "//a/text()")
    @Formatter(formatter = StringTrimFormatter.class)
    private String forecasterName;

    @ExtractBy(value = "//")
    @Formatter(formatter = StringTrimFormatter.class)
    private String containStockName;

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        StockDao stockDao = SpringUtil.getBean(StockDao.class);

        long beginTime = System.currentTimeMillis();

        //TODO 这块不统一
        List<String> forecasters = new ArrayList<>();
        forecasters.add("今古传奇吗");
        forecasters.add("maosc01");
        forecasters.add("天赋闲荡");
        forecasters.add("股道之友");
        forecasters.add("一字连板");
        forecasters.add("不是我迷恋花");
        forecasters.add("xcr033@sina.com");
        forecasters.add("品桦Ph");

        loadForecasters(forecasters);

        grab(1, 50);
        System.out.println("抓取完成！耗时【" + (System.currentTimeMillis() - beginTime) + "】");

        for (String forecasterName : stockWithForecasterMap.keySet()) {
            System.out.println(forecasterName);
            List<String> stockNames = stockWithForecasterMap.get(forecasterName);
            for(String stockName : stockNames) {
                System.out.println(stockName);

                StockModel stock = stockDao.selectLikeName(stockName);
                if(stock != null) {
                    stockDao.update(stock.getCode());
                }

            }
            System.out.println();
        }
    }
}
