package gov.nasa.jpf.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Set;

public class JPFReadOnlyByteChannel implements SeekableByteChannel {

  private long position;
  private int state = 0;
  private JPFPath nativePath;
  private int nativeIdx;
  private OpenOption[] openOptions;

  public JPFReadOnlyByteChannel(Path path, Set<? extends OpenOption> options) {
    this.nativePath = (JPFPath) path;
    this.openOptions = options.toArray(new OpenOption[0]);
    this.nativeIdx = this.open0();
    state = 1;
  }

  private native int open0();

  @Override
  public void close() throws IOException {
    close0();
    state = 2;
  }

  private native void close0();

  @Override
  public boolean isOpen() {
    return state == 1;
  }

  @Override
  public long position() throws IOException {
    return this.position;
  }

  @Override
  public SeekableByteChannel position(long arg0) throws IOException {
    this.position = arg0;
    return this;
  }
  
  public native int readNative0(int remaining, ByteBuffer b) throws IOException;
  
  public native byte[] readNativeSlow0(int i) throws IOException;

  @Override
  public int read(ByteBuffer arg0) throws IOException {
    if(!isOpen()) {
      throw new ClosedChannelException();
    }
    if(arg0.getClass().getName().equals("java.nio.HeapByteBuffer")) {
      return readNative0(arg0.remaining(), arg0);
    } else {
      byte[] tmp = readNativeSlow0(arg0.remaining());
      arg0.put(tmp);
      return tmp.length;
    }
  }

  @Override
  public native long size() throws IOException;

  @Override
  public SeekableByteChannel truncate(long arg0) throws IOException {
    throw new NonWritableChannelException();
  }

  @Override
  public int write(ByteBuffer arg0) throws IOException {
    throw new NonWritableChannelException();
  }

}
