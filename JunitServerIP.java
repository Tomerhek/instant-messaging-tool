import static org.junit.Assert.*;

import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

public class JunitServerIP {

	@Test
	public void test() {
		
		NewClientGUI ncg= new NewClientGUI();
		String str =ncg.IpTXT.getText();
		assertEquals("127.0.0.1", str);
		
		
	}

}
