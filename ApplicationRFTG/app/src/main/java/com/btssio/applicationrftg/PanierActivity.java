package com.btssio.applicationrftg;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PanierActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView listViewPanier;
    private List<String> filmList;
    private ArrayAdapter<String> adapter;
    private Button buttonSupprimerPanier, buttonValiderPanier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        listViewPanier = findViewById(R.id.listViewPanier);
        buttonSupprimerPanier = findViewById(R.id.buttonSupprimerPanier);
        buttonValiderPanier = findViewById(R.id.buttonValiderPanier);

        sharedPreferences = getSharedPreferences("Panier", MODE_PRIVATE);
        filmList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filmList);
        listViewPanier.setAdapter(adapter);

        chargerPanier();

        buttonSupprimerPanier.setOnClickListener(v -> viderPanier());
        buttonValiderPanier.setOnClickListener(v -> envoyerPanierEnBDD());
    }

    private void chargerPanier() {
        filmList.clear(); // Réinitialiser la liste avant de la recharger
        String panier = sharedPreferences.getString("films", "");

        if (!panier.isEmpty()) {
            String[] films = panier.split(";");
            for (String film : films) {
                String[] details = film.split(":");
                if (details.length == 2) {
                    String inventoryId = details[0];
                    String title = details[1];
                    filmList.add("📽 " + title + " (ID: " + inventoryId + ")");
                }
            }
        }

        adapter.notifyDataSetChanged(); // Mise à jour de la liste affichée
        if (filmList.isEmpty()) {
            Toast.makeText(this, "Le panier est vide", Toast.LENGTH_SHORT).show();
        }
    }

    private void viderPanier() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("films"); // Supprime les films du panier
        editor.apply();

        filmList.clear(); // Efface la liste en mémoire
        adapter.notifyDataSetChanged(); // Met à jour l'affichage

        Toast.makeText(this, "Le panier a été vidé", Toast.LENGTH_SHORT).show();
    }

    private void envoyerPanierEnBDD() {
        if (filmList.isEmpty()) {
            Toast.makeText(this, "Le panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String film : filmList) {
            String[] parts = film.replace("📽 ", "").replace(" (ID: ", ":").replace(")", "").split(":");
            if (parts.length == 2) {
                int inventoryId = Integer.parseInt(parts[1]);
                envoyerLocation(inventoryId);
            }
        }
    }

    private void envoyerLocation(int inventoryId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://10.0.2.2:8080/toad/rental/add");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    String params = "rental_date=2025-02-27&inventory_id=" + inventoryId +
                            "&customer_id=1&return_date=2025-03-10&staff_id=1&last_update=2025-02-27";

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(params.getBytes());
                        os.flush();
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    Log.e("API_ERROR", "Erreur lors de l'envoi des données", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Toast.makeText(PanierActivity.this, "Location enregistrée !", Toast.LENGTH_SHORT).show();
                    viderPanier(); // Vide le panier après validation
                } else {
                    Toast.makeText(PanierActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
