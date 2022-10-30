package io.varhttp;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class VarWrappedInputStream extends FilterInputStream {
    private InputStream innerStream;
    private static Field field;

    static {
        try {
            field = FilterInputStream.class.getDeclaredField("in");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public VarWrappedInputStream(InputStream innerStream) {
        super(unwrap(innerStream));

        int i =0;
    }

    private static InputStream unwrap(InputStream innerStream) {
        if (innerStream instanceof FilterInputStream) {
            try {
                InputStream in = (InputStream) field.get(innerStream);
                InputStream in2 = (InputStream) field.get(in);
                return in2;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return innerStream;
    }

    @Override
    public void close() throws IOException {
//        super.close();
        int i = 0;
    }
}
