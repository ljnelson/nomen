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
 * An {@link AbstractValued} implementation that represents the value
 * of a name of something.
 *
 * <p>In the nomenclature of the Nomen project, a {@linkplain Name
 * <em>name</em>} is a {@linkplain NameValue <em>name value</em>} that
 * is {@linkplain Name#getNamed() <em>owned</em>} by {@linkplain Named
 * something that is <em>named</em>}.  Each concept has a
 * corresponding class.</p>
 *
 * <p>A {@link NameValue} may be <em>atomic</em>&mdash;basically a
 * glorified {@link String}, indivisible, not subject to any further
 * interpretation&mdash;or not atomic&mdash;in which case it is
 * interpreted as an <a href="http://mvel.codehaus.org/">MVEL</a> <a
 * href="http://mvel.codehaus.org/MVEL+2.0+Templating+Guide">template</a>
 * that will be {@linkplain Name#computeValue() interpolated} once the
 * given {@link NameValue} is {@linkplain Name#getNameValue() owned}
 * by a {@link Name}.  {@link NameValue}s are <em>not</em> atomic by
 * default.</p>
 *
 * <p>A {@link NameValue} may have an {@linkplain
 * #getWhitespaceReplacement() associated whitespace replacement
 * <code>String</code>} that will be used to replace any sequence of
 * one or more consecutive whitespace characters that occur after any
 * {@linkplain Name#computeValue() interpolation} of a {@link
 * NameValue} within the context of a given {@linkplain
 * Name#getNamed() owner} when that {@link NameValue} is not
 * {@linkplain #isAtomic() atomic}.</p>
 *
 * <p>A {@link NameValue}, once initialized with a {@linkplain
 * #setValue(String) value} and an {@linkplain #setAtomic(boolean)
 * atomicity}&mdash;whether via {@linkplain #NameValue(String,
 * boolean, String) the constructor} or the {@link #setValue(String)}
 * and {@link #setAtomic(boolean)} and {@link
 * #setWhitespaceReplacement(String)} methods&mdash;can be treated as
 * though it is immutable.  Subsequent attempts to call either the
 * {@link #setValue(String)} or {@link #setAtomic(boolean)} or {@link
 * #setWhitespaceReplacement(String)} methods will fail with {@link
 * IllegalStateException}s.</p>
 *
 * <p>Two {@link NameValue}s are {@linkplain #equals(Object) equal} if
 * their {@linkplain AbstractValued#getValue() values} are {@linkplain
 * String#equals(Object) equal} and if they are both {@linkplain
 * #isAtomic() atomic} and if their {@linkplain
 * #getWhitespaceReplacement() whitespace replacement
 * <code>String</code>}s are {@linkplain String#equals(Object)
 * equal}.</p>
 *
 * <h3>Design Notes</h3>
 *
 * <p>This class is designed to be treated as immutable once fully
 * {@linkplain #isInitialized() initialized}.  Consequently, certain
 * methods, like {@link #setAtomic(boolean)} and {@link
 * #setValue(String)} and {@link #setWhitespaceReplacement(String)},
 * may only be called once with a given parameter value.</p>
 *
 * <p>Methods and fields that might otherwise be {@code final} are
 * explicitly left non-{@code final} so that this class may be used as
 * a JPA entity.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Name
 *
 * @see AbstractValued
 *
 * @see Named
 *
 * @see #setValue(String)
 *
 * @see #setAtomic(boolean)
 *
 * @see #setWhitespaceReplacement(String)
 *
 * @see #isInitialized()
 */
public class NameValue extends AbstractValued {


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
   * Whether this {@link NameValue} is considered to be
   * <em>atomic</em>&mdash;i.e. not a <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Templating+Guide">template</a>
   * in need of further interpolation, but just a simple textual
   * value.
   *
   * <p>This field may be {@code null}.</p>
   *
   * <h3>Design Notes</h3>
   *
   * <p>This field is a {@link Boolean} and not a {@code boolean} so
   * that its initial setting via the {@link #setAtomic(boolean)}
   * method can be tracked.  It is not {@code final} only so that this
   * class may be used as a JPA entity.</p>
   *
   * @see #isAtomic()
   *
   * @see #setAtomic(boolean)
   */
  private Boolean atomic;

  /**
   * A {@link String} that will replace one or more consecutive
   * occurrences of whitespace characters in a non-{@linkplain
   * #isAtomic() atomic} {@link NameValue}.
   *
   * <p>This field may be {@code null} in which case no whitespace
   * replacement will occur.</p>
   *
   * @see #getWhitespaceReplacement()
   *
   * @see #setWhitespaceReplacement(String)
   */
  private String whitespaceReplacement;

  /**
   * Indicates whether the {@link #whitespaceReplacement} field has
   * been set via the {@link #setWhitespaceReplacement(String)} method
   * or not.
   *
   * @see #setWhitespaceReplacement(String)
   */
  private boolean whitespaceReplacementSet;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NameValue} <b>in an {@linkplain
   * #isInitialized() incomplete state}</b>; to fully initialize the
   * new {@link NameValue} callers must invoke the {@link
   * #setValue(String)} method, the {@link #setAtomic(boolean)} method
   * and the {@link #setWhitespaceReplacement(String)} method, or must
   * use a {@linkplain #NameValue(String, boolean, String) more
   * suitable constructor} instead.
   *
   * <h3>Design Notes</h3>
   *
   * <p>This constructor exists primarily for JavaBeans, serialization
   * and JPA compatibility.  Please consider using the {@link
   * #NameValue(String, boolean, String)} constructor instead which
   * fully initializes a {@link NameValue} instance as it creates
   * it.</p>
   *
   * @see #NameValue(String, boolean, String)
   *
   * @see #isInitialized()
   */
  protected NameValue() {
    super();
  }

  /**
   * Creates a new {@link NameValue} that is not {@linkplain
   * #isAtomic() atomic}.
   *
   * <p>This constructor calls the {@link #NameValue(String, boolean,
   * String)} constructor with {@code false} as the value for its
   * second parameter and a single space ("&nbsp;") as the value of
   * its third parameter.</p>
   *
   * @param value the {@linkplain #setValue(String) value} for this
   * {@link NameValue}; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #NameValue(String, boolean, String)
   */
  public NameValue(final String value) {
    this(value, false /* not atomic */, " ");
  }

  /**
   * Creates a new {@link NameValue}.
   *
   * <p>This constructor calls the {@link #NameValue(String, boolean,
   * String)} constructor with the supplied {@code atomic} parameter
   * value as the value for its second parameter and either {@code
   * null} or a single space ("&nbsp;") as the value of its third
   * parameter, depending on whether {@code atomic} is {@code true} or
   * not.</p>
   *
   * @param value the {@linkplain #setValue(String) value} for this
   * {@link NameValue}; must not be {@code null}
   *
   * @param atomic whether or not this new {@link NameValue} will be
   * {@linkplain #isAtomic() atomic}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #NameValue(String, boolean, String)
   */
  public NameValue(final String value, final boolean atomic) {
    this(value, atomic, atomic ? null : " ");
  }

  /**
   * Creates a new {@link NameValue}.
   *
   * <p>This constructor calls the {@link #NameValue(String, boolean,
   * String)} constructor with {@code false} as the value for its
   * second parameter and the supplied {@code whitespaceReplacement}
   * as the value for its third parameter.</p>
   *
   * @param value the {@linkplain #setValue(String) value} for this
   * {@link NameValue}; must not be {@code null}
   *
   * @param whitespaceReplacement the {@link String} to be used for
   * whitespace replacement; may be {@code null} in which case no
   * whitespace replacement will be performed.  Normally, a single
   * space ("&nbsp;") is a good value for this parameter.
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #NameValue(String, boolean, String)
   */
  public NameValue(final String value, final String whitespaceReplacement) {
    this(value, false /* not atomic */, whitespaceReplacement);
  }

  /**
   * Creates a new {@link NameValue}.
   *
   * @param value the {@linkplain #setValue(String) value} for this
   * {@link NameValue}; must not be {@code null}
   *
   * @param atomic the {@linkplain #setAtomic(boolean) atomicity} for
   * this {@link NameValue}
   *
   * @param whitespaceReplacement the {@link String} to be used for
   * whitespace replacement; may be {@code null} in which case no
   * whitespace replacement will be performed.  Normally, a single
   * space ("&nbsp;") is a good value for this parameter.  <strong>If
   * the {@code atomic} parameter value is {@code true}, then no other
   * value besides {@code null} may be used as the value of the {@code
   * whitespaceReplacement} parameter.</strong>
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}, or if {@code atomic} is {@code true} and {@code
   * whitespaceReplacement} is any value other than {@code null}
   *
   * @see AbstractValued#AbstractValued(String)
   *
   * @see #setValue(String)
   *
   * @see #setAtomic(boolean)
   *
   * @see #setWhitespaceReplacement(String)
   */
  public NameValue(final String value, final boolean atomic, final String whitespaceReplacement) {
    super(value);
    this.setAtomic(atomic);
    this.setWhitespaceReplacement(whitespaceReplacement);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns the {@link String} that will be used instead of
   * consecutive occurrences of one or more whitespace characters when
   * this {@link NameValue}'s {@linkplain #getValue() value} is
   * interpolated by a {@link Name}.
   *
   * <p>This method may return {@code null}, in which case no
   * whitespace replacement will occur.</p>
   *
   * <p>This method always returns {@code null} if the value of the
   * {@link #isAtomic()} method is {@code true}.  Since this method
   * calls the {@link #isAtomic()} method, take care not to override
   * that method to call this one, or an infinite loop will
   * result.</p>
   *
   * @return a whitespace replacement {@link String}, or {@code null}
   *
   * @see #setWhitespaceReplacement(String)
   *
   * @see #isAtomic()
   */
  public String getWhitespaceReplacement() {
    return this.isAtomic() ? null : this.whitespaceReplacement;
  }

  /**
   * Sets the {@link String} that will be used instead of consecutive
   * occurences of one or more whitespace characters when this {@link
   * NameValue}'s {@linkplain #getValue() value} is interpolated by a
   * {@link Name}.
   *
   * <p>If this {@link NameValue} {@linkplain #isAtomic() is atomic},
   * then the only permissible value for the {@code
   * whitespaceReplacement} parameter is {@code null}.</p>
   *
   * @param whitespaceReplacement the replacement {@link String}; if
   * {@code null} then no whitespace replacement will occur
   *
   * @exception IllegalArgumentException if this {@link NameValue}
   * {@linkplain #isAtomic() is atomic} and {@code
   * whitespaceReplacement} is not {@code null}
   *
   * @exception IllegalStateException if this method is called more
   * than once with different values for the {@code
   * whitespaceReplacement} parameter
   *
   * @see Name#getValue()
   */
  public void setWhitespaceReplacement(final String whitespaceReplacement) {
    if (this.whitespaceReplacementSet) {
      if (whitespaceReplacement == null) {
        if (this.getWhitespaceReplacement() != null) {
          throw new IllegalStateException("setWhitespaceReplacement() cannot be called more than once with different parameter values");
        }
      } else if (!whitespaceReplacement.equals(this.getWhitespaceReplacement())) {
        throw new IllegalStateException("setWhitespaceReplacement() cannot be called more than once with different parameter values");
      }
    } else if (this.isAtomic() && whitespaceReplacement != null) {
      throw new IllegalArgumentException("whitespaceReplacement", new IllegalStateException("isAtomic()"));
    } else {
      this.whitespaceReplacement = whitespaceReplacement;
      this.whitespaceReplacementSet = true;
    }
  }
  
  /**
   * Returns {@code true} if this {@link NameValue} is fully
   * initialized and hence immutable.
   *
   * <p>A {@link NameValue} is fully initialized if each of its {@link
   * #setAtomic(boolean)}, {@link #setValue(String)} and {@link
   * #setWhitespaceReplacement(String)} methods has been called,
   * either directly or by its constructors.</p>
   *
   * @return {@code true} if this {@link NameValue} is fully
   * initialized; {@code false} in all other cases
   *
   * @see #setValue(String)
   *
   * @see #setAtomic(boolean)
   */
  public boolean isInitialized() {
    return this.atomic != null && this.getValue() != null && this.whitespaceReplacementSet;
  }

  /**
   * Returns {@code true} if this {@link NameValue} is <em>atomic</em>
   * &mdash;if it is a simple textual value and therefore not a <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Templating+Guide">template</a>
   * that must be interpolated.
   *
   * <p>This method is called by the {@link
   * #getWhitespaceReplacement()} method, so do not call the {@link
   * #getWhitespaceReplacement()} method from an override of this
   * method, or an infinite loop will result.</p>
   *
   * @return {@code true} if this {@link NameValue} is an indivisible
   * textual value; {@code false} otherwise
   *
   * @see #getWhitespaceReplacement()
   */
  public boolean isAtomic() {
    return this.atomic != null && this.atomic.booleanValue();
  }

  /**
   * Sets whether this {@link NameValue} is <em>atomic</em> or
   * not&mdash;whether it is a simple textual value or is a <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Templating+Guide">template</a>
   * that must be interpolated.
   *
   * <p>This method may only be called once to set the initial value
   * for this property.  If it is called again, an {@link
   * IllegalStateException} will be thrown.</p>
   *
   * @param atomic whether this {@link NameValue} is <em>atomic</em>
   * or not&mdash;whether it is a simple textual value or is a <a
   * href="http://mvel.codehaus.org/MVEL+2.0+Templating+Guide">template</a>
   * that must be interpolated
   *
   * @exception IllegalStateException if this method is called more
   * than once
   */
  public void setAtomic(final boolean atomic) {
    if (this.atomic == null) {
      this.atomic = Boolean.valueOf(atomic);
    } else if (this.atomic.booleanValue() != atomic) {
      throw new IllegalStateException("setAtomic() cannot be called more than once with different parameter values");
    }
  }

  /**
   * Returns a hashcode for this {@link NameValue} based off the
   * return value of this {@link NameValue}'s {@link #isAtomic()}
   * method, the return value of this {@link NameValue}'s {@link
   * #getWhitespaceReplacement()} method and its {@linkplain
   * AbstractValued#hashCode() superclass' hashcode}.
   *
   * @return a hashcode for this {@link NameValue}
   *
   * @see #equals(Object)
   */
  @Override
  public int hashCode() {
    int result = 17;
    
    int c = Boolean.valueOf(this.isAtomic()).hashCode();
    result = result * 37 + c;
    
    final String whitespaceReplacement = this.getWhitespaceReplacement();
    if (whitespaceReplacement == null) {
      c = 0;
    } else {
      c = whitespaceReplacement.hashCode();
    }
    result = result * 37 + c;

    c = super.hashCode();
    result = result * 37 + c;
    
    return result;
  }

  /**
   * Returns {@code true} if the supplied {@link Object} is an
   * instance of {@link NameValue} and returns the same value from its
   * {@link #isAtomic()} method as is returned from this {@link
   * NameValue}'s {@link #isAtomic()} method and returns a value from
   * its {@link #getWhitespaceReplacement()} method that is
   * {@linkplain String#equals(Object) equal to} the value returned by
   * this {@link NameValue}'s {@link #getWhitespaceReplacement()}
   * method and returns a value from its {@link
   * AbstractValued#getValue()} method that is {@linkplain
   * String#equals(Object) equal to} the value returned by this {@link
   * NameValue}'s {@link AbstractValued#getValue()} method.
   *
   * @param other the {@link Object} to compare this {@link NameValue}
   * against for equality; may be {@code null}
   *
   * @return {@code true} if the supplied {@link Object} is equal to
   * this {@link NameValue}; {@code false} otherwise
   *
   * @see #hashCode()
   *
   * @see AbstractValued#equals(Object)
   */
  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other instanceof NameValue) {
      final NameValue him = (NameValue)other;

      if (this.isAtomic()) {
        if (!him.isAtomic()) {
          return false;
        }
      }

      final String whitespaceReplacement = this.getWhitespaceReplacement();
      if (whitespaceReplacement == null) {
        if (him.getWhitespaceReplacement() != null) {
          return false;
        }
      } else if (!whitespaceReplacement.equals(him.getWhitespaceReplacement())) {
        return false;
      }
      
      return super.equals(other);
    } else {
      return false;
    }
  }

}
