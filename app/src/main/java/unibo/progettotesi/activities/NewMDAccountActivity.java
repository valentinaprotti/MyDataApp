package unibo.progettotesi.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.controller.IController;
import unibo.progettotesi.controller.MyController;
import unibo.progettotesi.model.services.AbstractService;
import unibo.progettotesi.model.services.GoBoBusService;
import unibo.progettotesi.utilities.VoiceSupport;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class NewMDAccountActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageButton mNewAccountButton;
	private SharedPreferences sharedPreferences;
	private ProgressBar mProgressBar;
	private boolean voiceSupport;
	private TextToSpeech tts;
	private String email;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_md_account);

		mNewAccountButton = (ImageButton) findViewById(R.id.button_add);
		mNewAccountButton.setOnClickListener(this);

		mProgressBar = (ProgressBar) findViewById(R.id.new_md_account_progress);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// controllo se l'utente preferisce l'assistente vocale o meno
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);

		if(voiceSupport) {
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});
		}

		if (this.getIntent().hasExtra(Intent.EXTRA_EMAIL)) {
			email = this.getIntent().getStringExtra(Intent.EXTRA_EMAIL);
		}
		if (this.getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			password = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(NewMDAccountActivity.this, SettingsActivity.class);
		i.putExtra("EXTRA_CLOSED", "Nessun account creato");
		startActivity(i);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.button_add:
				newAccount(view);
				break;
		}
	}

	public void newAccount(View v) {
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Sta per essere creato un nuovo account MyData per questo servizio. Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Sta per essere creato un nuovo account MyData per questo servizio. Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(NewMDAccountActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Nuovo account MyData")
				.setMessage("Sta per essere creato un nuovo account MyData per questo servizio.\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// viene creato un account per l'utente
							// vengo reindirizzato alla schermata di gestione dei consent

							mProgressBar.setVisibility(View.VISIBLE);
							mNewAccountButton.setVisibility(View.GONE);
							mProgressBar.animate().setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime)).alpha(
									1).setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									mProgressBar.setVisibility(View.VISIBLE);
								}});

							AbstractService service = new GoBoBusService();
							IController controller = new MyController();
							controller.logInUser("nomecognome@prova.it", "password".toCharArray());
							controller.addService(service);

							SharedPreferences.Editor editor = sharedPreferences.edit();
							editor.putBoolean("LocationConsent", true);
							editor.putBoolean("LatitudeConsent", true);
							editor.putBoolean("LongitudeConsent", true);
							editor.putBoolean("RouteConsent", true);
							editor.putBoolean("StopConsent", true);
							editor.commit();

							Intent i = new Intent(NewMDAccountActivity.this, UserMDProfileActivity.class);
							i.putExtra(EXTRA_MESSAGE, "Account creato con successo");
							i.putExtra(Intent.EXTRA_EMAIL, email);
							i.putExtra(Intent.EXTRA_TEXT, password);
							startActivity(i);
						}
						})
				.setNegativeButton("No", null)
				.show();
	}

	@Override
	protected void onDestroy() {
		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}
}
