/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2011-2013 Edugility LLC.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package com.edugility.nomen;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.edugility.nomen.Range.ContinuousDateInterpolator;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCaseRange {

  public TestCaseRange() {
    super();
  }

  public static final void zeroTime(final Calendar cal) {
    if (cal != null) {
      cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.HOUR, cal.getMinimum(Calendar.HOUR)); // for maximum correctness and safety you need to set both (!)
      cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
      cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
      cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
      cal.set(Calendar.AM_PM, cal.getMinimum(Calendar.AM_PM)); // this makes it "really correct" for future modifications
    }
  }

  @Test
  public void testDateRange() {
    Calendar cal = Calendar.getInstance();
    assert cal != null;
    zeroTime(cal);
    cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.YEAR, 2013);
    final Date januaryFirstAtMidnight = cal.getTime();

    cal = Calendar.getInstance();
    assert cal != null;
    zeroTime(cal);
    cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_MONTH, 2);
    cal.set(Calendar.YEAR, 2013);
    final Date januarySecondAtMidnight = cal.getTime();

    assertTrue(januarySecondAtMidnight.after(januaryFirstAtMidnight));

    cal = Calendar.getInstance();
    assert cal != null;
    zeroTime(cal);
    cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.YEAR, 2013);
    cal.set(Calendar.HOUR_OF_DAY, 13);
    final Date januaryFirstAtOnePM = cal.getTime();

    assertTrue(januaryFirstAtOnePM.after(januaryFirstAtMidnight));
    assertTrue(januaryFirstAtOnePM.before(januarySecondAtMidnight));

    cal = Calendar.getInstance();
    assert cal != null;
    zeroTime(cal);
    cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_MONTH, 2);
    cal.set(Calendar.YEAR, 2013);
    cal.set(Calendar.HOUR_OF_DAY, 13);
    final Date januarySecondAtOnePM = cal.getTime();

    assertTrue(januarySecondAtOnePM.after(januarySecondAtMidnight));

    Range<Date> dateRange = new Range<Date>();
    dateRange.setLowerBound(januaryFirstAtMidnight);
    dateRange.setUpperBound(januarySecondAtMidnight);
    dateRange.setInterpolator(new ContinuousDateInterpolator(dateRange.getLowerBound(), dateRange.getUpperBound()));

    assertFalse(dateRange.contains(januarySecondAtOnePM));
    assertFalse(dateRange.contains(januarySecondAtMidnight));
    assertTrue(dateRange.contains(januaryFirstAtOnePM));

    final Iterator<Date> i = dateRange.iterator();
    assertNotNull(i);
    assertTrue(i.hasNext());
    long current = januaryFirstAtMidnight.getTime();
    while (i.hasNext()) {
      final Date d = i.next();
      assertNotNull(d);
      assertEquals(current, d.getTime());
      current = current + 1L;
    }
    assertEquals(current + 1L, januarySecondAtMidnight.getTime());

  }

}
