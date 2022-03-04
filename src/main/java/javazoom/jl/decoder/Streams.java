package javazoom.jl.decoder;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Streams extends InputStream {

    public  AtomicInteger len = new AtomicInteger(0);
    public byte buf[];
    public AtomicBoolean flag = new AtomicBoolean(false);

    public int pos;


    protected int mark = 0;


    protected int count;

    public Streams(byte buf[]) {
        this.buf = buf;
        this.pos = 0;
        len.set(buf.length);
        this.count = buf.length;
        flag.set(false);
    }

    public Streams() {
        this.buf = new byte[1152];
        this.pos = 0;
        len.set(0);
        this.count = buf.length;
    }

    public Streams(byte buf[], int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.mark = offset;
    }

    public void closeStream() {
        flag.set(true);
    }

    public void addBuffers(ByteArrayInputStream in) {
        int r = 0;
        int i = len.get();
        ;
        while ((r = in.read()) != -1) {

            growBuffers(i);
            this.buf[i] = (byte) r;
            i += 1;
            len.set(len.get() + 1);
        }

        this.count = len.get();
    }

    public synchronized void addBuffers(byte[] in) {
        int r = 0;
        int i = len.get();
        while (r < in.length) {
            growBuffers(i);
            this.buf[i] = in[r++];
            i += 1;
            len.set(len.get() + 1);
        }

        this.count = len.get();
    }

    public synchronized void growBuffers(int row) {
        if (row == this.buf.length) {
            byte[] temp = new byte[this.buf.length + 144];
            for (int i = 0; i < this.buf.length; i++) {
                temp[i] = this.buf[i];
            }
            this.buf = temp;
        }

    }

    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public synchronized long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }


    public synchronized int available() {
        return count - pos;
    }


    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }


    public synchronized void reset() {
        pos = mark;
    }

    public void close() throws IOException {
    }

}
