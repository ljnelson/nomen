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
package com.edugility.nomen.mvel;

import java.io.Serializable; // for javadoc only

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edugility.nomen.Name;
import com.edugility.nomen.Named;
import com.edugility.nomen.NameType;
import com.edugility.nomen.NameValue; // for javadoc only

import org.mvel2.UnresolveablePropertyException;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;

import org.mvel2.integration.impl.BaseVariableResolverFactory;

/**
 * A {@link BaseVariableResolverFactory} that produces {@link
 * NameResolver} instances for use by <a
 * href="http://mvel.codehaus.org/">MVEL</a> expressions.
 *
 * <p>Subclasses of this class normally have to override only the
 * {@link #getName(NameType)} method.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see NameResolver
 *
 * @see <a href="http://mvel.codehaus.org/">MVEL</a>
 *
 * @see BaseVariableResolverFactory
 *
 * @see VariableResolverFactory
 *
 * @see VariableResolver
 *
 * @see #getName(NameType)
 *
 * @see Name#getValue()
 *
 * @see NameValue#setAtomic(boolean)
 */
public class NameResolverFactory extends BaseVariableResolverFactory implements Named {

  
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
   * The {@link Named} that serves as the virtual universe or domain
   * of {@link Name}s that are potentially {@linkplain
   * #isResolveable(String) resolvable} into {@link NameResolver}s
   * vended by this {@link NameResolverFactory}.
   *
   * <p>This field is never {@code null}.</p>
   */
  protected final Named named;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NameResolverFactory} given a non-{@code
   * null} {@link Named} that serves as the universe or domain from
   * which {@link Name}s may ultimately be resolved.
   *
   * @param named the {@link Named} that serves as the universe or
   * domain from which {@link Name}s may ultimately be resolved; must
   * not be {@code null}
   *
   * @exception IllegalArgumentException if {@code named} is {@code
   * null}
   */
  public NameResolverFactory(final Named named) {
    super();
    if (named == null) {
      throw new IllegalStateException("named", new NullPointerException("named"));
    }
    this.named = named;
  }


  /*
   * Instance methods.
   */


  /**
   * Given a {@link NameType}, returns a {@link Name} corresponding to
   * it in some fashion.
   *
   * <p>This method may return {@code null}.</p>
   *
   * <p>The default implementation of this method invokes the {@link
   * Named#getName(NameType)} method on the value of the {@link
   * #named} field.</p>
   *
   * @param nameType the {@link NameType} for which a corresponding
   * {@link Name} should be returned; may be {@code null}
   *
   * @return a {@link Name} corresponding to the supplied {@link
   * NameType}, or {@code null}
   *
   * @see #named
   *
   * @see Named#getName(NameType)
   */
  @Override
  public Name getName(final NameType nameType) {
    final Name returnValue;
    if (nameType != null && this.named != null) {
      returnValue = this.named.getName(nameType);
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  /**
   * Returns {@code true} if {@code name} is equal to the {@linkplain
   * NameType#getValue() value} of a {@link NameType} that, when
   * passed to {@link Named#getName(NameType)}, results in a
   * non-{@code null} {@link Name}, or if subsequent {@link
   * VariableResolver}s are able to make sense of the supplied {@code
   * name} parameter.
   *
   * @param name the {@linkplain NameType#getValue() value} of a
   * {@link NameType}; may be {@code null}
   *
   * @return {@code true} if this {@link NameResolverFactory} or any
   * of its chained {@link VariableResolverFactory} instances can
   * resolve an MVEL variable of the given name; {@code false}
   * otherwise
   *
   * @see VariableResolverFactory
   *
   * @see BaseVariableResolverFactory#isNextResolveable(String)
   *
   * @see VariableResolver
   *
   * @see NameResolver
   */
  @Override
  public boolean isResolveable(final String name) {
    final boolean returnValue;
    if (name == null) {
      returnValue = this.isNextResolveable(null);
    } else if (this.isTarget(name)) {
      returnValue = true;
    } else {
      final Name n = this.getName(new NameType(name));
      if (n == null) {
        returnValue = this.isNextResolveable(name);
      } else {
        Map<String, VariableResolver> resolvers = this.getVariableResolvers();
        if (resolvers == null) {
          resolvers = new HashMap<String, VariableResolver>();
        }
        resolvers.put(name, this.createNameResolver(this.named, name));
        if (resolvers != this.getVariableResolvers()) {
          this.setVariableResolvers(resolvers);
        }
        returnValue = true;
      }
    }
    return returnValue;
  }

  /**
   * Overrides the {@link
   * BaseVariableResolverFactory#getVariableResolver(String)} method
   * for performance and robustness only.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param name the name of the variable whose {@link
   * VariableResolver} should be returned, if possible; may be {@code
   * null}
   *
   * @return a {@link VariableResolver} suitable for resolving a
   * variable with the supplied {@code name}; never {@code null}
   *
   * @exception UnresolveablePropertyException if no {@link
   * VariableResolver} could be found
   *
   * @see BaseVariableResolverFactory#getVariableResolver(String)
   */
  @Override
  public final VariableResolver getVariableResolver(final String name) {
    VariableResolver returnValue = null;
    if (this.isResolveable(name)) {
      final Map<String, VariableResolver> resolvers = this.getVariableResolvers();
      if (resolvers != null && !resolvers.isEmpty()) {
        returnValue = resolvers.get(name);
        if (returnValue == null && this.isNextResolveable(name)) {
          final VariableResolverFactory nextFactory = this.getNextFactory();
          if (nextFactory != null) {
            returnValue = nextFactory.getVariableResolver(name);
          }
        }
      }
    }
    if (returnValue == null) {
      throw new UnresolveablePropertyException("unable to resolve variable '" + name + "'");
    }
    return returnValue;
  }
  
  /**
   * Creates and returns a {@link NameResolver} appropriate for the
   * supplied {@link Named} and the supplied {@code value}.
   *
   * @param named the {@link Named} that serves as the virtual
   * universe or domain of {@link Name}s that are potentially
   * {@linkplain #isResolveable(String) resolvable} into {@link
   * NameResolver}s vended by this {@link NameResolverFactory}; must
   * not be {@code null}
   *
   * @param name the name of the variable the new {@link NameResolver}
   * will return from its {@link NameResolver#getName()} method; must
   * not be {@code null}
   *
   * @return a non-{@code null} {@link NameResolver}
   *
   * @exception IllegalArgumentException if either {@code named} or
   * {@code name} is {@code null}
   */
  protected NameResolver createNameResolver(final Named named, final String name) {
    return new NameResolver(named, new NameType(name));
  }

  /**
   * Returns {@code true} if the supplied {@code name} is non-{@code
   * null} and this {@link NameResolverFactory} has a {@link
   * NameResolver} that has been installed by the inner workings of
   * the {@link #isResolveable(String)} method to resolve a {@link
   * Name} indexed under the supplied {@code name}.
   *
   * @param name the {@linkplain NameType#getValue() value} of a
   * {@link NameType}; may be {@code null}
   *
   * @return {@code true} if the supplied {@code name} is non-{@code
   * null} and this {@link NameResolverFactory} has a {@link
   * NameResolver} that has been installed by the inner workings of
   * the {@link #isResolveable(String)} method to resolve a {@link
   * Name} indexed under the supplied {@code name}; {@code false} in
   * all other cases
   */
  @Override
  public boolean isTarget(final String name) {
    final boolean returnValue;
    if (name == null) {
      returnValue = false;
    } else {
      final Map<?, ?> resolvers = this.getVariableResolvers();
      returnValue = resolvers != null && resolvers.containsKey(name);
    }
    return returnValue;
  }

  /**
   * Throws an {@link UnsupportedOperationException} when invoked.
   *
   * @param name the name of a variable to create; may be {@code
   * null}; ignored by this implementation
   *
   * @param value the value of the new variable; may be {@code null};
   * ignored by this implementation
   *
   * @return a {@link VariableResolver} responsible for resolving the
   * new variable, or {@code null}
   *
   * @exception UnsupportedOperationException if this implementation
   * is invoked
   */
  @Override
  public VariableResolver createVariable(final String name, final Object value) {
    throw new UnsupportedOperationException("createVariable");
  }

  /**
   * Throws an {@link UnsupportedOperationException} when invoked.
   *
   * @param name the name of a variable to create; may be {@code
   * null}; ignored by this implementation
   *
   * @param value the value of the new variable; may be {@code null};
   * ignored by this implementation
   *
   * @param type the type of the new variable; may be {@code null};
   * ignored by this implementation
   *
   * @return a {@link VariableResolver} responsible for resolving the
   * new variable, or {@code null}
   *
   * @exception UnsupportedOperationException if this implementation
   * is invoked
   */
  @Override
  public VariableResolver createVariable(final String name, final Object value, final Class<?> type) {
    throw new UnsupportedOperationException("createVariable");
  }

}
