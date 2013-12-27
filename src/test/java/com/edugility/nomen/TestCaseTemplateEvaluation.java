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

public class TestCaseTemplateEvaluation {

  public TestCaseTemplateEvaluation() {
    super();
  }

  @Test
  public void testEvaluation() {
    final NameValue laird = new NameValue("Laird", true);
    assertTrue(laird.isAtomic());
    assertNull(laird.getWhitespaceReplacement());

    final NameValue jarrett = new NameValue("Jarrett", true);
    assertTrue(jarrett.isAtomic());
    assertNull(jarrett.getWhitespaceReplacement());

    final NameValue nelson = new NameValue("Nelson", true);
    assertTrue(nelson.isAtomic());
    assertNull(nelson.getWhitespaceReplacement());

    final NameValue lairdJarrettNelson = new NameValue("${firstName}        ${middleName} ${lastName}");
    assertFalse(lairdJarrettNelson.isAtomic());
    assertEquals(" ", lairdJarrettNelson.getWhitespaceReplacement());

    final Name firstName = new Name(laird);
    final Name middleName = new Name(jarrett);
    final Name lastName = new Name(nelson);
    final Name fullName = new Name(lairdJarrettNelson);

    // All these Names are initially unowned.
    assertNull(firstName.getNamed());
    assertNull(middleName.getNamed());
    assertNull(lastName.getNamed());
    assertNull(fullName.getNamed());

    final Named dude = new Named() {
        private static final long serialVersionUID = 1L;
        @Override
        public final Name getName(final NameType nameType) {
          if (nameType == null) {
            throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
          }
          final String value = nameType.getValue();
          if ("firstName".equals(value)) {
            return firstName;
          } else if ("middleName".equals(value)) {
            return middleName;
          } else if ("lastName".equals(value)) {
            return lastName;
          } else if ("fullName".equals(value)) {
            return fullName;
          } else {
            return null;
          }
        }
      };

    // Now that we've got the chicken-and-egg problem out of the way,
    // "own" all the names.
    firstName.setNamed(dude);
    middleName.setNamed(dude);
    lastName.setNamed(dude);
    fullName.setNamed(dude);

    // Make sure the ownership "stuck".
    assertSame(dude, firstName.getNamed());
    assertSame(dude, middleName.getNamed());
    assertSame(dude, lastName.getNamed());
    assertSame(dude, fullName.getNamed());

    final String value = fullName.getValue();
    assertEquals("Laird Jarrett Nelson", value);
  }

  @Test(expected = IllegalStateException.class)
  public void testSyntacticallyInvalidTemplate() {
    final Name firstName = new Name();
    final NameValue template = new NameValue("${BAD SYNTAX ON PURPOSE");
    assertFalse(template.isAtomic());

    // Throws IllegalStateException
    firstName.setNameValue(template);
  }

}
