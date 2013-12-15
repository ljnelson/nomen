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

/**
 * An <a href="http://mvel.codehaus.org/">MVEL</a> {@link
 * VariableResolver} for resolving MVEL variables from a {@link
 * Named}.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see VariableResolver
 */
public final class NameResolver implements VariableResolver {

  private static final long serialVersionUID = 1L;

  private final Named named;

  private final String name;

  /**
   * Creates a new {@link NameResolver}.
   *
   * @param named the {@link Named} that will be used by the {@link
   * #getValue()} method; must not be {@code null}
   *
   * @param name the name of the variable this {@link NameResolver}
   * will return from its {@link #getName()} method; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if either {@code named} or
   * {@code name} is {@code null}
   */
  public NameResolver(final Named named, final String name) {
    super();
    if (named == null) {
      throw new IllegalArgumentException("named", new NullPointerException("named"));
    }
    if (name == null) {
      throw new IllegalArgumentException("name", new NullPointerException("name"));
    }
    this.named = named;
    this.name = name;
  }

  /**
   * Returns {@code 0}, following the recommendations of the <a
   * href="http://mvel.codehaus.org/">MVEL</a> project.
   *
   * <p>The {@link VariableResolver} documentation does not describe
   * what this method is used for.</p>
   *
   * @return {@code 0}
   */
  @Override
  public final int getFlags() {
    return 0; // per VariableResolver "documentation"
  }

  /**
   * Returns the {@link String} supplied at {@linkplain
   * #NameResolver(Named, String) construction time} as the value of
   * {@linkplain #NameResolver(Named, String) that constructor}'s
   * {@code name} parameter.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link String} naming the {@link Name}
   * whose value will be returned by the {@link #getValue()} method
   *
   * @see #getValue()
   */
  @Override
  public final String getName() {
    return this.name;
  }

  /**
   * Returns the value of {@code Name.class} when invoked.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return {@code Name.class}
   */
  @Override
  public final Class<Name> getType() {
    return Name.class;
  }

  /**
   * Returns the value of a {@link Name} reference in a <a
   * href="http://mvel.codehaus.org/">MVEL</a> program whose name is
   * given by the return value of this {@link NameResolver}'s {@link
   * #getName()} method.
   *
   * <p>If a suitable value cannot be determined, then the {@linkplain
   * String#isEmpty() empty string} ("") is returned.</p>
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link String}
   *
   * @see #getName()
   */
  @Override
  public final Object getValue() {
    Object returnValue = null;
    final String name = this.getName();
    if (name != null) {
      final NameType nt = NameType.valueOf(name);
      assert nt != null;
      final Name n = this.named.getName(nt);
      if (n != null) {
        returnValue = n.getValue();
      }
    }
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Does nothing on purpose.
   *
   * @param type ignored
   */
  @Override
  @SuppressWarnings("rawtypes")
  public final void setStaticType(final Class type) {

  }

  /**
   * Does nothing on purpose.
   *
   * @param value ignored
   */
  @Override
  public final void setValue(final Object value) {

  }

}
