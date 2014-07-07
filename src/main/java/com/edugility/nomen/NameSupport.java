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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NameSupport implements Named {

  private static final long serialVersionUID = 1L;

  private final Named delegate;

  private final Map<NameType, Name> map;

  private transient PropertyChangeListener ownershipMonitor;

  public NameSupport() {
    super();
    this.delegate = this;
    this.map = new HashMap<NameType, Name>();
    this.ownershipMonitor = new NameOwnershipMonitor(this.delegate, this.map.entrySet());
  }

  public NameSupport(final Named delegate) {
    this(delegate, null);
  }

  public NameSupport(final Named delegate, Map<NameType, Name> map) {
    super();
    if (map == null) {
      map = new HashMap<NameType, Name>();
    }
    this.delegate = delegate;
    this.map = map;
    this.ownershipMonitor = new NameOwnershipMonitor(this.delegate, map.entrySet());
  }

  @Override
  public Name getName(final NameType nameType) {
    final Name returnValue;
    final Map<NameType, Name> map = this.map;
    if (map != null) {
      returnValue = map.get(nameType);
    } else {
      returnValue = null;
    }
    return returnValue;
  }

  public Set<NameType> getNameTypes() {
    final Set<NameType> returnValue;
    final Map<NameType, Name> map = this.map;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableSet(map.keySet());
    }
    return returnValue;
  }

  public Collection<Name> getNames() {
    final Collection<Name> returnValue;
    final Map<NameType, Name> map = this.map;
    if (map == null || map.isEmpty()) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = Collections.unmodifiableCollection(map.values());
    }
    return returnValue;
  }

  public Name putName(final NameType nameType, final Name name) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    } else if (name == null) {
      throw new IllegalArgumentException("name", new NullPointerException("name"));
    }
    final Named delegate = this.delegate;
    final Name returnValue;
    final Name old;
    if (delegate == null) {
      old = null;
    } else {
      old = delegate.getName(nameType);
    }
    if (old == name) {
      returnValue = old;
    } else {
      name.setNamed(delegate);
      this.addNameOwnershipMonitor(name);
      returnValue = map.put(nameType, name);
      assert returnValue == old;
      if (returnValue != null) {
        this.disown(returnValue);
      }
    }
    return returnValue;
  }

  private final void addNameOwnershipMonitor(final Name name) {
    if (name != null) {
      boolean add = true;
      final PropertyChangeListener[] pcls = name.getPropertyChangeListeners("named");
      if (pcls != null && pcls.length > 0) {
        for (final PropertyChangeListener pcl : pcls) {
          if (pcl == this.ownershipMonitor) {
            add = false;
            break;
          }
        }
      }
      if (add) {
        name.addPropertyChangeListener("named", this.ownershipMonitor);
      }
    }
  }

  public Name removeName(final NameType nameType) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }
    final Name returnValue;
    final Map<NameType, Name> map = this.map;
    if (map != null && !map.isEmpty()) {
      returnValue = map.remove(nameType);
      if (returnValue != null) {
        this.disown(returnValue);
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
  private final void disown(final Name name) {
    if (name != null) {
      final Map<NameType, Name> map = this.map;
      if (map != null && !map.isEmpty()) {
        final Iterable<?> values = this.map.values();
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
  }

  private final void readObject(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
    if (stream != null) {
      stream.defaultReadObject();
    }
    this.ownershipMonitor = new NameOwnershipMonitor(this, this.map.entrySet());
  }

}

