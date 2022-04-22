import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;

public class App {

    private final static String URI = "https://api.nasa.gov/planetary/apod?api_key=oyt7QBhMZOPRbrKAzgg4OtURFqIAeGb2R7M3XeIy";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        Function<String, HttpGet> request = x -> new HttpGet(x);
        Function<HttpGet, CloseableHttpResponse> response = x -> {
            try {
                return httpClient.execute(x);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };

        ObjectMapper mapper = new ObjectMapper();

        Image image = mapper.readValue(response.apply(request.apply(URI)).getEntity().getContent(),
                new TypeReference<>() {
                });
        String url = image.getUrl();
        System.out.println(url);

        byte[] body = (response.apply(request.apply(url))).getEntity().getContent().readAllBytes();

        String[] urlSplit = url.split("/");
        File file = new File("C:\\Users\\sasha\\IdeaProjects\\http\\http-task2\\"
                + urlSplit[urlSplit.length - 1]);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(body, 0, body.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
