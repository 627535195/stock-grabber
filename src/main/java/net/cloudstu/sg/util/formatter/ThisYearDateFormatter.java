package net.cloudstu.sg.util.formatter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.model.formatter.ObjectFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 去掉空格后再date format
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
public class ThisYearDateFormatter implements ObjectFormatter{
    private String pattern;
    private final String year = new SimpleDateFormat("yyyy").format(new Date());


    @Override
    public Object format(String raw) throws Exception {
        Assert.notNull(pattern);

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(String.format("%s-%s", year, StringUtils.trimWhitespace(raw)));
    }

    @Override
    public Class clazz() {
        return Date.class;
    }

    @Override
    public void initParam(String[] extra) {
        pattern = extra[0];
    }
}
