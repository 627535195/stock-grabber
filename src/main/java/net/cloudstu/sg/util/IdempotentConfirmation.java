package net.cloudstu.sg.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 带返回值的幂等操作
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
public abstract class IdempotentConfirmation<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int DEFAULT_RETRY = 3;
    private int retry;

    protected IdempotentConfirmation(int retry) {
        this.retry = retry;
    }

    protected IdempotentConfirmation() {
        this.retry = DEFAULT_RETRY;
    }

    protected abstract Result execute();

    public T run() {
        while (retry-- > 0) {
            try {
                Result r = execute();
                if (r.isOk()){
                    return r.data;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
        }
        return null;
    }


    @Data
    @AllArgsConstructor
    public final class Result {
        private boolean ok;
        private T data;
    }
}
