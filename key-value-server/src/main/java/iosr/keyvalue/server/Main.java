package iosr.keyvalue.server;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import iosr.keyvalue.command.PutCommand;
import iosr.keyvalue.command.RemoveCommand;
import iosr.keyvalue.query.GetQuery;
import iosr.keyvalue.statemachine.StoreStateMachine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            throw new IllegalArgumentException("args: <server host:port> <member1 host:port> <member2 host:port> ...");
        }

        // Parse the address to which to bind the server.
        final String[] serverInfo = args[0].split(":");
        final Address address = new Address(serverInfo[0], Integer.valueOf(serverInfo[1]));

        // Build a list of all member addresses to which to connect.
        final List<Address> members = new ArrayList<>();
        for (String arg : args) {
            final String[] memberInfo = arg.split(":");
            members.add(new Address(memberInfo[0], Integer.valueOf(memberInfo[1])));
        }

        Thread.sleep(1000 + new Random().nextInt(5000));

        final CopycatServer server = CopycatServer.builder(address)
                .withStateMachine(StoreStateMachine::new)
                .withTransport(NettyTransport.builder()
                        .withThreads(4)
                        .build()
                )
                .withElectionTimeout(Duration.ofMillis(500))
                .withSessionTimeout(Duration.ofSeconds(10))
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
