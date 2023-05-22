package io.varhttp;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class VarServletOutputStream extends ServletOutputStream {

	public final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	public final PrintWriter printWriter = new PrintWriter(outputStream);

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public void write(int b) throws IOException {
		outputStream.write(b);
	}
}
