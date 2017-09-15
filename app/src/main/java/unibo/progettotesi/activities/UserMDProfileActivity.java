package unibo.progettotesi.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.controller.IController;
import unibo.progettotesi.controller.MyController;
import unibo.progettotesi.model.MyData.MyData;
import unibo.progettotesi.model.consents.ServiceConsent;
import unibo.progettotesi.model.services.GoBoBusService;
import unibo.progettotesi.model.users.IUser;
import unibo.progettotesi.utilities.VoiceSupport;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/* Questa classe rappresenta l'interfaccia grafica che va inserita all'interno dell'applicazione
 * di GoBoBus (aggiornata con l'interfaccia p2p) per consentire all'utente (si scelga se modellare
 * l'utente, mantenendone ad esempio un database in cloud che a questo livello risulterebbe ancora
 * abbastanza inutile, oppure farne a meno gestendo solamente le preferenze dell'utente a livello
 * locale) di gestire nel dettaglio i dati condivisi con gli altri utenti della rete di dispositivi
 * che devono scambiarsi i dati.
 *
 * Allo stato attuale i dati qui inseriti sono di prova, non avendo sotto mano i veri dati di cui
 * dover regolare lo scambio, ma il meccanismo è quello corretto: ho la possibilità di scegliere
 * quali dati condividere e quali no, oppure di disattivare lo scambio dei dati, oppure di revocare
 * del tutto il consenso allo scambio dei dati. In questi ultimi due casi è ancora da stabilire
 * cosa ciò comporti nell'utilizzo dell'applicazione (probabilmente nulla? Oppure si può scegliere
 * di impedire la fruizione dei dati in tempo reale forniti dalla rete degli utenti se non si
 * condividono i propri?).
 *
 * Nello specifico questa classe si appoggia ad un controller (MyController) che regola la creazione
 * e l'accesso dell'utente (per ora hard cabled in attesa di sapere se possa essere di interesse,
 * come indicato sopra) e l'eventuale creazione di un account utente presso il servizio, l'unico
 * per ora, che dovrebbe essere GoBoBus (attualmente servizio di prova).
 * Inizializza poi l'interfaccia grafica con le attuali preferenze dell'utente: in questo caso
 * cerca se l'utente desideri l'assistente vocale (in tal caso lo inizializza) e dovrebbe cercare
 * se esiste ed è attivo, disabilitato o revocato il goBoBusService consent e quali dei dati l'utente abbia
 * interesse a condividere e quali meno, presumibilmente attingendo per il primo caso dai goBoBusService
 * consent dell'utente, e nel secondo caso nei data consent..? Nel dubbio ho inserito la preferenza
 * tra le sharedpreferences come tutto il resto.
 */

public class UserMDProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch mLocationSwitch;
	private Switch mLatitudeSwitch;
	private Switch mLongitudeSwitch;
	private Switch mRouteSwitch;
	private Switch mStopSwitch;
    private Button mDisableButton;
    private Button mWithdrawButton;
	private TextToSpeech tts;

	private boolean voiceSupport;
	private SharedPreferences sharedPreferences;
	private IUser user;
	private GoBoBusService goBoBusService;
	private ServiceConsent userSC;
	private IController controller;
	private String email;
	private String pass;
	private boolean locationConsent;
	private boolean latitudeConsent;
	private boolean longitudeConsent;
	private boolean routeConsent;
	private boolean stopConsent;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_md_profile);

		mLocationSwitch = (Switch) findViewById(R.id.locationSwitch);
		mLatitudeSwitch = (Switch) findViewById(R.id.latitudeSwitch);
		mLongitudeSwitch = (Switch) findViewById(R.id.longitudeSwitch);
		mRouteSwitch = (Switch) findViewById(R.id.routeSwitch);
		mStopSwitch = (Switch) findViewById(R.id.stopSwitch);

		mDisableButton = (Button) findViewById(R.id.button_disable);
		mDisableButton.setOnClickListener(this);

		mWithdrawButton = (Button) findViewById(R.id.button_withdraw);
		mWithdrawButton.setOnClickListener(this);

		setTitle("Gestione Consent");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// servizio a cui si riferisce (l'app GoBoBus stessa)
		goBoBusService = new GoBoBusService();

		// dummy credentials
		email = "nomecognome@prova.it";
		pass = "password";
		if (this.getIntent().hasExtra(Intent.EXTRA_EMAIL)) {
			email = this.getIntent().getStringExtra(Intent.EXTRA_EMAIL);
		}
		if (this.getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			pass = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
		}

		controller = new MyController();
		controller.logInUser(email, pass.toCharArray());
		user = MyData.getInstance().loginUser(email, pass.toCharArray());

		userSC = user.getActiveSCForService(goBoBusService);
		if (userSC == null) {
			// l'utente ha un account ma non è attivo: inizializzo di conseguenza la gui
			mDisableButton.setText("Abilita\n" +
					"consenso");
			mLocationSwitch.setEnabled(false);
		}

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

		// ottengo l'attuale preferenza per la condivisione dei vari dati
		// Nel caso tutti i dati non avessero il consenso ad essere condivisi è come aver
		// disabilitato il goBoBusService consent del tutto
		locationConsent = sharedPreferences.getBoolean("LocationConsent", true);
		latitudeConsent = sharedPreferences.getBoolean("LatitudeConsent", true);
		longitudeConsent = sharedPreferences.getBoolean("LongitudeConsent", true);
		routeConsent = sharedPreferences.getBoolean("RouteConsent", true);
		stopConsent = sharedPreferences.getBoolean("StopConsent", true);

		mLocationSwitch.setChecked(locationConsent);
		mLocationSwitch.setTextOn("ON");
		mLocationSwitch.setTextOff("OFF");
		mLocationSwitch.setOnClickListener(this);

		mLatitudeSwitch.setChecked(latitudeConsent);
		mLatitudeSwitch.setTextOn("ON");
		mLatitudeSwitch.setTextOff("OFF");
		mLatitudeSwitch.setOnClickListener(this);

		mLongitudeSwitch.setChecked(longitudeConsent);
		mLongitudeSwitch.setTextOn("ON");
		mLongitudeSwitch.setTextOff("OFF");
		mLongitudeSwitch.setOnClickListener(this);

		mRouteSwitch.setChecked(routeConsent);
		mRouteSwitch.setTextOn("ON");
		mRouteSwitch.setTextOff("OFF");
		mRouteSwitch.setOnClickListener(this);

		mStopSwitch.setChecked(stopConsent);
		mStopSwitch.setTextOn("ON");
		mStopSwitch.setTextOff("OFF");
		mStopSwitch.setOnClickListener(this);

		// se arrivo in questa schermata dopo aver creato un nuovo account:
		if (this.getIntent().hasExtra(EXTRA_MESSAGE))
			Toast.makeText(this, this.getIntent().getStringExtra(EXTRA_MESSAGE), Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, SettingsActivity.class);
		i.putExtra("EXTRA_CLOSED", "Sessione terminata");
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

	/*
			 * Handler di tutti i pulsanti e gli switch che avvia il metodo corretto
			 */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_disable:
            	disableOption(view);
                break;
            case R.id.button_withdraw:
            	withdrawOption(view);
                break;
			case R.id.locationSwitch:
				changePartConsent(view);
				break;
			case R.id.latitudeSwitch:
				changePartConsent(view);
				break;
			case R.id.longitudeSwitch:
				changePartConsent(view);
				break;
			case R.id.routeSwitch:
				changePartConsent(view);
				break;
			case R.id.stopSwitch:
				changePartConsent(view);
				break;
        }
    }

    /* Questo metodo viene invocato al cambio della preferenza della condivisione del dato
     * (switch). Esso chiede conferma della decisione dell'utente, con supporto vocale, e se
     * confermato, modifica la preferenza dell'utente nella condivisione di quel dato nelle
     * sharedpreferences. Contestualmente deve anche creare un nuovo dataconsent? Direi di no..
     * Se non viene confermato, lo switch torna nella posizione precedente e nulla cambia.
     */
	public void changePartConsent(View view) {
		final Switch toChange = (Switch) view;
		final String text = toChange.getText().toString().substring(10);

		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso per " + text + " sarà " + (toChange.isChecked() ? "attivato" : "disattivato") + ". Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consens per " + text + " sarà " + (toChange.isChecked() ? "attivato" : "disattivato") + ". Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(toChange.isChecked() ? "Attiva" : "Disattiva")
				.setMessage("Il consenso per " + text + " sarà " + (toChange.isChecked() ? "attivato" : "disattivato") + ".\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia la preferenza nelle sharedPreferences
						SharedPreferences.Editor editor = sharedPreferences.edit();
						switch (text) {
							case "Posizione":
								editor.putBoolean("LocationConsent", mLocationSwitch.isChecked());
								break;
							case "Latitudine":
								editor.putBoolean("LatitudeConsent", mLatitudeSwitch.isChecked());
								break;
							case "Longitudine":
								editor.putBoolean("LongitudeConsent", mLongitudeSwitch.isChecked());
								break;
							case "Percorso":
								editor.putBoolean("RouteConsent", mRouteSwitch.isChecked());
								break;
							case "Fermata":
								editor.putBoolean("StopConsent", mRouteSwitch.isChecked());
								break;
						}
						editor.commit();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						toChange.setChecked(!toChange.isChecked());
					}
				})
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Revoca consenso".
	 * Anch'esso chiede conferma della decisione dell'utente, con supporto vocale, e se
	 * confermato, cambia opportunamente lo stato del goBoBusService consent. In questo caso viene
	 * lanciata una nuova schermata, la stessa della creazione dell'account presso
	 * questo servizio che ho messo nel to do in alto, perché il consent è stato revocato e non
	 * è più attivabile da questo status (withdrawn).
	 * Se non viene confermato, nulla cambia.
	 */
	public void withdrawOption(final View view) {
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso sarà revocato. Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consenso sarà revocato. Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Revoca")
				.setMessage("Verrà eliminato l'account presso questo servizio.\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia lo stato del consent a withdrawn
						controller.withdrawConsentForService(goBoBusService);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.remove("LocationConsent");
						editor.remove("LatitudeConsent");
						editor.remove("LongitudeConsent");
						editor.remove("RouteConsent");
						editor.remove("StopConsent");
						editor.commit();

						// avvia l'activity per creare un nuovo account MyData
						Intent i = new Intent(UserMDProfileActivity.this, NewMDAccountActivity.class);
						i.putExtra(EXTRA_MESSAGE, "Account eliminato con successo");
						i.putExtra(Intent.EXTRA_EMAIL, email);
						i.putExtra(Intent.EXTRA_TEXT, pass);
						startActivity(i);
					}
				})
				.setNegativeButton("No", null)
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Disabilita/Abilita consenso".
     * Anch'esso chiede conferma della decisione dell'utente, con supporto vocale, e se
     * confermato, cambia opportunamente lo stato del goBoBusService consent. Disattiva o attiva inoltre
     * tutti gli switch e cambia il testo del pulsante in modo opportuno.
     * Se non viene confermato, nulla cambia.
     */
	public void disableOption(View v){
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso sarà " + ((mDisableButton.getText().toString().contains("Disabilita")) ? "disattivato" : "attivato") + ". Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consenso sarà " + ((mDisableButton.getText().toString().contains("Disabilita")) ? "disattivato" : "attivato") + ". Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(mDisableButton.getText().toString().contains("Disabilita") ? "Disabilita" : "Abilita")
				.setMessage("Il consenso sarà " + ((mDisableButton.getText().toString().contains("Disabilita")) ? "disattivato" : "attivato") + ".\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia lo stato del consent enabled>disabled o disabled>enabled
						// cambia il pulsante ad "Attiva"/"Disattiva"
						if (mDisableButton.getText().toString().contains("Disabilita")) {
							controller.toggleStatus(goBoBusService, false);
							mDisableButton.setText("Abilita\n" +
									"consenso");
							mLocationSwitch.setEnabled(false);
						} else {
							controller.toggleStatus(goBoBusService, true);
							mDisableButton.setText("Disabilita\n" +
									"consenso");
							mLocationSwitch.setEnabled(true);
						}
					}
				})
				.setNegativeButton("No", null)
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Mostra Data Consents".
	 * Esso apre una schermata che stampa a video tutti i Data Consent relativi a tutti gli eventuali
	 * account che l'utente ha presso questo servizio.
	 * Nella versione finale probabilmente non avrà ragione d'essere, ma per testing è utile.
	 */
	public void viewDataConsents(View v) {
		String allDConsents = controller.getAllDConsents(goBoBusService);
		Intent i = new Intent(this,DataConsentActivity.class);
		i.putExtra(Intent.EXTRA_TEXT, allDConsents);
		startActivity(i);
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
