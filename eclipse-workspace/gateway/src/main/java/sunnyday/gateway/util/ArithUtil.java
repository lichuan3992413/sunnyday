package sunnyday.gateway.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArithUtil {
	private static final int DEF_DIV_SCALE = 10;
	public final static DecimalFormat df = new DecimalFormat(".####");
	public final static Lock lock_1 = new ReentrantLock();
	public final static Lock lock_2 = new ReentrantLock();

	private ArithUtil() {
	}

	public static void main(String[] args) {
		System.out.println(df.format(Double.parseDouble("3.141592353")));
		/*
		 * System.out.println(ArithUtil.add(0.01, 0.05));
		 * System.out.println(ArithUtil.sub(1.0, 0.42));
		 * System.out.println(ArithUtil.mul(4.015, 100));
		 * System.out.println(ArithUtil.div(123.3, 100));
		 */
	}

	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();

	}

	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();

	}

	public static double mul(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();

	}

	public static double div(double d1, double d2) {

		return div(d1, d2, DEF_DIV_SCALE);

	}

	public static double div(double d1, double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}
}
