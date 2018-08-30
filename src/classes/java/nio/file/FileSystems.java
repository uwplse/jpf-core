package java.nio.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.util.Map;

import gov.nasa.jpf.nio.JPFFileSystemProvider;

public final class FileSystems {
  private FileSystems() {
  }

  private static class DefaultFileSystemHolder {
    static JPFFileSystemProvider fsp = new JPFFileSystemProvider();
    static final FileSystem defaultFileSystem = defaultFileSystem();

    private static FileSystem defaultFileSystem() {
      return fsp.getFileSystem(URI.create("file:///"));
    }

  }
  
  public static FileSystem newFileSystem(Path path,
     ClassLoader loader) throws IOException {
    throw new ProviderNotFoundException();
  }
  
  public static FileSystem getDefault() {
    return DefaultFileSystemHolder.defaultFileSystem;
  }
  
  public static FileSystem newFileSystem(URI uri, Map<String,?> env, ClassLoader loader)
      throws IOException {
    throw new ProviderNotFoundException();
  }
  
  public static FileSystem newFileSystem(URI uri, Map<String,?> env)
      throws IOException {
      return newFileSystem(uri, env, null);
  }
  
  public static FileSystem getFileSystem(URI uri) {
    throw new ProviderNotFoundException();
  }
}
