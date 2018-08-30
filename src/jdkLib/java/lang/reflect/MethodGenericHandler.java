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

import java.lang.annotation.AnnotationFormatError;

import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.MethodScope;

public interface MethodGenericHandler {
  // bridge methods
  MethodRepository getCachedGenericInfo();
  void setCachedGenericInfo(MethodRepository info);
  String getGenericSignature();
  
  // Implemented by Method anyway
  Type getReturnType();
  Method getMethod();
  Class<?> getDeclaringClass();
  String getName();
  
  @SuppressWarnings("unchecked")    
  default TypeVariable<?>[] getTypeParameters0() {
    if(getGenericSignature() != null)
      return (TypeVariable<Method>[]) getGenericInfo0().getTypeParameters();
    else
      return (TypeVariable<Method>[]) new TypeVariable[0];
  }

  default Type getGenericReturnType0() {
    if (getGenericSignature() != null) {
      return getGenericInfo0().getReturnType();
    } else { return getReturnType();}
  }
  
  default MethodRepository getGenericInfo0() {
    MethodRepository genericInfo = getCachedGenericInfo();
    if(genericInfo == null) {
      genericInfo = MethodRepository.make(getGenericSignature(), getFactory());
      setCachedGenericInfo(genericInfo);
    }
    return genericInfo;
  }
  
  default GenericsFactory getFactory() {
    Method thisRef = getMethod();
    return CoreReflectionFactory.make(thisRef, MethodScope.make(thisRef));
  }
  
  default void specificToGenericStringHeader0(StringBuilder sb) {
    Type genRetType = getReturnType();
    sb.append(genRetType.getTypeName()).append(' ');
    sb.append(getDeclaringClass().getTypeName()).append('.');
    sb.append(getName());
  }
  
  default void specificToStringHeader0(StringBuilder sb) {
    sb.append(getReturnType().getTypeName()).append(' ');
    sb.append(getDeclaringClass().getTypeName()).append('.');
    sb.append(getName());
  }
  
  default void handleParameterNumberMismatch0(int resultLength, int numParameters) {
    throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
  }
}
