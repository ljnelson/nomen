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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.integration.VariableResolverFactory; // for javadoc only

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

/**
 * A {@link Serializable} combination of a {@link NameValue} and the
 * {@link Named} that it is currently assigned to.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public class Name implements Serializable {


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
   * \\s+}.  This field is never {@code null}.
   *
   * @see #getValue()
   *
   * @see #getCollapseWhitespace()
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
   * Whether to collapse {@linkplain #whitespacePattern multiple
   * consecutive occurrences of whitespace} into a single space during
   * the invocation of the {@link #getValue()} method.
   *
   * @see #getValue()
   *
   * @see #getCollapseWhitespace()
   *
   * @see #setCollapseWhitespace(boolean)
   */
  private boolean collapseWhitespace;

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
   * Creates a new {@link Name} in an initially useless state.  This
   * no-argument constructor is provided for JPA compatibility and
   * serialization purposes.
   *
   * @see #Name(Named, NameValue, boolean)
   *
   * @see #setNamed(Named)
   *
   * @see #setNameValue(NameValue)
   *
   * @see #setCollapseWhitespace(boolean)
   */
  public Name() {
    super();
  }

  public Name(final Named named, final String nameValue) {
    this(named, NameValue.valueOf(nameValue), true);
  }

  public Name(final Named named, final NameValue nameValue) {
    this(named, nameValue, true);
  }

  /**
   * Creates a new {@link Name}.
   *
   * @param named the {@link Named} named by this {@link Name}; must
   * not be {@code null}
   *
   * @param nameValue the {@link NameValue} that represents the actual
   * name value; must not be {@code null}
   *
   * @param collapseWhitespace whether&mdash;during template
   * execution&mdash;to collapse sequences of whitespace into a single
   * space character
   *
   * @see #setNamed(Named)
   *
   * @see #setNameValue(NameValue)
   *
   * @see #setCollapseWhitespace(boolean)
   */
  public Name(final Named named, final NameValue nameValue, final boolean collapseWhitespace) {
    this();
    this.setCollapseWhitespace(collapseWhitespace);
    this.setNamed(named);
    this.setNameValue(nameValue);
  }


  /*
   * Instance methods.
   */


  public boolean getCollapseWhitespace() {
    return this.collapseWhitespace;
  }

  public void setCollapseWhitespace(final boolean collapseWhitespace) {
    this.collapseWhitespace = collapseWhitespace;
  }

  public Named getNamed() {
    return this.named;
  }

  public void setNamed(final Named named) {
    this.named = named;
    this.compiledTemplate = null;
    this.installTemplate();
  }

  public NameValue getNameValue() {
    return this.nameValue;
  }

  public void setNameValue(final NameValue nameValue) {
    if (nameValue == null) {
      throw new IllegalArgumentException("nameValue", new NullPointerException("nameValue"));
    }
    this.nameValue = nameValue;
    this.compiledTemplate = null;
    this.installTemplate();
  }

  /**
   * If the {@link #compiledTemplate} field is {@code null} and the
   * {@linkplain #getNameValue() affiliated <code>NameValue</code>} is
   * non-{@code null} and its {@linkplain NameValue#getValue() value}
   * is non-{@code null}, {@linkplain
   * TemplateCompiler#compileTemplate(String) compiles} that value for
   * quick {@linkplain TemplateRuntime#execute(CompiledTemplate,
   * Object, VariableResolverFactory) execution} later by the {@link
   * #getValue()} method.
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
          this.compiledTemplate = TemplateCompiler.compileTemplate(template);
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
   * @exception IllegalStateException if there was a problem
   * interpolating the template
   */
  public String getValue() {
    final String returnValue;
    this.installTemplate();
    final Named named = this.getNamed();
    if (this.compiledTemplate == null || named == null) {
      final NameValue nv = this.getNameValue();
      if (nv != null && (named == null || nv.isAtomic())) {
        returnValue = nv.getValue();
      } else {
        returnValue = "";
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
      } else if (this.getCollapseWhitespace()) {
        final Matcher m = whitespacePattern.matcher(rawValue.toString());
        assert m != null;
        returnValue = m.replaceAll(" ");
      } else {
        returnValue = rawValue.toString();
      }
    }
    assert returnValue != null;
    return returnValue;
  }

  @Override
  public int hashCode() {
    int result = 17;

    int c = 0;
    final Object value = this.getValue();
    if (value != null) {
      c = value.hashCode();
    }
    result = 37 * result + c;

    return result;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && this.getClass().equals(other.getClass())) {
      final Name him = (Name)other;

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
    final StringBuilder sb = new StringBuilder();
    final Object value = this.getValue();
    if (value != null) {
      sb.append(value);
    }
    return sb.toString();
  }

}
