package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.CookieModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 特定域下的cookie
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
@Repository
public interface CookieDao {

    List<CookieModel> selectByDomain(String domain);
}
