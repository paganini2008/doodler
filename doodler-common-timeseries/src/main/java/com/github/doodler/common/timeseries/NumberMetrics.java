
package com.github.doodler.common.timeseries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import lombok.ToString;

/**
 * 
 * @Description: NumberMetrics
 * @Author: Fred Feng
 * @Date: 15/11/2024
 * @Version 1.0.0
 */
public abstract class NumberMetrics {

    public static LongMetric nullLongMetric(long timestamp) {
        return new LongMetric(timestamp);
    }

    public static LongMetric valueOf(long value, long timestamp) {
        return new LongMetric(value, timestamp);
    }

    public static DoubleMetric nullDoubleMetric(long timestamp) {
        return new DoubleMetric(timestamp);
    }

    public static DoubleMetric valueOf(double value, long timestamp) {
        return new DoubleMetric(value, timestamp);
    }

    public static DecimalMetric nullDecimalMetric(long timestamp) {
        return new DecimalMetric(timestamp);
    }

    public static DecimalMetric valueOf(BigDecimal value, long timestamp) {
        return new DecimalMetric(value, timestamp);
    }

    public static LongMetric valueOf(long value, long baseValue, long timestamp) {
        return new LongMetric(value, baseValue, timestamp);
    }

    public static DoubleMetric valueOf(double value, double baseValue, long timestamp) {
        return new DoubleMetric(value, baseValue, timestamp);
    }

    public static DecimalMetric valueOf(BigDecimal value, BigDecimal baseValue, long timestamp) {
        return new DecimalMetric(value, baseValue, timestamp);
    }

    @ToString
    public static class DoubleMetric implements NumberMetric<Double> {

        DoubleMetric(long timestamp) {
            this.timestamp = timestamp;
        }

        DoubleMetric(double value, long timestamp) {
            this(value, value, timestamp);
        }

        DoubleMetric(double value, double baseValue, long timestamp) {
            this.highestValue = value;
            this.lowestValue = value;
            this.totalValue = value;
            this.baseValue = baseValue;
            this.count = 1;
            this.baseCount = value >= baseValue ? 1 : 0;
            this.timestamp = timestamp;
        }

        public DoubleMetric(double highestValue, double lowestValue, double totalValue,
                double baseValue, long count, long baseCount, long timestamp) {
            this.highestValue = highestValue;
            this.lowestValue = lowestValue;
            this.totalValue = totalValue;
            this.baseValue = baseValue;
            this.count = count;
            this.baseCount = baseCount;
            this.timestamp = timestamp;
        }

        private Double highestValue;
        private Double lowestValue;
        private Double totalValue;
        private Double baseValue;
        private long count;
        private long baseCount;
        private long timestamp;

        @Override
        public Double getHighestValue() {
            return highestValue;
        }

        @Override
        public Double getLowestValue() {
            return lowestValue;
        }

        @Override
        public Double getTotalValue() {
            return totalValue;
        }

        @Override
        public Double getBaseValue() {
            return baseValue;
        }

        @Override
        public long getCount() {
            return count;
        }

        @Override
        public long getBaseCount() {
            return baseCount;
        }

        @Override
        public Double getAverageValue() {
            return count > 0
                    ? Double.valueOf(new DecimalFormat("0.0000").format(totalValue / count))
                    : 0D;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public NumberMetric<Double> reset(NumberMetric<Double> currentMetric) {
            double highestValue =
                    Double.max(this.highestValue, currentMetric.getHighestValue().doubleValue());
            double lowestValue = this.lowestValue != null
                    ? Double.min(this.lowestValue.doubleValue(),
                            currentMetric.getLowestValue().doubleValue())
                    : currentMetric.getLowestValue().doubleValue();
            double totalValue = this.totalValue - currentMetric.getTotalValue().doubleValue();
            long count = this.count - currentMetric.getCount();
            long baseCount = this.baseCount - currentMetric.getBaseCount();
            long timestamp = Long.max(this.timestamp, currentMetric.getTimestamp());
            return new DoubleMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public NumberMetric<Double> merge(NumberMetric<Double> currentMetric) {
            double highestValue =
                    Double.max(this.highestValue, currentMetric.getHighestValue().doubleValue());
            double lowestValue = this.lowestValue != null
                    ? Double.min(this.lowestValue.doubleValue(),
                            currentMetric.getLowestValue().doubleValue())
                    : currentMetric.getLowestValue().doubleValue();
            double totalValue = this.totalValue + currentMetric.getTotalValue().doubleValue();
            long count = this.count + currentMetric.getCount();
            long baseCount = this.baseCount + currentMetric.getBaseCount();
            long timestamp = currentMetric.getTimestamp();
            return new DoubleMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public Map<String, Object> represent() {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("totalValue", getTotalValue());
            data.put("highestValue", getHighestValue());
            data.put("lowestValue", getLowestValue());
            data.put("averageValue", getAverageValue());
            data.put("baseValue", getBaseValue());
            data.put("count", getCount());
            data.put("baseCount", getBaseCount());
            data.put("timestamp", getTimestamp());
            return data;
        }

    }

    @ToString
    public static class LongMetric implements NumberMetric<Long> {

        LongMetric(long timestamp) {
            this.timestamp = timestamp;
        }

        LongMetric(long value, long timestamp) {
            this(value, value, timestamp);
        }

        LongMetric(long value, long baseValue, long timestamp) {
            this.highestValue = value;
            this.lowestValue = value;
            this.totalValue = value;
            this.baseValue = baseValue;
            this.count = 1L;
            this.baseCount = value >= baseValue ? 1L : 0L;
            this.timestamp = timestamp;
        }

        public LongMetric(long highestValue, long lowestValue, long totalValue, long baseValue,
                long count, long baseCount, long timestamp) {
            this.highestValue = highestValue;
            this.lowestValue = lowestValue;
            this.totalValue = totalValue;
            this.baseValue = baseValue;
            this.count = count;
            this.baseCount = baseCount;
            this.timestamp = timestamp;

        }

        private Long highestValue;
        private Long lowestValue;
        private Long totalValue;
        private Long baseValue;
        private long count;
        private long baseCount;
        private long timestamp;

        @Override
        public Long getHighestValue() {
            return highestValue;
        }

        @Override
        public Long getLowestValue() {
            return lowestValue;
        }

        @Override
        public Long getTotalValue() {
            return totalValue;
        }

        @Override
        public long getCount() {
            return count;
        }

        @Override
        public long getBaseCount() {
            return baseCount;
        }

        @Override
        public Long getBaseValue() {
            return baseValue;
        }

        @Override
        public Double getAverageValue() {
            return count > 0
                    ? Double.valueOf(new DecimalFormat("0.0000").format(totalValue / count))
                    : 0D;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public NumberMetric<Long> reset(NumberMetric<Long> currentMetric) {
            long highestValue =
                    Long.max(this.highestValue, currentMetric.getHighestValue().longValue());
            long lowestValue = this.lowestValue != null
                    ? Long.min(this.lowestValue, currentMetric.getLowestValue().longValue())
                    : currentMetric.getLowestValue().longValue();
            long totalValue = this.totalValue - currentMetric.getTotalValue().longValue();
            long count = this.count - currentMetric.getCount();
            long baseCount = this.baseCount - currentMetric.getBaseCount();
            long timestamp = Long.max(this.timestamp, currentMetric.getTimestamp());
            return new LongMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public NumberMetric<Long> merge(NumberMetric<Long> currentMetric) {
            long highestValue =
                    Long.max(this.highestValue, currentMetric.getHighestValue().longValue());
            long lowestValue = this.lowestValue != null
                    ? Long.min(this.lowestValue, currentMetric.getLowestValue().longValue())
                    : currentMetric.getLowestValue().longValue();
            long totalValue = this.totalValue + currentMetric.getTotalValue().longValue();
            long count = this.count + currentMetric.getCount();
            long baseCount = this.baseCount + currentMetric.getBaseCount();
            long timestamp = currentMetric.getTimestamp();
            return new LongMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public Map<String, Object> represent() {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("totalValue", getTotalValue());
            data.put("highestValue", getHighestValue());
            data.put("lowestValue", getLowestValue());
            data.put("averageValue", getAverageValue());
            data.put("baseValue", getBaseValue());
            data.put("count", getCount());
            data.put("baseCount", getBaseCount());
            data.put("timestamp", getTimestamp());
            return data;
        }

    }

    @ToString
    public static class DecimalMetric implements NumberMetric<BigDecimal> {

        DecimalMetric(long timestamp) {
            this.timestamp = timestamp;
        }

        DecimalMetric(BigDecimal value, long timestamp) {
            this(value, value, timestamp);
        }

        DecimalMetric(BigDecimal value, BigDecimal baseValue, long timestamp) {
            this.highestValue = value;
            this.lowestValue = value;
            this.totalValue = value;
            this.baseValue = baseValue;
            this.count = 1;
            this.baseCount = value.compareTo(baseValue) >= 0 ? 1 : 0;
            this.timestamp = timestamp;
        }

        public DecimalMetric(BigDecimal highestValue, BigDecimal lowestValue, BigDecimal totalValue,
                BigDecimal baseValue, long count, long baseCount, long timestamp) {
            this.highestValue = highestValue;
            this.lowestValue = lowestValue;
            this.totalValue = totalValue;
            this.count = count;
            this.baseCount = baseCount;
            this.timestamp = timestamp;
        }

        private BigDecimal highestValue;
        private BigDecimal lowestValue;
        private BigDecimal totalValue;
        private BigDecimal baseValue;
        private long count;
        private long baseCount;
        private long timestamp;

        @Override
        public BigDecimal getHighestValue() {
            return highestValue;
        }

        @Override
        public BigDecimal getLowestValue() {
            return lowestValue;
        }

        @Override
        public BigDecimal getTotalValue() {
            return totalValue;
        }

        @Override
        public long getCount() {
            return count;
        }

        @Override
        public Double getAverageValue() {
            return count > 0
                    ? totalValue.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0D;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public NumberMetric<BigDecimal> reset(NumberMetric<BigDecimal> currentMetric) {
            BigDecimal highestValue =
                    this.highestValue.max((BigDecimal) currentMetric.getHighestValue());
            BigDecimal lowestValue = this.lowestValue != null
                    ? this.lowestValue.min((BigDecimal) currentMetric.getLowestValue())
                    : (BigDecimal) currentMetric.getLowestValue();
            BigDecimal totalValue =
                    this.totalValue.subtract((BigDecimal) currentMetric.getTotalValue());
            long count = this.count - currentMetric.getCount();
            long baseCount = this.baseCount - currentMetric.getBaseCount();
            long timestamp = Long.max(this.timestamp, currentMetric.getTimestamp());
            return new DecimalMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public NumberMetric<BigDecimal> merge(NumberMetric<BigDecimal> currentMetric) {
            BigDecimal highestValue =
                    this.highestValue.max((BigDecimal) currentMetric.getHighestValue());
            BigDecimal lowestValue = this.lowestValue != null
                    ? this.lowestValue.min((BigDecimal) currentMetric.getLowestValue())
                    : (BigDecimal) currentMetric.getLowestValue();
            BigDecimal totalValue = this.totalValue.add((BigDecimal) currentMetric.getTotalValue());
            long count = this.count + currentMetric.getCount();
            long baseCount = this.baseCount + currentMetric.getBaseCount();
            long timestamp = currentMetric.getTimestamp();
            return new DecimalMetric(highestValue, lowestValue, totalValue, baseValue, count,
                    baseCount, timestamp);
        }

        @Override
        public Map<String, Object> represent() {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("totalValue", getTotalValue());
            data.put("highestValue", getHighestValue());
            data.put("lowestValue", getLowestValue());
            data.put("averageValue", getAverageValue());
            data.put("baseValue", getBaseValue());
            data.put("count", getCount());
            data.put("baseCount", getBaseCount());
            data.put("timestamp", getTimestamp());
            return data;
        }

        public BigDecimal getBaseValue() {
            return baseValue;
        }

        public long getBaseCount() {
            return baseCount;
        }

    }

}
