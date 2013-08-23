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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Range<T extends Comparable<T>> extends AbstractSet<T> {

  private static final long serialVersionUID = 1L;

  private T lowerBound;

  private T upperBound;

  private Interpolator<T> interpolator;

  public Range() {
    super();
  }

  public Interpolator<T> getInterpolator() {
    if (this.interpolator == null) {
      throw new IllegalStateException();
    }
    return this.interpolator;
  }

  public void setInterpolator(final Interpolator<T> interpolator) {
    if (interpolator == null) {
      throw new IllegalArgumentException("interpolator", new NullPointerException("interpolator"));
    }
    this.interpolator = interpolator;
  }

  public T getLowerBound() {
    return this.lowerBound;
  }

  public void setLowerBound(final T lowerBound) {
    this.lowerBound = lowerBound;
  }

  public T getUpperBound() {
    return this.upperBound;
  }

  public void setUpperBound(final T upperBound) {
    this.upperBound = upperBound;
  }

  @Override
  public boolean add(final T t) {
    throw new UnsupportedOperationException("add");
  }

  @Override
  public boolean addAll(final Collection<? extends T> t) {
    throw new UnsupportedOperationException("addAll");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("clear");
  }
  
  public static final <T extends Comparable<T>> boolean excludes(final T lowerBound, final T upperBound, final Object o) {
    if (o == null) {
      return lowerBound != null;
    } else {
      T him = null;
      try {
        him = (T)o;
      } catch (final ClassCastException boom) {
        boom.printStackTrace();
        return true;
      }
      if (lowerBound == null) {
        if (upperBound != null && upperBound.compareTo(him) <= 0) {
          return true;
        }
      } else if (upperBound == null) {
        if (lowerBound.compareTo(him) > 0) {
          return true;
        }
      } else if (lowerBound.compareTo(him) > 0 || upperBound.compareTo(him) <= 0) {
        return true;
      }
      return false;
    }
  }

  @Override
  public boolean contains(final Object o) {
    if (excludes(this.getLowerBound(), this.getUpperBound(), o)) {
      return false;
    }
    final Interpolator<T> interpolator = this.getInterpolator();
    if (interpolator == null) {
      throw new IllegalStateException();
    }
    T him = null;
    try {
      him = (T)o;
    } catch (final ClassCastException boom) {
      return false;
    }
    return interpolator.contains(lowerBound, upperBound, him);
  }

  @Override
  public boolean remove(final Object o) {
    throw new UnsupportedOperationException("remove");
  }

  @Override
  public boolean removeAll(final Collection<?> stuff) {
    throw new UnsupportedOperationException("removeAll");
  }

  @Override
  public boolean retainAll(final Collection<?> stuff) {
    throw new UnsupportedOperationException("retainAll");
  }

  @Override
  public Iterator<T> iterator() {
    final Interpolator<T> interpolator = this.getInterpolator();
    if (interpolator == null) {
      throw new IllegalStateException();
    }
    return interpolator.iterator(this.getLowerBound(), this.getUpperBound());
  }

  @Override
  public boolean isEmpty() {
    final Interpolator<T> interpolator = this.getInterpolator();
    if (interpolator == null) {
      throw new IllegalStateException();
    }
    return interpolator.isEmpty(this.getLowerBound(), this.getUpperBound());
  }

  @Override
  public int size() {
    final Interpolator<T> interpolator = this.getInterpolator();
    if (interpolator == null) {
      throw new IllegalStateException();
    }
    return interpolator.size(this.getLowerBound(), this.getUpperBound());
  }

  public static abstract class Interpolator<T extends Comparable<T>> {
    
    /**
     * Returns {@code true} if the range defined by the supplied
     * {@code lowerBound} and {@code upperBound} parameters logically
     * contains the supplied {@link Comparable}.
     *
     * <p>When this method is called by the {@link Range} class, it is
     * guaranteed that the supplied {@link Comparable} will have been
     * found to possibly lie between the lower and upper bounds.</p>
     *
     * @param lowerBound a {@link Comparable} representing the
     * inclusive lower bound; may be {@code null} in which case the
     * inclusive lower bound is infinite
     *
     * @param upperBound a {@link Comparable} representing the
     * exclusive upper bound; may be {@code null} in which case the
     * exclusive upper bound is infinite
     *
     * @param o the {@link Comparable} to test; must not be {@code null}
     *
     * @return {@code true} if this {@link Interpolator} concludes
     * that the supplied {@code Comparable} falls between the supplied
     * inclusive {@code lowerBound} and exclusive {@code upperBound}
     * parameter values; {@code false} in all other cases
     *
     * @exception IllegalArgumentException if {@code o} is {@code null}
     */
    public abstract boolean contains(final T lowerBound, final T upperBound, final T o);

    public boolean isEmpty(final T lowerBound, final T upperBound) {
      return lowerBound != null && upperBound != null && lowerBound.compareTo(upperBound) == 0;
    }

    public abstract Iterator<T> iterator(final T lowerBound, final T upperBound);

    public abstract int size(final T lowerBound, final T upperBound);

  }

  public static abstract class InterpolatingIterator<T extends Comparable<T>> implements Iterator<T> {

    public final T lowerBound;

    public final T upperBound;

    public InterpolatingIterator(final T lowerBound, final T upperBound) {
      super();
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }

  }

  public static class ContinuousDateInterpolator extends Interpolator<Date> implements Iterator<Date> {

    private long millis;

    private final long lowerMillis;

    private final Long upperMillis;

    public ContinuousDateInterpolator(final Date lowerBound, final Date upperBound) {
      super();
      if (lowerBound == null) {
        this.lowerMillis = Long.MIN_VALUE;
      } else {
        this.lowerMillis = lowerBound.getTime();
      }
      if (upperBound == null) {
        this.upperMillis = null;
      } else {
        this.upperMillis = Long.valueOf(upperBound.getTime());
      }
      this.millis = this.lowerMillis;
    }

    @Override
    public boolean contains(final Date lowerBound, final Date upperBound, final Date date) {
      return !Range.excludes(lowerBound, upperBound, date);
    }

    @Override
    public int size(final Date lowerBound, final Date upperBound) {
      if (lowerBound == null || upperBound == null) {
        return Integer.MAX_VALUE;
      }
      final long size = upperBound.getTime() - lowerBound.getTime();
      if (size >= Integer.MAX_VALUE) {
        return Integer.MAX_VALUE;
      }
      return (int)size;
    }

    @Override
    public boolean isEmpty(final Date lowerBound, final Date upperBound) {
      return lowerBound != null && upperBound != null && upperBound.getTime() - lowerBound.getTime() == 0L;
    }

    @Override
    public Iterator<Date> iterator(final Date lowerBound, final Date upperBound) {
      return new ContinuousDateInterpolator(lowerBound, upperBound);
    }

    @Override
    public boolean hasNext() {
      if (this.upperMillis == null) {
        if (this.millis == Long.MAX_VALUE) {
          return false;
        }
      } else if (this.millis >= this.upperMillis.longValue() - 1) {
        return false;
      }
      return true;
    }

    @Override
    public Date next() {
      if (this.upperMillis == null) {
        if (this.millis == Long.MAX_VALUE) {
          throw new NoSuchElementException();
        }
      } else if (this.millis >= this.upperMillis.longValue() - 1) {
        throw new NoSuchElementException();
      }
      final Date date = new Date(this.millis);
      this.millis = this.millis + 1L;
      return date;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }

  }

}
