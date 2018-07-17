# embulk-encoder-command

Command encoder plugin for Embulk.

This plugin implement like this command.

```
$ ... | embulk-formatter-plugin | lzop -1 | embulk-output-plugin
                                  ^^^^^^^
                                  (here)
```

## Overview

* **Plugin type**: encoder

## Configuration

- **command**: execute command (string, required)

## Example

```yaml
out:
  type: file
  encoders:
    - type: command
      command: lzop -1
```

## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```
