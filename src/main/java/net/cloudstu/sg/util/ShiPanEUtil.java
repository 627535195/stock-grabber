package net.cloudstu.sg.util;

import com.alibaba.fastjson.JSON;
import net.cloudstu.sg.app.RootConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class ShiPanEUtil {

    public static String API_URL = "http://39.105.55.173:8888/%s";

    public static void buy(String code, int amount, double price) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        Map map = new HashMap();
        map.put("action", "BUY");
        map.put("symbol", code);
        map.put("priceType", 0);
        map.put("type", "LIMIT");
        map.put("price", price);
        map.put("amount", amount);

        String param = JSON.toJSONString(map);
        HttpEntity<String> formEntity = new HttpEntity<String>(param, getHeaders());
        String result = restTemplate.postForObject(url, formEntity, String.class);
    }

    public static void cancelAll() {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        restTemplate.delete(url);
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

//        ShiPanEUtil.cancelAll();

        ShiPanEUtil.buy("000001", 200, 11);
    }
}
