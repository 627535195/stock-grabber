package net.cloudstu.sg.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockHoldDao;
import net.cloudstu.sg.entity.StockHoldModel;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
public abstract class ShiPanEUtil {

//    public static String API_URL = "http://39.105.55.173:8888/%s";
    public static String API_URL = "http://localhost:8888/%s";
    /**
     * 定量定价卖出
     *
     * @param code   代码
     * @param amount 股数
     * @param price  价格
     */
    public static void sell(String code, int amount, double price) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        Map map = new HashMap(8);
        map.put("action", "SELL");
        map.put("symbol", code);
        map.put("priceType", 0);
        map.put("type", "LIMIT");
        map.put("price", price);
        map.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(map), getHeaders());

        try {
            restTemplate.postForObject(url, request, String.class);
        }catch (Exception e) {
            log.error("交易异常！", e);
        }
    }

    /**
     * 五档即时成交剩余撤销 卖出
     *
     * @param code   代码
     * @param amount 股数
     */
    public static void sell(String code, int amount) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        Map map = new HashMap(8);
        map.put("action", "SELL");
        map.put("symbol", code);
        map.put("priceType", 4);
        map.put("type", "MARKET");
        map.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(map), getHeaders());

        try {
            restTemplate.postForObject(url, request, String.class);
        }catch (Exception e) {
            log.error("交易异常！", e);
        }
    }

    /**
     * 定量定价买入
     *
     * @param code   代码
     * @param amount 股数
     */
    public static void buy(String code, int amount, double price) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        Map map = new HashMap(8);
        map.put("action", "BUY");
        map.put("symbol", code);
        map.put("priceType", 0);
        map.put("type", "LIMIT");
        map.put("price", price);
        map.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(map), getHeaders());
        try {
            restTemplate.postForObject(url, request, String.class);
        }catch (Exception e) {
            log.error("交易异常！", e);
        }
    }

    /**
     * 五档即时成交剩余撤销 买入
     *
     * @param code   代码
     * @param amount 股数
     */
    public static void buy(String code, int amount) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        Map map = new HashMap(8);
        map.put("action", "BUY");
        map.put("symbol", code);
        map.put("priceType", 4);
        map.put("type", "MARKET");
        map.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(map), getHeaders());
        try {
            restTemplate.postForObject(url, request, String.class);
        }catch (Exception e) {
            log.error("交易异常！", e);
        }

    }

    /**
     * 撤销所有委托
     */
    public static void cancelAll() {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        try {
            restTemplate.delete(url);
        }catch (Exception e) {
            log.error("交易异常！", e);
        }
    }

    public static List<StockHoldModel> getHoldStocks() {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "positions");
        String content;
        try {
            content = restTemplate.getForEntity(url, String.class).getBody();
        }catch (Exception e) {
            log.error("获取持仓失败！", e);
            return Collections.emptyList();
        }
        if(StringUtils.isEmpty(content)) {
            return Collections.emptyList();
        }

        List<StockHoldModel> stockHolds = new ArrayList<>(10);

        try {
            JSONObject jsonObject = JSON.parseObject(content);
            int count = jsonObject.getIntValue("count");
            JSONObject dataTable = (JSONObject) jsonObject.get("dataTable");
            JSONArray data = (JSONArray) dataTable.get("rows");

            if(data.size()!=count) {
                return Collections.emptyList();
            }
            for(int i=0; i<data.size(); i++) {
                JSONArray tmp = (JSONArray)data.get(i);
                StockHoldModel stockHold = new StockHoldModel();
                stockHold.setCode(tmp.getString(1));
                stockHold.setName(tmp.getString(2));
                stockHold.setHold(tmp.getIntValue(3));
                stockHold.setAvailableHold(tmp.getIntValue(4));
                stockHold.setNowPrice(tmp.getDoubleValue(9));
                stockHold.setExpectPrice(1000);
                stockHold.setUpdateTime(new Date());

                stockHolds.add(stockHold);
            }
        }catch (Exception e) {
            log.error("解析持仓失败！", e);
            return Collections.emptyList();
        }

        return stockHolds;
    }

    public static boolean isOk() {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "statuses");

        String statusStr;
        try {
            statusStr = restTemplate.getForEntity(url, String.class).getBody();
        }catch (Exception e) {
            log.error("获取实盘易状态异常！", e);
            return false;
        }

        return "\"运行正常\"".equals(statusStr);
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);
        long begin = System.currentTimeMillis();
//        ShiPanEUtil.buy("600381", 500);
        List<StockHoldModel> list = ShiPanEUtil.getHoldStocks();

        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        stockHoldDao.clear();

        for (StockHoldModel stockHold : list) {
            stockHoldDao.insert(stockHold);
        }

        System.out.println(String.format("cost %s s", (System.currentTimeMillis() - begin)));

    }
}
