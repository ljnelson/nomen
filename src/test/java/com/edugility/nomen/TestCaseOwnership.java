/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013-2014 Edugility LLC.
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCaseOwnership {

  private AbstractNamed dude1;

  private AbstractNamed dude2;

  public TestCaseOwnership() {
    super();
  }

  @Before
  public void setUp() {
    this.dude1 = new AbstractNamed() {
        private static final long serialVersionUID = 1L;
      };
    this.dude2 = new AbstractNamed() {
        private static final long serialVersionUID = 1L;
      };
  }

  @Test
  public void testOwnershipMonitoring() {

    // Create a new Name, consisting of a just-in-time-created
    // NameValue of "Laird" assigned to dude1.  That Name is now owned
    // by dude1, but unindexed.
    final Name laird = new Name(this.dude1, new NameValue("Laird"));
    assertSame(this.dude1, laird.getNamed());
    assertEquals(0, laird.getPropertyChangeListeners("named").length);

    final NameType first = new NameType("first");
    final NameType preferred = new NameType("preferred");

    // Store the very same Name under two types.  The assertNull()
    // call makes sure that there wasn't one in there already.
    assertNull(this.dude1.putName(first, laird));
    assertNull(this.dude1.putName(preferred, laird));

    assertEquals(1, laird.getPropertyChangeListeners("named").length);

    assertEquals(2, this.dude1.getNameTypes().size());
    
    laird.setNamed(this.dude2);

    assertTrue(this.dude1.getNameTypes().isEmpty());

  }

}
