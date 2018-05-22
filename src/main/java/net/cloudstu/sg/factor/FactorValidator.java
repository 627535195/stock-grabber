package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.sinastock.data.StockData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhiming.li
 * @date 2018/5/22
 */
public class FactorValidator {

    private List<Factor> factors = new ArrayList<>(8);

    private FactorValidator() {
        factors.add(new HighOpenFactor());
    }

    private static final FactorValidator INSTANCE = new FactorValidator();

    public static boolean validate(StockData data) {
        for (Factor f : INSTANCE.factors) {
            if(!f.isSatisfied(data)) {
                return false;
            }
        }
        return true;
    }

}
