package io.varhttp;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WrappedServletInputStream extends ServletInputStream {
    private InputStream in;
    private ReadListener readListener;

    public WrappedServletInputStream(InputStream newInput) {
        this.in = newInput;
    }

    public InputStream getInnerStream() {
        return in;
    }
    public int read() throws IOException {
        return in.read();
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
    }
}
