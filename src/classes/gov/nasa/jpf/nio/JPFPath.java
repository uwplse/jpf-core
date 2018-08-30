package gov.nasa.jpf.nio;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class JPFPath implements Path {
  
  private int nativeIdx;
  private JPFFileSystem rootSystem;

  public JPFPath(String base, String[] arg, JPFFileSystem rootSystem) {
    this.nativeIdx = loadPath0(base, arg);
    this.rootSystem = rootSystem;
  }

  private native static int loadPath0(String base, String[] arg);

  @Override
  public native int compareTo(Path other);

  @Override
  public native boolean endsWith(Path other);

  @Override
  public native boolean endsWith(String other);

  @Override
  public native Path getFileName();

  @Override
  public FileSystem getFileSystem() {
    return rootSystem;
  }

  @Override
  public native Path getName(int index);

  @Override
  public native int getNameCount();

  @Override
  public native Path getParent();

  @Override
  public native Path getRoot();

  @Override
  public native boolean isAbsolute();

  @Override
  public Iterator<Path> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public native Path normalize();

  @Override
  public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public native Path relativize(Path other);

  @Override
  public native Path resolve(Path other);

  @Override
  public native Path resolve(String other);

  @Override
  public native Path resolveSibling(Path other);

  @Override
  public native Path resolveSibling(String other);

  @Override
  public native boolean startsWith(Path other);

  @Override
  public native boolean startsWith(String other);

  @Override
  public native Path subpath(int beginIndex, int endIndex);

  @Override
  public native Path toAbsolutePath();

  @Override
  public File toFile() {
    return new File(toString());
  }

  @Override
  public native Path toRealPath(LinkOption... options) throws IOException;
  
  private native String getURISpec();

  @Override
  public URI toUri() {
    try {
      return new URI(getURISpec());
    } catch (URISyntaxException x){
      return null;
    }
  }

  @Override
  public native boolean equals(Object other);
  
  @Override
  public native int hashCode();
  
  @Override
  public native String toString();
}
