package net.cloudstu.sg.util.sinastock;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.util.IdempotentConfirmation;
import net.cloudstu.sg.util.ThreadUtil;
import net.cloudstu.sg.util.sinastock.data.SinaStockResponse;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 基于sina http接口获取股票实时信息
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
@Slf4j
public class StockRealTimeInfoTemplate {

    /**
     * 获取实时信息并处理
     *
     * @param infoSetup 设置需要获取实时信息的股票代码列表
     * @param callback  处理股票实时信息的回调
     */
    public static void get(StockRealTimeInfoSetup infoSetup, StockRealTimeInfoCallback callback) {
        List<String> codes = infoSetup.setup();
        for (String code : codes) {
            if (StringUtils.isEmpty(code)) {
                continue;
            }

            //避免网络抖动重试3次，每次重试时间叠加500ms
            SinaStockResponse res;
            res = new IdempotentConfirmation<SinaStockResponse>() {
                private int sleepTime = 500;

                @Override
                public Result execute() {
                    SinaStockResponse res = SinaStockClient.getStockData(code);
                    if(res == null) {
                        ThreadUtil.sleep(sleepTime);
                        sleepTime += 500;
                    }

                    return new Result(res != null, res);
                }
            }.run();

            if (SinaStockResponse.SUCCESS != res.getCode()) {
                log.error("获取实时股票信息异常！【{}】状态码【{}】原因【{}】", code, res.getCode(), res.getOriginalData());
                continue;
            }

            callback.call(res);
        }

    }

    /**
     * 执行命令回调
     */
    public interface StockRealTimeInfoCallback {
        /**
         * 执行回调处理 sina http接口获取股票实时信息
         *
         * @param response 通过sina stock http接口获取的数据
         */
        void call(SinaStockResponse response);
    }

    /**
     * 准备需要实时获取的数据
     */
    public interface StockRealTimeInfoSetup {
        /**
         * 准备数据的方法
         *
         * @return 股票代码集合
         */
        List<String> setup();
    }

}
