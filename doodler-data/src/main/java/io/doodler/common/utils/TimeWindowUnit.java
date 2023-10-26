package io.doodler.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 
 * @Description: TimeWindowUnit
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public enum TimeWindowUnit {

	DAYS {
		@Override
		public int sizeOf(int span, int days) {
			return span * days;
		}

		@Override
		public LocalDateTime locate(Instant timestamp, int span) {
			LocalDateTime ldt = timestamp.atZone(ZoneId.systemDefault()).toLocalDate().atTime(0, 0);
			return ldt;
		}
	},

	HOURS {

		@Override
		public LocalDateTime locate(Instant timestamp, int span) {
			final ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());
			int hour = zdt.getHour();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour - hour % span, 0, 0));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (24 % span == 0 ? (24 / span) : (24 / span + 1)) * days;
		}

	},
	MINUTES {

		@Override
		public LocalDateTime locate(Instant timestamp, int span) {
			final ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());
			int hour = zdt.getHour();
			int minute = zdt.getMinute();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute - minute % span, 0));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 24 * days;
		}

	},
	SECONDS {

		@Override
		public LocalDateTime locate(Instant timestamp, int span) {
			final ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());
			int hour = zdt.getHour();
			int minute = zdt.getMinute();
			int second = zdt.getSecond();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute, second - second % span));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 60 * 24 * days;
		}

	};

	public abstract LocalDateTime locate(Instant timestamp, int span);

	public abstract int sizeOf(int span, int days);
	
}
