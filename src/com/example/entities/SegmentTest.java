/**
 * 
 */
package com.example.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zxyqwe
 * 
 */
public class SegmentTest {
	private String data;
	private HashSet<Integer> numSet = new HashSet<Integer>();
	private LinkedList<FileFragment> fraList = new LinkedList<>();
	private int base = 5000000;
	private int fra = 500;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		data = getRandomString(base);
		Random random = new Random();
		numSet.add(0);
		numSet.add(base);
		while (true) {
			if (numSet.size() == fra + 1)
				break;
			numSet.add(random.nextInt(base));
		}
		LinkedList<Integer> setSort = new LinkedList<Integer>(numSet);
		Collections.sort(setSort);
		for (int i = 0; i < fra; i++) {
			int start = i == 0 ? 0 : random.nextInt(setSort.get(i));
			int stop = i == fra - 1 ? base : setSort.get(i + 1)
					+ random.nextInt(base - setSort.get(i + 1));

			FileFragment tmp = new FileFragment(start, stop, 1);
			tmp.setData(data.substring(start, stop).getBytes());
			fraList.add(tmp);
		}
		Collections.shuffle(fraList);
	}

	/**
	 * Test method for
	 * {@link com.example.entities.Segment#insert(com.example.entities.FileFragment)}
	 * .
	 */
	@Test
	public final void testInsert() {
		Segment seg = new Segment(1, base);
		for (FileFragment ff : fraList) {
			seg.insert(ff);
		}
	}

	/**
	 * Test method for {@link com.example.entities.Segment#checkIntegrity()}.
	 */
	@Test
	public final void testCheckIntegrity() {
		Segment seg = new Segment(1, base);
		boolean b = seg.checkIntegrity();
		Assert.assertFalse(b);
		for (FileFragment ff : fraList) {
			seg.insert(ff);
		}
		b = seg.checkIntegrity();
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link com.example.entities.Segment#getData()}.
	 */
	@Test
	public final void testGetData() {
		Segment seg = new Segment(1, base);
		byte[] tmp = seg.getData();
		Assert.assertNull(tmp);
		for (FileFragment ff : fraList) {
			seg.insert(ff);
		}
		tmp = seg.getData();
		Assert.assertNotEquals(tmp, data.getBytes());
		boolean b = seg.checkIntegrity();
		Assert.assertTrue(b);
		tmp = seg.getData();
		Assert.assertArrayEquals(tmp, data.getBytes());
	}

	private String getRandomString(int length) { 
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
