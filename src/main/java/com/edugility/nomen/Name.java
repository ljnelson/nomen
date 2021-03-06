/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013-2015 Edugility LLC.
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

import java.beans.PropertyChangeEvent; // for javadoc only
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
 * it is currently assigned to.  A {@link Name} joins a {@link
 * NameValue} with a {@link Named} so that {@link NameValue}s can be
 * shared among several {@link Named}s.
 *
 * <p>A {@link Name} may be a name for a party or agent or less
 * tangible concept.  There is no guarantee that a {@link Name} serves
 * as any kind of unique identifier.</p>
 *
 * <p>In normal usage, a {@link Name} is set up and then not further
 * altered.  However, it can be {@linkplain #setNamed(Named)
 * reassigned} and have its {@linkplain #setNameValue(NameValue) value
 * changed}.  Both its {@code named} and {@code nameValue} properties
 * are bound and thus fire {@link PropertyChangeEvent}s when
 * altered.</p>
 *
 * <p>Two {@link Name}s are considered {@linkplain #equals(Object)
 * equal} if their {@linkplain #getValue() values} are equal.  Note in
 * particular that for {@linkplain #equals(Object) equality purposes}
 * <strong>a {@link Name}'s {@linkplain #getNamed() owner} is deliberately not
 * also considered.</strong></p>
 *
 * <p>The {@linkplain #getValue() value} of a non-{@linkplain
 * NameValue#isAtomic() atomic} {@link Name} may change as its
 * {@linkplain #getNamed() associated owner}'s set of names changes.
 * Bear this in mind if you are using {@link Name}s as keys in a
 * {@link Map} (generally a bad idea).</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #getValue()
 *
 * @see #computeValue()
 *
 * @see NameValue
 *
 * @see Named
 *
 * @see Valued
 *
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
   * @see NameValue#getWhitespaceReplacement()
   */
  private static final Pattern whitespacePattern = Pattern.compile("\\s+");


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
   * The {@link Object} representing a compiled template that will be
   * executed by the {@link #getValue()} method.
   *
   * @see #installTemplate()
   *
   * @see #setNameValue(NameValue)
   *
   * @see #getValue()
   *
   * @see CompiledTemplate
   */
  private transient Object compiledTemplate;

  /**
   * A {@link PropertyChangeSupport} that assists with firing Java
   * Beans-compatible {@link PropertyChangeEvent}s.
   */
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
   * @see #Name(Named, NameValue)
   *
   * @see #setNamed(Named)
   *
   * @see #setNameValue(NameValue)
   */
  protected Name() {
    super();
  }

  /**
   * Creates a new {@link Name} that is owned by the supplied {@link
   * Named}.
   *
   * <p>This constructor calls the {@link #Name(Named, NameValue)}
   * constructor.</p>
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
    super();
    this.setNamed(named);
    this.setNameValue(nameValue);
  }

  /**
   * Creates a new {@link Name} with the supplied {@link NameValue}
   * that is initially {@linkplain #getNamed() unowned}.  The caller
   * is expected to call {@link #setNamed(Named)} with a non-{@code
   * null} {@link Named} to complete initialization.
   *
   * @param nameValue the {@link NameValue} that represents the actual
   * name value; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   *
   * @see #setNameValue(NameValue)
   */
  public Name(final NameValue nameValue) {
    super();
    this.setNameValue(nameValue);
  }

  /**
   * Creates a new {@link Name} that is initially {@linkplain
   * #getNamed() unowned} with a {@link NameValue} constructed by the
   * {@link #createNameValue(String)} method.
   *
   * @param nameValue the value for this new {@link Name}; must not be
   * {@code null}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   *
   * @see #createNameValue(String)
   *
   * @see #setNameValue(NameValue)
   */
  public Name(final String nameValue) {
    super();
    this.setNameValue(this.createNameValue(nameValue));
  }


  /*
   * Instance methods.
   */


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
   * owner.  If the supplied {@link Named} is equal to the {@link
   * Named} returned by the {@link #getNamed()} method, no action is
   * taken.
   *
   * @param named the new owner; may be {@code null}
   *
   * @see #getNamed()
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
      this.firePropertyChange("named", old, this.getNamed());
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
    if (!nameValue.equals(old)) {
      this.nameValue = nameValue;
      this.compiledTemplate = null;
      this.installTemplate();
      this.firePropertyChange("nameValue", old, this.getNameValue());
    }
  }


  /**
   * If the {@link #compiledTemplate} field is {@code null} and the
   * {@linkplain #getNameValue() affiliated <code>NameValue</code>} is
   * non-{@code null}, {@linkplain NameValue#isAtomic() is not atomic}
   * and its {@linkplain NameValue#getValue() value} is non-{@code
   * null}, {@linkplain #compileTemplate(String) compiles} that value
   * for quick {@linkplain #execute(Object) execution} later by the
   * {@link #getValue()} method.
   *
   * <h3>Design Notes</h3>
   *
   * <p>This method is not {@code final} only so this class can be
   * used as a JPA entity.<p>
   *
   * @exception IllegalStateException if a template compilation error
   * occurs
   *
   * @see #compileTemplate(String)
   *
   * @see #execute(Object)
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
          this.compiledTemplate = this.compileTemplate(template);
          assert this.compiledTemplate != null;
          this.firePropertyChange("compiledTemplate", null, this.compiledTemplate);
        }          
      }
    }    
  }

  /**
   * Interprets the supplied {@code template} as the source code of a
   * template and compiles it into an implementation-specific
   * representation that can subsequently be executed efficiently.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param template the source code of the template to be compiled;
   * may be {@code null} in which case {@code null} will be returned
   *
   * @return an {@link Object} representing the compilation of the
   * source code, or {@code null}
   *
   * @exception IllegalStateException if there was a problem compiling
   * the template
   */
  protected Object compileTemplate(final String template) {
    final Object returnValue;
    if (template == null) {
      returnValue = null;
    } else {
      Object temp = null;
      try {
        temp = TemplateCompiler.compileTemplate(template);
      } catch (final CompileException wrapMe) {
        throw new IllegalStateException(wrapMe);
      } finally {
        returnValue = temp;
      }
    }
    return returnValue;
  }

  /**
   * Returns the result of evaluating this {@link Name}'s {@linkplain
   * #getNameValue() associated, possibly non-atomic
   * <code>NameValue</code>} against this {@link Name}'s {@linkplain
   * #getNamed() associated <code>Named</code>}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <h3>Implementation Notes</h3>
   *
   * <p>This method calls the {@link #computeValue()} method and
   * returns its result.</p>
   *
   * @return a non-{@code null} {@link String} with the
   * just-in-time-computed value of this {@link Name}
   *
   * @exception IllegalStateException if there was a problem compiling the
   * template
   *
   * @see #computeValue()
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
   * <p>This method is called by the {@link #computeValue()}
   * method.</p>
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
   * Returns {@code true} if the supplied {@code compiledTemplate} is
   * an executable representation of a template that was previously
   * returned by the {@link #compileTemplate(String)} method.
   *
   * <p>This method is called by the default implementation of {@link
   * #computeValue()}.</p>
   *
   * @param compiledTemplate the template to test; may be {@code null}
   *
   * @return {@code true} if the supplied {@code compiledTemplate} is
   * something that was produced by the {@link
   * #compileTemplate(String)} method and can be executed by the
   * {@link #execute(Object)} method; {@code false} in all other cases
   *
   * @see #compileTemplate(String)
   *
   * @see #execute(Object)
   *
   * @see #computeValue()
   */
  protected boolean canExecute(final Object compiledTemplate) {
    return compiledTemplate instanceof CompiledTemplate;
  }

  /**
   * Executes the supplied {@code compiledTemplate} and returns the
   * result.
   *
   * <p>This method may return {@code null}.</p>
   *
   * <p>If the supplied {@link Object} cannot be executed because it
   * is the wrong sort of {@link Object} or if it is otherwise
   * determined to be an invalid argument, {@code null} will be
   * returned.  Overrides of this method must conform to this
   * requirement.</p>
   *
   * <p>This method is called by the default implementation of {@link
   * #computeValue()}.</p>
   *
   * @param compiledTemplate a template returned by the {@link
   * #compileTemplate(String)} method; may be {@code null}
   *
   * @return the result of executing the supplied {@code
   * compiledTemplate}, or {@code null}
   *
   * @exception IllegalStateException if there was a problem with
   * execution
   *
   * @see #canExecute(Object)
   *
   * @see #compileTemplate(String)
   */
  protected Object execute(final Object compiledTemplate) {
    Object returnValue = null;
    if (compiledTemplate instanceof CompiledTemplate) {
      try {
        returnValue = TemplateRuntime.execute((CompiledTemplate)compiledTemplate, this.getNamed(), this.nameResolverFactory);
      } catch (final IllegalStateException throwMe) {
        throw throwMe;
      } catch (final RuntimeException wrapMe) {          
        throw new IllegalStateException(wrapMe);
      }
    }
    return returnValue;
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
   *
   * @see #canExecute(Object)
   *
   * @see #execute(Object)
   */
  protected String computeValue() {
    String returnValue = "";
    final NameValue nv = this.getNameValue();
    if (nv != null) {
      if (nv.isAtomic()) {
        final Object rawValue = nv.getValue();
        if (rawValue != null) {
          returnValue = this.toString(rawValue);
        }
      } else {
        this.installTemplate();
        if (this.canExecute(this.compiledTemplate)) {
          final String rawStringValue = this.toString(this.execute(this.compiledTemplate));
          if (rawStringValue != null && !rawStringValue.isEmpty()) {
            final String whitespaceReplacement = nv.getWhitespaceReplacement();
            if (whitespaceReplacement == null) {
              returnValue = rawStringValue;
            } else {
              returnValue = whitespacePattern.matcher(rawStringValue).replaceAll(whitespaceReplacement);
            }
          }
        }
      }
    }
    assert returnValue != null;
    return returnValue;
  }

  /**
   * Overrides the {@link AbstractValued#setValue(String)} method to
   * call <code>{@linkplain #setNameValue(NameValue)
   * setNameValue}({@link NameValue#NameValue(String) new
   * NameValue(value)})</code>.
   *
   * @param value the value to set; must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   * 
   * @exception IllegalStateException if the {@link
   * #createNameValue(String)} method, called internally, returns
   * {@code null}
   *
   * @see #setNameValue(NameValue)
   *
   * @see #createNameValue(String)
   */
  @Override
  public void setValue(final String value) {
    boolean doit = false;
    final AbstractValued oldNameValue = this.getNameValue();
    if (oldNameValue == null) {
      doit = true;
    } else {
      final String templateSource = oldNameValue.getValue();
      if (value == null) {
        if (templateSource != null) {
          doit = true;
        }
      } else if (!value.equals(templateSource)) {
        doit = true;
      }
    }
    if (doit) {
      final NameValue nv = this.createNameValue(value);
      if (nv == null) {
        throw new IllegalStateException("createNameValue(\"" + value + "\") == null");
      }
      final String old = this.getValue();
      this.setNameValue(nv);
      this.firePropertyChange("value", old, this.getValue());
    }
  }

  /**
   * Returns a {@link NameValue} suitable for the supplied {@link
   * String} value.
   *
   * <p>This method never returns {@code null} and overrides of it
   * must not return {@code null} either.</p>
   *
   * <p>This implementation returns a new {@link NameValue}.</p>
   *
   * <p>Overrides are permitted to return a new {@link NameValue} or
   * subclass, or to somehow alter an already existing one.</p>
   *
   * @param value the value for the new {@link NameValue}; must not be
   * {@code null}
   *
   * @return a new {@link NameValue} with {@code value} as its
   * {@linkplain AbstractValued#getValue() value}; never {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   */
  protected NameValue createNameValue(final String value) {
    return new NameValue(value);
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Name}.
   *
   * <h3>Implementation Notes</h3>
   *
   * <p>This implementation calls the {@link #toString(Object)} method
   * with the return value of the {@link #getValue()} method.</p>
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Name}
   */
  @Override
  public String toString() {
    String returnValue = this.toString(this.getValue());
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
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

}
