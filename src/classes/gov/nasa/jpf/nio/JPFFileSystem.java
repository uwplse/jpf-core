package gov.nasa.jpf.nio;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JPFFileSystem extends FileSystem {

  @Override
  public void close() throws IOException {
  }
  
  private native FileStore[] getFileStores0();

  @Override
  public Iterable<FileStore> getFileStores() {
    return Arrays.asList(getFileStores0());
  }

  @Override
  public Path getPath(String arg0, String... arg1) {
    return new JPFPath(arg0, arg1, this);
  }

  @Override
  public PathMatcher getPathMatcher(String arg0) {
    throw new UnsupportedOperationException();
  }
  
  private native Path[] getRootDirectories0();

  @Override
  public Iterable<Path> getRootDirectories() {
    return Arrays.asList(getRootDirectories0());
  }

  @Override
  public native String getSeparator();

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public WatchService newWatchService() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileSystemProvider provider() {
    return new JPFFileSystemProvider();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    return Collections.emptySet();
  }

}
