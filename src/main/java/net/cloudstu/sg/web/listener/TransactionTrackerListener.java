package net.cloudstu.sg.web.listener;


import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.dao.TrackerUserDao;
import net.cloudstu.sg.dao.TransactionTrackerDao;
import net.cloudstu.sg.entity.TrackerUserModel;
import net.cloudstu.sg.entity.TransactionTrackerModel;
import net.cloudstu.sg.grab.TransactionTrackerRepo;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.ThreadUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * 初始化模拟交易跟踪器
 *
 * @author zhiming.li
 * @date 2018/4/17
 */
@Slf4j
public class TransactionTrackerListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        TrackerUserDao trackerUserDao = SpringUtil.getBean(TrackerUserDao.class);
        List<TrackerUserModel> trackerUserModelList =  trackerUserDao.selectAll();
        if(StringUtils.isEmpty(trackerUserModelList)) {
            return;
        }

        // 初始化排重交易集合
        TransactionTrackerDao transactionTrackerDao = SpringUtil.getBean(TransactionTrackerDao.class);
        List<TransactionTrackerModel> initTransactionTrackers = transactionTrackerDao.selectInitTransactionTracker();
        TransactionTrackerRepo.existedTransactionTrackers.addAll(initTransactionTrackers);

        List<Long> userIds = trackerUserModelList.stream().map(TrackerUserModel::getUserId).collect(Collectors.toList());

        //按一定频率抓取 实验1号 278542 当月72 的交易记录 3秒一次
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TransactionTrackerRepo.grab(72, userIds);
            }
        }, 2000L, 3000L);

        // 起一个线程查看交易记录待入库队列的大小
        ((Runnable) () -> {
            while (true) {
                log.warn("当前队列大小：【{}】", TransactionTrackerRepo.synTransactionTrackerQueue.size());
                log.warn("当前交易记录排重集合大小：【{}】", TransactionTrackerRepo.existedTransactionTrackers.size());
            }
        }).run();

        // 将新交易记录入库
        while (true) {
            try {
                TransactionTrackerModel tt = TransactionTrackerRepo.synTransactionTrackerQueue.take();
                transactionTrackerDao.create(tt);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
