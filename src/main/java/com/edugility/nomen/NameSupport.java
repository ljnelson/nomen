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
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Named
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

  public NameSupport(final Named delegate) {
    super();
    this.delegate = delegate;
  }

  public Set<NameType> getNameTypes(final Map<NameType, Name> map) {
    final Set<NameType> returnValue;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableSet(map.keySet());
    }
    return returnValue;
  }

  public Collection<Name> getNames(final Map<NameType, Name> map) {
    final Collection<Name> returnValue;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableCollection(map.values());
    }
    return returnValue;
  }

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
      this.disown(old, map);
      if (priorMapValue != old) {
        this.disown(priorMapValue, map);
      }
    }

    return old;
  }

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

  public Name removeName(final Map<NameType, Name> map, final NameType nameType) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }

    final Name returnValue;
    if (map != null && !map.isEmpty()) {
      returnValue = map.remove(nameType);
      if (returnValue != null) {
        this.disown(returnValue, map);
      }
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  /**
   * Sets the ownership of the supplied {@link Name} to {@code null}
   * if it can be proved that no {@link NameType} indexes it.
   */
  private final void disown(final Name name, final Map<NameType, Name> map) {
    if (name != null && name.getNamed() != null && map != null && !map.isEmpty()) {
      final Iterable<?> values = map.values();
      if (values != null) {
        boolean found = false;
        for (final Object value : values) {
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

  private final void readObject(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
    if (stream != null) {
      stream.defaultReadObject();
    }
  }

}

