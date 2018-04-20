package net.cloudstu.sg.web.listener;


import net.cloudstu.sg.dao.TrackerUserDao;
import net.cloudstu.sg.entity.StockHoldModel;
import net.cloudstu.sg.entity.TrackerUserModel;
import net.cloudstu.sg.grab.TransactionTrackerRepo;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.SpringUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * 初始化模拟交易跟踪器
 *
 * @author zhiming.li
 * @date 2018/4/17
 */
public class TransactionTrackerListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        TrackerUserDao trackerUserDao = SpringUtil.getBean(TrackerUserDao.class);
        List<TrackerUserModel> trackerUserModelList =  trackerUserDao.selectAll();
        if(StringUtils.isEmpty(trackerUserModelList)) {
            return;
        }

        List<Long> userIds = trackerUserModelList.stream().map(TrackerUserModel::getUserId).collect(Collectors.toList());

        //按一定频率抓取 实验1号 278542 当月72 的交易记录 3秒一次
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TransactionTrackerRepo.grab(72, userIds);
            }
        }, 2000L, 3000L);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
