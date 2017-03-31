package biz.eventual.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
public interface ServiceLoan
{
	BigDecimal getMonthlyAmount(final double borrowed, final int period, final double rate, final RatePeriod ratePeriod);
	BigDecimal getMonthlyAmount(final double borrowed, final int period, final double rate, RatePeriod ratePeriod, final int refundStartingAt);

	List<Payment> getPayments(final double borrowed, final double rate, final int period, final RatePeriod ratePeriod);
	List<Payment> getPayments(final double borrowed, final double rate, final int period, final RatePeriod ratePeriod, final int refundStartingAt);

	LoanPlan getLoanWithPayments(final double borrowed,
								 final double rate,
								 final int period,
								 final RatePeriod ratePeriod);

	List<LoanPlan> findPeriod(final double borrowed, final double basePayment, double rate, final RatePeriod ratePeriod);

	LoanPlan findScheduleStepPayment(final double borrowed, final TreeMap<Integer, Payment> payments, final int initialPeriod, final double rate, final RatePeriod ratePeriod, final double insurance);
	LoanPlan findScheduleStepPayment(final double borrowed, final TreeMap<Integer, Payment> payments, final int initialPeriod, final double rate, final RatePeriod ratePeriod, final double insurance, final int refundStartingAt);
}