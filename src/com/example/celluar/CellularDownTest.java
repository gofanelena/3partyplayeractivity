/**
 * 
 */
package com.example.celluar;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.example.Integrity.IntegrityCheck;

/**
 * @author zxyqwe
 * 
 */
public class CellularDownTest extends AndroidTestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.example.celluar.CellularDown#queryFragment(java.lang.String)}.
	 */
	public final void testQueryFragment() {
		IntegrityCheck iTC = IntegrityCheck.getInstance();
		for (int i = 1; i < 6; i++) {
			int tmpp = i;
			//String tmpp = IntegrityCheck.URI_TAG + i + ".mp4";
			byte[] tmp = iTC.getSegments(tmpp);
			Assert.assertNotNull(tmp);
		}
	}
}
