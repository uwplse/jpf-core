/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package java.lang.reflect;

import java.lang.annotation.Annotation;

import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;

/**
 * minimal Method reflection support.
 * Note that we share peer code between Method and Constructor (which aren't
 * really different on the JPF side), so don't change field names!
 */
public final class Method extends Executable implements Member, MethodGenericHandler { 
  int regIdx; // the link to the corresponding MethodInfo
  String name; // deferred set by the NativePeer getName()
  private transient MethodRepository genericInfo;

  @Override
  public native String getName();
  public String toGenericString() {
	  // TODO: return real generic string
	  return toString();
  }
  public native Object invoke (Object object, Object... args)
        throws IllegalAccessException, InvocationTargetException;

  @Override
  public native int getModifiers();
  public native Class<?> getReturnType();
  public native Class<?>[] getParameterTypes();
  
  @Override
  public int getParameterCount() {
    return getParameterTypes().length;
  }
  
  public native Class<?>[] getExceptionTypes();

  @Override
  public native Class<?> getDeclaringClass();

  @Override
  public native Annotation[] getAnnotations();
  @Override
  public native Annotation[] getDeclaredAnnotations();
  @Override
  public native <T extends Annotation> T getAnnotation( Class<T> annotationCls);
  public native Annotation[][] getParameterAnnotations();

  @Override
  public boolean isSynthetic (){
    return Modifier.isSynthetic(getModifiers());
  }

  @Override
  public native String toString();

  // for Annotations - return the default value of the annotation member
  // represented by this method
  public native Object getDefaultValue();

  @Override
  public native boolean equals (Object obj);

  public boolean isVarArgs (){
    return (getModifiers() & Modifier.VARARGS) != 0;
  }

  @Override
  public native int hashCode ();

  public boolean isBridge (){
    return (getModifiers() & Modifier.BRIDGE) != 0;
  }
  
  @Override
  public AnnotatedType getAnnotatedReturnType() {
      return getAnnotatedReturnType0(getReturnType());
  }
  
  @Override
  byte[] getAnnotationBytes() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  Executable getRoot() {
    // we never actually copy
    return null;
  }

  @Override
  void handleParameterNumberMismatch(int resultLength, int numParameters) {
    this.handleParameterNumberMismatch0(resultLength, numParameters);
  }
  
  public native String getGenericSignature(); 
  
  @Override
  boolean hasGenericInformation() {
      return (getGenericSignature() != null);
  }
  
  @Override
  void specificToGenericStringHeader(StringBuilder sb) {
    this.specificToGenericStringHeader0(sb);
  }
  
  @Override
  void specificToStringHeader(StringBuilder sb) {
    this.specificToGenericStringHeader0(sb);
  }
  
  @Override
  public MethodRepository getCachedGenericInfo() {
    return genericInfo;
  }
  
  @Override
  public void setCachedGenericInfo(MethodRepository info) {
    genericInfo = info;
  }
  
  @Override
  public Method getMethod() {
    return this;
  }
  
  @Override
  public TypeVariable<?>[] getTypeParameters() {
    return this.getTypeParameters0();
  }
  
  @Override
  ConstructorRepository getGenericInfo() {
    return this.getGenericInfo0();
  }
  
  public Type getGenericReturnType() {
    return this.getGenericReturnType0();
  }
}
