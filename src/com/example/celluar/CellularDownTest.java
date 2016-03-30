/**
 * 
 */
package com.example.celluar;

import org.junit.Before;
import org.junit.Test;

import com.example.Integrity.IntegrityCheck;

/**
 * @author zxyqwe
 * 
 */
public class CellularDownTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.example.celluar.CellularDown#queryFragment(java.lang.String)}.
	 */
	@Test
	public final void testQueryFragment() {
		CellularDown cs = new CellularDown();
		cs.queryFragment(IntegrityCheck.URL_TAG + 1 + ".php");
	}

}
