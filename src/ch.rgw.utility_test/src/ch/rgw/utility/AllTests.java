package ch.rgw.utility;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	ch.rgw.tools.Test_JdbcLink.class,
	ch.rgw.tools.Test_Money.class
})
public class AllTests {
	public static Test suite() throws ClassNotFoundException{
		TestSuite suite = new TestSuite("ch.rgw.utility tests");
		return suite;
	}
}
