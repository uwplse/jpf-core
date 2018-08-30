package gov.nasa.jpf.vm;

import static gov.nasa.jpf.util.nio.NioSafeRunner.*;

import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.IOSupplier;
import gov.nasa.jpf.util.PathRegistry;
import gov.nasa.jpf.util.RunListener;
import gov.nasa.jpf.util.RunRegistry;
import gov.nasa.jpf.util.nio.JPFNIOConstants;

public class JPF_gov_nasa_jpf_nio_JPFPath extends NativePeer implements JPFNIOConstants {
  static PathRegistry registry;
  
  public static boolean init(Config v) {
    if(registry == null) {
      registry = new PathRegistry();
      RunRegistry.getDefaultRegistry().addListener( new RunListener() {
        @Override
        public void reset (RunRegistry reg){
          registry = null;
        }
      });
    }
    return true;
  }
  
  static java.nio.file.Path getNativePath(MJIEnv env, int pathRef) {
    ClassInfo ci = env.getClassInfo(pathRef);
    if(!ci.getName().equals(JPF_PATH_CLASS)) {
      throw new IllegalArgumentException();
    }
    assert registry != null;
    return registry.getForIndex(env.getIntField(pathRef, JPF_PATH_NATIVE_FIELD));
  }

  @MJI
  public int loadPath0__Ljava_lang_String_2_3Ljava_lang_String_2__I (MJIEnv env, int clsObjRef, int rString0, int rString1) {
    return runSafeInteger(env, () -> {
      String s = env.getStringObject(rString0);
      String[] arr = env.getStringArrayObject(rString1);
      java.nio.file.Path p = FileSystems.getDefault().getPath(s, arr);
      return registry.register(p);
    });
  }

  @MJI
  public int compareTo__Ljava_nio_file_Path_2__I (MJIEnv env, int objRef, int rPath0) {
    return runSafeInteger(env, () -> getNativePath(env, objRef).compareTo(getNativePath(env, rPath0)));
  }

  @MJI
  public boolean endsWith__Ljava_lang_String_2__Z (MJIEnv env, int objRef, int rString0) {
    return runSafeBoolean(env, () -> getNativePath(env, objRef).endsWith(env.getStringObject(rString0)));
  }

  @MJI
  public boolean endsWith__Ljava_nio_file_Path_2__Z (MJIEnv env, int objRef, int rPath0) {
    return runSafeBoolean(env, () -> getNativePath(env, objRef).endsWith(getNativePath(env, rPath0)));
  }

  @MJI
  public int getFileName____Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).getFileName());
  }

  public static int wrapNativePath(MJIEnv env, int pathRef, Path path) {
    int newRef = env.newObject(env.getClassInfo(pathRef));
    int fileSystemReference = env.getReferenceField(pathRef, JPF_PATH_ROOT_FIELD);
    env.setReferenceField(newRef, JPF_PATH_ROOT_FIELD, fileSystemReference);
    int nativeId = registry.register(path);
    env.setIntField(newRef, JPF_PATH_NATIVE_FIELD, nativeId);
    return newRef;
  }
  
  @MJI
  public int getURISpec____Ljava_lang_String_2 (MJIEnv env, int objRef) {
    return runSafeReference(env, () -> env.newString(getNativePath(env, objRef).toUri().toString()));
  }

  @MJI
  public boolean equals__Ljava_lang_Object_2__Z (MJIEnv env, int objRef, int rObject0) {
    if(rObject0 == MJIEnv.NULL) {
      return false;
    }
    if(!env.getClassInfo(rObject0).equals(env.getClassInfo(objRef))) {
      return false;
    }
    return runSafeBoolean(env, () -> getNativePath(env, objRef).equals(getNativePath(env, rObject0)));
  }

  @MJI
  public int hashCode____I (MJIEnv env, int objRef) {
    return runSafeInteger(env, () -> getNativePath(env, objRef).hashCode());
  }
  
  private static int runSafePath(MJIEnv env, int obj, IOSupplier<Path> supplier) {
    return runSafeReference(env, () -> wrapNativePath(env, obj, supplier.get()));
  }

  @MJI
  public int getName__I__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int v0) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).getName(0));
  }

  @MJI
  public boolean startsWith__Ljava_lang_String_2__Z (MJIEnv env, int objRef, int rString0) {
    return runSafeBoolean(env, () -> getNativePath(env, objRef).startsWith(env.getStringObject(rString0)));
  }

  @MJI
  public boolean startsWith__Ljava_nio_file_Path_2__Z (MJIEnv env, int objRef, int rPath0) {
    return runSafeBoolean(env, () -> getNativePath(env, objRef).startsWith(getNativePath(env, rPath0)));
  }


  @MJI
  public int getParent____Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).getParent());
  }

  @MJI
  public boolean isAbsolute____Z (MJIEnv env, int objRef) {
    return runSafeBoolean(env, () -> getNativePath(env, objRef).isAbsolute());
  }

  @MJI
  public int resolve__Ljava_lang_String_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rString0) {
    return runSafePath(env, objRef, () -> {
      return getNativePath(env, objRef).resolve(env.getStringObject(rString0));
    });
  }

  @MJI
  public int resolve__Ljava_nio_file_Path_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rPath0) {
    return runSafePath(env, objRef, () -> {
      return getNativePath(env, objRef).resolve(getNativePath(env, rPath0));
    });
  }

  @MJI
  public int getRoot____Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).getParent());
  }

  @MJI
  public int normalize____Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).normalize());
  }

  @MJI
  public int getNameCount____I (MJIEnv env, int objRef) {
    return runSafeInteger(env, () -> getNativePath(env, objRef).getNameCount());
  }

  @MJI
  public int subpath__II__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int v0, int v1) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).subpath(v0, v1));
  }

  @MJI
  public int resolveSibling__Ljava_lang_String_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rString0) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).resolveSibling(env.getStringObject(rString0)));
  }

  @MJI
  public int resolveSibling__Ljava_nio_file_Path_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rPath0) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).resolveSibling(getNativePath(env, rPath0)));
  }

  @MJI
  public int relativize__Ljava_nio_file_Path_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rPath0) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).relativize(getNativePath(env, rPath0)));
  }

  @MJI
  public int toAbsolutePath____Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafePath(env, objRef, () -> getNativePath(env, objRef).toAbsolutePath());
  }

  @MJI
  public int toRealPath___3Ljava_nio_file_LinkOption_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rLinkOption0) {
    return runSafePath(env, objRef, () ->{
      LinkOption[] opts = JPF_gov_nasa_jpf_nio_JPFFileSystemProvider.liftLinkOptions(env, rLinkOption0);
      return getNativePath(env, objRef).toRealPath(opts);
    });
  }
  
  @MJI
  public int toString____Ljava_lang_String_2 (MJIEnv env, int objRef) {
    return runSafeReference(env, () -> env.newString(getNativePath(env, objRef).toString()));
  }
}
