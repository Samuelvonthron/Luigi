package com.btssio.applicationrftg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
   public static final String EXTRA_MESSAGE = "com.btssio.applicationrftg.EXTRA_MESSAGE";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Bouton pour naviguer vers AfficherListeDvdsActivity
      Button buttonAfficherDvds = findViewById(R.id.button_afficher_dvds);

      buttonAfficherDvds.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // Créer un intent pour lancer AfficherListeDvdsActivity
            Intent intent = new Intent(MainActivity.this, AfficherListeDvdsActivity.class);
            startActivity(intent);  // Lancer l'activité
         }
      });
   }
}

  /* @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_pageconnexion);

      // Exemple d'envoi d'un message via l'Intent
      // Intent intent = new Intent(this, AfficherListeDvdsActivity.class);
      // intent.putExtra(EXTRA_MESSAGE, "liste résultat ici");
      // startActivity(intent);
      // intent.putExtra(EXTRA_MESSAGE, "Message d'exemple pour AfficherListeDvdsActivity");
      // startActivity(intent);

      EditText emailInput = findViewById(R.id.emailInput);
      TextView sendEmailLink = findViewById(R.id.sendEmailLink);

      sendEmailLink.setOnClickListener(v -> {
         String email = emailInput.getText().toString().trim();

         if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            intent.putExtra(Intent.EXTRA_SUBJECT, "nouveau mot de passe");
            intent.putExtra(Intent.EXTRA_TEXT, "cliquez sur ce lien pour changez votre email  http://127.0.0.1:8000/pertesmd");

            // Vérifiez si une application email est disponible
            if (intent.resolveActivity(getPackageManager()) != null) {
               startActivity(intent);
            } else {
               Toast.makeText(this, "Aucune application email trouvée", Toast.LENGTH_SHORT).show();
            }
         } else {
            Toast.makeText(this, "Veuillez entrer une adresse email valide", Toast.LENGTH_SHORT).show();
         }
      });
   }*/