package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.TrackerUserDao;
import net.cloudstu.sg.entity.CookieModel;
import net.cloudstu.sg.entity.TrackerUserModel;
import net.cloudstu.sg.util.SpringUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易频率
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
@Slf4j
@TargetUrl("http://moni.178448.com/ShowMatchUser-\\w+-\\w+.html")
public class TransactionFrequencyRepo extends NeedLoginRepo implements AfterExtractor {

    @Override
    public void afterProcess(Page page) {
        System.out.println(this.userName);
        System.out.println(getCnt(this.cntStr));
    }

    private int getCnt(String cntStr) {
        if(StringUtils.isEmpty(cntStr)) {
            return 0;
        }
        String tmp = cntStr.split("共 ")[2];
        if(StringUtils.isEmpty(tmp)) {
            return 0;
        }

        return Integer.parseInt(tmp.split(" ")[0]);
    }


    /**
     * 抓取交易频率
     *
     * @param month   追踪月，比如当月是72
     * @param userIds 被追踪用户的id
     */
    public static void grab(int month, List<Long> userIds) {

        OOSpider ooSpider = null;
        try {
            ooSpider = buildOOSpider();
            for (long userId : userIds) {
                try {
                    TransactionFrequencyRepo repo = ooSpider.get(getGrabUrl(month, userId));
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

    @ExtractBy("//div[@class='listno']/ul/text()")
    private String cntStr;
    @ExtractBy("//strong[@class='name']/text()")
    private String userName;

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
                new ConsolePageModelPipeline(), TransactionFrequencyRepo.class);
        return ooSpider;
    }

    private static final String DOMAIN = "moni.178448.com";

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        TrackerUserDao trackerUserDao = SpringUtil.getBean(TrackerUserDao.class);
        List<TrackerUserModel> trackerUserModels =  trackerUserDao.selectAll();

        List<Long> userIds = trackerUserModels.stream().filter(x -> x.getCnt()>1).map(TrackerUserModel::getUserId).collect(Collectors.toList());
        grab(72, userIds);

    }
}
