package gov.nasa.jpf.nio;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class JPFBasicFileAttributes implements BasicFileAttributes {
  private long creationTime, lastAccessTime, lastModifiedTime, size;
  private int statusFlag;

  @Override
  public FileTime creationTime() {
    return FileTime.fromMillis(creationTime);
  }

  @Override
  public Object fileKey() {
    return this;
  }

  @Override
  public boolean isDirectory() {
    return (statusFlag & 0x2) != 0; 
  }

  @Override
  public boolean isOther() {
    return (statusFlag & 0x4) != 0;
  }

  @Override
  public boolean isRegularFile() {
    return (statusFlag & 0x1) != 0;
  }

  @Override
  public boolean isSymbolicLink() {
    return (statusFlag & (0x8)) != 0;
  }

  @Override
  public FileTime lastAccessTime() {
    return FileTime.fromMillis(lastAccessTime);
  }

  @Override
  public FileTime lastModifiedTime() {
    return FileTime.fromMillis(lastModifiedTime);
  }

  @Override
  public long size() {
    return size;
  }

}
