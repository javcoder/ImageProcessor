package com.javcoder.test;

import java.io.File;
import java.io.IOException;

public class T1 {

	public static void main(String[] args) {
		File file = new File("E:/temp/1.png");
		
		System.out.println(file.getAbsoluteFile());
		System.out.println(file.getAbsolutePath());
		try {
			System.out.println(file.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(file.getName());
		System.out.println(file.getParent());
		System.out.println(file.getPath());
		System.out.println(file.getParentFile());
		
		
		System.out.println(File.separator);
	}
}
