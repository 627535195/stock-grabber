package net.cloudstu.sg.grab;

import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockDao;
import net.cloudstu.sg.entity.StockModel;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.sinastock.SinaStockClient;
import net.cloudstu.sg.util.sinastock.data.SinaStockResponse;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * 查看被预测的股票当前价格
 *
 * @author zhiming.li
 * @date 2018/5/7
 */
public class SelectedRepo {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        StockDao stockDao = SpringUtil.getBean(StockDao.class);

        List<StockModel> selectedStocks = stockDao.selectMonitored();

        for(StockModel stock : selectedStocks) {
            SinaStockResponse res = SinaStockClient.getStockData(stock.getCode());
            System.out.println(String.format("%s(%s): %s", stock.getName(), stock.getCode(), res.getData().getSwing()));
        }
    }
}
