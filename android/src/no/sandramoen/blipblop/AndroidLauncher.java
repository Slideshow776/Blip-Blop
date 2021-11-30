package no.sandramoen.blipblop;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.games.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import no.sandramoen.blipblop.utils.GooglePlayServices;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {
	private static final String token = "AndroidLauncher.java";
	private static Long highScore = 0L;
	private static Long startTime;

	private static final int RC_SIGN_IN = 9001;
	private static final int RC_UNUSED = 5001;
	private static final int RC_LEADERBOARD_UI = 9004;
	private static final int RC_ACHIEVEMENT_UI = 9003;

	private GoogleSignInClient mGoogleSignInClient;
	private GoogleSignInAccount mGoogleSignInAccount;
	private AchievementsClient mAchievementClient;
	private PlayersClient mPlayersClient;

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		config.useImmersiveMode = true;

		// locale
		String locale = "en";
		try {
			if (getResources().getConfiguration().getLocales().get(0).toString().equals("nb_NO") || getResources().getConfiguration().getLocales().get(0).toString().equals("nn_NO")) {
				locale = "no";
			} else if (Locale.getDefault().toString().equals("nb_NO") || Locale.getDefault().toString().equals("nn_NO")) {
				locale = "no";
			} else if (Resources.getSystem().getConfiguration().locale.toString().equals("nb_NO") || Resources.getSystem().getConfiguration().locale.toString().equals("nn_NO")) {
				locale = "no";
			}
		} catch(Exception e) {
			System.err.println("Error setting up locale in AndroidLauncher.java: " + e);
		}

		initialize(new BlipBlopGame(this, locale), config);
	}

	private void startSignInIntent() {
		mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
		Intent intent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(intent, RC_SIGN_IN);
	}

	private void popup() {
		// Display the 'Connecting' pop-up appropriately during sign-in.
		GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, mGoogleSignInAccount);
		gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				// The signed in account is stored in the result.
				mGoogleSignInAccount = result.getSignInAccount();
				popup();

				// fetch clients
				mPlayersClient = Games.getPlayersClient(this, mGoogleSignInAccount);
				mAchievementClient = Games.getAchievementsClient(this, mGoogleSignInAccount);
			} else {
				String message = result.getStatus().getStatusMessage();
				System.out.println("AndroidLauncher.java: " + result.getStatus());
				// Status{statusCode=SIGN_IN_REQUIRED, resolution=null}
				/*if (message == null || message.isEmpty()) {
					message = "There was an issue with sign in: " + result.getStatus();
				}
				new AlertDialog.Builder(this).setMessage(message)
						.setNeutralButton(android.R.string.ok, null).show();*/
			}
		}
	}

	@Override
	public boolean isSignedIn() {
		return GoogleSignIn.getLastSignedInAccount(this) != null;
	}

	public void signOut() {
		if (!isSignedIn()) {
			return;
		}

		popup();
		mGoogleSignInClient.signOut().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						mAchievementClient = null;
						mPlayersClient = null;
					}
				});
	}

	@Override
	public void signIn() {
		startSignInIntent();
	}

	public void incrementClassicAchievements() {
		// popup view
		GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, mGoogleSignInAccount);
		gamesClient.setGravityForPopups(Gravity.CENTER_HORIZONTAL);
		gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());

		// achievement increment
		mAchievementClient.increment(getString(R.string.classicBronzeAchievement), 1);
		mAchievementClient.increment(getString(R.string.classicSilverAchievement), 1);
		mAchievementClient.increment(getString(R.string.classicGoldAchievement), 1);
	}

	public void incrementChallengeAchievements() {
		// popup view
		GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, mGoogleSignInAccount);
		gamesClient.setGravityForPopups(Gravity.CENTER_HORIZONTAL);
		gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());

		// achievement increment
		mAchievementClient.increment(getString(R.string.challengeBronzeAchievement), 1);
		mAchievementClient.increment(getString(R.string.challengeSilverAchievement), 1);
		mAchievementClient.increment(getString(R.string.challengeGoldAchievement), 1);
	}

	public void showAchievements() {
		mAchievementClient
				.getAchievementsIntent()
				.addOnSuccessListener(new OnSuccessListener<Intent>() {
					@Override
					public void onSuccess(Intent intent) {
						startActivityForResult(intent, RC_ACHIEVEMENT_UI);
					}
				});
	}

	@Override
	public void getLeaderboard() {}

	@Override
	public void fetchHighScore() {}

	@Override
	public int getHighScore() {return 0;}

	@Override
	public void submitScore(int score) {}
}
