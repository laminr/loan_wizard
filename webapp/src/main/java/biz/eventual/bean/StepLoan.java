package biz.eventual.bean;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by tweety on 20/06/15.
 */
@Getter
@Setter
public class StepLoan {

    private BigDecimal payment;
    private List<Payment> payments;

    private LoanPlan loanPlan;
    private List<SubLoan> subloans;

    public StepLoan (LoanPlan loanPlan, List<SubLoan> subLoans) {
        this.loanPlan = loanPlan;
        this.subloans = subLoans;
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            json = "{erreur : Impossible to parse object StepLoan in JSON}";
        }

        return json;
    }
}
