package biz.eventual.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import junit.framework.TestCase;

import biz.eventual.LoanwizardApplication;
import biz.eventual.bean.StepLoan;
import biz.eventual.bean.SubLoan;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LoanwizardApplication.class)
@WebAppConfiguration
public class ServiceStepLoanTest extends TestCase
{

	@Autowired
	ServiceLoan serviceLoan;

	@Autowired
	ServiceStepLoan serviceStepLoan;

	@Test
	public void checkStepMonthlyAmount() {

        StepLoan stepLoan;

		// Subloan empty
		stepLoan = serviceStepLoan.getPayment(65000.0, 96, 0.2375, RatePeriod.MONTHLY, new ArrayList<SubLoan>(), 0);
		assertEquals(758.00, stepLoan.getPayment().doubleValue());


        List<SubLoan> subLoans = new ArrayList<SubLoan>();
        BigDecimal borrowed = new BigDecimal(15000.0).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal rate = new BigDecimal(1).setScale(2, BigDecimal.ROUND_HALF_UP);
        SubLoan subLoan = new SubLoan(borrowed, rate, 60, 1);

        BigDecimal payment = serviceLoan.getMonthlyAmount(15000.0, 60, 1, RatePeriod.ANNUAL);
        subLoan.setPayment(payment);
        subLoans.add(subLoan);

        stepLoan = serviceStepLoan.getPayment(90000.0, 240, 2, RatePeriod.ANNUAL, subLoans, 0);
        log.debug("XXXXX "+stepLoan.toJson());

    }

	@Test
	public void getStepLoan() {

        BigDecimal monthlyPayment;

        monthlyPayment = serviceLoan.getMonthlyAmount(20000, 36, 12, RatePeriod.ANNUAL);
		assertEquals(664.29, monthlyPayment);

		StepLoan stepLoan = serviceStepLoan.getPayment(20000, 36, 12, RatePeriod.ANNUAL, null, 0);
        assertEquals(664.29, stepLoan.getPayments().get(0).getBasePayment().doubleValue());

    }


}