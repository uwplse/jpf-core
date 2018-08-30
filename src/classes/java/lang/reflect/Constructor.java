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

/**
 * (incomplete) support for consructor reflection
 * 
 * pretty stupid - this is almost identical to Method, but we can't derive,
 * and the delegation happens at the peer level anyways.
 * 
 * NOTE: 'regIdx' and 'name' need to be like Method, or the peer delegation
 * fails (this is the hack'ish part)
 * 
 * NOTE: we ditch the 'final' modifier so that we can provide our
 * own serialization ctor objects - that's probably going away
 * once we replace ObjectStreamClass
 */
public /*final*/ class Constructor <T> extends Executable implements Member, ConstructorGenericHandler<T> {
  
  protected int regIdx;
  protected String name;
  private ConstructorRepository genericInfo;

  @Override
  public native String getName();
  public native T newInstance (Object... args)
        throws IllegalAccessException, InvocationTargetException, InstantiationException;
  
  @Override
  public native int getModifiers();
  public native Class<?> getReturnType();
  public native Class<?>[] getParameterTypes();
  @Override
  public int getParameterCount() {
    return getParameterTypes().length;
  }
  
  @Override
  public native Class<?>[] getExceptionTypes();
  
  @Override
  public native Class<T> getDeclaringClass();
  
  @Override
  public native Annotation[] getAnnotations();
  @Override
  public native Annotation[] getDeclaredAnnotations();
  @Override
  public native <T extends Annotation> T getAnnotation( Class<T> annotationCls);
  public native Annotation[][] getParameterAnnotations();
  
  @Override
  public boolean isSynthetic () {
    return false;
  }
  
  @Override
  public native String toString();
  
  @Override
  public native boolean equals (Object obj);

  public boolean isVarArgs (){
    return (getModifiers() & Modifier.VARARGS) != 0;
  }

  @Override
  public native int hashCode ();

  public native String toGenericString ();
  
  @Override
  public AnnotatedType getAnnotatedReturnType() {
    return getAnnotatedReturnType0(getDeclaringClass());
  }
  
  @Override
  byte[] getAnnotationBytes() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  Executable getRoot() {
    return null;
  }

  
  @Override
  boolean hasGenericInformation() {
    return getSignature() != null;
  }
  
  public native String getSignature();

  
  @Override
  ConstructorRepository getGenericInfo() {
    return this.getGenericInfo0();
  }
  
  @Override
  public TypeVariable<?>[] getTypeParameters() {
    return this.getTypeParameters0();
  }
  
  @Override
  public ConstructorRepository getCachedGenericInfo() {
    return genericInfo;
  }
  
  @Override
  public void setCachedGenericInfo(ConstructorRepository info) {
    this.genericInfo = info;
  }
  
  @Override
  public Constructor<T> getConstructor() {
    return this;
  }
  
  @Override
  void handleParameterNumberMismatch(int arg0, int arg1) {
    this.handleParameterNumberMismatchInternal(arg0, arg1);
  }
  
  @Override
  void specificToGenericStringHeader(StringBuilder sb) {
    this.specificToGenericStringHeader0(sb);
  }
  
  @Override
  void specificToStringHeader(StringBuilder sb) {
    this.specificToStringHeader0(sb);
  }
}
