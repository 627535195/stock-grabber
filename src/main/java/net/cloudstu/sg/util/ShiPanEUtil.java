package net.cloudstu.sg.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.util.sinastock.SinaStockClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class ShiPanEUtil {

    public static String API_URL = "http://39.105.55.173:8888/%s";

    public static void buy(String code, int amount) {
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = String.format(API_URL, "orders");

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

//        {
//            "action": "BUY",
//                "symbol" : "000001",
//                "type": "LIMIT",
//                "priceType" : 0,
//                "price" : 11.69,
//                "amountProportion" : "1/3"
//        }

        Map map = new HashMap();
        map.put("action", "BUY");
        map.put("symbol", code);
        map.put("priceType", 0);
        map.put("type", "LIMIT");
        map.put("price", 11);
        map.put("amount", amount);

        String param = JSON.toJSONString(map);
        System.out.println(param);
        HttpEntity<String> formEntity = new HttpEntity<String>(param, headers);

        String result = restTemplate.postForObject(url, formEntity, String.class);
        System.out.println(result);

    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        ShiPanEUtil.buy("000001", 200);
    }
}
