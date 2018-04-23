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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http://moni.178448.com/ShowMatchInfo-72.html
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
@Slf4j
@TargetUrl("http://moni.178448.com/ShowMatchInfo-\\w+.html")
@ExtractBy(value = "//table[@class='paimtab mt10']/tbody/tr", multi = true)
public class TrackerRankRepo extends NeedLoginRepo implements AfterExtractor {

    private static BlockingQueue<TrackerUserModel> trackerUserQueue = new LinkedBlockingQueue<>();

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void afterProcess(Page page) {
        if (count.get() >= 50) {
            count = new AtomicInteger(0);
        }
//        System.out.println(this.rank);
        TrackerUserModel trackerUser = new TrackerUserModel();
        trackerUser.setUserName(this.userName);
        trackerUser.setUserId(getUserId(this.trackFunctionName));
        trackerUser.setType(0);
        trackerUser.setRank(count.addAndGet(1));

        try {
            log.warn(String.format("%d-%s", trackerUser.getRank(), trackerUser.getUserName()));
            if(trackerUser.getRank()<11) { //只持久化前10
                trackerUserQueue.put(trackerUser);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void grab(int month) {

        OOSpider ooSpider = null;
        try {
            ooSpider = buildOOSpider();
            System.out.println(getGrabUrl(month));
            TrackerRankRepo repo = ooSpider.get(getGrabUrl(month));

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

    private long getUserId(String trackFunctionName) {
        if (StringUtils.isEmpty(trackFunctionName)) {
            return 0;
        }

        return Long.parseLong(trackFunctionName.split(",")[2]);
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
                new ConsolePageModelPipeline(), TrackerRankRepo.class);
        ooSpider.thread(1);
        return ooSpider;
    }

    private static final String DOMAIN = "moni.178448.com";

    private static String getGrabUrl(int month) {
        return String.format("http://moni.178448.com/ShowMatchInfo-%d.html", month);
    }

    @ExtractBy(value = "//a/text()", notNull = true)
    private String userName;
    @ExtractBy(value = "//a[@style='color: Red']/@onclick", notNull = true)
    private String trackFunctionName;

//    @ExtractBy(value = "//td[position()=1]", notNull = true)
//    private int rank;

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        for (int i = 69; i < 73; i++) {
            grab(i);
        }

        TrackerUserDao trackerUserDao = SpringUtil.getBean(TrackerUserDao.class);

//        ThreadUtil.sleep(10, TimeUnit.SECONDS);

        while (trackerUserQueue.size() > 0) {
            TrackerUserModel trackerUser = trackerUserQueue.poll();
            trackerUserDao.insert(trackerUser);
        }

    }

}
