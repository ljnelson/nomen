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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NameValue extends AbstractValued {

  private static final long serialVersionUID = 1L;

  private static final int cacheSize = 20;

  private static final Map<String, NameValue> cache = new LinkedHashMap<String, NameValue>(cacheSize, 0.75F, true) {
    private static final long serialVersionUID = 1L;
    @Override
    protected final boolean removeEldestEntry(final Entry<String, NameValue> entry) {
      return this.size() > cacheSize;
    }
  };

  private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

  private Boolean atomic;

  public NameValue() {
    super();
  }

  public NameValue(final String value) {
    this(value, false);
  }

  public NameValue(final String value, final boolean atomic) {
    super(value);
    this.setAtomic(atomic);
  }

  public boolean isAtomic() {
    if (this.atomic == null) {
      return false;
    }
    return this.atomic.booleanValue();
  }

  public void setAtomic(final boolean atomic) {
    if (this.atomic != null && !this.atomic.booleanValue() == atomic) {
      throw new IllegalStateException();
    }
    this.atomic = Boolean.valueOf(atomic);
  }

  @Override
  public int hashCode() {
    int result = 17;
    
    int c = Boolean.valueOf(this.isAtomic()).hashCode();
    result = result * 37 + c;
    
    c = super.hashCode();
    result = result * 37 + c;
    
    return result;
  }

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
      return super.equals(other);
    } else {
      return false;
    }
  }


  /*
   * Static methods.
   */


  public static final NameValue nv(final String value) {
    return valueOf(value);
  }

  public static final NameValue valueOf(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    cacheLock.readLock().lock();
    NameValue nv = cache.get(value);
    if (nv == null) {
      cacheLock.readLock().unlock();
      cacheLock.writeLock().lock();
      if (cache.containsKey(value)) {
        cacheLock.readLock().lock();
        cacheLock.writeLock().unlock();
        nv = cache.get(value);
        assert nv != null;
        cacheLock.readLock().unlock();
      } else {
        nv = new NameValue(value);
        cache.put(value, nv);
        cacheLock.writeLock().unlock();
      }
    } else {
      cacheLock.readLock().unlock();
    }
    assert nv != null;
    return nv;
  }

  static final boolean isCached(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    try {
      cacheLock.readLock().lock();
      return cache.containsKey(value);
    } finally {
      cacheLock.readLock().unlock();
    }
  }

}
