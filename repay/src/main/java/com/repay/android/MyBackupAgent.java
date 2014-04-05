package com.repay.android;

import java.io.IOException;

import com.repay.android.database.DatabaseHandler;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 * This class is currently not functional. It is called by the Android system but does not perform correctly
 *
 */

public class MyBackupAgent extends BackupAgentHelper {

	private static final String TAG = MyBackupAgent.class.getName();

	// A key to uniquely identify the set of backup data
	static final String DATABASE_BACKUP_KEY = "repaydb";

	@Override
	public void onCreate() {
		FileBackupHelper dbHelper = new FileBackupHelper(this, this.getDatabasePath(DatabaseHandler.DB_NAME).getAbsolutePath());
		addHelper(DATABASE_BACKUP_KEY, dbHelper);
		Log.i(TAG, "Backup Agent Initialised");
		Log.i(TAG, this.getDatabasePath(DatabaseHandler.DB_NAME).getAbsolutePath());
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		// Hold the lock while the FileBackupHelper performs backup
		super.onBackup(oldState, data, newState);
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		// Hold the lock while the FileBackupHelper restores the file
		super.onRestore(data, appVersionCode, newState);
	}
}
