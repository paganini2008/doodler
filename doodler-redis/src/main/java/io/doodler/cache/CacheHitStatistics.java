package io.doodler.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description: CacheHitStatistics
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
public class CacheHitStatistics {

    private final LongAdder total = new LongAdder();
    private final LongAdder hits = new LongAdder();
    private final LongAdder puts = new LongAdder();
    private final LongAdder evicts = new LongAdder();

    public long incrTotal() {
        total.increment();
        return total.longValue();
    }

    public long incrHits() {
        hits.increment();
        return hits.longValue();
    }
    
    public long incrPuts() {
    	puts.increment();
        return puts.longValue();
    }
    
    public long incrEvicts() {
    	evicts.increment();
    	return evicts.longValue();
    }

    public long getTotal() {
        return total.longValue();
    }

    public long getHits() {
        return hits.longValue();
    }
    
    public long getPuts() {
    	return puts.longValue();
    }
    
    public long getEvicts() {
    	return evicts.longValue();
    }

    public String getHitRatio() {
        if (getTotal() == 0) {
            return "-";
        }
        BigDecimal value = BigDecimal.valueOf(getHits()).divide(BigDecimal.valueOf(getTotal()), 4,
                RoundingMode.HALF_UP);
        return DecimalFormat.getPercentInstance().format(value);
    }
}