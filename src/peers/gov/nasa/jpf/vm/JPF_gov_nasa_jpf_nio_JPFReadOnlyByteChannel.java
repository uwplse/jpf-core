package gov.nasa.jpf.vm;

import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.util.DynamicObjectArray;
import gov.nasa.jpf.util.nio.JPFNIOConstants;

import static gov.nasa.jpf.util.nio.NioSafeRunner.*;

public class JPF_gov_nasa_jpf_nio_JPFReadOnlyByteChannel extends NativePeer implements JPFNIOConstants {
  
  private static final OpenOption[] EMPTY_OPEN_OPTIONS = new OpenOption[0];
  private int count=0;
  private DynamicObjectArray<SeekableByteChannel> content;
  
  public JPF_gov_nasa_jpf_nio_JPFReadOnlyByteChannel (Config conf){
    JPF_gov_nasa_jpf_nio_JPFPath.init(conf);
    JPF_gov_nasa_jpf_nio_JPFFileSystemProvider.init(conf);
    content = new DynamicObjectArray<SeekableByteChannel>();
    count = 0;
  }

  private SeekableByteChannel getNativeByteChannel(MJIEnv env, int objRef) throws IOException {
    int slot = env.getIntField(objRef, BYTE_CHANNEL_NATIVE_FIELD);
    long position = env.getLongField(objRef, BYTE_CHANNEL_POSITION_FIELD);
    SeekableByteChannel chan = content.get(slot);
    if(chan == null) {
      SeekableByteChannel nativeChannel = openNativeChannel(env, objRef);
      content.set(slot, chan = nativeChannel);
    }
    chan.position(position);
    return chan;
  }

  private SeekableByteChannel openNativeChannel(MJIEnv env, int objRef) throws IOException {
    int pathRef = env.getReferenceField(objRef, BYTE_CHANNEL_PATH_FIELD);
    int nativePathSlot = env.getIntField(pathRef, JPF_PATH_NATIVE_FIELD);
    Path p = JPF_gov_nasa_jpf_nio_JPFPath.registry.getForIndex(nativePathSlot);
    OpenOption[] opts = liftOpenOptions(env, env.getReferenceField(objRef, BYTE_CHANNEL_OPTIONS_FIELD));
    Set<OpenOption> optSet = new HashSet<>();
    Arrays.stream(opts).forEach(optSet::add);
    SeekableByteChannel nativeChannel = JPF_gov_nasa_jpf_nio_JPFFileSystemProvider.theFileSystemProvider.newByteChannel(p, optSet);
    return nativeChannel;
  }
  
  private OpenOption[] liftOpenOptions(MJIEnv env, int optionArray) {
    int len = env.getArrayLength(optionArray);
    if(len == 0) { 
      return EMPTY_OPEN_OPTIONS;
    }
    OpenOption[] opt = new OpenOption[env.getArrayLength(optionArray)];
    for(int i = 0; i < len; i++) {
      int ref = env.getReferenceArrayElement(optionArray, i);
      ClassInfo ci = env.getClassInfo(ref);
      int ord = env.getIntField(ref, "ordinal");
      if(ci.getName().equals(LinkOption.class.getName())) {
        opt[i] = LinkOption.values()[ord];
      } else if(ci.getName().equals(StandardOpenOption.class.getName())) {
        opt[i] = StandardOpenOption.values()[ord];
      }
    }
    return opt;
  }

  @MJI
  public int readNative0__ILjava_nio_ByteBuffer_2__I (MJIEnv env, int objRef, int v0, int rByteBuffer1) {
    return runSafeInteger(env, () -> {
      SeekableByteChannel sb = getNativeByteChannel(env, objRef);
      
      int rawArrayRef = env.getReferenceField(rByteBuffer1, BB_BUFFER_FIELD);
      int offset = env.getIntField(rByteBuffer1, BB_OFFSET_FIELD);
      int targetPosition = env.getIntField(rByteBuffer1, BB_POSITION_FIELD);

      ArrayFields af = env.getModifiableElementInfo(rawArrayRef).getArrayFields();
      assert af instanceof ByteArrayFields;
      ByteArrayFields baf = (ByteArrayFields) af;
      byte[] underlyingBuffer = baf.values;
      int realPosition = offset + targetPosition;
      ByteBuffer wrapped = ByteBuffer.wrap(underlyingBuffer, realPosition, v0);
      int toReturn = sb.read(wrapped);
      env.setIntField(rByteBuffer1, BB_POSITION_FIELD, targetPosition + toReturn);
      env.setLongField(objRef, BYTE_CHANNEL_POSITION_FIELD, sb.position());
      return toReturn;
    });
  }

  @MJI
  public int readNativeSlow0__I___3B (MJIEnv env, int objRef, int sz) {
    return runSafeReference(env, () -> {
      SeekableByteChannel sb = getNativeByteChannel(env, objRef);
      ByteBuffer bb = ByteBuffer.allocate(sz);
      int read = sb.read(bb);
      return env.newByteArray(Arrays.copyOfRange(bb.array(), 0, read));
    });
  }

  @MJI
  public long size____J (MJIEnv env, int objRef) {
    return runSafeLong(env, () -> getNativeByteChannel(env, objRef).size());
  }

  @MJI
  public int open0____I (MJIEnv env, int objRef) {
    int slot = count++;
    return runSafeInteger(env, () -> {
      SeekableByteChannel chan = openNativeChannel(env, objRef);
      content.set(slot, chan);
      return slot;
    });
  }

  @MJI
  public void close0____V (MJIEnv env, int objRef) {
    runSafe(env, () -> {
      int slot = env.getIntField(objRef, BYTE_CHANNEL_NATIVE_FIELD);
      if(content.get(slot) != null) {
        content.get(slot).close();
        content.set(slot, null);
      }
      return null;
    }, (Void)null);
  }
}
