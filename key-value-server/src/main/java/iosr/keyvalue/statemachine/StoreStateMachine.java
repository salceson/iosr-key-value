package iosr.keyvalue.statemachine;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import iosr.keyvalue.command.PutCommand;
import iosr.keyvalue.command.RemoveCommand;
import iosr.keyvalue.query.GetQuery;

import java.util.HashMap;
import java.util.Map;

public class StoreStateMachine extends StateMachine {

    private final Map<Object, Object> store = new HashMap<>();

    public Object put(Commit<PutCommand> commit) {
        try {
            final PutCommand command = commit.operation();
            return store.put(command.getKey(), command.getValue());
        } finally {
            commit.close();
        }
    }

    public void remove(Commit<RemoveCommand> commit) {
        try {
            final RemoveCommand command = commit.operation();
            store.remove(command.getKey());
        } finally {
            commit.close();
        }
    }

    public Object get(Commit<GetQuery> commit) {
        try {
            final GetQuery query = commit.operation();
            return store.get(query.getKey());
        } finally {
            commit.close();
        }
    }

}
