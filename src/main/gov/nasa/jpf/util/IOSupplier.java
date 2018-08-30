package gov.nasa.jpf.util;

import java.io.IOException;

public interface IOSupplier<V> {
  V get() throws IOException;
}
