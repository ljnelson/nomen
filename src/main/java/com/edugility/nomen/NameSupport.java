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

import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object that manages {@link Name}s on behalf of some {@link
 * Named}.  A {@link NameSupport} can be used as an internal delegate
 * in your {@link Named} implementation to handle common use cases.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public class NameSupport {

  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The {@link Named} on whose behalf this {@link NameSupport} is
   * operating.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final Named delegate;

  /**
   * Creates a new {@link NameSupport} that will manage {@link Name}s
   * for the supplied {@link Named}.
   *
   * @param delegate the delegate whose {@link Name}s will be managed;
   * must not be {@code null}
   *
   * @exception IllegalArgumentException if {@code delegate} is {@code
   * null}
   */
  public NameSupport(final Named delegate) {
    super();
    if (delegate == null) {
      throw new IllegalArgumentException("delegate", new NullPointerException("delegate"));
    }
    this.delegate = delegate;
  }

  /**
   * Returns a {@link Set} of {@link NameType}s that the supplied
   * {@link Map} contains as {@linkplain Map#keySet() keys}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param map the {@link Map} whose {@linkplain Map#keySet() keys}
   * are to be returned; may be {@code null} in which case an
   * {@linkplain Collections#emptySet() empty <code>Set</code>} will
   * be returned
   *
   * @return a non-{@code null}, {@linkplain
   * Collections#unmodifiableSet(Set) immutable <code>Set</code>}
   */
  public Set<? extends NameType> getNameTypes(final Map<? extends NameType, ?> map) {
    final Set<? extends NameType> returnValue;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableSet(map.keySet());
    }
    return returnValue;
  }

  /**
   * Returns a non-{@code null}, {@linkplain
   * Collections#unmodifiableCollection(Collection) immutable
   * <code>Collection</code>} of {@link Name}s housed in the supplied
   * {@link Map} as its {@linkplain Map#values() values}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @param map a {@link Map} containing {@link Name}s as its
   * {@linkplain Map#values() values}; may be {@code null} in which
   * case an {@linkplain Collections#emptySet() empty
   * <code>Set</code>} will be returned
   *
   * @return a non-{@code null} {@linkplain
   * Collections#unmodifiableCollection(Collection) immutable
   * <code>Collection</code>} of {@link Name}s
   */
  public Collection<? extends Name> getNames(final Map<?, ? extends Name> map) {
    final Collection<Name> returnValue;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableCollection(map.values());
    }
    return returnValue;
  }

  /**
   * {@linkplain Map#put(Object, Object) Puts} the supplied {@link
   * Name} into the supplied {@link Map} indexed under the supplied
   * {@link NameType}, while ensuring that {@link Name} ownership is
   * sanely managed.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param map a mutable {@link Map} containing {@link Name}s indexed
   * by {@link NameType}s; must not be {@code null}; this {@link Map}
   * will be modified as a result of invoking this method
   *
   * @param nameType the {@link NameType} under which to index the
   * supplied {@link Name} in the supplied {@link Map}; must not be
   * {@code null}
   *
   * @param name the {@link Name} to index in the supplied {@link
   * Map}; must not be {@code null}
   *
   * @return the {@link Name} previously indexed under the supplied
   * {@link NameType}, or {@code null}
   */
  public Name putName(final Map<NameType, Name> map, final NameType nameType, final Name name) {
    if (map == null) {
      throw new IllegalArgumentException("map", new NullPointerException("map"));
    } else if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    } else if (name == null) {
      throw new IllegalArgumentException("name", new NullPointerException("name"));
    }

    final Named delegate = this.delegate;
    assert delegate != null;

    final Name old = delegate.getName(nameType);
    if (old != name) {
      
      name.setNamed(delegate);
      this.addNameOwnershipMonitor(name, map);
      
      final Name priorMapValue = map.put(nameType, name);
      final Collection<? extends Name> names = map.values();
      this.disown(old, names);
      if (priorMapValue != old) {
        this.disown(priorMapValue, names);
      }
    }

    return old;
  }

  /**
   * Adds a new {@link NameOwnershipMonitor} to the supplied {@link
   * Name} as a {@link PropertyChangeListener} that listens for
   * changes in its {@code named} property.  The {@link
   * NameOwnershipMonitor} is responsible for ensuring that ownership
   * of the {@link Name} is properly maintained.
   *
   * @param name the {@link Name} to monitor; may be {@code null} in
   * which case no action will be taken
   *
   * @param map a {@link Map} whose {@linkplain Map#values() values}
   * are {@link Name}s; may be {@code null} in which case no action
   * will be taken
   */
  private final void addNameOwnershipMonitor(final Name name, final Map<?, ? extends Name> map) {
    if (name != null && map != null) {
      boolean add = true;
      final PropertyChangeListener[] pcls = name.getPropertyChangeListeners("named");
      if (pcls != null && pcls.length > 0) {
        for (final PropertyChangeListener pcl : pcls) {
          if (pcl instanceof NameOwnershipMonitor) {
            add = false;
            break;
          }
        }
      }
      if (add) {
        name.addPropertyChangeListener("named", new NameOwnershipMonitor(this.delegate, map.entrySet()));
      }
    }
  }

  /**
   * Removes any {@link Name} present in the supplied {@link Map} that
   * is indexed under the supplied {@link NameType}.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param map the {@link Map} from which a {@link Name} should be
   * removed; may be {@code null} in which case no action will be
   * taken and {@code null} will be returned
   *
   * @param nameType the {@link NameType} identifying the {@link Name}
   * to be removed; must not be {@code null}
   *
   * @return the {@link Name} that was removed, or {@code null}
   *
   * @exception IllegalArgumentException if {@code nameType} is {@code
   * null}
   */
  public Name removeName(final Map<?, ? extends Name> map, final NameType nameType) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }

    final Name returnValue;
    if (map != null && !map.isEmpty()) {
      returnValue = map.remove(nameType);
      if (returnValue != null) {
        this.disown(returnValue, map.values());
      }
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  /**
   * Sets the ownership of the supplied {@link Name} to {@code null}
   * if and only if it can be proved that no {@link NameType} indexes
   * it.
   *
   * @param name the {@link Name} to disown; may be {@code null} in
   * which case no action will be taken
   *
   * @param names a {@link Collection} of {@link Name}s that will be
   * consulted for ownership maintenance purposes; may be {@code null}
   * in which case no action will be taken
   */
  private final void disown(final Name name, final Collection<? extends Name> names) {
    if (name != null && name.getNamed() != null && names != null && !names.isEmpty()) {
      boolean found = false;
      for (final Object value : names) {
        if (value == name) {
          found = true;
          break;
        }
      }
      if (!found) {
        name.setNamed(null);
      }
    }
  }

}

