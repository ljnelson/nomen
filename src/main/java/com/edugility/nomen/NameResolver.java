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

import org.mvel2.integration.VariableResolver;

import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class NameResolver implements VariableResolver {

  private static final long serialVersionUID = 1L;

  private Named named;

  private String name;

  public NameResolver(final Named named, final String name) {
    super();
    if (named == null) {
      throw new IllegalStateException("named", new NullPointerException("named"));
    }
    if (name == null) {
      throw new IllegalArgumentException("name", new NullPointerException("name"));
    }
    this.named = named;
    this.name = name;
  }

  @Override
  public int getFlags() {
    return 0; // per documentation
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Class<Name> getType() {
    return Name.class;
  }

  @Override
  public Object getValue() {
    final Name n = this.named.getName(new NameType(this.getName()));
    if (n == null) {
      return null;
    }
    return n.getValue();
  }

  @Override
  public void setStaticType(final Class type) {
    throw new UnsupportedOperationException("setValue");
  }

  @Override
  public void setValue(final Object value) {
    throw new UnsupportedOperationException("setValue");
  }

}
