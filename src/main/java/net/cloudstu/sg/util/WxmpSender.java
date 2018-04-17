package net.cloudstu.sg.util;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpMessageSendResult;
import me.chanjar.weixin.cp.config.WxCpInMemoryConfigStorage;

/**
 * 微信企业消息发送器
 *
 * @author zhiming.li
 * @date 2018/4/11
 */
@Slf4j
public class WxmpSender {

    /**
     * @see WxmpSender#messageSend(String, String)
     * @param content
     */
    public static void messageSendToAdmin(String content) {
        messageSend(ADMIN, content);
    }

    /**
     * 发送企业微信信息
     * @param userId 微信企业号账号
     * @param content 内容
     */
    public static void messageSend(String userId, String content) {
        WxCpMessage message = new WxCpMessage();
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser(userId);
        message.setContent(content);

        WxCpMessageSendResult messageSendResult = null;
        try {
            messageSendResult = wxService.messageSend(message);
            log.warn("Send result [{}]", messageSendResult.getErrMsg());
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
    }

    public final static String ADMIN = "lizhiming";

    private final static WxCpInMemoryConfigStorage configStorage;
    private final static WxCpService wxService;

    static {
        configStorage = new WxCpInMemoryConfigStorage();
        configStorage.setCorpId("wx7f5f6aa60de5065a");
        configStorage.setAgentId(1);
        configStorage.setCorpSecret("37wvjfW7Ix_rgoTM36rpNzTNZ4mEuer0m4jmzPiOJY4");

        wxService = new WxCpServiceImpl();
        wxService.setWxCpConfigStorage(configStorage);
    }

    public static void main(String[] args) {
        messageSend("lizhiming", "测试！");
    }
}
