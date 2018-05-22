package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.sinastock.data.StockData;

import java.util.ArrayList;
import java.util.List;

/**
 * 因子校验器
 *
 * @author zhiming.li
 * @date 2018/5/22
 */
public class FactorValidator {

    private List<Factor> factors = new ArrayList<>(8);

    private FactorValidator() {
        factors.add(new HighOpenFallbackFactor());
        factors.add(new LowOpenFactor());
    }

    private static final FactorValidator INSTANCE = new FactorValidator();

    public static boolean validate(String code, StockData data) {
        for (Factor f : INSTANCE.factors) {
            if(!f.isSatisfied(code, data)) {
                return false;
            }
        }
        return true;
    }

}
