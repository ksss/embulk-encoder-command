Embulk::JavaPlugin.register_encoder(
  "command", "org.embulk.encoder.command.CommandEncoderPlugin",
  File.expand_path('../../../../classpath', __FILE__))
