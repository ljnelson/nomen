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

public class NameResolverFactory extends BaseVariableResolverFactory {

  private static final long serialVersionUID = 1L;

  private final Named named;

  private static final VariableResolver emptyStringResolver = new SimpleValueResolver("");

  public NameResolverFactory(final Named named) {
    super();
    if (named == null) {
      throw new IllegalStateException("named", new NullPointerException("named"));
    }
    this.named = named;
  }

  @Override
  public boolean isResolveable(final String name) {
    final boolean returnValue;
    if (name == null) {
      returnValue = this.isNextResolveable(null);
    } else if (this.variableResolvers.containsKey(name)) {
      returnValue = true;
    } else {
      assert this.named != null;
      final Name n = this.named.getName(new NameType(name));
      if (n == null) {
        returnValue = this.isNextResolveable(name);
      } else {
        assert this.variableResolvers != null;
        this.variableResolvers.put(name, this.createNameResolver(this.named, name));
        returnValue = true;
      }
    }
    return returnValue;
  }

  protected NameResolver createNameResolver(final Named named, final String name) {
    return new NameResolver(named, name);
  }

  @Override
  public boolean isTarget(final String name) {
    return false;
  }

  @Override
  public VariableResolver createVariable(final String name, final Object value) {
    throw new UnsupportedOperationException("createVariable");
  }

  @Override
  public VariableResolver createVariable(final String name, final Object value, final Class<?> type) {
    throw new UnsupportedOperationException("createVariable");
  }

}
