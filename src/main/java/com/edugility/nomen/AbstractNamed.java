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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A skeletal implementation of the {@link Named} interface, together
 * with mutator methods.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public abstract class AbstractNamed implements Named {


  /*
   * Static fields.
   */


  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   */
  private static final long serialVersionUID = 1L;


  /*
   * Instance fields.
   */


  /**
   * A {@link Map} of the {@link Name}s this {@link AbstractNamed}
   * has, indexed by {@link NameType}.
   *
   * <p>This field may be {@code null}.</p>
   */
  private Map<NameType, Name> names;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link AbstractNamed}.
   */
  protected AbstractNamed() {
    super();
  }


  /*
   * Instance methods.
   */


  /**
   * Retrieves a {@link Name} that belongs to this {@link
   * AbstractNamed} that is appropriate for the supplied {@link
   * NameType}, or {@code null} if no such {@link Name} can be found.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param nameType the {@link NameType} for which a {@link Name}
   * will be sought; may be {@code null} in which case {@code null}
   * will be returned
   *
   * @return a {@link Name} indexed under the supplied {@link
   * NameType}, or {@code null}
   *
   * @exception IllegalArgumentException if {@code nameType} is not
   * accepted for any reason
   */
  @Override
  public Name getName(final NameType nameType) {
    Name name = null;
    if (nameType != null && this.names != null && !this.names.isEmpty()) {
      name = this.names.get(nameType);
      assert name == null ? true : name.getNamed() == this;
    }
    return name;
  }

  /**
   * Stores the supplied {@link Name} under the supplied {@link
   * NameType} in such a way that it can be retrieved later by the
   * #getName(NameType)} method when that method is supplied with a
   * {@link NameType} {@linkplain NameType#equals(Object) equal to}
   * the supplied {@link NameType}.
   *
   * <h3>Implementation Notes</h3>
   *
   * <p>To properly orphan {@link Name}s that are no longer indexed
   * under a given {@link NameType} in this {@link AbstractNamed},
   * {@linkplain #getNames() all the <code>Name</code>s affiliated
   * with this <code>AbstractNamed</code> implementation} must be
   * scanned, so this operation has performance that decreases as
   * the total number of {@link Name}s stored by this {@link
   * AbstractNamed} implementation increases.</p>
   *
   * @param nameType a {@link NameType} under which a new {@link Name}
   * will be stored; must not be {@code null}
   *
   * @param name a {@link Name} to store; must not be {@code null};
   * its {@link Name#setNamed(Named)} method will be called with
   * {@code this} as its argument.
   *
   * @return the prior {@link Name} stored under the supplied {@link
   * NameType}, or {@code null} if there was no such {@link Name}.
   * The returned {@link Name}, if non-{@code null}, will return
   * {@code null} from its {@link Name#getNamed()} method only if it
   * is now truly orphaned, that is only if it is not indexed in this
   * {@link AbstractNamed} under any other {@link NameType}.
   *
   * @exception IllegalArgumentException if {@code nameType} or {@code
   * name} is {@code null}, or if {@code nameType} is unsuitable, or
   * if {@code name} is unsuitable
   */
  public Name putName(final NameType nameType, final Name name) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    } else if (name == null) {
      throw new IllegalArgumentException("name", new NullPointerException("name"));
    }
    if (this.names == null) {
      this.names = new HashMap<NameType, Name>(11);
    }
    final Name returnValue;
    final Name old = this.names.get(nameType);
    if (old == name) {
      returnValue = old;
    } else {
      name.setNamed(this);
      returnValue = this.names.put(nameType, name);
      assert returnValue == old;
      if (returnValue != null) {
        final Collection<?> values = this.names.values();
        if (values != null && !values.isEmpty()) {
          boolean found = false;
          for (final Object value : values) {
            if (value == returnValue) {
              // Can't use equals() here, because two Names are equal
              // if their computed values result in the same string.
              // Here we are truly interested in object identity, not
              // equality.
              found = true;
              break;
            }
          }
          if (!found) {
            // This returnValue is not stored anywhere else in the
            // map, so we can orphan it.
            returnValue.setNamed(null);
          }
        }
      }
    }
    return returnValue;
  }

  /**
   * Creates a new {@link Name} implementation given a non-{@code
   * null} {@link NameValue}.
   *
   * <p>This implementation simply {@linkplain Name#Name(Named,
   * NameValue) calls the relevant <code>Name</code> constructor}.</p>
   *
   * @param nameValue the {@link NameValue} that will serve as the
   * value for the new {@link Name}; must not be {@code null}
   *
   * @return a new, non-{@code null} {@link Name}
   *
   * @exception IllegalArgumentException if {@code nameValue} is
   * {@code null}
   *
   * @see Name#Name(Named, NameValue)
   */
  protected Name createName(final NameValue nameValue) {
    return new Name(this, nameValue);
  }

  /**
   * Removes the single {@link Name} stored under a {@link NameType}
   * that is {@linkplain AbstractValued#equals(Object) equal} or
   * identical to the supplied {@link NameType} and returns it.
   *
   * <p>This method may return {@code null} if there is no {@link
   * Name} indexed under a {@link NameType} {@linkplain
   * AbstractValued#equals(Object) equal} or identical to the supplied
   * {@link NameType}.</p>
   *
   * @param nameType the {@link NameType} to use to find the {@link
   * Name} in question; must not be {@code null}
   *
   * @return the {@link Name} that was removed, or {@code null}.  The
   * {@link Name} that is returned, if non-{@code null}, will return
   * {@code null} from its {@link Name#getNamed()} method.
   *
   * @exception IllegalArgumentException if {@code nameType} is {@code
   * null}
   */
  public Name removeName(final NameType nameType) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }
    Name returnValue = null;
    if (this.names != null && !this.names.isEmpty()) {
      returnValue = this.names.remove(nameType);
    }
    if (returnValue != null) {
      returnValue.setNamed(null);
    }
    return returnValue;
  }

  /**
   * Returns a non-{@code null} {@link Set} of {@link NameType}
   * instances, each element of which can be used as an argument to
   * the {@link #getName(NameType)} method.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Set} of {@link NameType}s
   */
  public Set<NameType> getNameTypes() {
    final Set<NameType> returnValue;
    if (this.names == null || this.names.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableSet(this.names.keySet());
    }
    return returnValue;
  }

  /**
   * Returns a non-{@code null} {@link Collection} of {@link Name}s
   * that this {@link AbstractNamed} is known by.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Collection} of {@link Name}s
   */
  public Collection<Name> getNames() {
    final Collection<Name> returnValue;
    if (this.names == null || this.names.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableCollection(this.names.values());
    }
    return returnValue;
  }

}
