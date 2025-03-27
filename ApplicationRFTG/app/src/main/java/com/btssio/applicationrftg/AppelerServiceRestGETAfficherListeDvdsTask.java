/*package com.btssio.applicationrftg;

import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask <String, Void, JSONArray> {
    //URL urlAAppeler = new URL(« <l’url à appelr : « http ::/…»>

    private SimpleCursorAdapter adapter;
    private MatrixCursor dvdCursor;

    @Override
    protected JSONArray doInBackground(String... urls) {
        JSONArray listeDvds = new JSONArray();
        try {
            // Vérifiez si l'URL est bien reçue
            String urlString = urls[0];
            Log.d("mydebug", "URL reçue pour l'appel API : " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000); // Timeout de connexion
            urlConnection.setReadTimeout(5000);    // Timeout de lecture

            // Vérifiez le code de réponse HTTP
            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Code de réponse HTTP : " + responseCode);

            // Si la réponse n'est pas OK (200), renvoyez une erreur
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("mydebug", "Erreur lors de l'appel API : Code de réponse " + responseCode);
                return null;
            }

        } catch (Exception e) {
            Log.e("AppelerServiceRestGETAfficherListeDvdsTask", "Erreur de connexion ou de lecture : ", e);
        }

        return listeDvds;
    }

    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(JSONArray listeDvds) {
        if (listeDvds == null) {
            Log.e("AppelerServiceRestGETAfficherListeDvdsTask", "Erreur : la liste films récupérée est nul");
            return;
        }

        try {
            dvdCursor.close();
            dvdCursor = new MatrixCursor(new String[]{"_id", "title", "releaseYear"});

            for (int i = 0; i < listeDvds.length(); i++) {
                JSONObject film = listeDvds.getJSONObject(i);
                int filmId = film.getInt("filmId");
                String title = film.getString("title");
                String annee = film.getString("releaseYear");

                dvdCursor.addRow(new Object[]{filmId, title, annee});
            }

            adapter.changeCursor(dvdCursor);
            Log.d("AppelerServiceRestGETAfficherListeDvdsTask", "Liste mise à jour avec succès");

        } catch (JSONException e) {
            Log.e("AppelerServiceRestGETAfficherListeDvdsTask", "Erreur de parsing du JSON : ", e);
        }
    }


}*/