package com.web.quiz_bot.util;

import com.web.quiz_bot.request.Request;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javax.net.ssl.SSLException;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.net.URI;
import reactor.netty.http.client.HttpClient;

public class RequestUtil {

    private RequestUtil() {}

    public static byte[] sendRequest(Request request, URI serverUrl) throws SSLException {
        if (serverUrl == null) {
            System.err.println("No server available");
            return null;
        }
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
        try {
            return client.post().uri(serverUrl)
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request.toString()).retrieve().bodyToMono(byte[].class).block();
        } catch (WebClientRequestException ce) {
            return null;
        }
    }
}
