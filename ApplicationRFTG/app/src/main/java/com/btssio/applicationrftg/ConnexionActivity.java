package com.btssio.applicationrftg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConnexionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] listeURLs = null;
    private EditText emailInput, passwordInput;
    private Button btnLogin;
    private TextView sendEmailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pageconnexion);

        listeURLs = getResources().getStringArray(R.array.listeURLs);
        Spinner spinnerURLs=findViewById(R.id.spinnerURLs);
        spinnerURLs.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence>adapterListeURLs=ArrayAdapter.createFromResource(this, R.array.listeURLs, android.R.layout.simple_spinner_item);
        adapterListeURLs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerURLs.setAdapter(adapterListeURLs);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.btnLogin);
        sendEmailLink = findViewById(R.id.sendEmailLink);

        // 🔹 Gestion du bouton de connexion
        btnLogin.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            EditText edittextURL = findViewById(R.id.URLText);
            DonneesPartagees.setURLConnexion(edittextURL.getText().toString());

            Toast.makeText(getApplicationContext(), DonneesPartagees.getURLConnexion(), Toast.LENGTH_SHORT).show();

            if (!email.isEmpty() && !password.isEmpty()) {
                verifierIdentifiants(email, password);
            } else {
                Toast.makeText(ConnexionActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Gestion du lien de récupération de mot de passe
        sendEmailLink.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Nouveau mot de passe");
                intent.putExtra(Intent.EXTRA_TEXT, "Cliquez sur ce lien pour changer votre mot de passe : http://127.0.0.1:8000/pertesmd");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Aucune application email trouvée", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Veuillez entrer une adresse email valide", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Vérification des identifiants via l'API
    private void verifierIdentifiants(String email, String password) {
        new Thread(() -> {
            try {
                String urlString = DonneesPartagees.getURLConnexion() + "/toad/customer/getByEmail?email=" + URLEncoder.encode(email, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    String response = result.toString();
                    JSONObject jsonResponse = new JSONObject(response);

                    int passwordFromApi = jsonResponse.getInt("password");
                    int customerId = jsonResponse.getInt("customerId");

                    if (passwordFromApi == Integer.parseInt(password)) {
                        // ✅ Enregistrer le customerId dans les préférences
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("customerId", customerId);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(ConnexionActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ConnexionActivity.this, AfficherListeDvdsActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ConnexionActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ConnexionActivity.this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ConnexionActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Make toast of the name of the course which is selected in the spinner
        //Toast.makeText(getApplicationContext(), listeURLs[position], Toast.LENGTH_SHORT).show();
        //DonneesPartagees.setURLConnexion(listeURLs[position]);
        EditText URLText = findViewById(R.id.URLText);
        URLText.setText(listeURLs[position]);
        //Toast.makeText(getApplicationContext(), URLText.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed when no selection is made
    }
}
