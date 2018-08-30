package gov.nasa.jpf.vm;

import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.RunListener;
import gov.nasa.jpf.util.RunRegistry;
import gov.nasa.jpf.util.nio.JPFNIOConstants;

import static gov.nasa.jpf.util.nio.NioSafeRunner.*;

public class JPF_gov_nasa_jpf_nio_JPFFileSystem extends NativePeer implements JPFNIOConstants {
  
  private static FileSystem theFileSystem;

  public static boolean init(Config conf) {
    if(theFileSystem == null) {
      theFileSystem = FileSystems.getDefault();
      JPF_gov_nasa_jpf_nio_JPFPath.init(conf);
      RunRegistry.getDefaultRegistry().addListener( new RunListener() {
        @Override
        public void reset (RunRegistry reg){
          theFileSystem = null;
        }
      });
    }
    return true;
  }
  
  private interface IOLongSupplier {
    public long provide() throws IOException;
  }
  
  public long getSpaceField(IOLongSupplier supp) {
    try {
      return supp.provide();
    } catch(IOException e) {
      return -1;
    }
  }

  @MJI
  public int getFileStores0_____3Ljava_nio_file_FileStore_2 (MJIEnv env, int objRef) {
    return runSafeReference(env, JPF_FILE_STORE_CLASS, () -> {
      List<Integer> tmp = new ArrayList<Integer>();
      theFileSystem.getFileStores().forEach(fs -> {
        int ref = env.newObject(JPF_FILE_STORE_CLASS);
        env.setLongField(ref, JPF_STORE_TOTAL_SPACE, getSpaceField(fs::getTotalSpace));
        env.setLongField(ref, JPF_STORE_UNALLOCATED_SPACE, getSpaceField(fs::getUnallocatedSpace));
        env.setLongField(ref, JPF_STORE_USABLE_SPACE, getSpaceField(fs::getUsableSpace));
        tmp.add(ref);
      });
      return copyToJPFArray(env, tmp, JPF_FILE_STORE_CLASS);
    });
  }

  @MJI
  public int getRootDirectories0_____3Ljava_nio_file_Path_2 (MJIEnv env, int objRef) {
    return runSafeInteger(env, JPF_PATH_CLASS, () -> {
      ClassInfo ci = ClassLoaderInfo.getSystemResolvedClassInfo(JPF_PATH_CLASS);
      List<Integer> toReturn = new ArrayList<>();
      theFileSystem.getRootDirectories().forEach(p -> {
        int ref = env.newObject(ci);
        env.setIntField(objRef, JPF_PATH_NATIVE_FIELD, JPF_gov_nasa_jpf_nio_JPFPath.registry.register(p));
        env.setReferenceField(ref, JPF_PATH_ROOT_FIELD, objRef);
        toReturn.add(ref);
      });
      return copyToJPFArray(env, toReturn, JPF_PATH_CLASS);
    });
  }

  @MJI
  public int getSeparator____Ljava_lang_String_2 (MJIEnv env, int objRef) {
    return env.newString(theFileSystem.getSeparator());
  }
}
