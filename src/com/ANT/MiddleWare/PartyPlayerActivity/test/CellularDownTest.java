/**
 * 
 */
package com.ANT.MiddleWare.PartyPlayerActivity.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ANT.MiddleWare.Integrity.IntegrityCheck;

/**
 * @author zxyqwe
 * 
 */
public class CellularDownTest extends AndroidTestCase {
	private static final String TAG = CellularDownTest.class.getSimpleName();

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.example.Celluar.CellularDown#queryFragment(java.lang.String)}.
	 */
	public final void testQueryFragment() {
		final IntegrityCheck iTC = IntegrityCheck.getInstance();
		for (int i = 1; i < 6; i++) {
			int tmpp = i;
			byte[] tmp = iTC.getSegments(tmpp);
			Assert.assertNotNull(tmp);
			Log.e(TAG, "" + tmpp + " " + tmp.length);
		}
	}
}
