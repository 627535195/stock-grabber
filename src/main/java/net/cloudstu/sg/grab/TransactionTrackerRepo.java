package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.TrackerUserDao;
import net.cloudstu.sg.dao.TransactionTrackerDao;
import net.cloudstu.sg.entity.CookieModel;
import net.cloudstu.sg.entity.TrackerUserModel;
import net.cloudstu.sg.entity.TransactionTrackerModel;
import net.cloudstu.sg.entity.TransactionTrackerQueryModel;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.WxmpSender;
import net.cloudstu.sg.util.formatter.StringTrimFormatter;
import net.cloudstu.sg.util.formatter.ThisYearDateFormatter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * http://moni.178448.com/ShowMatchUser-72-387634.html
 * 特定人模拟买入跟踪
 *
 * @author zhiming.li
 * @date 2018/4/9
 */
@Slf4j
@TargetUrl("http://moni.178448.com/ShowMatchUser-\\w+-\\w+.html")
public class TransactionTrackerRepo extends NeedLoginRepo implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        if (isLatest()) {
            TransactionTrackerModel tt = new TransactionTrackerModel();
            tt.setUserId(Long.parseLong(this.userId));
            tt.setName(this.name);
            tt.setAction(this.action);
            tt.setApplyPrice(this.applyPrice);
            tt.setPrice(this.price);
            tt.setApplyTime(this.applyTime == null ? 0 : this.applyTime.getTime());
            tt.setTime(this.time == null ? 0 : this.time.getTime());
            tt.setState(this.state);

            log.warn("发生交易！【{}】", tt.getUserId());

            WxmpSender.messageSendToAdmin(getTransactionInfo(tt));

            try {
                SpringUtil.getBean(TransactionTrackerDao.class).create(tt);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 构造交易信息
     *
     * @param tt 交易实体
     * @return
     */
    private String getTransactionInfo(TransactionTrackerModel tt) {
        TrackerUserDao trackerUserDao = SpringUtil.getBean(TrackerUserDao.class);
        TrackerUserModel tu = trackerUserDao.selectByUserId(tt.getUserId());


        return String.format("%s【%s】！价格【%s】申请时间【%s】状态【%s】买入人类型【%s】",
                tt.getAction(),
                tt.getName(),
                tt.getApplyTime(),
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(tt.getApplyTime())),
                tt.getState(),
                tu.getUserId(),
                getTypeDesc(tu));
    }

    private String getTypeDesc(TrackerUserModel tu) {
        if(tu == null) {
            return "";
        }

        switch (tu.getType()) {
            case 1 :
                return "追高";
            case 2 :
                return "稳健";
            default:return "";
        }

    }

    /**
     * 抓取最新用户交易记录
     *
     * @param month  追踪月，比如当月是72
     * @param userId 被追踪用户的id
     */
    public static void grab(int month, long userId) {

        OOSpider ooSpider = null;
        try {
            ooSpider = buildOOSpider();
            TransactionTrackerRepo repo = ooSpider.get(getGrabUrl(month, userId));

            if (repo == null) {//没有抓取到内容就需要重新登录，因为有可能是因为session过期导致cookie更换
                getManualLoginCookies(DOMAIN, true);
            }
        } catch (Exception e) {
            log.warn("手动登录cookie重新从数据库获取！");
            log.error(e.getMessage(), e);
            getManualLoginCookies(DOMAIN, true);
        } finally {
            if (ooSpider != null) {
                ooSpider.close();
            }
        }
    }

    /**
     * @param month
     * @param userIds
     * @see TransactionTrackerRepo#grab(int, long)
     */
    public static void grab(int month, List<Long> userIds) {

        OOSpider ooSpider = null;
        try {
            ooSpider = buildOOSpider();
            for (long userId : userIds) {
                try {
                    TransactionTrackerRepo repo = ooSpider.get(getGrabUrl(month, userId));
                    if (repo == null) {//没有抓取到内容就需要重新登录，因为有可能是因为session过期导致cookie更换
                        getManualLoginCookies(DOMAIN, true);
                    }
                } catch (Exception e) {
                    log.warn("手动登录cookie重新从数据库获取！");
                    log.error(e.getMessage(), e);
                    getManualLoginCookies(DOMAIN, true);
                }
            }

        } finally {
            if (ooSpider != null) {
                ooSpider.close();
            }
        }
    }

    private static String getGrabUrl(int month, long userId) {
        return String.format("http://moni.178448.com/ShowMatchUser-%d-%d.html", month, userId);
    }

    /**
     * 是否最新的数据
     * 满足：
     * 1 申请时间在3分钟以内的
     * 2 数据库还没有记录的
     *
     * @return
     */
    private boolean isLatest() {
        if (applyTime == null || Math.abs(this.applyTime.getTime() - System.currentTimeMillis()) > EXPIRED_TIME) {
            return false;
        }

        TransactionTrackerQueryModel qm = TransactionTrackerQueryModel.builder()
                .userId(this.userId)
                .name(this.name)
                .applyTime(this.applyTime.getTime())
                .action(this.action)
                .build();
        List<TransactionTrackerModel> ttList = SpringUtil.getBean(TransactionTrackerDao.class).getByCondition(qm);
        return CollectionUtils.isEmpty(ttList);
    }

    /**
     * 初始化spider
     *
     * @return spider
     */
    private static OOSpider buildOOSpider() {

        // site公共信息设置
        Site site = Site.me()
                .setSleepTime(100)
                .setDomain(DOMAIN)
                .setUserAgent(
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

        // 登录cookie设置
        for (CookieModel cm : getManualLoginCookies(DOMAIN, false)) {
            site.addCookie(cm.getKey(), cm.getValue());
        }

        OOSpider ooSpider = OOSpider.create(site,
                new ConsolePageModelPipeline(), TransactionTrackerRepo.class);
        return ooSpider;
    }

    @ExtractBy(value = "//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[2]/a/text()", notNull = true)
    @Formatter(formatter = StringTrimFormatter.class)
    private String name;

    @ExtractBy("//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[3]/span/text()")
    @Formatter(formatter = StringTrimFormatter.class)
    private String action;

    @ExtractBy("//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[5]/text()")
    private double applyPrice;

    @ExtractBy("//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[6]/text()")
    private double price;

    @ExtractBy("//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[7]/text()")
    @Formatter(value = "yyyy-MM-dd HH:mm", formatter = ThisYearDateFormatter.class)
    private Date applyTime;

    @ExtractBy("//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[8]/text()")
    @Formatter(value = "yyyy-MM-dd HH:mm", formatter = ThisYearDateFormatter.class)
    private Date time;

    @ExtractBy(value = "//div[@id='ap_info']/table[@class='sjtab2']/tbody/tr[2]/td[9]/text()")
    @Formatter(formatter = StringTrimFormatter.class)
    private String state;

    @ExtractByUrl("http://moni.178448.com/ShowMatchUser-\\w+-(\\w+).html")
    private String userId;

    private static final String DOMAIN = "moni.178448.com";

    /**
     * 处理申请时间在3分钟内的数据，这么长的时间主要是为了预防该网站数据生成数据的延迟
     */
    private static final long EXPIRED_TIME = 1000 * 60 * 3;

    public static void main(String[] args) {

        new AnnotationConfigApplicationContext(RootConfig.class);
        grab(72, 278542);
    }
}
