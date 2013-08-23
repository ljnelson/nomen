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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import org.mvel2.integration.impl.CachedMapVariableResolverFactory;


/**
 * A {@link Serializable} combination of a {@link NameValue}, the
 * {@link Named} that it is currently assigned to, and the {@link
 * NameType} under which it is indexed.
 */
public class Name implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final Pattern whitespacePattern = Pattern.compile("\\s+");

  private Named named;

  private NameType nameType;

  private NameValue nameValue;

  private boolean collapseWhitespace;

  private transient NameResolverFactory nameResolverFactory;

  private transient CompiledTemplate compiledTemplate;

  public Name() {
    super();
  }

  public Name(final Named named, final NameType nameType, final NameValue nameValue) {
    this(named, nameType, nameValue, true);
  }

  public Name(final Named named, final NameType nameType, final NameValue nameValue, final boolean collapseWhitespace) {
    this();
    this.setCollapseWhitespace(collapseWhitespace);
    this.setNamed(named);
    this.setNameType(nameType);
    this.setNameValue(nameValue);
  }

  public boolean getCollapseWhitespace() {
    return this.collapseWhitespace;
  }

  public void setCollapseWhitespace(final boolean collapseWhitespace) {
    this.collapseWhitespace = collapseWhitespace;
  }

  public Named getNamed() {
    return this.named;
  }

  public void setNamed(final Named named) {
    if (named == null) {
      throw new IllegalArgumentException("named", new NullPointerException("named"));
    }
    this.named = named;
    this.compiledTemplate = null;
    this.installTemplate();
  }

  public NameType getNameType() {
    return this.nameType;
  }

  public void setNameType(final NameType nameType) {
    if (nameType == null) {
      throw new IllegalArgumentException("nameType", new NullPointerException("nameType"));
    }
    this.nameType = nameType;
  }

  public NameValue getNameValue() {
    return this.nameValue;
  }

  public void setNameValue(final NameValue nameValue) {
    if (nameValue == null) {
      throw new IllegalArgumentException("nameValue", new NullPointerException("nameValue"));
    }
    this.nameValue = nameValue;
    this.compiledTemplate = null;
    this.installTemplate();
  }

  private final void installTemplate() {
    if (this.compiledTemplate == null) {
      final NameValue nv = this.getNameValue();
      if (nv != null) {
        final String template = nv.getValue();
        if (template != null) {
          this.compiledTemplate = TemplateCompiler.compileTemplate(template);
          assert this.compiledTemplate != null;
        }          
      }
    }    
  }

  public String getValue() {
    String returnValue = null;
    this.installTemplate();
    if (this.compiledTemplate != null) {
      final Named named = this.getNamed();
      if (this.nameResolverFactory == null) {
        this.nameResolverFactory = new NameResolverFactory(named);
      }
      final Object rawValue = TemplateRuntime.execute(this.compiledTemplate, named, this.nameResolverFactory);
      if (rawValue == null) {
        returnValue = "";
      } else if (this.getCollapseWhitespace()) {
        final Matcher m = whitespacePattern.matcher(rawValue.toString());
        assert m != null;
        returnValue = m.replaceAll(" ");
      } else {
        returnValue = rawValue.toString();
      }
    }
    return returnValue;
  }

}
