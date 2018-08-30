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
import sun.reflect.generics.repository.ClassRepository;
import sun.reflect.generics.scope.ClassScope;

public interface ClassGenericHandler<T> {
  Type[] getInterfaces();
  boolean isInterface();
  Class<? super T> getSuperclass();
  Class<T> getSelf();
  String getGenericSignature0();
  void setCachedGenericInfo(ClassRepository genericInfo);
  ClassRepository getCachedGenericInfo();

  default public Type getGenericSuperclass0() {
    final ClassRepository info = getGenericInfo();
    if(info == null) {
      return getSuperclass();
    }

    if(isInterface()) {
      return null;
    }
    return info.getSuperclass();
  }

  default public Type[] getGenericInterfaces0() {
    final ClassRepository info = getGenericInfo();
    return (info == null) ? getInterfaces() : info.getSuperInterfaces();
  }

  default public GenericsFactory getFactory() {
    return CoreReflectionFactory.make(getSelf(), ClassScope.make(getSelf()));
  }
  
  @SuppressWarnings("unchecked")
  default public TypeVariable<Class<T>>[] getTypeParameters0() {
    final ClassRepository info = getGenericInfo();
    if(info != null)
      return (TypeVariable<Class<T>>[]) info.getTypeParameters();
    else
      return (TypeVariable<Class<T>>[]) new TypeVariable<?>[0];
  }

  
  default public ClassRepository getGenericInfo() {
    ClassRepository genericInfo = getCachedGenericInfo();
    if(genericInfo == null) {
      final String signature = getGenericSignature0();
      if(signature == null) {
        genericInfo = ClassRepository.NONE;
      } else {
        genericInfo = ClassRepository.make(signature, getFactory());
      }
      setCachedGenericInfo(genericInfo);
    }
    return (genericInfo != ClassRepository.NONE) ? genericInfo : null;
  }
}
