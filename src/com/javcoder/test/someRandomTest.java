package com.javcoder.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class someRandomTest {

	private static ExecutorService exec = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors() + 1);

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println(Arrays.toString(args));
			System.err
					.println("�÷�\njava -jar JpgConvert.jar ���� input�ļ��� output�ļ���");
			System.exit(-1);
		}

		File folder = new File(args[1]);
		if (!folder.exists()) {
			System.err.println("input�ļ���" + args[1] + "������");
			return;
		}

		File outputFolder = new File(args[2]);
		if (outputFolder.exists()) {
			outputFolder.delete();
		}

		outputFolder.mkdir();

		long startT = System.currentTimeMillis();
		int latchCount = 0;

		if (folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				String fileName = file.getName().toLowerCase();

				if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
						|| fileName.endsWith(".bmp")) {
					latchCount++;
				}
			}

			CountDownLatch latch = new CountDownLatch(latchCount);

			for (File file : folder.listFiles()) {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
						|| fileName.endsWith(".bmp")) {
					exec.execute(new Generator(file, args[0], args[2], latch));
				}
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endT = System.currentTimeMillis();

			System.out.println("Done. Cost time: " + (endT - startT) / 1000f
					+ " sec");
			System.exit(0);
		} else {
			System.err.println("input�ļ���" + args[1] + "���Ǹ��ļ���");
		}

	}

	private static class Generator implements Runnable {
		File file;
		String cmd;
		String output;
		CountDownLatch latch;

		private Generator(File file, String cmd, String output,
				CountDownLatch latch) {
			super();
			this.file = file;
			this.cmd = cmd;
			this.output = output;
			this.latch = latch;
		}

		public void run() {
			try {
				String fileName = file.getName().toLowerCase();
				int pos = fileName.lastIndexOf(".jpg");
				if (pos < 0) {
					pos = fileName.lastIndexOf(".png");

					if (pos < 0) {
						pos = fileName.lastIndexOf(".bmp");
					}
				}

				if (pos < 0) {
					System.err.println("error: " + fileName);
					return;
				}

				String outputName = fileName.substring(0, pos);

				System.out.println("���ڴ��� " + file.getName());
				cmd = cmd + " --sheet " + output + File.separator + outputName
						+ ".jpg" + " " + file.getAbsolutePath();
				System.out.println(cmd);
				Runtime run = Runtime.getRuntime();
				Process pr = null;
				try {
					pr = run.exec(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					pr.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				latch.countDown();
			}
		}
	}

	
	
//	"C:\Program Files (x86)\TexturePacker\bin\TexturePacker.exe --format json --width 200 --height 200 --no-trim --sheet  *.png  --opt RGBA4444 " "d:\inputbmp" "d:\outputbmp30"
}