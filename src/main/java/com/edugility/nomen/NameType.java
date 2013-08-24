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

public class NameType implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int cacheSize = 20;
  
  private static final Map<String, NameType> cache = new LinkedHashMap<String, NameType>(cacheSize, 0.75F, true) {
    private static final long serialVersionUID = 1L;
    @Override
    protected boolean removeEldestEntry(final Entry<String, NameType> entry) {
      return this.size() > cacheSize;
    }
  };
  
  private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

  private String value;

  public NameType() {
    super();
  }

  public NameType(final String value) {
    this();
    this.setValue(value);
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    final Object old = this.getValue();
    if (old != null && !old.equals(value)) {
      throw new IllegalStateException();
    }
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int hashCode;
    final String value = this.getValue();
    if (value == null) {
      hashCode = 0;
    } else {
      hashCode = value.hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && this.getClass().equals(other.getClass())) {
      final NameType him = (NameType)other;
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
    return String.valueOf(this.getValue());
  }


  /*
   * Static methods.
   */


  public static final NameType nt(final String value) {
    return valueOf(value);
  }

  public static final NameType valueOf(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    cacheLock.readLock().lock();
    NameType nt = cache.get(value);
    if (nt == null) {
      cacheLock.readLock().unlock();
      cacheLock.writeLock().lock();
      if (cache.containsKey(value)) {
        cacheLock.readLock().lock();
        cacheLock.writeLock().unlock();
        nt = cache.get(value);
        assert nt != null;
        cacheLock.readLock().unlock();
      } else {
        nt = new NameType(value);
        cache.put(value, nt);
        cacheLock.writeLock().unlock();
      }
    } else {
      cacheLock.readLock().unlock();
    }
    assert nt != null;
    return nt;
  }


}
