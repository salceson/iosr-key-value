package iosr.keyvalue.command;

import io.atomix.copycat.Command;

public class PutCommand implements Command<Object> {

    private final Object key;
    private final Object value;

    public PutCommand(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    //TODO check CompactionModes
}
