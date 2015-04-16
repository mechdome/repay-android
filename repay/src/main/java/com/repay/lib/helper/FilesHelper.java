package com.repay.lib.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Matt Allen
 * 16/04/15
 * mattallen092@gmail.com
 */
public class FilesHelper
{
	private static FilesHelper instance;

	private String mCacheDir, mFilesDir;

	public static FilesHelper getInstance()
	{
		if (instance == null)
		{
			synchronized (FilesHelper.class)
			{
				if (instance == null)
				{
					instance = new FilesHelper();
				}
			}
		}
		return instance;
	}

	public void setCacheDir(String cacheDir)
	{
		if (!cacheDir.substring(cacheDir.length()-1).equalsIgnoreCase("/"))
		{
			mCacheDir = cacheDir + "/";
		}
		else
		{
			mCacheDir = cacheDir;
		}
	}

	public void setFilesDir(String filesDir)
	{
		if (!filesDir.substring(filesDir.length()-1).equalsIgnoreCase("/"))
		{
			mFilesDir = filesDir + "/";
		}
		else
		{
			mFilesDir = filesDir;
		}
	}

	private void write(String fileName, String content) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(mCacheDir + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(content.getBytes());
		bos.close();
		fos.close();
	}

	public void writeToCache(String fileName, String content) throws IOException
	{
		write(mCacheDir + fileName, content);
	}

	public void writeToFiles(String fileName, String content) throws IOException
	{
		write(mFilesDir + fileName, content);
	}

	public boolean fileExistsInCache(String fileName)
	{
		return new File(mCacheDir + fileName).exists();
	}

	public boolean fileExistsInFiles(String fileName)
	{
		return new File(mFilesDir + fileName).exists();
	}
}
