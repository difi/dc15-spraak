package connectors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by camp-mli on 19.06.2015.
 */
public class ElasticConnector {
    public static void main(String[] args) {
        // http://elasticsearch.difi.local:8080/
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("spraak", "spraak").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("elasticsearch.difi.local", 8080));
    }
}
