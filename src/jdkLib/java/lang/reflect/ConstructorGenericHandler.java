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
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ConstructorScope;

public interface ConstructorGenericHandler<T> {
  ConstructorRepository getCachedGenericInfo();
  void setCachedGenericInfo(ConstructorRepository info);
  public Constructor<T> getConstructor();
  public String getSignature();
  Class<?> getDeclaringClass();
  
  default void specificToGenericStringHeader0(StringBuilder sb) {
    specificToStringHeader0(sb);
  }

  default void specificToStringHeader0(StringBuilder sb) {
    sb.append(getDeclaringClass().getTypeName());
  }


  @SuppressWarnings({"rawtypes", "unchecked"})
  public default TypeVariable<?>[] getTypeParameters0() {
    if(getSignature() != null) {
      return (TypeVariable<Constructor<T>>[]) getGenericInfo0().getTypeParameters();
    } else
      return (TypeVariable<Constructor<T>>[]) new TypeVariable[0];
  }
  

  default ConstructorRepository getGenericInfo0() {
    ConstructorRepository genericInfo = getCachedGenericInfo();
    if(genericInfo == null) {
      genericInfo = ConstructorRepository.make(getSignature(), getFactory());
      setCachedGenericInfo(genericInfo);
    }
    return genericInfo;
  }

  default GenericsFactory getFactory() {
    Constructor<?> thisRef = getConstructor();
    return CoreReflectionFactory.make(thisRef, ConstructorScope.make(thisRef));
  }
  
  default void handleParameterNumberMismatchInternal(int resultLength, int numParameters) {
    Class<?> declaringClass = getDeclaringClass();
    if (declaringClass.isEnum() ||
        declaringClass.isAnonymousClass() ||
        declaringClass.isLocalClass() )
        return ; // Can't do reliable parameter counting
    else {
        if (!declaringClass.isMemberClass() || // top-level
            // Check for the enclosing instance parameter for
            // non-static member classes
            (declaringClass.isMemberClass() &&
             ((declaringClass.getModifiers() & Modifier.STATIC) == 0)  &&
             resultLength + 1 != numParameters) ) {
            throw new AnnotationFormatError(
                      "Parameter annotations don't match number of parameters");
        }
    }
  }
}
