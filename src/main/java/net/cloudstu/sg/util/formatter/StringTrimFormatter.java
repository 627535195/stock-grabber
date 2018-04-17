package net.cloudstu.sg.util.formatter;

import org.springframework.util.StringUtils;
import us.codecraft.webmagic.model.formatter.ObjectFormatter;

/**
 * 去掉前后空格
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
public class StringTrimFormatter implements ObjectFormatter {
    @Override
    public Object format(String raw) throws Exception {
        return StringUtils.trimWhitespace(raw);
    }

    @Override
    public Class clazz() {
        return String.class;
    }

    @Override
    public void initParam(String[] extra) {
    }
}
