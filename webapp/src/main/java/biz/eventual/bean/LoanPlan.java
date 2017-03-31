package biz.eventual.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
/*
@Entity
@Table(name = "loanplan")
*/
@Getter
@Setter
public class LoanPlan
{
	public final static int PER_YEAR = 12;
	public final static int REFUND_NOW = 0;

/*	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	int id;
*/
	/***
	 * Said to be MONTHLY rate
	 */
    private BigDecimal rate;

    private BigDecimal borrowed;

    @Setter(AccessLevel.NONE)
    private BigDecimal totalCost = BigDecimal.ZERO;

    private BigDecimal insurance = BigDecimal.ZERO;

    private BigDecimal fee = BigDecimal.ZERO;

	/***
	 * Number of month
	 */
    private int period = -1;

    /***
     * When starting to refund
     */
    private int shiftRefundBy = 0;

    private BigDecimal payment;

    private List<Payment> payments;

    private List<LoanPlan> subLoan;

    public LoanPlan() {}

    public LoanPlan(BigDecimal borrowed, BigDecimal rate, int period) {
        this.borrowed = borrowed;
        this.rate   = rate;
        this.period = period;
    }

    public List<Payment> getPayments() {
        if (payments == null) {
            payments = new ArrayList<Payment>();
        }

        return payments;
    }

    /***
     * Calculate the totalt cost by add all Payments
     * @return
     */
    public BigDecimal getTotalCost() {

        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            for(Payment payment : payments) {
                totalCost = totalCost.add(payment.getBasePayment());
            }
        }

        return totalCost.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            json = "{erreur : Impossible to parse object LoanPlan in JSON}";
        }

        return json;
    }
}
