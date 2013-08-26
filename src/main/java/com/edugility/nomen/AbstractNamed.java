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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractNamed implements Named {

  private static final long serialVersionUID = 1L;

  private Map<NameType, Name> names;

  protected AbstractNamed() {
    super();
  }

  @Override
  public Name getName(final NameType nameType) {
    Name name = null;
    if (nameType != null && this.names != null && !this.names.isEmpty()) {
      name = this.names.get(nameType);
    }
    return name;
  }

  public Name putName(final NameType nameType, final NameValue nameValue) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }
    if (nameValue == null) {
      throw new IllegalArgumentException("nameValue", new NullPointerException("nameValue"));
    }
    if (this.names == null) {
      this.names = new HashMap<NameType, Name>(11);
    }
    final Name returnValue = this.names.put(nameType, new Name(this, nameValue));
    if (returnValue != null) {
      returnValue.setNamed(null);
    }
    return returnValue;
  }

  public Set<NameType> getNameTypes() {
    final Set<NameType> returnValue;
    if (this.names == null || this.names.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableSet(this.names.keySet());
    }
    return returnValue;
  }

  public Collection<Name> getNames() {
    final Collection<Name> returnValue;
    if (this.names == null || this.names.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableCollection(this.names.values());
    }
    return returnValue;
  }

}
