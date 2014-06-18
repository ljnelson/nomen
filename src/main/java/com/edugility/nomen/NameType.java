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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An {@link AbstractValued} implementation that serves as a key
 * within a {@link Named} to identify particular {@link Name}
 * instances.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Named
 *
 * @see Name
 *
 * @see Named#getName(NameType)
 */
public class NameType extends AbstractValued {

  /**
   * The version of this class for {@linkplain Serializable
   * serialization purposes}.
   *
   * @see Serializable
   */
  private static final long serialVersionUID = 1L;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NameType}.
   */
  public NameType() {
    super();
  }

  /**
   * Creates a new {@link NameType} with the supplied {@code value}.
   *
   * @param value the value for this {@link NameType}; must not be
   * {@code null}
   * 
   * @exception IllegalArgumentException if {@code value} is {@code
   * null}
   *
   * @see AbstractValued#AbstractValued(String)
   */
  public NameType(final String value) {
    super(value);
  }

}
