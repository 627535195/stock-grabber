package net.cloudstu.sg.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 配置
 *
 * @author lizhiming
 */
@Configuration
@ComponentScan(value = "net.cloudstu.sg",
        excludeFilters={@ComponentScan.Filter(type= FilterType.ANNOTATION,value=EnableWebMvc.class)})
@Import({DataConfig.class/*,JspViewsConfig.class,MailConfig.class*/})
//@Import({DataConfig.class})
public class RootConfig {
}
