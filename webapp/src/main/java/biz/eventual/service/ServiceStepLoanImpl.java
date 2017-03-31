package biz.eventual.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.StepLoan;
import biz.eventual.bean.SubLoan;
import biz.eventual.business.LoanBusiness;
import biz.eventual.business.StepLoanBusiness;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Slf4j
@Service
public class ServiceStepLoanImpl implements ServiceStepLoan
{
	@Override
	public StepLoan getPayment(final double borrowed,
							   final int period,
							   final double rate,
							   final RatePeriod ratePeriod,
							   final List<SubLoan> subLoans,
							   final double interest)
	{
		LoanPlan loan = LoanBusiness.getLoanPlan(borrowed, period, rate, ratePeriod, interest);

		return StepLoanBusiness.findPayment(loan, subLoans);
	}
}
