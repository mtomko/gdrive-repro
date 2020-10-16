import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String TEAM_DRIVE_ID = "0AA2wbF6f49XjUk9PVA";

    private static GoogleCredentials getCredentials() throws IOException {
        // Load client secrets.
        try (InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);
            credentials.refreshIfExpired();
            return credentials;
        }
    }


    public static void main(String[] args) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final GoogleCredentials credentials = getCredentials();
        final Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName("PoolQ Launcher")
                .build();

        final FileList list = drive.files()
                .list()
                .setCorpora("drive")
                .setDriveId(TEAM_DRIVE_ID)
                .setIncludeTeamDriveItems(true)
                // this query doesn't find anything, but it should
                .setQ("name = 'XY20181203_AADI05_pXPR064_M-AF32_G0-run2' and '1VbuHdYWYPGApmPu3u_nWQX2aPGIbh2rZ' in parents")
                // this query, built the same way but searching in a different folder, does find what it's looking for
                //.setQ("name = 'XY20170110_AAAX02_TaqC18NoC10_GGTACCflank_GGHP4T_MatchExact' and '1NaoyAK0jdGPe7FJsYDOGF7Ff4Si30KA-' in parents")
                .setSpaces("drive")
                .setSupportsTeamDrives(true)
                .setFields("nextPageToken, files(id, name)")
                .execute();

        System.out.println("Query found " + list.getFiles().size() + " files");
        for (File file : list.getFiles()) {
            System.out.println(file.getId() + " " + file.getName());
        }
        if (list.getNextPageToken() != null) {
            System.out.println("Next page token: " + list.getNextPageToken());
        }
    }

}
