/*
 * Copyright (c) 2018 John Toman.
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.lang.reflect;

import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.FieldRepository;
import sun.reflect.generics.scope.ClassScope;

public interface FieldGenericHandler {
  Type getType();
  String getGenericSignature();
  public void setCachedGenericInfo(FieldRepository genericInfo);
  public FieldRepository getCachedGenericInfo();
  public Class<?> getDeclaringClass();
  
  default Type getGenericType0() {
    if (getGenericSignature() != null)
        return getGenericInfo().getGenericType();
    else
        return getType();
  }
  
  
  default FieldRepository getGenericInfo() {
    FieldRepository genericInfo = getCachedGenericInfo();
      if (genericInfo == null) {
          genericInfo = FieldRepository.make(getGenericSignature(),
                                             getFactory());
         setCachedGenericInfo(genericInfo); 
      }
      return genericInfo;
  }
  

  default GenericsFactory getFactory() {
    Class<?> c = getDeclaringClass();
    // create scope and factory
    return CoreReflectionFactory.make(c, ClassScope.make(c));
  }

}
