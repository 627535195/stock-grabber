package net.cloudstu.sg.app;

import net.cloudstu.sg.web.listener.StockHoldListener;
import net.cloudstu.sg.web.listener.StockLoadListener;
import net.cloudstu.sg.web.listener.StockScreamListener;
import net.cloudstu.sg.web.listener.TransactionTrackerListener;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * web程序入口
 *
 * @author zhiming.li
 * @date 2018/4/17
 */
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {

        initSpringWebApplicationContext(container);

        //增加抓取listener
//        container.addListener(new TransactionTrackerListener());

        //增加持仓监控
        container.addListener(new StockHoldListener());

        //初始化涨停预测股
        container.addListener(new StockLoadListener());

        //增加涨停预测监控
        container.addListener(new StockScreamListener());

    }

    /**
     * 初始化spring web 上下文
     *
     * @param container
     */
    private void initSpringWebApplicationContext(ServletContext container) {
        //定位配置类
        AnnotationConfigWebApplicationContext appContext = getContext();

        //加载spring环境
        container.addListener(new ContextLoaderListener(appContext));

        ServletRegistration.Dynamic dispatcher = container.addServlet("DispatcherServlet", new DispatcherServlet(appContext));
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(getClass().getPackage().getName());
        return context;
    }

}
