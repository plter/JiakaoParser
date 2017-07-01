import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;

/**
 * Created by plter on 7/1/17.
 */
public class JiakaoParser {

    public static void main(String[] args) {
//        new JiakaoParser(dataSubject1Json,subject1DistDirName);
        new JiakaoParser(dataSubject4Json, subject4DistDirName);
    }

    private static String subject1DistDirName = "subject1";
    private static String subject4DistDirName = "subject4";
    private static String imagesDirName = "images";
    private static String videosDirName = "videos";
    private static String questionsFileName = "questions.json";
    private static String dataSubject1Json = "DataSubject1.json";
    private static String dataSubject4Json = "DataSubject4.json";

    public JiakaoParser(String dataJsonFile, String distDirName) {

        File data = new File(dataJsonFile);
        try {
            FileInputStream fis = new FileInputStream(data);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();


            String jsonString = new String(bytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);

            generateSubject1Package(jsonObject, distDirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] buffer = new byte[2048];

    private boolean downloadFile(String url, File dist) {
        InputStream imageStream = null;
        try {
            System.out.println("Start download " + url);

            imageStream = new URL(url).openStream();

            if (!dist.exists()) {
                dist.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dist);
            int size = -1;
            while ((size = imageStream.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
            fos.close();
            imageStream.close();

            System.out.println("Downloaded " + url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private void tryToTransferMediaFileData(String distDirName, JSONObject question, String mediaTagName, String targetDirName) {

        File distDir = createDirIfNotExists(new File(distDirName));
        File mediaDistDir = createDirIfNotExists(new File(distDir, targetDirName));

        if (question.has(mediaTagName)) {
            String url = question.getString(mediaTagName);
            String fileName = getFileNameFromUrl(url);

            if (downloadFile(url, new File(mediaDistDir, fileName))) {
                question.put(mediaTagName, targetDirName + "/" + fileName);
            }
        }
    }

    private void generateSubject1Package(JSONObject jsonObject, String distDirName) {

        JSONArray questions = jsonObject.getJSONArray("questions");
        JSONObject question = null;

        for (int i = 0; i < questions.length(); i++) {
            question = questions.getJSONObject(i);

            System.out.println("title " + question.getString("title"));

            tryToTransferMediaFileData(distDirName, question, "image", imagesDirName);
            tryToTransferMediaFileData(distDirName, question, "video", videosDirName);
        }


        File questionsJsonFile = new File(createDirIfNotExists(new File(distDirName)), questionsFileName);
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

    private File createDirIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
