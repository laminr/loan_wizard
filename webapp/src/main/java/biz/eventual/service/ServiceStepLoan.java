package biz.eventual.service;

import java.util.List;

import biz.eventual.bean.StepLoan;
import biz.eventual.bean.SubLoan;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
public interface ServiceStepLoan
{
	StepLoan getPayment(final double borrowed, final int period, final double rate, final  RatePeriod ratePeriod, final List<SubLoan> subLoans, final double interest);

}
