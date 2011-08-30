package com.javcoder.image;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageConverter_topng {

	private static ExecutorService exec = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors() + 1);

	private static final String defaultTexturePackerParameters = "--format json --no-trim --allow-free-size";

	private static String texturePackerLocation;
	private static String texturePackerParameters;

	private static String inputBaseDir;
	private static String outputBaseDir;

	public static void process(String texturePackerLocation,
			String texturePackerParameters, String inputDir, String outputDir)
			throws Exception {

		if (texturePackerLocation == null || inputDir == null
				|| outputDir == null) {
			throw new NullPointerException();
		}

		ImageConverter_topng.texturePackerLocation = texturePackerLocation;
		if (texturePackerParameters != null) {
			ImageConverter_topng.texturePackerParameters = texturePackerParameters;
		} else {
			ImageConverter_topng.texturePackerParameters = defaultTexturePackerParameters;
		}

		File inputFolder = new File(inputDir);
		if (!inputFolder.exists() && !inputFolder.isDirectory()) {
			throw new Exception("inputDir: " + inputDir
					+ " doesn't exist or isn't a directory!");
		}

		File outputFolder = new File(outputDir);
		if (outputFolder.exists() && !outputFolder.isDirectory()) {
			throw new Exception("outputDir: " + outputDir
					+ " isn't a directory!");
		}

		if (!outputFolder.exists()) {
			outputFolder.mkdirs();
		}

		inputBaseDir = inputFolder.getParent();
		outputBaseDir = outputFolder.getAbsolutePath();

		mkdir(inputFolder);
		convert(inputFolder);

		exec.shutdown();
	}

	private static void convert(File rootFolder) throws Exception {
		for (File file : rootFolder.listFiles()) {
			if (file.isFile()) {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
						|| fileName.endsWith(".bmp")) {
					exec.execute(new Generator(file.getAbsolutePath(),
							getOutputPath(file)));
				}
			} else if (file.isDirectory()) {
				mkdir(file);
				convert(file);
			}
		}
	}

	private static void mkdir(File inputDir) throws Exception {
		String outputDirPath = getOutputPath(inputDir);
		if (!new File(outputDirPath).mkdir()) {
			throw new Exception("mkdir failure, dir: " + outputDirPath);
		}
	}

	private static String getOutputPath(File file) {
		String inputFilePath = file.getAbsolutePath();
		String cutPath = inputFilePath.substring(inputBaseDir.length(),
				inputFilePath.length());

		if (cutPath.startsWith("\\")) {
			cutPath = cutPath.substring(1);
		}

		String outputPath = null;

		if (outputBaseDir.endsWith("\\")) {
			outputPath = outputBaseDir + cutPath;
		} else {
			outputPath = outputBaseDir + "\\" + cutPath;
		}

		return outputPath;
	}

	private static class Generator implements Runnable {
		private String inputFile;
		private String outputFile;

		private Generator(String inputFile, String outpuFile) {
			this.inputFile = inputFile;
			this.outputFile = outpuFile;
		}

		public void run() {
			String cmd = texturePackerLocation + " " + texturePackerParameters
					+ " --sheet " + outputFile + " --opt RGBA4444 " + inputFile;
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
		}
	}

	public static void main(String[] args) throws Exception {

		String tl = "E:/Javcoder/TexturePacker/bin/TexturePacker.exe";
		String tp = "--format json --no-trim --allow-free-size";

		String inputDir = "E:/单手近战";
		String outputDir = "E:/del";

		ImageConverter_topng.process(tl, tp, inputDir, outputDir);
	}
}
