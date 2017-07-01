import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;

/**
 * Created by plter on 7/1/17.
 */
public class JiakaoParser {

    public static void main(String[] args) {

        new JiakaoParser();
    }

    private static String distDirName = "subject1";
    private static String imagesDirName = "images";
    private static String questionsFileName = "questions.json";

    public JiakaoParser() {

        File data = new File("data.json");
        try {
            FileInputStream fis = new FileInputStream(data);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();


            String jsonString = new String(bytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);

            generateSubject1Package(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateSubject1Package(JSONObject jsonObject) {

        File distDir = new File(distDirName);
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        File imagesDir = new File(distDir, imagesDirName);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        JSONArray questions = jsonObject.getJSONArray("questions");
        JSONObject question = null;
        byte[] buffer = new byte[2048];
        for (int i = 0; i < questions.length(); i++) {
            question = questions.getJSONObject(i);
            if (question.has("image")) {
                String imageUrl = question.getString("image");
                String imageFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                try {
                    InputStream imageStream = new URL(imageUrl).openStream();

                    File imageOutput = new File(imagesDir, imageFileName);
                    if (!imageOutput.exists()) {
                        imageOutput.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(imageOutput);
                    int size = -1;
                    while ((size = imageStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, size);
                    }
                    fos.close();
                    imageStream.close();

                    System.out.println("Downloaded " + imageUrl);

                    question.put("image", imagesDirName + "/" + imageFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File questionsJsonFile = new File(distDir, questionsFileName);
        try {
            if (!questionsJsonFile.exists()) {
                questionsJsonFile.createNewFile();
            }

            FileOutputStream outputStream = null;
            outputStream = new FileOutputStream(questionsJsonFile);
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Completed");
    }
}
