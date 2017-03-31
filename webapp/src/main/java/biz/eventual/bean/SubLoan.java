package biz.eventual.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import com.google.gson.Gson;

import biz.eventual.bean.LoanPlan;

/**
 * Created by tweety on 19/06/15.
 */
@Getter
@Setter
public class SubLoan extends LoanPlan {

    int startingAt = 0;

    public SubLoan() {}

    public SubLoan(BigDecimal borrowed, BigDecimal rate, int period, int startingAt) {
        super(borrowed, rate, period);
        this.startingAt = startingAt;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
