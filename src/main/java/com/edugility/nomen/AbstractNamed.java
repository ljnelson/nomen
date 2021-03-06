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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An implementation of the {@link MutableNamed} interface that uses a
 * {@link NameSupport} delegate internally.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see NameSupport
 */
public abstract class AbstractNamed implements MutableNamed {


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
   * A {@link Map} holding {@link Name}s {@linkplain Name#getNamed()
   * owned} by this {@link AbstractNamed} indexed by {@link
   * NameType}s.
   *
   * <p>This field is never {@code null}.</p>
   */
  private Map<NameType, Name> names;

  /**
   * A {@link NameSupport} instance that implements some of the
   * complex ownership logic required.
   *
   * <p>This field is never {@code null}.</p>
   */
  private transient NameSupport nameSupport;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link AbstractNamed}.
   */
  protected AbstractNamed() {
    super();
    this.names = new HashMap<NameType, Name>();
    this.nameSupport = new NameSupport(this);
  }


  /*
   * Instance methods.
   */


  @Override
  public Name getName(final NameType nameType) {
    assert this.names != null;
    return this.names.get(nameType);
  }

  @Override
  public Name putName(final NameType nameType, final Name name) {
    assert this.nameSupport != null;
    return this.nameSupport.putName(this.names, nameType, name);
  }

  @Override
  public Name removeName(final NameType nameType) {
    assert this.nameSupport != null;
    return this.nameSupport.removeName(this.names, nameType);
  }

  public Set<? extends NameType> getNameTypes() {
    assert this.nameSupport != null;
    return this.nameSupport.getNameTypes(this.names);
  }

  public Set<? extends Entry<? extends NameType, ? extends Name>> getNames() {
    assert this.nameSupport != null;
    return this.nameSupport.getNames(this.names);
  }

  private void readObject(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
    if (stream != null) {
      stream.defaultReadObject();
    }
    if (this.names == null) {
      this.names = new HashMap<NameType, Name>();
    }
    if (this.nameSupport == null) {
      this.nameSupport = new NameSupport(this);
    }
  }

}
