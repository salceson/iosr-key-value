package iosr.keyvalue.query;

import io.atomix.copycat.Query;

public class GetQuery implements Query<Object> {

    private final Object key;

    public GetQuery(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    //TODO check ConsistencyLevels

}
