package org.dodo.common.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 令牌桶限速器
 * @author maxlim
 *
 */
public class TokenRateLimiter {
    private int capacity;
    private int ratePerSecond;
    private AtomicInteger currentTokens;
    private AtomicLong lastFilledAt;

    public TokenRateLimiter(int capacity, int ratePerSecond) {
        this.capacity = capacity;
        this.ratePerSecond = ratePerSecond;
        this.currentTokens = new AtomicInteger();
        this.lastFilledAt = new AtomicLong();
    }

    public boolean acquire() {
        fill();
        int currentTokens = this.currentTokens.updateAndGet(x -> {
            if (x <= 0) return 0;
            return x - 1;
        });
        return currentTokens > 0;
    }

    /**
     * 填充：lastFilledAt是过去的时间点了，这里是根据上次填充时间把token和时间补回来
     */
    private void fill() {
        long lastFilledAt = this.lastFilledAt.get();
        long currentTimeMillis = System.currentTimeMillis();
        int newTokens = lastFilledAt == 0 ? capacity : (int) (ratePerSecond * (currentTimeMillis - lastFilledAt)/1000);
        if (newTokens > 0) {
            long newLastFilledAt = lastFilledAt == 0 ? currentTimeMillis : (lastFilledAt + newTokens * 1000 / ratePerSecond);
            if (this.lastFilledAt.compareAndSet(lastFilledAt, newLastFilledAt)) {
                while (true) {
                    int currentTokens = this.currentTokens.get();
                    int newCurrentTokens = Math.min(capacity, currentTokens + newTokens) + 1;//这里额外加1是为了防止，currentTokens在最后一个减1后为0会被判断借取失败
                    if (this.currentTokens.compareAndSet(currentTokens, newCurrentTokens)) {
                        break;
                    }
                }
            }
        }
    }
}
