package biz.eventual.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import biz.eventual.bean.LoanPlan;
import biz.eventual.bean.Payment;
import biz.eventual.dto.PaymentDto;
import biz.eventual.enums.RatePeriod;
import biz.eventual.service.ServiceLoan;

/**
 * Created by tweety on 29/06/15.
 */
@RestController
public class LoanController {

    @Autowired
    ServiceLoan serviceLoan;


    /*
    /!\ Spring considers that anything behind the last dot is a file extension such as .json or .xml and trucate it to retrieve your parameter.
     */
    @RequestMapping(value = "/loan/get/{capital}/{period}/{rate:.+}", method = RequestMethod.GET)
    public LoanPlan getLoanNoInsurance(@PathVariable double capital,
                                       @PathVariable int period,
                                       @PathVariable double rate) {

        // return  serviceLoan.findScheduleStepPayment(54537.35, payments, 180, 3.6, RatePeriod.ANNUAL, 31.82);
        return serviceLoan.findScheduleStepPayment(capital, null, period, rate, RatePeriod.ANNUAL, 0);
    }

    @RequestMapping(value = "/loan/get/{loan}", method = RequestMethod.POST)
    public LoanPlan getLoanFromLoan(@PathVariable LoanPlan loan) {

        // return  serviceLoan.findScheduleStepPayment(54537.35, payments, 180, 3.6, RatePeriod.ANNUAL, 31.82);
        return serviceLoan.findScheduleStepPayment(
            loan.getBorrowed().doubleValue(),
            null,
            loan.getPeriod(),
            loan.getRate().doubleValue(),
            RatePeriod.MONTHLY,
            0
        );
    }


    @RequestMapping(value = "/loan/get/{capital}/{period}/{rate}/{insurance:.+}", method = RequestMethod.GET)
    public LoanPlan getLoan(@PathVariable double capital,
                            @PathVariable int period,
                            @PathVariable double rate,
                            @PathVariable double insurance) {

        return serviceLoan.findScheduleStepPayment(capital, null, period, rate, RatePeriod.ANNUAL, insurance);
    }

    @RequestMapping(value = "/loan/step/{capital}/{period}/{rate}/{insurance}/{steps:.+}", method = RequestMethod.GET)
    public LoanPlan getLoanWithStepPayment(@PathVariable double capital,
                            @PathVariable int period,
                            @PathVariable double rate,
                            @PathVariable double insurance,
                            @PathVariable String steps) {

        // http://localhost:5001/loan/step/1000.0/12/2.0/0/[{"k":1000, "i":1.0, "p": 6}]

        ObjectMapper mapper = new ObjectMapper();
        List<PaymentDto> paymentsData = null;

        try {
            // List
            paymentsData = mapper.readValue(steps, new TypeReference<List<PaymentDto>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        TreeMap<Integer, Payment> payments = new TreeMap<Integer, Payment>();

        for (PaymentDto dto : paymentsData) {
            Payment payment = new Payment();
            payment.setCapital(new BigDecimal(dto.getK()).setScale(2, BigDecimal.ROUND_HALF_UP));
            payment.setInsurance(new BigDecimal(dto.getI()).setScale(2, BigDecimal.ROUND_HALF_UP));

            payments.put(dto.getP(), payment);
        }

        /*
        Payment payment = new Payment();
        payment.setCapital(new BigDecimal(510).setScale(2, BigDecimal.ROUND_HALF_UP));
        payment.setInsurance(new BigDecimal(31.82).setScale(2, BigDecimal.ROUND_HALF_UP));
        payments.put(79, payment);
        */

        return serviceLoan.findScheduleStepPayment(capital, payments, period, rate, RatePeriod.ANNUAL, insurance);
    }
}
