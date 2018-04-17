package net.cloudstu.sg.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhiming.li
 * @date 2018/4/10
 */
@ToString
@Data
public class CookieModel {
    private String key;
    private String value;
    private String domain;
}
