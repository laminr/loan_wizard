package biz.eventual.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Slf4j
public class LoanBusiness
{
	// http://www.math.hawaii.edu/~hile/math100/consf.htm
	public static BigDecimal calculateMonthlyAmount(LoanPlan loanPlan) {

		BigDecimal borrowed = loanPlan.getBorrowed().setScale(2);

		BigDecimal zeroRate = loanPlan.getRate().divide(new BigDecimal(100.00));
		BigDecimal oneRate = zeroRate.add(new BigDecimal(1));

		int period = loanPlan.getPeriod() - loanPlan.getShiftRefundBy(); // starting at 1
		BigDecimal powerRate = oneRate.pow(period);

		// (borrowed * zeroRate * powerRate) / (powerRate - 1);
		BigDecimal top = (borrowed.multiply(zeroRate)).multiply(powerRate);
		//top.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal divideBy = powerRate.subtract(new BigDecimal(1));
		BigDecimal payment = top.divide(divideBy, 2, RoundingMode.HALF_UP);

		return payment.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static List<Payment> getPayments(LoanPlan loanPlan) {

		List<Payment> payments = new ArrayList<Payment>();

		BigDecimal borrowed = loanPlan.getBorrowed();
		BigDecimal zeroRate = loanPlan.getRate().divide(new BigDecimal("100.0"));

		BigDecimal basePayment = LoanBusiness.calculateMonthlyAmount(loanPlan);
		int period = loanPlan.getPeriod();

		for (int i=0; i < period; i++) {

			BigDecimal interest = borrowed.multiply(zeroRate).setScale(2, BigDecimal.ROUND_HALF_UP);

			Payment payment = new Payment();
			payment.setInterest(interest);

			BigDecimal refunded = BigDecimal.ZERO;

			if (i > (loanPlan.getShiftRefundBy()-1)) {
				refunded = basePayment.subtract(interest);
			}

			payment.setCapital(refunded);
			payments.add(payment);

			borrowed = borrowed.subtract(refunded);
		}

		return payments;
	}

	/***
	 * Find how many payments for an amount loaned with a monthly payment according a rate
	 * @param borrowed
	 * @param basePayment
	 * @param rate
	 * @return
	 */
	public static List<LoanPlan> findPeriod(BigDecimal borrowed, BigDecimal basePayment, BigDecimal rate)
	{

		List<LoanPlan> loans = new ArrayList<LoanPlan>();
        int period = 0;

		BigDecimal zeroRate = rate.divide(new BigDecimal(100.00));

		BigDecimal left = borrowed;

		BigDecimal interest;
		// interest.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal refunded;
		// refunded.setScale(2, BigDecimal.ROUND_HALF_UP);

        do {

            interest = left.multiply(zeroRate);
            refunded = basePayment.subtract(interest);
            left 	 = left.subtract(refunded);

            period++;

        } while(left.intValue() > 0 && period < 1000);

        loans.add(new LoanPlan(borrowed, rate, (period-1)));
        loans.add(new LoanPlan(borrowed, rate, period));

		return loans;
	}

    /***
     * Transform values to get a LoanPlan object
     * @param borrowed
     * @param period
     * @param rate
     * @param ratePeriod
     * @return LoanPlan
     */
    public static LoanPlan getLoanPlan(final BigDecimal borrowed, final int period, final BigDecimal rate, final RatePeriod ratePeriod, final BigDecimal insurance) {

        LoanPlan loan = new LoanPlan();
        loan.setBorrowed(borrowed);
        loan.setPeriod(period);
		loan.setInsurance(insurance);

		BigDecimal actualRate = rate;

        if (ratePeriod == RatePeriod.ANNUAL) {
			actualRate = getMontlyRateFromYearly(actualRate);
        }

        loan.setRate(actualRate);

        return loan;
    }

	/***
	 * Get LoanPlan without shiftRefundBy paramater
	 * @param borrowed
	 * @param period
	 * @param rate
	 * @param ratePeriod
	 * @param insurance
	 * @return
	 */
	public static LoanPlan getLoanPlan(final double borrowed,
									   final int period,
									   final double rate,
									   final RatePeriod ratePeriod,
									   final double insurance) {

		return getLoanPlan(borrowed, period,rate, ratePeriod, insurance, LoanPlan.REFUND_NOW);
	}

	/***
	 * Get LoanPlan including shiftRefundBy paramater
	 * @param borrowed
	 * @param period
	 * @param rate
	 * @param ratePeriod
	 * @param insurance
	 * @param refundStartingAt
	 * @return
	 */
	public static LoanPlan getLoanPlan(final double borrowed,
									   final int period,
									   final double rate,
									   final RatePeriod ratePeriod,
									   final double insurance,
									   final int refundStartingAt) {

		BigDecimal borrowedBg 	= new BigDecimal(borrowed).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal actualRate 	= new BigDecimal(rate).setScale(4, BigDecimal.ROUND_HALF_UP);
		BigDecimal insuranceBg 	= new BigDecimal(insurance).setScale(4, BigDecimal.ROUND_HALF_UP);

		LoanPlan loan = new LoanPlan();
		loan.setBorrowed(borrowedBg);
		loan.setPeriod(period);
		loan.setInsurance(insuranceBg);
		loan.setShiftRefundBy(refundStartingAt);

		if (ratePeriod == RatePeriod.ANNUAL) {
			actualRate = getMontlyRateFromYearly(actualRate);
		}

		loan.setRate(actualRate);

		return loan;
	}

	/***
	 * Centralized method to get the period rate according to the annual capital
	 * @param rate
	 * @return
	 */
	public static BigDecimal getMontlyRateFromYearly(BigDecimal rate)
	{
		return rate.divide(new BigDecimal(LoanPlan.PER_YEAR), 4, BigDecimal.ROUND_HALF_UP);
	}

    public static LoanPlan findScheduleStepPayment(LoanPlan loanPlan, TreeMap<Integer, Payment> payments) {

		BigDecimal insurance = loanPlan.getInsurance();
		BigDecimal borrowed = loanPlan.getBorrowed();
		BigDecimal zeroRate = loanPlan.getRate().divide(new BigDecimal("100.0"), 6, BigDecimal.ROUND_HALF_UP);

		BigDecimal basePayment = loanPlan.getPayment();
		BigDecimal baseAjustedPayment = basePayment;

		int period = 1;
		do {

			BigDecimal interest = borrowed.multiply(zeroRate).setScale(2, BigDecimal.ROUND_HALF_UP);

			// get the current payment
			BigDecimal whichBasePayment = loanPlan.getPayment();
			// If step payment
			if (payments != null && payments.size() > 0) {
                BigDecimal value = getBasePayment(payments, period);
                if (value != null) {
                    whichBasePayment = value;
                }
			}

			// changing base
			if ( whichBasePayment.compareTo(basePayment) != 0) { // 0 means equals
				basePayment = whichBasePayment;
				/* TODO: To be found!!
				LoanPlan revisedLoan = findPeriod(borrowed, basePayment, loanPlan.getRate()).get(0);
				baseAjustedPayment = calculateMonthlyAmount(loanPlan);
				 */
                baseAjustedPayment = basePayment;
			}

			BigDecimal refunded = BigDecimal.ZERO;
			// Do I have started to refund ?
			if (period > loanPlan.getShiftRefundBy()) {
				refunded = baseAjustedPayment.subtract(interest);
			}

			borrowed = borrowed.subtract(refunded);

			Payment payment = new Payment();
			payment.setBorrowed(borrowed);
			payment.setPeriod(period++);
			payment.setInterest(interest);
			payment.setCapital(refunded);
			payment.setInsurance(insurance);

			loanPlan.getPayments().add(payment);

		} while(borrowed.intValue() > 0 && period < 500);

		return loanPlan;
    }

	/***
	 * Return the current Payment according to the current period
	 * @param payments
	 * @param period
	 * @return
	 */
	private static BigDecimal getBasePayment(TreeMap<Integer, Payment> payments, int period)
	{
		BigDecimal basePayment = null;

		for (Map.Entry<Integer, Payment> entry : payments.entrySet()) {
			if (period >= entry.getKey()) {
				basePayment = ((Payment) entry.getValue()).getBasePayment();
			}
		}

		return basePayment;
	}

    public static BigDecimal getBorrowedAmountLeftAt(LoanPlan loanPlan, int until) {

        BigDecimal borrowed = loanPlan.getBorrowed();
        BigDecimal zeroRate = loanPlan.getRate().divide(new BigDecimal("100.0"));

        BigDecimal basePayment = LoanBusiness.calculateMonthlyAmount(loanPlan);

        for (int i=0; i < until; i++) {

            BigDecimal interest = borrowed.multiply(zeroRate).setScale(2, BigDecimal.ROUND_HALF_UP);

            Payment payment = new Payment();
            payment.setInterest(interest);

            BigDecimal refunded = basePayment.subtract(interest);
            borrowed = borrowed.subtract(refunded);
        }

        return borrowed.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
