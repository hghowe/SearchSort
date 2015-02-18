/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

/**
 * This is a shared interface, which makes use of the lesser-known function of interfaces
 * the common set of final (i.e. constant) variables, shared between any class that implements it.
 * @author harlanhowe
 */
public interface Constants {
        // SORTABLE BAR CONSTANTS
		public static final int BAR_COMPARE_BY_TOP          = 0;
		public static final int BAR_COMPARE_BY_BOTTOM       = 1;
		public static final int BAR_COMPARE_BY_SIZE         = 2;
		public static final int BAR_COMPARE_BY_RED          = 3;
		public static final int BAR_COMPARE_BY_GREEN        = 4;
		public static final int BAR_COMPARE_BY_BLUE         = 5;
		public static final int BAR_COMPARE_BY_BRIGHTNESS   = 6;
		public static final String BAR_FILE_DELIM = "\\";
        public static final String[] BAR_COMPARE_TYPE_STRINGS = {"Top","Bottom","Size","Red","Green","Blue","Brightness"};

        public static final int DEFAULT_BAR_ARRAY_SIZE = 100;

        // STATUS CONSTANTS
        public static final int STATUS_RUNNING = 0;
        public static final int STATUS_PAUSED = 1;
        public static final int STATUS_CANCELLED = 2;
        public static final int STATUS_FINISHED = 3;
        public static final int STATUS_FINISHED_FAILED = 4;
        public static final int STATUS_FINISHED_FOUND = 5;
        public static final int STATUS_SORTED = 6;
        public static final int STATUS_UNSORTED = 7;
        public static final int STATUS_DUPLICATES = 8;
        public static final String[] STATUS_STRINGS = {"Running",
                                               "Paused",
                                               "Cancelled",
                                               "Finished",
                                               "Bar Not Found",
                                               "Found Bar",
                                                "Sorted",
                                                "Unsorted",
                                                "Duplicates found"};


        //----------  Action Indicators
        static final int MAX_NUM_INDICATORS = 25;
        static final String RED_KEY = "red";
        static final String GREEN_KEY = "green";
        static final String BLUE_KEY = "blue";
        static final String TYPE_KEY = "type";
        static final String LOCATION_KEY = "location";
        static final String LOCATION2_KEY = "location2";
        static final int ACTION_PUT_TYPE = 0;
        static final int ACTION_GET_TYPE = 1;
        static final int ACTION_COMPARE_TYPE = 2; // not yet supported.
        static final int ACTION_SWAP_TYPE = 3;
        static final int ACTION_ARROW_HEIGHT= 15;
        static final int ACTION_ARROWHEAD_SIZE = 4;

}
