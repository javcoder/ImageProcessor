package com.javcoder.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Png2Bmp_ReplaceAlpha {


	public static List<String> getAllFileNames(String dir, String fileExtension) throws Exception {
		File root = new File(dir);
		if(!root.exists() || !root.isDirectory()) {
			throw new Exception("Directory: " + dir + "does not exist!");
		}
		List<String> fileNames = new ArrayList<String>();
		File[] files = root.listFiles();
		
		
		return null;
	}
}
