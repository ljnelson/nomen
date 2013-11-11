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

public class NameType extends AbstractValued {

  private static final long serialVersionUID = 1L;

  private static final int cacheSize = Integer.getInteger("nomen.NameType.cacheSize", 20);
  
  private static final Map<String, NameType> cache = new LinkedHashMap<String, NameType>(cacheSize, 0.75F, true) {
    private static final long serialVersionUID = 1L;
    @Override
    protected boolean removeEldestEntry(final Entry<String, NameType> entry) {
      return this.size() > cacheSize;
    }
  };
  
  private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

  public NameType() {
    super();
  }

  public NameType(final String value) {
    super(value);
  }


  /*
   * Static methods.
   */


  /**
   * Returns the result of calling the {@link #valueOf(String)}
   * method.  This method exists primarily for using via the {@code
   * import static} directive.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>This method is safe for use by multiple concurrent
   * threads.</p>
   *
   * @param value a {@link String} naming the {@link NameType} to be
   * returned; must not be {@code null}
   * 
   * @return the result of calling the {@link #valueOf(String)}
   * method; never {@code null}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #valueOf(String)
   *
   * @see #NameType(String)
   */
  public static final NameType nt(final String value) {
    return valueOf(value);
  }

  /**
   * Returns a {@link NameType} whose {@linkplain #getValue() value}
   * will be equal to the supplied {@code value}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * <p>This method is safe for use by multiple concurrent
   * threads.</p>
   *
   * <p>This method does not necessarily create a new {@link NameType}
   * on each invocation, but may return a cached copy.</p>
   *
   * @param value a {@link String} naming the {@link NameType} to be
   * returned; must not be {@code null}
   *
   * @return a non-{@code null} {@link NameType}
   *
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see #NameType(String)
   */
  public static final NameType valueOf(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("value", new NullPointerException("value"));
    }
    NameType nt = null;
    // Because the cache is a LinkedHashMap in access order, get()
    // calls are structural modifications.  Therefore we need a write
    // lock.
    cacheLock.writeLock().lock();
    try {
      nt = cache.get(value);
      if (nt == null) {
        nt = new NameType(value);
        cache.put(value, nt);
      }
    } finally {
      cacheLock.writeLock().unlock();
    }
    return nt;
  }

  /**
   * Returns {@code true} if and only if the supplied {@code value}
   * identifies a {@link NameType} that is cached by the internals of
   * this class.
   *
   * <p>The internal cache's contents can change over time, so there
   * is no guarantee&mdash;explicit or implicit&mdash;about how long a
   * given cached {@link NameType} will continue to be cached.</p>
   *
   * <p>This method is intended for use by single-threaded unit tests,
   * although it is safe for use by multiple concurrent threads.</p>
   *
   * @param value a {@link String} identifying a {@link NameType};
   * must not be {@code null}
   *
   * @return {@code true} if the supplied {@code value} identifies a
   * cached {@link NameType}; {@code false} otherwise
   */
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
