package gov.nasa.jpf.util.nio;

import java.util.List;

import gov.nasa.jpf.vm.MJIEnv;

public interface JPFNIOConstants {
  public static final String JPF_PATH_CLASS = "gov.nasa.jpf.nio.JPFPath";
  public static final String JPF_PATH_ROOT_FIELD = "rootSystem";
  public static final String JPF_PATH_NATIVE_FIELD = "nativeIdx";
  
  public static final String BASIC_ATTRIBUTE_CLASS = "gov.nasa.jpf.nio.JPFBasicFileAttributes";
  
  public static final String JPF_FILE_STORE_CLASS = "gov.nasa.jpf.nio.JPFFileStore";
  
  public static final String JPF_STORE_TOTAL_SPACE = "totalSpace";
  public static final String JPF_STORE_UNALLOCATED_SPACE = "unallocatedSpace";
  public static final String JPF_STORE_USABLE_SPACE= "usableSpace";
  
  public static final String BB_OFFSET_FIELD = "offset";
  public static final String BB_BUFFER_FIELD = "hb";
  public static final String BB_POSITION_FIELD = "position";
  public static final String BYTE_CHANNEL_PATH_FIELD = "nativePath";
  public static final String BYTE_CHANNEL_POSITION_FIELD = "position";
  public static final String BYTE_CHANNEL_NATIVE_FIELD = "nativeIdx";
  public static final String BYTE_CHANNEL_OPTIONS_FIELD = "openOptions";

  default int copyToJPFArray(MJIEnv env, List<Integer> refs, String hostClass) {
    int ref = env.newObjectArray(hostClass, refs.size());
    for(int i = 0; i < refs.size(); i++) {
      env.setReferenceArrayElement(ref, i, refs.get(i));
    }
    return ref;
  };
}
