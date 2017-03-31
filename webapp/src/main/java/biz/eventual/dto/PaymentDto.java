package biz.eventual.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by thibault.delambilly on 02/07/2015.
 */
@Getter
@Setter
public class PaymentDto
{
	private double 	k = 0.0; // capital
	private double 	i = 0.0; // interest
	private int 	p = 0; 	 // period
	private int 	f = 0; 	 // insurance
}
