package biz.eventual.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

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
import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.enums.RatePeriod;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LoanwizardApplication.class)
@WebAppConfiguration
public class ServiceLoanTest extends TestCase
{

	@Autowired
	ServiceLoan serviceLoan;

	@Autowired
	ServiceStepLoan serviceStepLoan;

	@Test
	public void checkMonthlyAmount() {

        StepLoan stepLoan;

        BigDecimal amount = serviceLoan.getMonthlyAmount(90000.0, 240, 2, RatePeriod.ANNUAL);
        log.debug("montant "+amount);

        amount = serviceLoan.getMonthlyAmount(15000.0, 60, 1, RatePeriod.ANNUAL);
        log.debug("montant "+amount);

		//double capital = serviceLoan.getMonthlyAmount(1000.0, 24, 18.0, RatePeriod.ANNUAL);
		//assertEquals(49.92, capital);

		amount = serviceLoan.getMonthlyAmount(65000, 96, 2.85, RatePeriod.ANNUAL);
		assertEquals(758.00, amount.doubleValue());

    }

    @Test
    public void findPeriod() {

        List<LoanPlan> loans = serviceLoan.findPeriod(20000, 660, 12, RatePeriod.ANNUAL);
        assertTrue(loans.size() == 2);
		assertEquals(36, loans.get(0).getPeriod());
		assertEquals(37, loans.get(1).getPeriod());
    }

	@Test
	public void getPayment() {

        List<Payment> payments;

        payments = serviceLoan.getPayments(20000, 12, 36, RatePeriod.ANNUAL);
        assertEquals(36, payments.size());
        assertEquals(664.29, payments.get(0).getBasePayment().doubleValue());

		payments = serviceLoan.getPayments(54537.35, 3.6, 180, RatePeriod.ANNUAL);
		assertEquals(180, payments.size());
        assertEquals(392.56, payments.get(0).getBasePayment().doubleValue());

        // shift by 12 payments
        payments = serviceLoan.getPayments(54537.35, 3.6, 192, RatePeriod.ANNUAL, 12);
        assertEquals(192, payments.size());
        // only interest
        assertEquals(163.61, payments.get(0).getBasePayment().doubleValue());
        // last interest
        assertEquals(163.61, payments.get(11).getBasePayment().doubleValue());
        // start refunding
        assertEquals(392.56, payments.get(12).getBasePayment().doubleValue());

	}

    @Test
    public void findScheduleStepPayment() {

        LoanPlan loanPlan;

        // My loan
        BigDecimal amount = serviceLoan.getMonthlyAmount(54537.35, 180, 3.6, RatePeriod.ANNUAL);
        assertEquals(392.56, amount.doubleValue());

        // No change
        loanPlan = serviceLoan.findScheduleStepPayment(54537.35, null, 180, 3.6, RatePeriod.ANNUAL, 0);
        assertEquals(180, loanPlan.getPayments().size());

        // With step
        TreeMap<Integer, Payment> payments = new TreeMap<Integer, Payment>();
        Payment payment = new Payment();

        payment.setCapital(new BigDecimal(510).setScale(2, BigDecimal.ROUND_HALF_UP));
        payment.setInsurance(new BigDecimal(31.82).setScale(2, BigDecimal.ROUND_HALF_UP));
        payments.put(79, payment);

        loanPlan = serviceLoan.findScheduleStepPayment(54537.35, payments, 180, 3.6, RatePeriod.ANNUAL, 0);
        assertEquals(154, loanPlan.getPayments().size());

        payment = new Payment();
        payment.setCapital(new BigDecimal(560).setScale(2, BigDecimal.ROUND_HALF_UP));
        payments.put(103, payment);

        loanPlan = serviceLoan.findScheduleStepPayment(54537.35, payments, 180, 3.6, RatePeriod.ANNUAL, 0);
        assertEquals(149, loanPlan.getPayments().size());
    }

}