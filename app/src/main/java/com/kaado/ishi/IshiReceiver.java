package com.kaado.ishi;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 *  Author: Taylor (hallfox)
 *  Does all the heavy lifting. Watches for broadcast events from Ishi and interacts with Anki
 *  to get the requested data or post an answer to a flashcard.
 */
public class IshiReceiver extends PebbleDataReceiver
{
    private static final UUID WATCHAPP_UUID = UUID.fromString("6c0bbc11-7214-4e08-9b45-806b5f1a07a4");
    private static final String WATCHAPP_FILENAME = "ishi.pbw";
    private static final String TAG = "IshiReceiver";

    public static final int EASE_FAILED = 1;
    public static final int EASE_HARD = 2;
    public static final int EASE_MID = 3;
    public static final int EASE_EASY = 4;

    private static final int
            KEY_BUTTON = 0,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2,

    KEY_VIBRATE = 1,

    KEY_ACTION = 2,
            ACTION_ANS = 0,
            ACTION_EASE = 1,
            ACTION_Q = 2,
            ACTION_DECK_SELECT = 3,

    INBOX_LIMIT = 126,

    KEY_DECKS = 3,
            KEY_QUESTION = 4,
            KEY_ANSWER = 5,
            KEY_EASE = 6;

    public IshiReceiver()
    {
        super(WATCHAPP_UUID);
    }

    @Override
    public void receiveData(Context context, int transactionId, PebbleDictionary data)
    {
        PebbleKit.sendAckToPebble(context, transactionId);
        Log.d(TAG, "Received Ack from pebble.");


        // Handle incoming request for getting the user's decks
        if (data.getString(KEY_DECKS) != null)
        {
            final String deck = data.getString(KEY_DECKS);

        }
        // Handles answer to the current card and sends out a new question
        if (data.getInteger(KEY_EASE) != null)
        {
            final int ease = data.getInteger(KEY_EASE).intValue();
        }
        // Handles general action
        // Originally handled multiple situations, now does nada
        if (data.getInteger(KEY_ACTION) != null)
        {
            final int action = data.getInteger(KEY_ACTION).intValue();
            switch (action)
            {
                case ACTION_DECK_SELECT:
                    Log.i(TAG, "Asking for decks...");
                    // Getting the content provider from AnkiDroid
                    Uri ankiURI = Uri.parse("content://com.ichi2.anki.flashcards/decks");
                    try
                    {
                        ContentProviderClient ankiClient = context.getContentResolver()
                                .acquireContentProviderClient(Constants.Deck.CONTENT_ALL_URI);
                        Cursor decksCursor = ankiClient.query(Constants.Deck.CONTENT_ALL_URI, null, null, null, null);
                        if (decksCursor.moveToFirst())
                        {
                            HashMap<Long, String> decks = new HashMap<Long, String>();
                            do
                            {
                                long deckID = decksCursor.getLong(decksCursor.getColumnIndex(Constants.Deck.DECK_ID));
                                String deckName = decksCursor.getString(decksCursor.getColumnIndex(Constants.Deck.DECK_NAME));
                                try
                                {
                                    JSONObject deckOptions = new JSONObject(decksCursor.getString(decksCursor.getColumnIndex(Constants.Deck.OPTIONS)));
                                    JSONArray deckCounts = new JSONArray(decksCursor.getString(decksCursor.getColumnIndex(Constants.Deck.DECK_COUNTS)));
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                decks.put(deckID, deckName);
                            } while (decksCursor.moveToNext());
                            Log.d(TAG, "Deck names: ");
                            for (String deck: decks.values())
                            {
                                Log.d(TAG, deck);
                            }
                            PebbleDictionary message = new PebbleDictionary();

                            String msg = "";
                            for (String s: decks.values())
                            {
                                if (s.equals("Default")) continue;
                                msg += s + ";";
                            }
                            message.addString(KEY_DECKS, msg);
                            PebbleKit.sendDataToPebble(context, WATCHAPP_UUID, message);
                        }
                    }
                    catch (RemoteException e)
                    {
                        Log.e(TAG, "Failed to connect to Anki ContentProvider.");
                    }

                    break;

                default:
                    Log.d(TAG, "Unrecognized action.");
            }
        }
    }
}
