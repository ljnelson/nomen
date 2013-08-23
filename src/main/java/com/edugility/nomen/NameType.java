/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013 Edugility LLC.
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

import java.io.Serializable;

public class NameType implements Serializable {

  private static final long serialVersionUID = 1L;

  private String value;

  public NameType() {
    super();
  }

  public NameType(final String value) {
    this();
    this.setValue(value);
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    final Object old = this.getValue();
    if (old != null && !old.equals(value)) {
      throw new IllegalStateException();
    }
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int hashCode;
    final String value = this.getValue();
    if (value == null) {
      hashCode = 0;
    } else {
      hashCode = value.hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && this.getClass().equals(other.getClass())) {
      final NameType him = (NameType)other;
      final Object value = this.getValue();
      if (value == null) {
        if (him.getValue() != null) {
          return false;
        }
      } else if (!value.equals(him.getValue())) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.valueOf(this.getValue());
  }

}
