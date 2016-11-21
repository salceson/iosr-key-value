package iosr.keyvalue.server;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import iosr.keyvalue.command.PutCommand;
import iosr.keyvalue.command.RemoveCommand;
import iosr.keyvalue.query.GetQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ServerLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(ServerLauncher.class);

    @Value("${server}")
    private String serverHost;

    @Value("${members:}")
    private String memberHosts;

    private final StateMachine stateMachine;

    @Autowired
    public ServerLauncher(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @PostConstruct
    public void start() throws Exception {

        LOG.info("Server hosts: " + serverHost + ", member hosts: " + memberHosts);

        if (StringUtils.isEmpty(serverHost)) {
            throw new IllegalArgumentException("missing argument: --server=\"host:port\"");
        }

        // Parse the address to which to bind the server.
        final String[] serverInfo = serverHost.split(":");
        final Address address = new Address(serverInfo[0], Integer.valueOf(serverInfo[1]));

        // Build a list of all member addresses to which to connect.
        final List<Address> members = new ArrayList<>();
        members.add(address);

        for (String memberHost : memberHosts.split(",")) {
            if(!memberHost.contains(":")) {
                continue;
            }
            final String[] memberInfo = memberHost.split(":");
            members.add(new Address(memberInfo[0], Integer.valueOf(memberInfo[1])));
        }

        Thread.sleep(200 + new Random().nextInt(1000));

        final CopycatServer server = CopycatServer.builder(address)
                .withStateMachine(() -> stateMachine)
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
    }
}
