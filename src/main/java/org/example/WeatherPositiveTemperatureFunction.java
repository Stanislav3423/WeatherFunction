package org.example;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

public class WeatherPositiveTemperatureFunction {
    private static final String API_URL = "https://weatherspringbootapi-aganc0dbc2hub4cg.polandcentral-01.azurewebsites.net/api/observations/getPositiveTemperature";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @FunctionName("GetWeatherPositiveTemperature")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Отримання погоди з температурою більше 0...");

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Дані з температурою більше 0 не знайдено")
                        .build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(httpResponse.body());

            return request.createResponseBuilder(HttpStatus.OK).body(jsonNode.toString()).build();
        } catch (Exception e) {
            context.getLogger().severe("Помилка: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Помилка при отриманні даних")
                    .build();
        }
    }
}
