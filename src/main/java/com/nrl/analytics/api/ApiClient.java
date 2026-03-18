package com.nrl.analytics.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Algorithm 1: Data Ingestion.
 *
 * <p>Fetches live match data from a remote API endpoint.
 */
public class ApiClient {

    private static final int HTTP_OK = 200;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    /** Package-private constructor for testing with a custom HttpClient. */
    ApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Fetches live match data from the given API URL using the provided API key.
     *
     * @param apiUrl  the URL of the live-data endpoint
     * @param apiKey  the authentication key sent in the {@code X-Api-Key} header
     * @return the raw JSON response body
     * @throws IOException          if the network request fails
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws ApiException         if the server returns a non-200 status code
     */
    public String fetchLiveMatchData(String apiUrl, String apiKey)
            throws IOException, InterruptedException, ApiException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-Api-Key", apiKey)
                .header("Accept", "application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK) {
            throw new ApiException(
                    "API request failed with status " + response.statusCode()
                            + ": " + response.body());
        }

        return response.body();
    }

    /**
     * Thrown when the API returns a non-200 HTTP status code.
     */
    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}
