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

import java.util.Map; // for javadoc only

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.CompileException;

import org.mvel2.integration.VariableResolverFactory; // for javadoc only

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

/**
 * A {@link Serializable} {@link AbstractValued} implementation that
 * is a combination of a {@link NameValue} and the {@link Named} that
 * it is currently assigned to.
 *
 * <p>A {@link Name} may be a name for a party or agent or less
 * tangible concept.  There is no guarantee that a {@link Name} serves
 * as any kind of unique identifier.</p>
 *
 * <p>Two {@link Name}s are considered {@linkplain #equals(Object)
 * equal} if their {@linkplain #getValue() values} are equal.</p>
 *
 * <p>The {@linkplain #getValue() value} of a {@link Name} may change,
 * since the {@linkplain NameValue#getValue() value of its associated
 * <code>NameValue</code>} may change.  Bear this in mind if you are
 * using {@link Name}s as keys in a {@link Map}.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #getValue()
 *
 * @see NameValue
 *
 * @see Named
 *
 * @see Valued
 */
public class Name extends AbstractValued {


  /*
   * Static fields.
   */


  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   */
  private static final long serialVersionUID = 1L;

  /**
   * A {@link Pattern} {@linkplain Pattern#compile(String) compiled}
   * from the following regular expression {@link String}: {@code
   * \s+}.  This field is never {@code null}.
   *
   * @see #getValue()
   *
   * @see #getWhitespaceReplacement()
   */
  public static final Pattern whitespacePattern = Pattern.compile("\\s+");


  /*
   * Instance fields.
   */


  /**
   * The {@link Named} to which this {@link Name} applies.  This field
   * may be {@code null}.
   *
   * @see #getNamed()
   *
   * @see #setNamed(Named)
   */
  private Named named;

  /**
   * The {@link NameValue} housing the actual name value, which may be
   * atomic or a template.  This field may be {@code null}.
   *
   * @see #getNameValue()
   *
   * @see #setNameValue(NameValue)
   */
  private NameValue nameValue;

  /**
   * The {@link String} used to replace whitespace during computation
   * of the result of the {@link #getValue()} method.
   *
   * <p>This field may be {@code null}, in which case no whitespace
   * replacement is performed.</p>
   *
   * @see #getWhitespaceReplacement()
   *
   * @see #setWhitespaceReplacement(String)
   */
  private String whitespaceReplacement;

  /**
   * The {@link NameResolverFactory} used to refer to other {@link
   * NameValue}s in the {@linkplain #getNamed() associated
   * <code>Named</code>} during the interpolation of a template.  This
   * field may be {@code null}.
   *
   * @see #getValue()
   *
   * @see #getNamed()
   */
  private transient NameResolverFactory nameResolverFactory;

  /**
   * The {@link CompiledTemplate} that will be {@linkplain
   * TemplateRuntime#execute(CompiledTemplate, Object,
   * VariableResolverFactory) executed} by the {@link #getValue()}
   * method.
   *
   * @see #installTemplate()
   *
   * @see CompiledTemplate
   *
   * @see #getValue()
   */
  private transient CompiledTemplate compiledTemplate;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Name} <strong>in an initially useless
   * state</strong>.  This no-argument constructor is provided for JPA
   * compatibility and serialization purposes.  Callers must typically
   * subsequently call {@link #setNamed(Named)} and {@link
   * #setNameValue(NameValue)}.
   *
   * @see #Name(Named, NameValue, String)
   *
   * @see #setNamed(Named)
   *
   * @see #setNameValue(NameValue)
   *
   * @see #setWhitespaceReplacement(String)
   */
  protected Name() {
    super();
    this.setWhitespaceReplacement(" ");
  }

  /**
   * Creates a new {@link Name} that is owned by the supplied {@link
   * Named}.
   *
   * <p>This constructor calls the {@link #Name(Named, NameValue,
   * String)} constructor.</p>
   *
   * @param named the {@link Named} named by this {@link Name}; may be
   * {@code null} in which case this {@link Name} is for all intents
   * and purposes just a glorified {@link NameValue}
   *
   * @param nameValue the value that this {@link Name} will have; must
   * not be {@code null}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   */
  public Name(final Named named, final String nameValue) {
    this(named, NameValue.valueOf(nameValue), " ");
  }

  /**
   * Creates a new {@link Name} that is owned by the supplied {@link
   * Named}.
   *
   * <p>This constructor calls the {@link #Name(Named, NameValue,
   * String)} constructor.</p>
   *
   * @param named the {@link Named} named by this {@link Name}; may be
   * {@code null} in which case this {@link Name} is for all intents
   * and purposes just a glorified {@link NameValue}
   *
   * @param nameValue the {@link NameValue} that represents the actual
   * name value; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   */
  public Name(final Named named, final NameValue nameValue) {
    this(named, nameValue, " ");
  }

  /**
   * Creates a new {@link Name}.
   *
   * @param named the {@link Named} named by this {@link Name}; may be
   * {@code null} in which case this {@link Name} is for all intents
   * and purposes just a glorified {@link NameValue}
   *
   * @param nameValue the {@link NameValue} that represents the actual
   * name value; must not be {@code null}
   *
   * @param whitespaceReplacement the {@link String} used as a
   * replacement value for whitespace characters within the
   * {@linkplain #getValue() value} of this {@link Name}; if {@code
   * null}, no whitespace replacement will occur; a {@link String}
   * consisting of a single space character ("&nbsp;") is usually the
   * best value to supply for this parameter for most {@code Name}s
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   *
   * @see #setNamed(Named)
   *
   * @see #setNameValue(NameValue)
   *
   * @see #setWhitespaceReplacement(String)
   */
  public Name(final Named named, final NameValue nameValue, final String whitespaceReplacement) {
    super();
    this.setWhitespaceReplacement(whitespaceReplacement);
    this.setNamed(named);
    this.setNameValue(nameValue);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns the {@link String} that will be used to replace
   * occurrences of whitespace in the {@linkplain #getValue() value}
   * of this {@link Name}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * <p>If this method returns {@code null}, then no whitespace
   * replacement will be performed.</p>
   *
   * @return a {@link String} that will be used to replace
   * occurrences of whitespace in the {@linkplain #getValue() value}
   * of this {@link Name}, or {@code null}
   *
   * @see #setWhitespaceReplacement(String)
   */
  public String getWhitespaceReplacement() {
    return this.whitespaceReplacement;
  }

  /**
   * Sets the {@link String} that will be used to replace occurrences
   * of whitespace in the {@linkplain #getValue() value} of this
   * {@link Name}.  If the supplied {@link String} is {@code null},
   * then no whitespace replacement will occur.
   *
   * @param s the {@link String} that will be used to replace
   * occurrences of whitespace in the {@linkplain #getValue() value}
   * of this {@link Name}; if {@code null} then no whitespace
   * replacement will occur
   *
   * @see #getWhitespaceReplacement()
   *
   * @see #getValue()
   */
  public void setWhitespaceReplacement(final String s) {
    this.whitespaceReplacement = s;
  }

  /**
   * Returns the {@link Named} that <em>owns</em> this {@link Name}.
   *
   * <p>This method may return {@code null}, in which case this {@link
   * Name} is <em>unowned</em>.</p>
   *
   * @return a {@link Named} that serves as this {@link Name}'s owner,
   * or {@code null} if this {@link Name} is unowned
   */
  public Named getNamed() {
    return this.named;
  }

  /**
   * Sets the {@link Named} that will serve as this {@link Name}'s
   * owner.  If the supplied {@link Named} is identical to the {@link
   * Named} returned by the {@link #getNamed()} method, no action is
   * taken.
   *
   * @param named the new owner; may be {@code null}
   */
  public void setNamed(final Named named) {
    final Named old = this.getNamed();
    if (named != old) {
      this.named = named;
      this.compiledTemplate = null;
      this.installTemplate();
    }
  }

  /**
   * Returns the {@link NameValue} fundamentally bound to this {@link
   * Name}.
   *
   * <p>This method may return {@code null} if the {@linkplain #Name()
   * no-argument constructor} has been used to construct this {@link
   * Name} and a subsequent call to {@link #setNameValue(NameValue)}
   * or {@link #setValue(String)} has not yet been issued.</p>
   *
   * @return a {@link NameValue}, or {@code null}
   *
   * @see #setNameValue(NameValue)
   */
  public NameValue getNameValue() {
    return this.nameValue;
  }

  /**
   * Installs the {@link NameValue} that this {@link Name}
   * fundamentally has as its value.
   *
   * @param nameValue the {@link NameValue} to install; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   *
   * @exception IllegalStateException if the supplied {@link
   * NameValue} is {@linkplain NameValue#isAtomic() not atomic} and
   * there was a problem interpolating the template it logically
   * represents; examine the {@link Throwable#getCause() root cause}
   * for details
   *
   * @see #getNameValue()
   *
   * @see NameValue#isAtomic()
   *
   * @see NameValue#setAtomic(boolean)
   */
  public void setNameValue(final NameValue nameValue) {
    if (nameValue == null) {
      throw new IllegalArgumentException("nameValue", new NullPointerException("nameValue"));
    }
    final NameValue old = this.getNameValue();
    if (nameValue != old) {
      this.nameValue = nameValue;
      this.compiledTemplate = null;
      this.installTemplate();
    }
  }

  /**
   * If the {@link #compiledTemplate} field is {@code null} and the
   * {@linkplain #getNameValue() affiliated <code>NameValue</code>} is
   * non-{@code null}, {@linkplain NameValue#isAtomic() is not atomic}
   * and its {@linkplain NameValue#getValue() value} is non-{@code
   * null}, {@linkplain TemplateCompiler#compileTemplate(String)
   * compiles} that value for quick {@linkplain
   * TemplateRuntime#execute(CompiledTemplate, Object,
   * VariableResolverFactory) execution} later by the {@link
   * #getValue()} method.
   *
   * @exception IllegalStateException if a template compilation error
   * occurs
   *
   * @see #getNameValue()
   *
   * @see NameValue#getValue()
   *
   * @see #getValue()
   */
  private final void installTemplate() {
    if (this.compiledTemplate == null) {
      final NameValue nv = this.getNameValue();
      if (nv != null && !nv.isAtomic()) {
        final String template = nv.getValue();
        if (template != null) {
          try {
            this.compiledTemplate = TemplateCompiler.compileTemplate(template);
          } catch (final CompileException wrapMe) {
            throw new IllegalStateException(wrapMe);
          }
          assert this.compiledTemplate != null;
        }          
      }
    }    
  }

  /**
   * Returns the result of evaluating this {@link Name}'s {@linkplain
   * #getNameValue() associated <code>NameValue</code>} against this
   * {@link Name}'s {@linkplain #getNamed() associated
   * <code>Named</code>} as an <a
   * href="http://mvel.codehaus.org/">MVEL template</a>.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link String} with the
   * just-in-time-computed value of this {@link Name}
   *
   * @exception IllegalStateException if there was a problem compiling the
   * template
   */
  public String getValue() {
    final String returnValue;
    final Named named = this.getNamed();
    if (named == null) {
      final NameValue nv = this.getNameValue();
      if (nv == null) {
        returnValue = "";
      } else {
        returnValue = nv.getValue();
      }
    } else {
      assert named != null;
      this.installTemplate();
      if (this.compiledTemplate == null) {
        final NameValue nv = this.getNameValue();
        if (nv == null || nv.isAtomic()) {
          returnValue = "";
        } else {
          returnValue = nv.getValue();
        }
      } else {
        assert this.compiledTemplate != null;
        assert named != null;
        if (this.nameResolverFactory == null) {
          this.nameResolverFactory = new NameResolverFactory(named);
        }
        Object rawValue = null;
        try {
          rawValue = TemplateRuntime.execute(this.compiledTemplate, named, this.nameResolverFactory);
        } catch (final IllegalStateException throwMe) {
          throw throwMe;
        } catch (final RuntimeException wrapMe) {          
          throw new IllegalStateException(wrapMe);
        }
        if (rawValue == null) {
          returnValue = "";
        } else {
          final String whitespaceReplacement = this.getWhitespaceReplacement();
          if (whitespaceReplacement != null) {
            final Matcher m = whitespacePattern.matcher(rawValue.toString());
            assert m != null;
            returnValue = m.replaceAll(whitespaceReplacement);
          } else {
            returnValue = rawValue.toString();
          }
        }
      }
    }
    assert returnValue != null;
    return returnValue;
  }


  /**
   * Calls {@link #setNameValue(NameValue)
   * setNameValue(NameValue.valueOf(value))}.
   *
   * @param value the value to set; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   * 
   * @see #setNameValue(NameValue)
   */
  @Override
  public void setValue(final String value) {
    this.setNameValue(NameValue.valueOf(value));
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Name}.
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Name}
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    final Object value = this.getValue();
    if (value != null) {
      sb.append(value);
    }
    return sb.toString();
  }

}
