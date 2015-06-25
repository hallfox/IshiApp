package com.kaado.ishi;

import android.net.Uri;

/**
 * Contains all the necessary constants from the FlashCardContract in AnkiDroid
 */
public class Constants
{
    public static final String AUTHORITY = "com.ichi2.anki.flashcards";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static class Deck
    {
        public static final Uri CONTENT_ALL_URI = Uri.withAppendedPath(AUTHORITY_URI, "decks");
        public static final Uri CONTENT_SELECTED_URI = Uri.withAppendedPath(AUTHORITY_URI, "select_deck");
        /**
         * The name of the Deck
         */
        public static final String DECK_NAME = "deck_name";

        /**
         * The unique identifier of the Deck
         */
        public static final String DECK_ID = "deck_id";

        /**
         * The number of cards in the Deck
         */
        public static final String DECK_COUNTS = "deck_count";

        /**
         * The options of the Deck
         */
        public static final String OPTIONS = "options";

        public static final String[] DEFAULT_PROJECTION = {
                DECK_NAME,
                DECK_ID,
                DECK_COUNTS,
                OPTIONS
        };


        /**
         * MIME type used for a Deck.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.ichi2.anki.review_info";

        /**
         * MIME type used for Deck.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.ichi2.anki.deck";
    }
}
