package gov.nasa.jpf.util.nio;

import java.io.IOException;

import gov.nasa.jpf.util.IOSupplier;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.MJIEnv;

public class NioSafeRunner {
  
  public static <V> V runSafe(MJIEnv env, IOSupplier<V> runner, V fail) {
    try {
      return runner.get();
    } catch(NullPointerException npe) {
      env.throwException("java.lang.NullPointerException");
      return fail;
    } catch(IOException e) {
      env.throwException("java.io.IOException");
      return fail;
    } catch(UnsupportedOperationException e) {
      env.throwException("java.lang.UnsupportedOperationException");
      return fail;
    }
  }
  
  public static long runSafeLong(MJIEnv env, IOSupplier<Long> runner) {
    return runSafe(env, runner, -1L);
  }
  
  public static boolean runSafeBoolean(MJIEnv env, IOSupplier<Boolean> runner) {
    return runSafe(env, runner, false);
  }
  
  public static int runSafeInteger(MJIEnv env, IOSupplier<Integer> runner) {
    return runSafe(env, runner, -1);
  }
  
  public static int runSafeReference(MJIEnv env, IOSupplier<Integer> runner) {
    return runSafe(env, runner, -1);
  }
  
  public static int runSafeReference(MJIEnv env, String klass, IOSupplier<Integer> runner) {
    ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(klass);
    if(ci.initializeClass(env.getThreadInfo())) {
      env.repeatInvocation();
      return -1;
    }
    return runSafeReference(env, runner);
  }
  
  public static boolean runSafeBoolean(MJIEnv env, String klass, IOSupplier<Boolean> runner) {
    ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(klass);
    if(ci.initializeClass(env.getThreadInfo())) {
      env.repeatInvocation();
      return false;
    }
    return runSafeBoolean(env, runner);
  }
  
  public static int runSafeInteger(MJIEnv env, String klass, IOSupplier<Integer> runner) {
    ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(klass);
    if(ci.initializeClass(env.getThreadInfo())) {
      env.repeatInvocation();
      return -1;
    }
    return runSafeInteger(env, runner);
  }
}
