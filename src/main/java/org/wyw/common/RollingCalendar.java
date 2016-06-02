package org.wyw.common;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @author wanghaibo
 * 
 */
class RollingCalendar extends GregorianCalendar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int type;

	RollingCalendar() {
		type = -1;
	}

	RollingCalendar(TimeZone tz, Locale locale) {
		super(tz, locale);
		type = -1;
	}

	public Date getNextCheckDate(Date now) {
		setTime(now);
		switch (type) {
		case 0: // '\0'
			set(13, 0);
			set(14, 0);
			add(12, 1);
			break;

		case 1: // '\001'
			set(12, 0);
			set(13, 0);
			set(14, 0);
			add(11, 1);
			break;

		case 2: // '\002'
			set(12, 0);
			set(13, 0);
			set(14, 0);
			int hour = get(11);
			if (hour < 12) {
				set(11, 12);
			} else {
				set(11, 0);
				add(5, 1);
			}
			break;

		case 3: // '\003'
			set(11, 0);
			set(12, 0);
			set(13, 0);
			set(14, 0);
			add(5, 1);
			break;

		case 4: // '\004'
			set(7, getFirstDayOfWeek());
			set(11, 0);
			set(13, 0);
			set(14, 0);
			add(3, 1);
			break;

		case 5: // '\005'
			set(5, 1);
			set(11, 0);
			set(13, 0);
			set(14, 0);
			add(2, 1);
			break;

		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return getTime();
	}

	public long getNextCheckMillis(Date now) {
		return getNextCheckDate(now).getTime();
	}

	void setType(int type) {
		this.type = type;
	}
}