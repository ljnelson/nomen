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

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCaseNameValue {

  public TestCaseNameValue() {
    super();
  }

  @Test
  public void testEquality() {
    final NameValue nv1 = new NameValue("Laird", true);
    final NameValue nv2 = new NameValue("Laird", true);
    assertEquals(nv1, nv2);
    assertEquals(nv1.hashCode(), nv2.hashCode());

    final NameValue nv3 = new NameValue("Laird", true);
    final NameValue nv4 = new NameValue("Laird", true, null);

    // Both are atomic, so the whitespace parameter is ignored.
    assertEquals(nv3, nv4);

    final NameValue nv5 = new NameValue("Laird");
    final NameValue nv6 = new NameValue("Laird", "XYZ");
    
    assertNotEquals(nv5, nv6);
  }

  @Test
  public void testAtomicityAndWhitespaceReplacementInteraction() {
    final NameValue nv1 = new NameValue("Laird", true);
    assertTrue(nv1.isInitialized());
    assertNull(nv1.getWhitespaceReplacement());
    assertTrue(nv1.isAtomic());

    final NameValue nv2 = new NameValue(); // protected constructor
    assertFalse(nv2.isInitialized());
    assertFalse(nv2.isAtomic());
    assertNull(nv2.getWhitespaceReplacement());
    nv2.setWhitespaceReplacement(" ");
    assertEquals(" ", nv2.getWhitespaceReplacement());
    nv2.setAtomic(true);
    assertTrue(nv2.isAtomic());
    nv2.setValue("Laird");
    assertTrue(nv2.isInitialized());
    assertNull(nv2.getWhitespaceReplacement());
    nv2.setWhitespaceReplacement(null); // should be OK, because it's the same value
    nv2.setWhitespaceReplacement(null); // should be OK, because it's the same value
    nv2.setWhitespaceReplacement(null); // should be OK, because it's the same value
  }

}
