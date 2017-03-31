package biz.eventual.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import biz.eventual.bean.StepLoan;
import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.bean.SubLoan;

/**
 * Created by Thibault de Lambilly on 25/06/2015.
 */
public class StepLoanBusiness
{

	public static StepLoan findPayment(LoanPlan loanPlan,  List<SubLoan> subLoans) {
		return StepLoanBusiness.findPayment(new StepLoan(loanPlan, subLoans));
	}

	public static StepLoan findPayment(StepLoan stepLoan) {

		BigDecimal searchAmount = LoanBusiness.calculateMonthlyAmount(stepLoan.getLoanPlan());
        // keep it for record!
        stepLoan.getLoanPlan().setPayment(searchAmount);

		double[] step = new double[] {100, 10, 5, 1, 0.1};

		for (int i = 0; i < step.length; i++) {
			BigDecimal max = findMax(stepLoan, step[i], searchAmount);
			if (max.compareTo(searchAmount) == 1) {
				searchAmount = max.subtract(new BigDecimal(step[i])).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}

		stepLoan.setPayment(searchAmount);

		return stepLoan;
	}

	private static BigDecimal findMax(StepLoan stepLoan, double step, BigDecimal amount) {

		BigDecimal leftAmount = BigDecimal.ZERO;
		BigDecimal stepBg = new BigDecimal(step)
							.setScale(1, BigDecimal.ROUND_HALF_UP);

		BigDecimal stepAmount = amount.subtract(stepBg); // -step 4 the first round

		do {
			stepAmount = stepAmount.add(stepBg);
			leftAmount = workAmountForScaleLoan(stepLoan, stepAmount);
		} while (leftAmount.intValue() > 0);

		return stepAmount;
	}


	private static BigDecimal workAmountForScaleLoan(StepLoan stepLoan, BigDecimal basePayment) {

		stepLoan.setPayments(new ArrayList<Payment>());

		BigDecimal searchAmount = stepLoan.getLoanPlan()
										.getBorrowed()
										.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal zeroRate = stepLoan.getLoanPlan()
                .getRate()
                .divide(new BigDecimal(100));

		int period = stepLoan.getLoanPlan().getPeriod();

		if (stepLoan.getSubloans() == null) stepLoan.setSubloans(new ArrayList<SubLoan>());

		List<SubLoan> subLoansClone = new ArrayList<SubLoan>(stepLoan.getSubloans());

		SubLoan currentSubLoan = null;
		if (subLoansClone != null && subLoansClone.size() > 0) {
			currentSubLoan = subLoansClone.get(0);
		}

		/***
		 * Attention : the sub loan can it be bigger than the monthly interest payment ?
		 */
		for (int i=0; i < period; i++) {

			BigDecimal interest = searchAmount
				.multiply(zeroRate)
				.setScale(2, BigDecimal.ROUND_HALF_UP);

			Payment onePayment = new Payment();
			onePayment.setInterest(interest);

			BigDecimal subPaymentDeduction = BigDecimal.ZERO;
			if (currentSubLoan != null) {
				int start = currentSubLoan.getStartingAt();
				int lastSubPayment = (start + currentSubLoan.getPeriod()) - 1;

				// Getting value
				if (i >= (start-1) && i < lastSubPayment ) {
					subPaymentDeduction = currentSubLoan.getPayment();
				}

				// subPayment done - switching to the next one
				if (i == lastSubPayment - 1) {
					subLoansClone.remove(currentSubLoan);

					if (subLoansClone.size() > 0) {
						currentSubLoan = subLoansClone.get(0);
					}
				}
			}

			BigDecimal stepAmount = basePayment.subtract(subPaymentDeduction);
			BigDecimal refunded = stepAmount.subtract(interest);

			onePayment.setCapital(refunded);
			stepLoan.getPayments().add(onePayment);

			searchAmount = searchAmount.subtract(refunded);
		}

		return searchAmount;
	}
}
