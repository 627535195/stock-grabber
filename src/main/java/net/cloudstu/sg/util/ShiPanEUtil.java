package net.cloudstu.sg.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class ShiPanEUtil {

    public static String API_URL = "http://39.105.55.173:8888/%s";

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

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);
        ShiPanEUtil.buy("600381", 500);
    }
}
