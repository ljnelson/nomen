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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.Serializable;

import java.util.Map; // for javadoc only

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.edugility.nomen.mvel.NameResolverFactory;

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
   * An empty array of {@link PropertyChangeListener}s, for use by the
   * {@link #getPropertyChangeListeners()} method.
   */
  private static final PropertyChangeListener[] EMPTY_PROPERTY_CHANGE_LISTENER_ARRAY = new PropertyChangeListener[0];

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

  private transient PropertyChangeSupport propertyChangeSupport;


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
    final Object old = this.getWhitespaceReplacement();
    this.whitespaceReplacement = s;
    if (this.propertyChangeSupport != null) {
      this.propertyChangeSupport.firePropertyChange("whitespaceReplacement", old, this.getWhitespaceReplacement());
    }
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
    if ((named == null && old != null) || (named != null && !named.equals(old))) {
      this.named = named;
      if (named != null) {
        this.nameResolverFactory = new NameResolverFactory(named);
      } else {
        this.nameResolverFactory = null;
      }
      if (this.propertyChangeSupport != null) {
        this.propertyChangeSupport.firePropertyChange("named", old, this.getNamed());
      }
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
   * {@code null} or {@linkplain NameValue#isInitialized() not
   * initialized}
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
    } else if (!nameValue.isInitialized()) {
      throw new IllegalArgumentException("!nameValue.isInitialized()");
    }
    final NameValue old = this.getNameValue();
    if (!nameValue.equals(old)) {
      this.nameValue = nameValue;
      this.compiledTemplate = null;
      this.installTemplate();
      if (this.propertyChangeSupport != null) {
        this.propertyChangeSupport.firePropertyChange("nameValue", old, this.getNameValue());
      }
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
   * <h4>Design Notes</h4>
   *
   * <p>This method is not {@code final} only so this class can be
   * used as a JPA entity.<p>
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
  private void installTemplate() {
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
          if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.firePropertyChange("compiledTemplate", null, this.compiledTemplate);
          }
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
   * <p>This method calls the {@link #computeValue()} method and
   * returns its result.</p>
   *
   * @return a non-{@code null} {@link String} with the
   * just-in-time-computed value of this {@link Name}
   *
   * @exception IllegalStateException if there was a problem compiling the
   * template
   */
  public String getValue() {
    return this.computeValue();
  }

  /**
   * Converts the supplied {@link Object} to a {@link String}.
   *
   * <p>The default implementation of this method never returns {@code
   * null}, but overrides are permitted to relax this restriction.</p>
   *
   * <p>This implementation returns the {@linkplain String#isEmpty()
   * empty string} if the supplied {@link Object} is {@code null}, and
   * otherwise returns the result of the supplied {@code object}'s
   * {@link Object#toString()} method.</p>
   *
   * @param object the {@link Object} to convert; may be {@code null}
   *
   * @return a {@link String} representing the conversion, or {@code
   * null}
   */
  protected String toString(final Object object) {
    if (object == null) {
      return "";
    } else {
      return object.toString();
    }
  }

  /**
   * Computes and returns the most up-to-date value possible for this
   * {@link Name}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>This method is not declared {@code final} only so that this
   * class may be used as a JPA entity.</p>
   *
   * @return a non-{@code null} {@link String} representing this
   * {@link Name}'s value
   *
   * @exception IllegalStateException if there was a problem compiling
   * a template
   */
  protected String computeValue() {
    final String returnValue;
    final NameValue nv = this.getNameValue();
    if (nv == null) {
      returnValue = "";
    } else if (nv.isAtomic() || this.compiledTemplate == null) {
      final String rawValue = nv.getValue();
      if (rawValue == null) {
        returnValue = "";
      } else {
        returnValue = rawValue;
      }
    } else {
      Object rawValue = null;
      try {
        rawValue = TemplateRuntime.execute(this.compiledTemplate, this.getNamed(), this.nameResolverFactory);
      } catch (final IllegalStateException throwMe) {
        throw throwMe;
      } catch (final RuntimeException wrapMe) {          
        throw new IllegalStateException(wrapMe);
      }
      final String rawStringValue = this.toString(rawValue);
      if (rawStringValue == null || rawStringValue.isEmpty()) {
        returnValue = "";
      } else {
        final String whitespaceReplacement = this.getWhitespaceReplacement();
        if (whitespaceReplacement != null) {
          returnValue = whitespacePattern.matcher(rawStringValue).replaceAll(whitespaceReplacement);
        } else {
          returnValue = rawStringValue;
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

  /*
   * PropertyChangeListener support.
   */

  /**
   * Adds the supplied {@link PropertyChangeListener} to this {@link
   * Name}, listening for changes to the JavaBeans property identified
   * by the value of the {@code name} parameter.
   *
   * <p>If the supplied {@link PropertyChangeListener} has already
   * been added, it <strong>will</strong> be added again.</p>
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#addPropertyChangeListener(String,
   * PropertyChangeListener)}.</p>
   *
   * @param name the name of the property to be listened to; if {@code
   * null} then no action will be taken
   *
   * @param listener the {@link PropertyChangeListener} to add; if
   * {@code null}, then no action will be taken
   *
   * @see #getPropertyChangeListeners(String)
   *
   * @see
   * PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
   */
  public void addPropertyChangeListener(final String name, final PropertyChangeListener listener) {
    if (listener != null) {
      if (this.propertyChangeSupport == null) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
      }
      this.propertyChangeSupport.addPropertyChangeListener(name, listener);
    }
  }

  /**
   * Adds the supplied {@link PropertyChangeListener} to this {@link
   * Name}.  The supplied {@link PropertyChangeListener} will be
   * notified of all property changes fired by this {@link Name}.
   *
   * <p>If the supplied {@link PropertyChangeListener} has already
   * been added, it <strong>will</strong> be added again.</p>
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)}.</p>
   *
   * @param listener the {@link PropertyChangeListener} to add; if
   * {@code null}, then no action will be taken
   *
   * @see #getPropertyChangeListeners()
   *
   * @see
   * PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
   */
  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    if (listener != null) {
      if (this.propertyChangeSupport == null) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
      }
      this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
  }

  /**
   * Removes the supplied {@link PropertyChangeListener} from this
   * {@link Name}, so that it will no longer be listening for changes
   * to the JavaBeans property identified by the value of the {@code
   * name} parameter.
   *
   * <p>If the supplied {@link PropertyChangeListener} has been
   * {@linkplain #addPropertyChangeListener(String,
   * PropertyChangeListener) added} more than once, only one instance
   * will be removed, and it is undefined which instance will be
   * removed.</p>
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#removePropertyChangeListener(String,
   * PropertyChangeListener)}.</p>
   *
   * @param name the name of the property to be listened to; if {@code
   * null} then no action will be taken
   *
   * @param listener the {@link PropertyChangeListener} to add; if
   * {@code null}, then no action will be taken
   *
   * @see #addPropertyChangeListener(String, PropertyChangeListener)
   *
   * @see #getPropertyChangeListeners(String)
   *
   * @see
   * PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
   */
  public void removePropertyChangeListener(final String name, final PropertyChangeListener listener) {
    if (listener != null && this.propertyChangeSupport != null) {
      this.propertyChangeSupport.removePropertyChangeListener(name, listener);
    }
  }

  /**
   * Removes the supplied {@link PropertyChangeListener} from this
   * {@link Name}, so that it will no longer be listening for changes
   * to JavaBeans properties exposed by the {@link Name} class.
   *
   * <p>If the supplied {@link PropertyChangeListener} has been
   * {@linkplain #addPropertyChangeListener(PropertyChangeListener)
   * added} more than once, only one instance will be removed, and it
   * is undefined which instance will be removed.</p>
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)}.</p>
   *
   * @param listener the {@link PropertyChangeListener} to add; if
   * {@code null}, then no action will be taken
   *
   * @see #addPropertyChangeListener(PropertyChangeListener)
   *
   * @see #getPropertyChangeListeners()
   *
   * @see
   * PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener(final PropertyChangeListener listener) {
    if (listener != null && this.propertyChangeSupport != null) {
      this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }

  /**
   * Returns a non-{@code null} array of the {@link
   * PropertyChangeListener}s that have been {@linkplain
   * #addPropertyChangeListener(String, PropertyChangeListener) added}
   * to this {@link Name} and that are listening for changes in the
   * property identified by the supplied {@code name}.
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#getPropertyChangeListeners(String)}.</p>
   *
   * @param name the name of the property in question; may be {@code
   * null} in which case a non-{@code null} empty {@link
   * PropertyChangeListener} array will be returned
   *
   * @return a non-{@code null}, possibly empty, array of {@link
   * PropertyChangeListener}s
   *
   * @see PropertyChangeSupport#getPropertyChangeListeners(String)
   */
  public PropertyChangeListener[] getPropertyChangeListeners(final String name) {
    if (this.propertyChangeSupport != null) {
      return this.propertyChangeSupport.getPropertyChangeListeners(name);
    }
    return EMPTY_PROPERTY_CHANGE_LISTENER_ARRAY;
  }

  /**
   * Returns a non-{@code null} array of the {@link
   * PropertyChangeListener}s that have been {@linkplain
   * #addPropertyChangeListener(PropertyChangeListener) added} to this
   * {@link Name} and that are listening for changes in all properties
   * exposed by the {@link Name} class.
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#getPropertyChangeListeners()}.</p>
   *
   * @return a non-{@code null}, possibly empty, array of {@link
   * PropertyChangeListener}s
   *
   * @see PropertyChangeSupport#getPropertyChangeListeners()
   */
  public PropertyChangeListener[] getPropertyChangeListeners() {
    if (this.propertyChangeSupport != null) {
      return this.propertyChangeSupport.getPropertyChangeListeners();
    }
    return EMPTY_PROPERTY_CHANGE_LISTENER_ARRAY;
  }

  /**
   * If appropriate, notifies registered {@link
   * PropertyChangeListener}s of a possible change in the property
   * named by the supplied {@code propertyName} parameter.
   *
   * <p>This method delegates to {@link
   * PropertyChangeSupport#firePropertyChange(String, Object,
   * Object)}.</p>
   *
   * <p>This method is not declared {@code final} only so that this
   * class may be used as a JPA entity.</p>
   *
   * @param propertyName the name of the property that has changed;
   * may be {@code null}
   *
   * @param old the old value for the property; may be {@code null}
   *
   * @param newValue the newValue for the property; may be {@code
   * null}
   *
   * @see PropertyChangeSupport#firePropertyChange(String, Object,
   * Object)
   */
  protected void firePropertyChange(final String propertyName, final Object old, final Object newValue) {
    if (this.propertyChangeSupport != null) {
      this.propertyChangeSupport.firePropertyChange(propertyName, old, newValue);
    }
  }


  /*
   * Static methods.
   */


  /**
   * A convenience method that creates and installs a new
   * non-{@linkplain NameValue#isAtomic() atomic} {@link Name} into
   * the supplied {@link AbstractNamed}.
   *
   * <h4>Design Notes</h4>
   *
   * <p>This method is not declared {@code final} only so that this
   * class may be used as a JPA entity.  The JPA specification
   * requires that no method on a potential entity may be {@code
   * final}.</p>
   *
   * <h4>Implementation Notes</h4>
   *
   * <p>This method calls the {@link #name(AbstractNamed,
   * String, String, boolean, String)} method passing {@code false}
   * and a space character (" ") as the value of the penultimate and
   * final parameters, and returns its return value.</p>
   *
   * @param named the {@link AbstractNamed} that will own the new
   * {@link Name}; must not be {@code null}
   * 
   * @param nameTypeValue the identifying information for the relevant
   * {@link NameType} under which a new {@link Name} will be indexed; must not be {@code null}
   *
   * @param nameValue the identifying information for the relevant
   * {@link NameValue}; must not be {@code null}
   *
   * @return a non-{@code null} {@link Name} {@linkplain #getNamed()
   * owned} by the supplied {@link AbstractNamed} under an appropriate
   * {@link NameType}
   *
   * @exception IllegalArgumentException if either {@code named},
   * {@code nameTypeValue} or {@code nameValue} is {@code null}.
   */
  public static Name name(final AbstractNamed named, final String nameTypeValue, final String nameValue) {
    return name(named, nameTypeValue, nameValue, false, " ");
  }


  /**
   * A convenience method that creates and installs a new
   * non-{@linkplain NameValue#isAtomic() atomic} {@link Name} into
   * the supplied {@link AbstractNamed}.
   *
   * <h4>Design Notes</h4>
   *
   * <p>This method is not declared {@code final} only so that this
   * class may be used as a JPA entity.  The JPA specification
   * requires that no method on a potential entity may be {@code
   * final}.</p>
   *
   * <h4>Implementation Notes</h4>
   *
   * <p>This method calls the {@link #name(AbstractNamed,
   * String, String, boolean, String)} method passing a space
   * character (" ") as the value of the final parameter, and returns
   * its return value.</p>
   *
   * @param named the {@link AbstractNamed} that will own the new
   * {@link Name}; must not be {@code null}
   * 
   * @param nameTypeValue the identifying information for the relevant
   * {@link NameType} under which a new {@link Name} will be indexed; must not be {@code null}
   *
   * @param nameValue the identifying information for the relevant
   * {@link NameValue}; must not be {@code null}
   *
   * @param atomic whether the {@code nameValue} parameter is
   * notionally {@linkplain NameValue#isAtomic() atomic}
   *
   * @return a non-{@code null} {@link Name} {@linkplain #getNamed()
   * owned} by the supplied {@link AbstractNamed} under an appropriate
   * {@link NameType}
   *
   * @exception IllegalArgumentException if either {@code named},
   * {@code nameTypeValue} or {@code nameValue} is {@code null}.
   */
  public static Name name(final AbstractNamed named, final String nameTypeValue, final String nameValue, final boolean atomic) {
    return name(named, nameTypeValue, nameValue, atomic, " ");
  }


  /**
   * A convenience method that creates and installs a new
   * non-{@linkplain NameValue#isAtomic() atomic} {@link Name} into
   * the supplied {@link AbstractNamed}.
   *
   * <h4>Design Notes</h4>
   *
   * <p>This method is not declared {@code final} only so that this
   * class may be used as a JPA entity.  The JPA specification
   * requires that no method on a potential entity may be {@code
   * final}.</p>
   *
   * @param named the {@link AbstractNamed} that will own the new
   * {@link Name}; must not be {@code null}
   * 
   * @param nameTypeValue the identifying information for the relevant
   * {@link NameType} under which a new {@link Name} will be indexed; must not be {@code null}
   *
   * @param nameValue the identifying information for the relevant
   * {@link NameValue}; must not be {@code null}
   *
   * @param atomic whether the {@code nameValue} parameter is
   * notionally {@linkplain NameValue#isAtomic() atomic}
   *
   * @param whitespaceReplacement the {@link String} to use in
   * replacing {@linkplain Character#isWhitespace(char) whitespace
   * characters}; may be {@code null} in which case no whitespace
   * replacement will occur.  A good reasonable default is a single
   * space (" ").
   *
   * @return a non-{@code null} {@link Name} {@linkplain #getNamed()
   * owned} by the supplied {@link AbstractNamed} under an appropriate
   * {@link NameType}
   *
   * @exception IllegalArgumentException if either {@code named},
   * {@code nameTypeValue} or {@code nameValue} is {@code null}.
   */
  public static Name name(final AbstractNamed named, final String nameTypeValue, final String nameValue, final boolean atomic, final String whitespaceReplacement) {
    if (named == null) {
      throw new IllegalArgumentException("named", new NullPointerException("named"));
    }
    final NameType nameType = NameType.valueOf(nameTypeValue);
    assert nameType != null;    
    named.putName(nameType, NameValue.valueOf(nameValue, atomic));
    return named.getName(nameType);
  }

}
