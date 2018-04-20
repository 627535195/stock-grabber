package net.cloudstu.sg.grab;

import net.cloudstu.sg.dao.StockHoldDao;
import net.cloudstu.sg.entity.StockHoldModel;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.sinastock.StockRealTimeInfoTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 持仓处理
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
public class StockHoldRepo {

    public static void process() {
        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        List<StockHoldModel> stockHolds = stockHoldDao.selectAll();
        if (CollectionUtils.isEmpty(stockHolds)) {
            return;
        }


        List<String> tableNames = stockHolds.stream().map(StockHoldModel::getCode).collect(Collectors.toList());
//        StockRealTimeInfoTemplate.get();
    }
}
