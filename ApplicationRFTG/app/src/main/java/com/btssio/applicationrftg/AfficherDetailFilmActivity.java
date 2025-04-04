package com.btssio.applicationrftg;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AfficherDetailFilmActivity extends AppCompatActivity {

    private Button buttonAjouterPanier;
    private String title;
    private int inventory_id = -1; // L'ID inventory récupéré
    private TextView textViewTitle, textViewDescription, textViewYear, textViewTarif,
            textViewDuree, textViewCout, textViewClassification, textViewSpecial;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficherdetail);

        buttonAjouterPanier = findViewById(R.id.buttonAjouterPanier);

        // Lier les TextView
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewYear = findViewById(R.id.textViewYear);
        textViewTarif = findViewById(R.id.textViewTarif);
        textViewDuree = findViewById(R.id.textViewDuree);
        textViewCout = findViewById(R.id.textViewCout);
        textViewClassification = findViewById(R.id.textViewClassification);
        textViewSpecial = findViewById(R.id.textViewSpecial);

        // Récupérer l'ID du film envoyé par l'autre activité
        int filmId = getIntent().getIntExtra("filmId", -1);
        if (filmId != -1) {
            fetchFilmDetail(filmId);
            fetchInventoryId(filmId); // Récupération de l'ID inventory
        } else {
            Toast.makeText(this, "Aucun film sélectionné", Toast.LENGTH_SHORT).show();
        }

        buttonAjouterPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inventory_id != -1) {
                    ajouterAuPanier(inventory_id, title);
                } else {
                    Toast.makeText(AfficherDetailFilmActivity.this, "Erreur : Aucun film disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFilmDetail(int filmId) {
        String url = DonneesPartagees.getURLConnexion() +"/toad/film/getById?id=" + filmId;

        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("API_RESPONSE", response.toString());

                    return new JSONObject(response.toString());
                } catch (Exception e) {
                    Log.e("API_ERROR", "Erreur lors de la récupération des données", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject film) {
                if (film != null) {
                    try {
                        title = film.optString("title", "Titre inconnu");

                        textViewTitle.setText("Titre : " + title);
                        textViewDescription.setText("Description : " + film.optString("description", "Pas de description"));
                        textViewYear.setText("Année : " + film.optInt("releaseYear", 0));
                        textViewTarif.setText("Tarif : " + film.optDouble("rentalRate", 0.0) + "€");
                        textViewDuree.setText("Durée : " + film.optInt("length", 0) + " minutes");
                        textViewCout.setText("Coût : " + film.optDouble("replacementCost", 0.0) + "€");
                        textViewClassification.setText("Classification : " + film.optString("rating", "Non classé"));
                        textViewSpecial.setText("Spécial : " + film.optString("specialFeatures", "Aucune info"));

                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Erreur de parsing JSON", e);
                    }
                } else {
                    Toast.makeText(AfficherDetailFilmActivity.this, "Erreur de chargement des détails", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void fetchInventoryId(int filmId) {
        String url = DonneesPartagees.getURLConnexion() +"/toad/inventory/available/getById?id=" + filmId;

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("API_RESPONSE", "Inventory ID disponible : " + response.toString());

                    return Integer.parseInt(response.toString().trim());
                } catch (Exception e) {
                    Log.e("API_ERROR", "Erreur lors de la récupération de inventory_id", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != null) {
                    inventory_id = result;
                    Log.d("INVENTORY_ID", "Inventory ID disponible récupéré : " + inventory_id);
                } else {
                    Toast.makeText(AfficherDetailFilmActivity.this, "Erreur : Aucun film disponible", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void ajouterAuPanier(int inventory_id, String title) {
        if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Erreur : Titre du film inconnu", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Panier", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String panierActuel = sharedPreferences.getString("films", "");

        // Vérifier si l'ID inventory est déjà dans le panier
        if (!panierActuel.contains(inventory_id + ":")) {
            panierActuel += inventory_id + ":" + title + ";"; // Format : inventory_id:titre;
            editor.putString("films", panierActuel);
            editor.apply();

            Toast.makeText(this, "Ajouté au panier : " + title + " (Inventory ID: " + inventory_id + ")", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ce film est déjà dans le panier", Toast.LENGTH_SHORT).show();
        }
    }
}
