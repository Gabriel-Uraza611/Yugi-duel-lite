
package co.edu.univalle.api;

import co.edu.univalle.model.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YgoApiClient
{
    public Card obtenerCartaAleatoria() throws IOException, InterruptedException
    {
        // Crea un cliente HTTP para hacer las peticiones
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // Crea una petición para obtener una carta aleatoria
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://db.ygoprodeck.com/api/v7/randomcard.php"))
                .build();

        // Ejecuta la petición
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Comprueba si la petición fue exitosa
        if (response.statusCode() == 200)
        {
            // Convierte la respuesta en un objeto JSON
            JSONObject json = new JSONObject(response.body());

            // Comprueba si los datos vienen dentro de "data"
            if (json.has("data"))
            {
                JSONArray datos = json.getJSONArray("data");

                if (datos.length() == 0)
                {
                    throw new IOException("La API no devolvió ninguna carta");
                }

                json = datos.getJSONObject(0);
            }

            // Comprueba que exista el tipo de carta
            if (!json.has("type"))
            {
                System.out.println("Respuesta de la API: " + response.body());
                throw new IOException("La respuesta no contiene el tipo de carta");
            }

            // Obtiene el tipo de carta
            String tipo = json.getString("type");

            // Comprueba que sea un monstruo
            if (!tipo.contains("Monster"))
            {
                return obtenerCartaAleatoria();
            }

            // Obtiene el nombre y las estadísticas
            String nombre = json.getString("name");
            int atk = json.getInt("atk");
            int def = json.getInt("def");

            // Obtiene la URL de la imagen
            JSONArray imagenes = json.getJSONArray("card_images");
            JSONObject imagen = imagenes.getJSONObject(0);
            String imageUrl = imagen.getString("image_url");

            // Crea y devuelve el objeto Card
            return new Card(nombre, atk, def, imageUrl);
        }
        else
        {
            System.out.println("Código HTTP: " + response.statusCode());
            System.out.println("Respuesta: " + response.body());

            throw new IOException("Error al consultar la API de Yu-Gi-Oh");
        }
    }

    // Método para probar la API
    public static void main(String[] args)
    {
        YgoApiClient api = new YgoApiClient();

        try
        {
            Card carta = api.obtenerCartaAleatoria();

            System.out.println("Nombre: " + carta.getName());
            System.out.println("ATK: " + carta.getAtk());
            System.out.println("DEF: " + carta.getDef());
            System.out.println("Imagen: " + carta.getImageUrl());
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}