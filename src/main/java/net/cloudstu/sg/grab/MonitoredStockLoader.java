package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockDao;
import net.cloudstu.sg.entity.StockModel;
import net.cloudstu.sg.util.SpringUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 被监控股加载器
 * 1从文件加载
 *
 * @author zhiming.li
 * @date 2018/5/1
 */
@Slf4j
public class MonitoredStockLoader {
    public static final List<String> forecasters = new ArrayList<>();

    public static void load() {
        //清空
        ScreamStockRepo.codes.clear();

        StockDao stockDao = SpringUtil.getBean(StockDao.class);

        List<StockModel> stocks = stockDao.selectMonitored();

        if(StringUtils.isEmpty(stocks)) {
           return;
        }

        ScreamStockRepo.codes.addAll(stocks.stream().map(StockModel::getCode).collect(Collectors.toList()));
    }

    public static void append() {
        ZtRepo.stockWithForecasterMap.clear();

        StockDao stockDao = SpringUtil.getBean(StockDao.class);

        ZtRepo.loadForecasters(forecasters);

        ZtRepo.grab(1, 10);

        for (String forecasterName : ZtRepo.stockWithForecasterMap.keySet()) {
            for (String stockName : ZtRepo.stockWithForecasterMap.get(forecasterName)) {
                StockModel stockModel = stockDao.selectLikeName(stockName);
                if (stockModel != null) {
                    if(ScreamStockRepo.codes.add(stockModel.getCode())) {
                        stockDao.update(stockModel.getCode());
                        log.warn("新增预测【{}】", stockModel.getCode());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);
        load();
        append();
        System.out.println(ScreamStockRepo.codes);
    }
}
