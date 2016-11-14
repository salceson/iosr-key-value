package iosr.keyvalue.server;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import iosr.keyvalue.command.PutCommand;
import iosr.keyvalue.command.RemoveCommand;
import iosr.keyvalue.query.GetQuery;
import iosr.keyvalue.statemachine.StoreStateMachine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            throw new IllegalArgumentException("args: <storage path> <server host:port> <member1 host:port> <member2 host:port> ...");
        }

        // Parse the address to which to bind the server.
        final String[] serverInfo = args[1].split(":");
        final Address address = new Address(serverInfo[0], Integer.valueOf(serverInfo[1]));

        // Build a list of all member addresses to which to connect.
        final List<Address> members = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            final String[] memberInfo = args[i].split(":");
            members.add(new Address(memberInfo[0], Integer.valueOf(memberInfo[1])));
        }

        final CopycatServer server = CopycatServer.builder(address)
                .withStateMachine(StoreStateMachine::new)
                .withTransport(new NettyTransport())
                .withStorage(Storage.builder()
                        .withDirectory(args[0])
                        .withMaxSegmentSize(1024 * 1024 * 32)
                        .withMinorCompactionInterval(Duration.ofMinutes(1))
                        .withMajorCompactionInterval(Duration.ofMinutes(15))
                        .build())
                .build();

        server.serializer().register(PutCommand.class, 1);
        server.serializer().register(GetQuery.class, 2);
        server.serializer().register(RemoveCommand.class, 3);

        server.bootstrap(members).join();

        while (server.isRunning()) {
            Thread.sleep(1000);
        }
    }
}
