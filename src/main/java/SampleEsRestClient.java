import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.* ;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SampleEsRestClient {
    private final static String USER     = "elastic";
    private final static String PASSWORD = "123456";

    public static void main (String[] args) {
        System.out.println("Sending request to elasticsearch cluster ");

        runRequest();
    }

    private static void runRequest () {
        try {

            System.out.println("Initialize rest client ");

            // Initialize REST client for elasticsearch
            final CredentialsProvider credentialsProvider = initCredentialProvider();
            final SSLContext sslContext = initSSLContextAsTrustAll();

            RestClient restClient = RestClient.builder(initHttpHost())
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                            return httpAsyncClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                                    .setSSLContext(sslContext)
                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                        }
                    }).build();

            // Send Request
            System.out.println("Perform request");
            Response response = restClient.performRequest(reqRoot());

            // Read response body
            System.out.println("Parse response body");
            String responseBody = EntityUtils.toString(response.getEntity());

            // Output result
            System.out.println(responseBody);

        } catch( ResponseException e) {
            System.out.println( "ResponseException error: " + e.getMessage() );

        } catch (IOException e) {
            System.out.println( "IOException error: " + e.getMessage() );
        }
    }

    // Initialize SSLContext
    private static SSLContext initSSLContextAsTrustAll() {
        SSLContext sslContext = null;

        try {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial( new TrustAllStrategy());

            sslContext = sslContextBuilder.build();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException error: " + e.getMessage());

        } catch (KeyStoreException e) {
            System.out.println("KeyStoreException error: " + e.getMessage());

        } catch (KeyManagementException e) {
            System.out.println("KeyManagementException error: " + e.getMessage());
        }

        return sslContext;
    }

    // Initialize Credential provider
    private static CredentialsProvider initCredentialProvider() {
        // Prepare credential instance for basic authentication
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(USER, PASSWORD));

        return credentialsProvider;
    }
    // Construct HTTP host object
    private static HttpHost initHttpHost() {
        return new HttpHost("localhost", 9200 , "https") ;
    }

    // Prepare url http request
    private static Request reqRoot(){
        Request request = new Request("GET", "/");

        // Make the output as pretty mode
        request.addParameter("pretty", "true");

        return request;
    }
}
