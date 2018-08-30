package gov.nasa.jpf.vm;

import static gov.nasa.jpf.util.nio.NioSafeRunner.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.RunListener;
import gov.nasa.jpf.util.RunRegistry;
import gov.nasa.jpf.util.nio.JPFNIOConstants;

public class JPF_gov_nasa_jpf_nio_JPFFileSystemProvider extends NativePeer implements JPFNIOConstants {
  
  static FileSystemProvider theFileSystemProvider;
  
  public static boolean init(Config conf) {
    if(theFileSystemProvider == null) {
      theFileSystemProvider = FileSystems.getDefault().provider();
      JPF_gov_nasa_jpf_nio_JPFPath.init(conf);
      RunRegistry.getDefaultRegistry().addListener( new RunListener() {
        @Override
        public void reset (RunRegistry reg){
          theFileSystemProvider = null;
        }
      });
    }
    return true;
  }
  
  private final static String[] BASIC_ATTRIBUTE_NAMES = {
    "lastAccessTime",
    "creationTime",
    "lastModifiedTime",
    "size",
    "isDirectory",
    "isSymbolicLink",
    "isOther",
    "isRegularFile",
  };
  
  @MJI
  public int readAttributesNative__Ljava_nio_file_Path_2Ljava_lang_String_2_3Ljava_nio_file_LinkOption_2___3Ljava_lang_Object_2 (
      MJIEnv env, int objRef, int rPath0, int rString1, int rLinkOption2) {
    return runSafeReference(env, () -> {
      Map<String, Object> read = theFileSystemProvider.readAttributes(getNativePath(env, rPath0), env.getStringObject(rString1), liftLinkOptions(env, rLinkOption2));
      
      Map<String, Integer> filtered = new HashMap<>();
      for(String str : BASIC_ATTRIBUTE_NAMES) {
        if(!read.containsKey(str)) {
          continue;
        }
        Object v = read.get(str);
        if(str.endsWith("Time")) {
          filtered.put(str, env.newLong(((FileTime)v).toMillis()));
        } else if(str.startsWith("is")) {
          filtered.put(str, env.newBoolean((boolean) v));
        } else {
          assert str.equals("size");
          filtered.put(str, env.newLong((long) v));
        }
      }
      if(filtered.size() == 0) {
        env.throwException("java.lang.IllegalArgumentException");
        return -1;
      }
      int toReturn = env.newObjectArray("java.lang.Object", filtered.size() * 2);
      int idx = 0;
      for(Map.Entry<String, Integer> kv : filtered.entrySet()) {
        env.setReferenceArrayElement(toReturn, idx, env.newString(kv.getKey()));
        env.setReferenceArrayElement(toReturn, idx + 1, kv.getValue());
        idx += 2;
      }
      return toReturn;
    });
  }

  @MJI
  public int readBasicAttributes0__Ljava_nio_file_Path_2_3Ljava_nio_file_LinkOption_2__Ljava_nio_file_attribute_BasicFileAttributes_2 (
      MJIEnv env, int objRef, int rPath0, int rLinkOption1) {
    return runSafeReference(env, BASIC_ATTRIBUTE_CLASS, () -> {
      int ref = env.newObject(BASIC_ATTRIBUTE_CLASS);
      BasicFileAttributes basic = theFileSystemProvider.readAttributes(getNativePath(env, rPath0), BasicFileAttributes.class, liftLinkOptions(env, rLinkOption1));
      int statusFlag = (basic.isRegularFile() ? 1 : 0) | (basic.isDirectory() ? 0x2 : 0) | (basic.isOther() ? 0x4 : 0) | (basic.isSymbolicLink() ? 0x8 : 0);
      env.setIntField(ref, "statusFlag", statusFlag);
      env.setLongField(ref, "size", basic.size());
      env.setLongField(ref, "creationTime", basic.creationTime().toMillis());
      env.setLongField(ref, "lastAccessTime", basic.lastAccessTime().toMillis());
      env.setLongField(ref, "lastModifiedTime", basic.lastModifiedTime().toMillis());
      return ref;
    });
  }
  
  private static LinkOption[] EMPTY_LINK_OPTIONS = new LinkOption[0];
  private static AccessMode[] EMPTY_ACCESS_OPTIONS = new AccessMode[0];

  static LinkOption[] liftLinkOptions(MJIEnv env, int rLinkOption1) {
    int len = env.getArrayLength(rLinkOption1);
    if(len == 0) {
      return EMPTY_LINK_OPTIONS;
    }
    LinkOption[] toReturn = new LinkOption[len];
    for(int i = 0; i < len; i++) {
      int enumRef = env.getReferenceArrayElement(rLinkOption1, i);
      if(enumRef == MJIEnv.NULL) {
        continue;
      }
      int ordinal = env.getIntField(enumRef, "ordinal");
      toReturn[i] = LinkOption.values()[ordinal];
    }
    return toReturn;
  }
  
  static AccessMode[] liftAccessMode(MJIEnv env, int rAccessMode1) {
    int len = env.getArrayLength(rAccessMode1);
    if(len == 0) {
      return EMPTY_ACCESS_OPTIONS;
    }
    AccessMode[] toReturn = new AccessMode[len];
    for(int i = 0; i < len; i++) {
      int enumRef = env.getReferenceArrayElement(rAccessMode1, i);
      if(enumRef == MJIEnv.NULL) {
        continue;
      }
      int ordinal = env.getIntField(enumRef, "ordinal");
      toReturn[i] = AccessMode.values()[ordinal];
    }
    return toReturn;
  }

  @MJI
  public int rawDirectoryStream0__Lgov_nasa_jpf_nio_JPFPath_2___3Ljava_nio_file_Path_2 (
      MJIEnv env, int objRef, int rJPFPath0) {
    return runSafeReference(env, JPF_PATH_CLASS, () -> {
      try(DirectoryStream<Path> stream = theFileSystemProvider.newDirectoryStream(getNativePath(env, rJPFPath0), null)) {
        List<Integer> references = new ArrayList<>();
        ClassInfo providerCI = env.getClassInfo(objRef);
        assert providerCI.getClassLoaderInfo().equals(ClassLoaderInfo.getCurrentSystemClassLoader());
        ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(JPF_PATH_CLASS);
        int rootSystemRef = env.getStaticReferenceField(providerCI, "theFileSystem");
        stream.forEach(p -> {
          int ref = env.newObject(ci);
          env.setReferenceField(ref, JPF_PATH_ROOT_FIELD, rootSystemRef);
          env.setIntField(ref, JPF_PATH_NATIVE_FIELD, JPF_gov_nasa_jpf_nio_JPFPath.registry.register(p));
          references.add(ref);
        });
        return copyToJPFArray(env, references, JPF_PATH_CLASS);
      }
    });
  }
  
  static java.nio.file.Path getNativePath(MJIEnv env, int ref) {
    return JPF_gov_nasa_jpf_nio_JPFPath.getNativePath(env, ref);
  }

  @MJI
  public boolean isSameFile__Ljava_nio_file_Path_2Ljava_nio_file_Path_2__Z (
      MJIEnv env, int objRef, int rPath0, int rPath1) {
    return runSafeBoolean(env, () -> {
      return theFileSystemProvider.isSameFile(getNativePath(env, rPath0), getNativePath(env, rPath1));
    });
  }

  @MJI
  public void checkAccess__Ljava_nio_file_Path_2_3Ljava_nio_file_AccessMode_2__V (
      MJIEnv env, int objRef, int rPath0, int rAccessMode1) {
    runSafe(env, () -> {
      theFileSystemProvider.checkAccess(getNativePath(env, objRef), liftAccessMode(env, rAccessMode1));
      return null;
    }, (Void)null);
  }

  @MJI
  public int getPath0__Ljava_lang_String_2__Ljava_nio_file_Path_2 (MJIEnv env, int objRef, int rString0) {
    return runSafeReference(env, JPF_PATH_CLASS, () -> {
      int ref = getNewJPFPath(env, objRef);
      Path toReturn;
      try {
        toReturn = theFileSystemProvider.getPath(new URI(env.getStringObject(rString0)));
      } catch (URISyntaxException e) {
        env.throwException(URISyntaxException.class.getName());
        return -1;
      }
      env.setIntField(ref, JPF_PATH_NATIVE_FIELD, JPF_gov_nasa_jpf_nio_JPFPath.registry.register(toReturn));
      return ref;
    });
  }

  private int getNewJPFPath(MJIEnv env, int objRef) {
    ClassInfo providerCI = env.getClassInfo(objRef);
    assert providerCI.getClassLoaderInfo().equals(ClassLoaderInfo.getCurrentSystemClassLoader());
    ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(JPF_PATH_CLASS);
    int rootSystemRef = env.getStaticReferenceField(providerCI, "theFileSystem");
    int ref = env.newObject(ci);
    env.setReferenceField(ref, JPF_PATH_ROOT_FIELD, rootSystemRef);
    return ref;
  }
  
  @MJI
  public boolean isHidden__Ljava_nio_file_Path_2__Z (MJIEnv env, int objRef, int rPath0) {
    return runSafeBoolean(env, () -> {
      return theFileSystemProvider.isHidden(getNativePath(env, rPath0));
    });
  }
}
