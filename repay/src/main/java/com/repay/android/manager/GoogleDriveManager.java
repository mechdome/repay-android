package com.repay.android.manager;

import android.content.Context;

/**
 * Manager for talking to the Google Drive API - part of Google Play Services
 *
 * @author Matt Allen
 * @project Repay
 */
public class GoogleDriveManager
{
	public static interface OnBackupCompleteListener
	{
		public void onBackup();
	}

	public static interface OnRestoreCompleteListener
	{
		public void onRestore();
	}

	private static GoogleDriveManager instance;

	public static GoogleDriveManager getInstance()
	{
		if (instance == null)
		{
			synchronized (GoogleDriveManager.class)
			{
				if (instance == null)
				{
					instance = new GoogleDriveManager();
				}
			}
		}
		return instance;
	}

	/**
	 * If the database file already exists in Google Drive
	 * @return
	 */
	public boolean doesFileExist()
	{
		return false;
	}

	public void doBackup(Context context, OnBackupCompleteListener listener)
	{
		// Check file exists
		// Put file in Google Drive
	}

	public void doRestore(Context context, String path, OnRestoreCompleteListener listener)
	{
		// Check file exists on Drive
		// Replace local file with one from Drive
	}

	/**
	 * Set the path used for all future SQLite backups.
	 * If path is not set, auto backups will fail.
	 *
	 * @param path Path to use in Drive
	 */
	public void setBackupPath(String path)
	{

	}

	public String getBackupPath()
	{
		return null;
	}
}
