package org.embulk.encoder.command;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.google.common.io.Resources;

import org.embulk.encoder.command.CommandOutputStream;

public class TestCommandEncoderPlugin
{
    @Test
    public void checkLzop() throws IOException, InterruptedException
    {
        File file = null;
        try {
            Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "prefix", ".suffix");
            file = tmpPath.toFile();
            FileOutputStream out = new FileOutputStream(file);
            CommandOutputStream cmd = new CommandOutputStream(out, "lzop -1q");
            byte[] buf = "aaa\nbbb\nccc\n".getBytes();
            cmd.write(buf);
            cmd.close();
            String path = tmpPath.toString();
            String[] cmdary = {"lzop", "-t", path};
            Process process = Runtime.getRuntime().exec(cmdary);
            process.waitFor();
            assertEquals(process.exitValue(), 0);
        }
        finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }
}
