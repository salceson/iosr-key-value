package iosr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class KeyValueVerifyApplication {

    private static final Logger LOG = LoggerFactory.getLogger(KeyValueVerifyApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(KeyValueVerifyApplication.class, args);

        final Map<String, Map<String, Integer>> resultsByMembers = getResultsFromMembers(args);

        final Map<String, Map<String, Integer>> storeValuesByClients = getResultsByClientsMap(resultsByMembers);

        verifyValues(storeValuesByClients);

    }

    private static Map<String, Map<String, Integer>> getResultsFromMembers(String[] args) {
        final ObjectMapper mapper = new ObjectMapper();
        final RestTemplate restTemplate = new RestTemplate();
        final Map<String, Map<String, Integer>> resultsByMembers = new LinkedHashMap<>();

        for (String member : args) {
            final Map<String, Integer> map;

            try {
                final String json = restTemplate.getForObject("http://" + member + "/store/all", String.class);

                LOG.debug(member + ": " + json);

                map = mapper.readValue(json, new TypeReference<HashMap<String, Integer>>(){});
            } catch (IOException | ResourceAccessException e) {
                LOG.error("Member " + member + " is down!");
                continue;
            }
            resultsByMembers.put(member, map);
        }

        return resultsByMembers;
    }

    private static Map<String, Map<String, Integer>> getResultsByClientsMap(Map<String, Map<String, Integer>> resultsByMembers) {
        final Map<String, Map<String, Integer>> storeValuesByClients = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : resultsByMembers.entrySet()) {
            final String member = entry.getKey();
            final Map<String, Integer> memberStore = entry.getValue();

            for (Map.Entry<String, Integer> storeEntry : memberStore.entrySet()) {
                final String clientKey = storeEntry.getKey();
                final Integer value = storeEntry.getValue();

                if(!storeValuesByClients.containsKey(clientKey)) {
                    storeValuesByClients.put(clientKey, new HashMap<>());
                }

                storeValuesByClients.get(clientKey).put(member, value);
            }
        }

        return storeValuesByClients;
    }

    private static void verifyValues(Map<String, Map<String, Integer>> storeValuesByClients) {
        boolean allTestsPassed = true;

        for (Map.Entry<String, Map<String, Integer>> entry : storeValuesByClients.entrySet()) {
            final String clientKey = entry.getKey();
            final Map<String, Integer> valuesByMembers = entry.getValue();

            LOG.info("Verifying client: " + clientKey);

            Integer lastValue = null;

            for (Map.Entry<String, Integer> memberValueEntry : valuesByMembers.entrySet()) {
                final String member = memberValueEntry.getKey();
                final Integer value = memberValueEntry.getValue();

                LOG.info(member + ": " + value);

                if(lastValue != null && !value.equals(lastValue)) {
                    allTestsPassed = false;
                }

                lastValue = value;
            }
        }

        if(!allTestsPassed) {
            System.exit(1);
        }
    }
}
