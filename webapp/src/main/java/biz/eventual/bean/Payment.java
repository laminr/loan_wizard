package biz.eventual.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import com.google.gson.Gson;

/**
 * Created by Thibault de Lambilly on 19/06/2015.
 */
@Getter
@Setter
public class Payment
{
	BigDecimal borrowed = BigDecimal.ZERO;

	BigDecimal capital = BigDecimal.ZERO;

	BigDecimal interest = BigDecimal.ZERO;

	int period = 0;

	BigDecimal insurance = BigDecimal.ZERO;

	public BigDecimal getBasePayment() {
		return capital.add(interest).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getFullPayment() {
		return capital
				.add(interest)
				.add(insurance)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
