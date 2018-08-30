package gov.nasa.jpf.nio;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class JPFFileSystemProvider extends FileSystemProvider {
  private static JPFFileSystem theFileSystem = new JPFFileSystem();
  
  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
    throw new ReadOnlyFileSystemException();
  }
  
  private native Object[] readAttributesNative(Path path, String attributes, LinkOption... options) throws IOException;

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    if(attributes.contains(":")) {
      if(!attributes.startsWith("basic:")) {
        throw new UnsupportedOperationException();
      }
    }
    Map<String, Object> toReturn = new HashMap<>();
    Object[] obj = readAttributesNative(path, attributes, options);
    assert obj.length % 2 == 0;
    for(int i = 0; i < obj.length; i+=2) {
      assert obj[i] instanceof String;
      String k = (String) obj[i];
      if(k.endsWith("Time")) {
        toReturn.put(k, FileTime.fromMillis((long) obj[i + 1]));
      } else {
        toReturn.put(k, obj[i+1]);
      }
    }
    return toReturn;
  }
  
  private native BasicFileAttributes readBasicAttributes0(Path path, LinkOption... options) throws IOException;

  @SuppressWarnings("unchecked")
  @Override
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
    if(type != BasicFileAttributes.class) {
      throw new UnsupportedOperationException();
    }
    return (A) readBasicAttributes0(path, options);
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    throw new FileSystemAlreadyExistsException();
  }
  
  private native Path[] rawDirectoryStream0(JPFPath dir); 
  
  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, final Filter<? super Path> filter) throws IOException {
    final Path[] directoryStr = rawDirectoryStream0(toJPFPath(dir));
    return new DirectoryStream<Path>() {
      @Override
      public void close() throws IOException {
      }
      
      @Override
      public Iterator<Path> iterator() {
        return new Iterator<Path>() {
          int idx = 0;
          
          {
            findNext();
          }
          
          @Override
          public Path next() {
            if(idx < directoryStr.length) {
              Path toReturn = directoryStr[idx++];
              findNext();
              return toReturn;
            }
            throw new NoSuchElementException();
          }
          
          private void findNext() {
            while(idx < directoryStr.length) {
              Path p = directoryStr[idx];
              if(accept(p)) {
                return;
              }
              idx++;
            }
          }
          
          private boolean accept(Path p) {
            try {
              if(filter == null || filter.accept(p)) {
                return true;
              }
              return false;
            } catch (IOException e) {
              return false;
            }
          }

          @Override
          public boolean hasNext() {
            return idx < directoryStr.length;
          }
        };
      }
    };
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    if(options.contains(StandardOpenOption.APPEND) || options.contains(StandardOpenOption.WRITE)) {
      throw new ReadOnlyFileSystemException();
    }
    return new JPFReadOnlyByteChannel(toJPFPath(path), options);
  }

  private static JPFPath toJPFPath(Path path) {
    if(path == null) {
      throw new NullPointerException();
    }
    if(!(path instanceof JPFPath)) {
      throw new ProviderMismatchException();
    }
    return (JPFPath) path;
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    throw new ReadOnlyFileSystemException();
  }

  @Override
  public native boolean isSameFile(Path path, Path path2) throws IOException;

  @Override
  public native boolean isHidden(Path path) throws IOException;

  @Override
  public String getScheme() {
    return "file";
  }

  @Override
  public Path getPath(URI uri) {
    return getPath0(uri.toString());
  }
  
  public native Path getPath0(String uri);

  @Override
  public FileSystem getFileSystem(URI uri) {
    if(uri.toString().equals("file:///")) {
      return theFileSystem;
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public native FileStore getFileStore(Path path) throws IOException;

  @SuppressWarnings("unchecked")
  @Override
  public <V extends FileAttributeView> V getFileAttributeView(final Path path, Class<V> type, final LinkOption... options) {
    if(type == BasicFileAttributeView.class) {
      return (V) new BasicFileAttributeView() {
        
        @Override
        public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
          throw new ReadOnlyFileSystemException();
        }
        
        @Override
        public BasicFileAttributes readAttributes() throws IOException {
          return readBasicAttributes0(path, options);
        }
        
        @Override
        public String name() {
          return "basic";
        }
      };
    }
    return null;
  }

  @Override
  public void delete(Path path) throws IOException {
    throw new ReadOnlyFileSystemException();
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    throw new ReadOnlyFileSystemException();
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    throw new ReadOnlyFileSystemException();
  }

  @Override
  public native void checkAccess(Path path, AccessMode... modes) throws IOException;
}