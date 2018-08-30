package gov.nasa.jpf.nio;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class JPFFileStore extends FileStore {
  
  private long totalSpace, unallocatedSpace, usableSpace;

  @Override
  public Object getAttribute(String attribute) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
    return null;
  }

  @Override
  public long getTotalSpace() {
    return totalSpace;
  }

  @Override
  public long getUnallocatedSpace() {
    return unallocatedSpace;
  }

  @Override
  public long getUsableSpace() {
    return usableSpace;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public String name() {
    return "JPF-STUB";
  }

  @Override
  public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
    return type == BasicFileAttributeView.class;
  }

  @Override
  public boolean supportsFileAttributeView(String name) {
    return name.equals("basic");
  }

  @Override
  public String type() {
    return "stub";
  }

}
