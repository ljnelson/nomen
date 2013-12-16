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

/**
 * An owner of {@link Name}s that can retrieve them efficiently when
 * {@linkplain #getName(NameType) asked for them} by {@link NameType}.
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #getName(NameType)
 *
 * @see Name
 */
public interface Named extends Serializable {

  /**
   * Retrieves a {@link Name} that belongs to this {@link Named} that
   * is appropriate for the supplied {@link NameType}, or {@code null}
   * if no such {@link Name} can be found.
   *
   * <p>Implementations of this method are permitted to return {@code
   * null}.</p>
   *
   * @param nameType the {@link NameType} for which a {@link Name}
   * will be sought; may be {@code null} in which case the
   * implementation may decide what kind of {@link Name} to return
   * (including {@code null})
   *
   * @return a {@link Name} indexed under the supplied {@link
   * NameType}, or {@code null}
   *
   * @exception IllegalArgumentException if {@code nameType} is not
   * accepted by the implementation of this method for any reason
   */
  public Name getName(final NameType nameType);

}
