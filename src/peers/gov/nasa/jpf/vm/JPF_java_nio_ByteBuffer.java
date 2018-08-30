package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

public class JPF_java_nio_ByteBuffer extends NativePeer {
  @MJI
  public int allocateDirect__I__Ljava_nio_ByteBuffer_2(MJIEnv env, int clsObjRef, int size) { 
    ClassInfo ci = env.getReferredClassInfo(clsObjRef);
    if(ci.initializeClass(env.getThreadInfo())) {
      env.repeatInvocation();
      return MJIEnv.NULL;
    }
    MethodInfo mi = ci.getMethod("allocate(I)Ljava/nio/ByteBuffer;", false);
    assert mi != null : ci;
    assert mi.isStatic() : mi;
    
    ThreadInfo ti = env.getThreadInfo();
    DirectCallStackFrame frame = ti.getReturnedDirectCall();
    if(frame == null) {
     frame = mi.createDirectCallStackFrame(ti, 0);
     frame.setArgument(0, size, null);
     ti.pushFrame(frame);;
     return MJIEnv.NULL;
    } else {
      Object attr = frame.getResultAttr();
      int ret = frame.getReferenceResult();
      env.setReturnAttribute(attr);
      return ret;
    }
  }
}
