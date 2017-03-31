package biz.eventual.Business;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import biz.eventual.LoanwizardApplication;

/**
 * Created by Thibault de Lambilly on 30/07/2015.
 */
@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = LoanwizardApplication.class)
@WebAppConfiguration
public class LoanBusinessTest
{
	@Before
	public void setUp() throws Exception
	{

	}
}