package mn.aug.restfulandroid.provider;


import android.net.Uri;

public final class TasksProviderContract {
	
	

		public static final String AUTHORITY = "WUNDERLIST";

		// TIMELINE TABLE CONTRACT
		public static final class TasksTable implements ResourceTable {

			public static final String TABLE_NAME = "TASKS";

			// URI DEFS
			static final String SCHEME = "content://";
			public static final String URI_PREFIX = SCHEME + AUTHORITY;
			private static final String URI_PATH_TASKS = "/" + TABLE_NAME;
			// Note the slash on the end of this one, as opposed to the URI_PATH_TASKS, which has no slash.
			private static final String URI_PATH_TASK = "/" + TABLE_NAME + "/";
			public static final int TASK_ID_PATH_POSITION = 1;

			// content://mn.aug.restfulandroid.timelineprovider/timeline
			public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_TASKS);
			// content://mn.aug.restfulandroid.timelineprovider/timeline/ -- used for content provider insert() call
			public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_TASK);
			// content://mn.aug.restfulandroid.timelineprovider/timeline/#
			public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_TASK + "#");

			public static final String[] ALL_COLUMNS;
			public static final String[] DISPLAY_COLUMNS;

			static {
				ALL_COLUMNS = new String[] { 
						TasksTable._ID, 
						TasksTable._STATUS, 
						TasksTable._RESULT, 					
						TasksTable.TITLE,
						TasksTable.DUE_DATE,
                        TasksTable.LIST_ID,
						TasksTable.CREATED				
				};

				DISPLAY_COLUMNS = new String[] {
						TasksTable._ID,
						TasksTable.TITLE,
                        TasksTable.DUE_DATE,
                        TasksTable.LIST_ID
				};
			}
			
			
			/**
			 * Column name for the tweet author
			 * <P>
			 * Type: TEXT
			 * </P>
			 */
			public static final String TITLE = "title";

			/**
			 * Column name for tweet content
			 * <P>
			 * Type: TEXT
			 * </P>
			 */
			public static final String DUE_DATE = "dueDate";

			/**
			 * Column name for the creation date
			 * <P>
			 * Type: LONG  (UNIX timestamp)
			 * </P>
			 */
			public static final String CREATED = "timestamp";

            /**
             * Column name for the list id
             * <P>
             * Type: LONG  (UNIX timestamp)
             * </P>
             */
            public static final String LIST_ID= "list_id";

			
			// Prevent instantiation of this class
			private TasksTable() {
			}
		}

		private TasksProviderContract() {
			// disallow instantiation
		}

}
