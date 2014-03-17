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

import java.io.Serializable;

/**
 * A {@link Serializable} {@link Valued} implementation that correctly
 * implements the {@link #equals(Object)} and {@link #hashCode()}
 * methods.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #getValue()
 */
public abstract class AbstractValued implements Serializable, Valued {


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
   * The value of this {@link AbstractValued}.
   *
   * <p>This field may be {@code null}.</p>
   *
   * @see #getValue()
   *
   * @see #setValue(String)
   */
  private String value;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link AbstractValued} with no {@linkplain
   * #getValue() value}.  The caller is expected to invoke the {@link
   * #setValue(String)} method to complete initialization.
   *
   * @see #setValue(String)
   */
  protected AbstractValued() {
    super();
  }

  /**
   * Creates a new {@link AbstractValued} with the supplied {@code
   * value}.
   *
   * @param value the new value; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #setValue(String)
   */
  protected AbstractValued(final String value) {
    this();
    this.setValue(value);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns the value of this {@link AbstractValued}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @return the value of this {@link AbstractValued}, or {@code
   * null}
   *
   * @see #setValue(String)
   *
   * @see #NameType(String)
   */
  @Override
  public String getValue() {
    return this.value;
  }

  /**
   * Sets the value of this {@link AbstractValued}.
   *
   * <p>The default implementation of this method may only be called
   * once, whether by {@linkplain #NameType(String) an appropriate
   * constructor} or afterwards.  Subsequent calls will throw an
   * {@link IllegalStateException}.  Subclasses are free to override
   * this restriction.</p>
   * 
   * @param value the new value; must not be {@code null}
   *
   * @exception IllegalStateException if this method is called more
   * than once
   *
   * @see #NameType(String)
   */
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

  /**
   * Returns a hashcode for this {@link AbstractValued} based off the
   * hashcode of the return value of its {@link #getValue()} method.
   *
   * @return a hashcode for this {@link AbstractValued} implementation
   */
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

  /**
   * Returns {@code true} if the supplied {@link Object} is an
   * instance of {@code Valued} and returns a value from its {@link
   * #getValue()} method that is {@linkplain String#equals(Object)
   * equal to} the return value of this {@link AbstractValued}'s
   * {@link #getValue()} method.
   *
   * @return {@code true} if the supplied {@link Object} is equal to
   * this {@link AbstractValued}; {@code false} in all other cases
   *
   * @see #getValue()
   */
  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other instanceof Valued) {
      final Valued him = (Valued)other;
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

  /**
   * Returns the return value of invoking {@link
   * String#valueOf(Object)} on the return value of the {@link
   * #getValue()} method.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return the return value of invoking {@link
   * String#valueOf(Object)} on the return value of the {@link
   * #getValue()} method; never {@code null}
   *
   * @see String#valueOf(Object)
   */
  @Override
  public String toString() {
    return String.valueOf(this.getValue());
  }

}
