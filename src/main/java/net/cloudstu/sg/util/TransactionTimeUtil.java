package net.cloudstu.sg.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 判断是否是交易时间
 *
 * @author zhiming.li
 */
public class TransactionTimeUtil {

	public static boolean isTransactionTime() {
		boolean transactionTime;
		LocalTime now = LocalTime.now();
		if((now.isAfter(LocalTime.of(9, 30))
				&& now.isBefore(LocalTime.of(11, 30)))
				|| (now.isAfter(LocalTime.of(13, 0))
				&& now.isBefore(LocalTime.of(15, 0)))
				) {//当日股票交易时间
			transactionTime = true;
		}else {
			transactionTime = false;
		}

		boolean transactionDate;
		DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
		if(dayOfWeek.equals(DayOfWeek.SUNDAY)
				|| dayOfWeek.equals(DayOfWeek.SATURDAY)) {
			transactionDate = false;
		}else {
			transactionDate = true;
		}
		return transactionDate && transactionTime;
	}
}
