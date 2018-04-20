package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.dao.CookieDao;
import net.cloudstu.sg.entity.CookieModel;
import net.cloudstu.sg.util.SpringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需要登录的抓取repo都继承该类。
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
@Slf4j
public abstract class NeedLoginRepo {

    private static Map<String, List<CookieModel>> loginCookieMap = new ConcurrentHashMap<>();



    /**
     * 通过人工登录，然后将值在数据库中做手动更新，适用于登录状态存活时间较长，或找不到验证码破译方法的登录
     *
     * @param domain 需要登录的域
     * @param forceUpdate 强制重新从数据库读取
     * @return 登录相关的cookie
     */
    protected static List<CookieModel> getManualLoginCookies(String domain, boolean forceUpdate) {
        List<CookieModel> cookies = loginCookieMap.get(domain);
        if(!forceUpdate && cookies != null) {
            return cookies;
        }

        CookieDao cookieDao = SpringUtil.getBean(CookieDao.class);
        try {
            cookies = cookieDao.selectByDomain(domain);

            log.info(cookies.toString());

        }catch (Exception e) {
            cookies = Collections.emptyList();
            log.error(e.getMessage(), e);
        }
        loginCookieMap.put(domain, cookies);
        return cookies;
    }
}
