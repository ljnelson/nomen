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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A special-purpose {@link PropertyChangeListener} that ensures that
 * if a {@link Name}'s ownership changes, then all entries within a
 * {@link Map} that reference it are removed.
 *
 * <p>Consider the case of a {@link Map} that a {@link Named} uses to
 * index its owned {@link Name}s under {@link NameType} keys.  A given
 * {@link Name} might be indexed in this {@link Map} under two
 * different {@link NameType}s.  If this {@link Name} changes its
 * "{@code named}" (ownership) property from the {@link Named} in
 * question to some other unrelated {@link Named}, then we need to
 * ensure that all references to that {@link Name} in the {@link Map}
 * are removed.</p>
 *
 * <p>The {@link NameSupport} class uses this class.  When any {@link
 * Name} is added via the {@link NameSupport#putName(Map, NameType,
 * Name)} method, a single instance of this class is {@linkplain
 * Name#addPropertyChangeListener(String, PropertyChangeListener)
 * added} as a {@link PropertyChangeListener} to the {@code named}
 * property of that {@link Name}.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see PropertyChangeListener
 *
 * @see Name
 *
 * @see NameSupport
 */
public final class NameOwnershipMonitor implements PropertyChangeListener {


  /*
   * Instance fields.
   */


  /**
   * The {@link Named} whose ownership of {@link Name}s is to be
   * monitored.
   *
   * <p>This field will never be {@code null}.</p>
   */
  private final Named owner;

  /**
   * The set of {@link Entry}s representing the related owner's {@link
   * Name}s.
   *
   * <p>This field will never be {@code null}.</p>
   */
  private final Iterable<? extends Entry<?, ? extends Name>> nameEntries;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NameOwnershipMonitor}.
   *
   * @param owner the {@link Named} whose ownership of {@link Name}s
   * will be monitored; must not be {@code null}
   *
   * @param nameEntries an {@link Iterable} of {@link Entry} instances
   * whose keys are usually {@link NameType}s and whose values are
   * {@link Name}s; when a {@link Name} changes its "{@code named}"
   * property then this {@link Iterable} will be combed to find any
   * {@link Entry} instances that reference it and those instances
   * will be removed
   *
   * @exception IllegalArgumentException if either parameter is {@code
   * null}
   *
   * @see #propertyChange(PropertyChangeEvent)
   */
  public NameOwnershipMonitor(final Named owner, final Iterable<? extends Entry<?, ? extends Name>> nameEntries) {
    super();
    if (owner == null) {
      throw new IllegalArgumentException("owner", new NullPointerException("owner"));
    }
    if (nameEntries == null) {
      throw new IllegalArgumentException("nameEntries", new NullPointerException("nameEntries"));
    }
    this.owner = owner;
    this.nameEntries = nameEntries;
  }

  
  /*
   * Instance methods.
   */


  /**
   * Reacts to a change in the "{@code named}" property exposed by
   * {@link Name} implementations by ensuring that any {@link Name}
   * owned by the {@link Named} supplied to this class at construction
   * time whose ownership is changing from that {@link Named} is
   * removed from the {@link Iterable} supplied at construction time.
   *
   * <p>Basically, when a {@link Name} is put into a {@link Map}
   * indexed by a {@link NameType} in some {@link Named} somewhere,
   * then if that {@link Name}'s ownership changes "away" from that
   * {@link Named}, that {@link Name} must be evicted from that {@link
   * Map}, since it is no longer owned by that {@link Named}.  This
   * method monitors a {@link Name} for that kind of ownership change,
   * and performs the {@link Map} eviction appropriately.</p>
   *
   * @param event a {@link PropertyChangeEvent} describing a property
   * change; must not be {@code null}
   */
  @Override
  public final void propertyChange(final PropertyChangeEvent event) {
    if (event != null && "named".equals(event.getPropertyName()) && this.owner == event.getOldValue()) {
      final Object source = event.getSource();
      if (source instanceof Name && this.owner != event.getNewValue() && this.nameEntries != null) {
        final Iterator<? extends Entry<?, ? extends Name>> nameEntryIterator = this.nameEntries.iterator();
        if (nameEntryIterator != null) {
          while (nameEntryIterator.hasNext()) {
            final Entry<?, ? extends Name> entry = nameEntryIterator.next();
            if (entry != null) {
              final Name n = entry.getValue();
              if (n == source) {
                // If we find a Name anywhere in the Entry set that is
                // *the same* Name as the one whose ownership has
                // changed, then remove that entry from the set, so
                // there aren't any more NameTypes in the set that
                // index it.
                nameEntryIterator.remove();

                // We remove ourselves from the name in question to
                // avoid memory leaks.
                n.removePropertyChangeListener("named", this);
              }
            }
          }
        }
      }
    }
  }

  

}
