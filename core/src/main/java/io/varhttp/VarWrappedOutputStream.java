package io.varhttp;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VarWrappedOutputStream extends FilterOutputStream {
    public VarWrappedOutputStream(OutputStream out) {
        super(out);
    }
}
