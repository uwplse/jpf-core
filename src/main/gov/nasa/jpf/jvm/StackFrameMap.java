package gov.nasa.jpf.jvm;

public class StackFrameMap {
  public final int[] offsets;
  public final byte[][] locals;
  public final byte[][] stack;

  public StackFrameMap(int[] offsets, byte[][] locals, byte[][] stack) {
    this.offsets = offsets;
    this.locals = locals;
    this.stack = stack;
  }
}
