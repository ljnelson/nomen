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
package com.edugility.nomen.mvel;

import java.io.Serializable; // for javadoc only

import com.edugility.nomen.Name;
import com.edugility.nomen.Named;
import com.edugility.nomen.NameType;
import com.edugility.nomen.NameValue;

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
 * @see NameResolverFactory
 *
 * @see VariableResolver
 *
 * @see <a href="http://mvel.codehaus.org">MVEL</a>
 */
public final class NameResolver implements VariableResolver, Named {

  
  /*
   * Static fields.
   */


  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;


  /*
   * Instance fields.
   */


  /**
   * The {@link Named} that owns {@link Name}s that might be resolved
   * by this {@link NameResolver}.
   *
   * <p>This field will never be {@code null}.</p>
   *
   * @see Named
   */
  private final Named named;

  /**
   * The {@link NameType} used by this {@link NameResolver} to {@linkplain
   * #getName(NameType) look up <code>Name</code>s}.
   *
   * <p>This field will never be {@code null}.</p>
   *
   * @see #getName(NameType)
   *
   * @see NameType
   */
  private final NameType nameType;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NameResolver}.
   *
   * @param named the {@link Named} that will be used by the {@link
   * #getValue()} method; must not be {@code null}
   *
   * @param nameType the {@link NameType} with which this {@link
   * NameResolver} will be affiliated; must not be {@code null}
   *
   * @exception IllegalArgumentException if either {@code named} or
   * {@code nameType} is {@code null}
   */
  public NameResolver(final Named named, final NameType nameType) {
    super();
    if (named == null) {
      throw new IllegalArgumentException("named", new NullPointerException("named"));
    }
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }
    this.named = named;
    this.nameType = nameType;
  }


  /*
   * Instance methods.
   */


  /**
   * Returns {@code 0}, following the recommendations of the <a
   * href="http://mvel.codehaus.org/">MVEL</a> project.
   *
   * <p>The {@link VariableResolver} documentation does not describe
   * what this method is used for.</p>
   *
   * @return {@code 0}
   *
   * @see VariableResolver#getFlags()
   */
  @Override
  public final int getFlags() {
    return 0; // per VariableResolver "documentation"
  }

  /**
   * Returns the {@link String} that is the {@linkplain
   * NameType#getValue() value} of the {@link NameType} supplied at
   * {@linkplain #NameResolver(Named, NameType) construction time}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link String} naming the {@link Name}
   * whose value will be returned by the {@link #getValue()} method
   *
   * @see #getValue()
   *
   * @see VariableResolver#getName()
   */
  @Override
  public final String getName() {
    assert this.nameType != null;
    return this.nameType.getValue();
  }

  /**
   * Returns a {@link Name} for the supplied {@link NameType}, or
   * {@code null} if no such {@link Name} could be found.
   *
   * @param nameType the {@link NameType} to use as a key; may be
   * {@code null}
   *
   * @return a {@link Name}, or {@code null}
   *
   * @see Named#getName(NameType)
   */
  @Override
  public final Name getName(final NameType nameType) {
    assert this.named != null;
    return this.named.getName(nameType);
  }

  /**
   * Returns the value of {@link Name}{@code .class} when invoked.
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
   *
   * @see VariableResolver#getValue()
   */
  @Override
  public final Object getValue() {
    Object returnValue = null;
    final Name n = this.getName(this.nameType);
    if (n != null) {
      returnValue = n.getValue();
    }
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Does nothing.
   *
   * @param type ignored
   *
   * @see #getType()
   */
  @Override
  @SuppressWarnings("rawtypes")
  public final void setStaticType(final Class type) {

  }


  /**
   * Does nothing when invoked.
   *
   * @param value ignored
   */
  @Override
  public final void setValue(final Object value) {

  }


}
