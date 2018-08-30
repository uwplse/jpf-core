package gov.nasa.jpf.util;

import java.nio.file.Path;

import gov.nasa.jpf.util.DynamicObjectArray;

public class PathRegistry {
  private DynamicObjectArray<Path> pathRegistry = new DynamicObjectArray<>();
  private int currIdx = 0;
  
  public Path getForIndex(int idx) {
    Path toReturn = pathRegistry.get(idx);
    if(toReturn == null) {
      throw new NullPointerException();
    }
    return toReturn;
  }
  
  public int register(Path p) {
    int idx;
    pathRegistry.set(idx = currIdx++, p);
    return idx;
  }
}
