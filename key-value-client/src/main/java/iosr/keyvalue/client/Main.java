package iosr.keyvalue.client;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.ConnectionStrategies;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.client.ServerSelectionStrategies;
import iosr.keyvalue.command.PutCommand;
import iosr.keyvalue.command.RemoveCommand;
import iosr.keyvalue.query.GetQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Main {

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new IllegalArgumentException("args: <member1 host:port> <member2 host:port> ...");

        // Build a list of all member addresses to which to connect.
        List<Address> members = new ArrayList<>();
        for (String arg : args) {
            String[] parts = arg.split(":");
            members.add(new Address(parts[0], Integer.valueOf(parts[1])));
        }

        Thread.sleep(15000);

        CopycatClient client = CopycatClient.builder()
                .withTransport(new NettyTransport())
                .withConnectionStrategy(ConnectionStrategies.FIBONACCI_BACKOFF)
                .withRecoveryStrategy(RecoveryStrategies.RECOVER)
                .withServerSelectionStrategy(ServerSelectionStrategies.ANY)
                .build();

        client.serializer().register(PutCommand.class, 1);
        client.serializer().register(GetQuery.class, 2);
        client.serializer().register(RemoveCommand.class, 3);

        LOGGER.info("Connecting to server...");
        client.connect(members).join();
        LOGGER.info("Connected");

        UUID key = UUID.randomUUID();

        LOGGER.info("Key: " + key);

        client.submit(new PutCommand(key, 0)).join();

        for (int i = 0; i < 10000; i++) {
            if (i % 10 == 0 || i == 999) {
                LOGGER.info(String.format("Performing %dth write...", i + 1));
            }
            Integer value = (Integer) client.submit(new GetQuery(key)).join();
            client.submit(new PutCommand(key, value + 1)).join();
        }

        Integer result = (Integer) client.submit(new GetQuery(key)).join();

        LOGGER.info("Final value: " + result);

        LOGGER.info("Closing...");
        client.close().join();
        LOGGER.info("Closed...");
    }
}
