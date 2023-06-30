package Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import database.PredefinedSQLCode;

/**
 * This class offers various methods to parse JSON files
 */
public class JsonParser 
{
    /**
    * This method is used to parse the JSON file and return the data.
    * 
    * @param path the path to the JSON file
    * @return the data of the JSON file
    */
    public static JsonNode readJsonFile(String path) throws IOException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);

        if (!file.exists()) {
            throw new IOException("File not found");
        }

        JsonNode jsonNode = objectMapper.readTree(file);
        return jsonNode;
    }

    
    public static void writeJsonFile(String path, JsonNode jsonNode) 
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(path);

        try {
            objectMapper.writeValue(file, jsonNode);
        } catch (IOException e) {
            System.out.println("error while saving settings");
            e.printStackTrace();
        }
    }


    public static int getAlbumsFile_element_count(String path) {

        try {
            JsonNode rootNode = readJsonFile(path);
            return rootNode.size();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static Object[] parseArtists(String path) 
    {
        Object [] data = new Object[3];
        HashMap<String, Object> artistsData = new HashMap<String, Object>();
        HashMap<String, Object> artistsImage = new HashMap<String, Object>();
        HashMap<String, Object> artistsGenres = new HashMap<String, Object>();
        JsonNode rootNode = null;
        
        data[0] = artistsData;
        data[1] = artistsImage;
        data[2] = artistsGenres;


        try {
            rootNode = readJsonFile(path);

            for (JsonNode artistNode : rootNode) 
            {
                HashMap<String, Object> artistInfo = new HashMap<>();
                HashMap<String, Object> artistImages = new HashMap<>();
                List<String> genres = new ArrayList<>();

                JsonNode imagesNode = artistNode.get("images");
                JsonNode genresNode = artistNode.get("genres");

                //Informazioni
                String artistId = artistNode.get("id").asText();
                String spotify_url = artistNode.get("spotify_url").asText();
                String artistName = artistNode.get("name").asText();
                int popularity = artistNode.get("popularity").asInt();
                int followers = artistNode.get("followers").asInt();

                
                artistInfo.put(PredefinedSQLCode.Colonne.NAME.getName(), artistName);
                artistInfo.put(PredefinedSQLCode.Colonne.POPULARITY.getName(), popularity);
                artistInfo.put(PredefinedSQLCode.Colonne.FOLLOWERS.getName(), followers);
                artistInfo.put(PredefinedSQLCode.Colonne.URL.getName(), spotify_url);
                artistInfo.put(PredefinedSQLCode.Colonne.ID.getName(), artistId);
                
                artistsData.put(artistId, artistInfo);

                //Immagini
                for (JsonNode imageNode : imagesNode) {
                    String imageUrl = imageNode.get("url").asText();
                    String height = imageNode.get("height").asText();
                    String width = imageNode.get("width").asText();
                  
                    artistImages.put(height + "x" + width, imageUrl);
                }
                artistsImage.put(artistId, artistImages);

                //Generi 
                for (JsonNode genreNode : genresNode) {
                    String genre = genreNode.asText();
                    genres.add(genre);
                }
                artistsGenres.put(artistId, genres);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return data;
    } 
}