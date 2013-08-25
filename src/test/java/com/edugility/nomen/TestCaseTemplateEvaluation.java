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
    final NameValue laird = new NameValue("Laird");
    final NameValue jarrett = new NameValue("Jarrett");
    final NameValue nelson = new NameValue("Nelson");
    final NameValue lairdJarrettNelson = new NameValue("${firstName}        ${middleName} ${lastName}");

    final Name firstName = new Name();
    firstName.setNameValue(laird);

    final Name middleName = new Name();
    middleName.setNameValue(jarrett);

    final Name lastName = new Name();
    lastName.setNameValue(nelson);


    final Name fullName = new Name();
    fullName.setCollapseWhitespace(true);
    fullName.setNameValue(lairdJarrettNelson);

    final Named dude = new Named() {
        private static final long serialVersionUID = 1L;
        @Override
        public Name getName(final NameType nameType) {
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

    firstName.setNamed(dude);
    middleName.setNamed(dude);
    lastName.setNamed(dude);
    fullName.setNamed(dude);

    final String value = fullName.getValue();
    assertEquals("Laird Jarrett Nelson", value);
  }

}
