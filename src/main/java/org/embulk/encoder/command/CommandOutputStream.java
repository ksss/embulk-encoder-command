package org.embulk.encoder.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class CommandOutputStream extends OutputStream
{
    protected OutputStream file;
    protected Process process;
    protected String command;
    protected InputStream in;
    protected OutputStream out;
    protected InputStream err;
    protected ProviderThread th;

    public CommandOutputStream(OutputStream file, String command) throws IOException
    {
        this.file = file;
        this.command = command;
        String[] cmdary = {"sh", "-c", command};
        this.process = Runtime.getRuntime().exec(cmdary);
        this.in = process.getInputStream();
        this.out = process.getOutputStream();
        this.err = process.getErrorStream();
        this.th = new ProviderThread(this);
        this.th.start();
    }

    @Override
    public void write(int b) throws IOException
    {
        // Not implemented yet
        throw new IOException();
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        this.out.write(b, off, len);
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        this.out.close();
        try {
            this.th.join();
        }
        catch (InterruptedException ex) {
            System.err.println("InterruptedException");
        }
        this.in.close();
        this.err.close();
        this.file.close();
        try {
            this.process.waitFor();
        }
        catch (InterruptedException ex) {
            System.err.println("InterruptedException");
        }
        if (this.process.exitValue() != 0) {
            byte[] buf = new byte[512];
            int n = this.err.read(buf, 0, 512);
            System.out.println("command:'" + this.command + "' failed with status:" + this.process.exitValue());
            System.err.println(new String(buf, 0, n, "UTF-8"));
            throw new RuntimeException();
        }
    }

    static class ProviderThread extends Thread
    {

        protected CommandOutputStream parent;

        public ProviderThread(CommandOutputStream parent)
        {
            this.parent = parent;
        }

        @Override
        public void run()
        {
            try {
                fill();
            }
            // FIXME
            catch (IOException ex) {
                System.err.println("throwed IOException");
            }
            catch (InterruptedException ex) {
                System.err.println("throwed InterruptedException");
            }
            catch (RuntimeException ex) {
                System.err.println("throwed RuntimeException");
            }
        }

        private void fill() throws IOException, InterruptedException, RuntimeException
        {
            int n = 0;
            int size = 65536;
            byte[] buf = new byte[size];

            while ((n = this.parent.in.read(buf, 0, size)) != -1) {
                this.parent.file.write(buf, 0, n);
            }
        }
    }
}
