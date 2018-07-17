package org.embulk.encoder.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilterOutputStream;

import com.google.common.base.Optional;

import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigInject;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskSource;
import org.embulk.spi.BufferAllocator;
import org.embulk.spi.EncoderPlugin;
import org.embulk.spi.FileOutput;
import org.embulk.spi.util.FileOutputOutputStream;
import org.embulk.spi.util.OutputStreamFileOutput;

public class CommandEncoderPlugin
        implements EncoderPlugin
{

    public interface PluginTask
            extends Task
    {
        @Config("command")
        public String getCommand();

        @ConfigInject
        public BufferAllocator getBufferAllocator();
    }

    @Override
    public void transaction(ConfigSource config, EncoderPlugin.Control control)
    {
        PluginTask task = config.loadConfig(PluginTask.class);

        control.run(task.dump());
    }

    @Override
    public FileOutput open(TaskSource taskSource, FileOutput fileOutput)
    {
        final PluginTask task = taskSource.loadTask(PluginTask.class);

        final FileOutputOutputStream output = new FileOutputOutputStream(fileOutput,
           task.getBufferAllocator(), FileOutputOutputStream.CloseMode.FLUSH);

        return new OutputStreamFileOutput(new OutputStreamFileOutput.Provider() {
           public OutputStream openNext() throws IOException
           {
               output.nextFile();
               return new CommandOutputStream(output, task.getCommand());
           }

           public void finish() throws IOException
           {
               output.finish();
           }

           public void close() throws IOException
           {
               output.close();
           }
        });
    }
}
