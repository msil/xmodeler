package uk.ac.mdx.xmf.swt.diagram.clipboard;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.internal.win32.BITMAPINFOHEADER;

class PrependWinBMPHeaderFilterInputStream extends InputStream {

	public static final int BITMAPFILEHEADER_SIZEOF = 14;
	private final InputStream in;
	private final byte[] buffer;
	private int index = 0;

	public PrependWinBMPHeaderFilterInputStream(InputStream dibStream) {
		// defined as 54
		final int offset = PrependWinBMPHeaderFilterInputStream.BITMAPFILEHEADER_SIZEOF
				+ BITMAPINFOHEADER.sizeof;

		this.in = dibStream;
		this.buffer = new byte[PrependWinBMPHeaderFilterInputStream.BITMAPFILEHEADER_SIZEOF];

		// see BITMAPFILEHEADER in windows documentation
		this.buffer[0] = 'B';
		this.buffer[1] = 'M';
		// write the offset in byte format
		// this.buffer[10] = offset;
	}

	public int read() throws IOException {
		if (this.index < this.buffer.length) {
			return 0xff & this.buffer[this.index++];
		}
		return this.in.read();
	}
}
