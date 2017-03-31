package biz.eventual.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import biz.eventual.business.LoanBusiness;
import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Slf4j
@Service
public class ServiceLoanImpl implements ServiceLoan
{
	@Override
	public BigDecimal getMonthlyAmount(final double borrowed, final int period, final double rate, RatePeriod ratePeriod)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, LoanPlan.REFUND_NOW);
		return LoanBusiness.calculateMonthlyAmount(loan);
	}

	@Override
	public BigDecimal getMonthlyAmount(final double borrowed, final int period, final double rate, RatePeriod ratePeriod, final int refundStartingAt)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, refundStartingAt);
		return LoanBusiness.calculateMonthlyAmount(loan);
	}

	@Override
	public List<Payment> getPayments(final double borrowed, final double rate, final int period, RatePeriod ratePeriod)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, 0, LoanPlan.REFUND_NOW);
		return LoanBusiness.getPayments(loan);
	}

	@Override
	public List<Payment> getPayments(final double borrowed, final double rate, final int period, RatePeriod ratePeriod, final int refundStartingAt)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, 0, refundStartingAt);
		return LoanBusiness.getPayments(loan);
	}

	@Override
	public LoanPlan getLoanWithPayments(double borrowed, double rate, int period, RatePeriod ratePeriod)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, 0);
		loan.setPayments(LoanBusiness.getPayments(loan));

		log.debug("getLoanWithPayments: %s", loan.toJson());

		return loan;
	}

	@Override
	public List<LoanPlan> findPeriod(double borrowed, double basePayment, double rate, RatePeriod ratePeriod) {

		BigDecimal borrowedBg 	= new BigDecimal(borrowed).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal baseBg 		= new BigDecimal(basePayment).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal actualRate 	= new BigDecimal(rate).setScale(4, BigDecimal.ROUND_HALF_UP);

		if (ratePeriod == RatePeriod.ANNUAL) {
			actualRate = LoanBusiness.getMontlyRateFromYearly(actualRate);
		}

		return LoanBusiness.findPeriod(borrowedBg, baseBg, actualRate);
	}

	/***
	 * Without shiftRefundBy
	 * @param borrowed
	 * @param payments
	 * @param initialPeriod
	 * @param rate
	 * @param ratePeriod
	 * @param insurance
	 * @return
	 */
	@Override
	public LoanPlan findScheduleStepPayment(final double borrowed,
											final TreeMap<Integer, Payment> payments,
											final int initialPeriod,
											final double rate,
											final RatePeriod ratePeriod,
											final double insurance) {

		return findScheduleStepPayment(borrowed, payments, initialPeriod, rate, ratePeriod, insurance, LoanPlan.REFUND_NOW);
	}

	/***
	 * Including shiftRefundBy
	 * @param borrowed
	 * @param refundStartingAt
	 * @param payments
	 * @param initialPeriod
	 * @param rate
	 * @param ratePeriod
	 * @param insurance
	 * @return
	 */
	@Override
	public LoanPlan findScheduleStepPayment(final double borrowed,
											final TreeMap<Integer, Payment> payments,
											final int initialPeriod,
											final double rate,
											final RatePeriod ratePeriod,
											final double insurance,
											final int refundStartingAt)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, initialPeriod, rate, ratePeriod, insurance, refundStartingAt);
		BigDecimal payment = LoanBusiness.calculateMonthlyAmount(loan);
		loan.setPayment(payment);

		return LoanBusiness.findScheduleStepPayment(loan, payments);
	}
}
