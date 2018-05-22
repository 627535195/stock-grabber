package net.cloudstu.sg.util.sinastock;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.util.sinastock.data.SinaStockResponse;
import net.cloudstu.sg.util.sinastock.data.StockData;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 *
 */
@Slf4j
public class SinaStockClient {

	public static final String SINA_STOCK_GET = "http://hq.sinajs.cn/list=%s";

	private static final SinaStockClient sinaStockClient = new SinaStockClient();

	private CloseableHttpClient httpClient;

	private SinaStockClient() {
		httpClient = HttpClients.createDefault();
	}

	/**
	 * 根据股票代码获取实时行情
	 *
	 * @param code
	 * @return
     */
	public static SinaStockResponse getStockData(String code) {
		return sinaStockClient.httpGet(String.format(SINA_STOCK_GET, getStockCode(code)));
	}

	public static String getStockCode(String code) {
		if (StringUtils.isEmpty(code)) {
			return "";
		}
		if (code.startsWith("60")) {
			return String.format("sh%s", code);
		} else {
			return String.format("sz%s", code);
		}
	}

	public static boolean isValidate(String code) {
		SinaStockResponse sinaStockResponse = getStockData(code);
		return sinaStockResponse.getCode() == SinaStockResponse.SUCCESS;
	}

	private SinaStockResponse httpGet(String url) {
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(new HttpGet(url));
			return processResponse(response);
		} catch (IOException ex) {
			return new SinaStockResponse(SinaStockResponse.ERROR, ex.getMessage(), null);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				log.error("关闭CloseableHttpResponse异常!", e);
			}
		}
	}

	private SinaStockResponse processResponse(CloseableHttpResponse response) throws IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			EntityUtils.consume(entity);
			return new SinaStockResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase(), null);
		}
		String responseContent = entity == null ? null : EntityUtils.toString(entity, Consts.UTF_8);
		if (StringUtils.isEmpty(responseContent)) {
			return new SinaStockResponse(SinaStockResponse.ERROR, responseContent, null);
		}
		String[] attrs = responseContent.split("\"")[1].split(",");
		StockData sd = new StockData();
		sd.setName(attrs[0]);
		sd.setCurrentPrice(Double.parseDouble(attrs[3]));
		BigDecimal b1 = new BigDecimal(attrs[2]);
		BigDecimal b2 = new BigDecimal(attrs[3]);
		BigDecimal result = b2.subtract(b1).divide(b1, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN).multiply
				(BigDecimal.TEN);
		sd.setSwing(result.doubleValue());

		sd.setOpenPrice(Double.parseDouble(attrs[1]));

		BigDecimal b3 = new BigDecimal(attrs[1]);
		result = b3.subtract(b1).divide(b1, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN).multiply
				(BigDecimal.TEN);
		sd.setOpenSwing(result.doubleValue());

		sd.setAmount(Long.parseLong(attrs[8]));

		return  new SinaStockResponse(SinaStockResponse.SUCCESS, responseContent, sd);
	}

	public static void main(String[] args) {
		System.out.println(SinaStockClient.getStockData("000011"));
	}
}
