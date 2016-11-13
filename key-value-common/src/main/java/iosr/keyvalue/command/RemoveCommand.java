package iosr.keyvalue.command;

import io.atomix.copycat.Command;

public class RemoveCommand implements Command<Void> {

    private final Object key;

    public RemoveCommand(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    //TODO check CompactionModes
}
