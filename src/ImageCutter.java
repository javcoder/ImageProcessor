import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class ImageCutter {

	private static ExecutorService exec = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors() + 1);
	private static CountDownLatch latch;

	private static Map<String, Integer> imageTypes;

	static {
		imageTypes = new HashMap<String, Integer>();
		imageTypes.put("bmp", 5);
		imageTypes.put("jpg", 5);
		imageTypes.put("png", 6);
	}

	private static String inputBaseDir;
	private static String outputBaseDir;

	private static String cuttingFileExtension;
	private static Integer cuttingFileImageType;

	private static int requiredWidth;
	private static int requiredHeight;

	private BufferedImage biOfOriginalImage;
	private String sourceFileName;

	private String targetDirectoryOfCuttingFile;

	public static void process(String inputDir, String outputDir,
			String targetFileExtension, int width, int height) throws Exception {

		if (inputDir == null || outputDir == null
				|| targetFileExtension == null) {
			throw new NullPointerException();
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

		inputBaseDir = inputFolder.getAbsolutePath();
		outputBaseDir = outputFolder.getAbsolutePath();

		cuttingFileExtension = targetFileExtension;
		cuttingFileImageType = imageTypes.get(cuttingFileExtension);

		requiredWidth = width;
		requiredHeight = height;

		System.out.println("start cutting...");
		cut(inputFolder);
		System.out.println("all cutting done...");
		
		exec.shutdown();
	}

	private static void cut(File inputFolder) throws Exception {
		for (File file : inputFolder.listFiles()) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
				new ImageCutter().cut0(file);
			} else if (file.isDirectory()) {
				cut(file);
			}
		}
	}

	private void cut0(File file) throws Exception {
		/*
		 * Init
		 */
		biOfOriginalImage = ImageIO.read(file);

		sourceFileName = file.getName();
		sourceFileName = sourceFileName.substring(0,
				sourceFileName.lastIndexOf("."));

		targetDirectoryOfCuttingFile = mkdirTargetDirectoryOfCuttingFile(file);

		/*
		 * Cutting
		 */
		int imgWidth = biOfOriginalImage.getWidth();
		int imgHeight = biOfOriginalImage.getHeight();

		int horizontalCuttingNum = imgWidth / requiredWidth;
		int verticalCuttingNum = imgHeight / requiredHeight;

		latch = new CountDownLatch(horizontalCuttingNum * verticalCuttingNum);

		System.out.println("cutting " + file.getAbsolutePath() + "...");
		for (int i = 0; i < horizontalCuttingNum; i++) {
			for (int j = 0; j < verticalCuttingNum; j++) {
				exec.execute(new ImageCutWorker(i * requiredWidth, j
						* requiredHeight));
			}
		}
	
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("cut " + file.getAbsolutePath() + " done!");
	}

	private String mkdirTargetDirectoryOfCuttingFile(File inputDir)
			throws Exception {
		String outputDirPath = getOutputPath(inputDir);
		File file = new File(outputDirPath);
		if(!file.exists()) {
			if(!file.mkdirs()) {
				throw new Exception("mkdir failure, dir: " + outputDirPath);
			}
		}
		return outputDirPath;
	}

	private static String getOutputPath(File file) {
		String inputFilePath = file.getAbsolutePath();
		String cutPath = inputFilePath.substring(inputBaseDir.length());

		if (cutPath.startsWith(File.separator)) {
			cutPath = cutPath.substring(1);
		}

		String outputPath = null;

		if (outputBaseDir.endsWith(File.separator)) {
			outputPath = outputBaseDir + cutPath;
		} else {
			outputPath = outputBaseDir + File.separator + cutPath;
		}
		outputPath = outputPath.substring(0, outputPath.lastIndexOf("."));
		return outputPath;
	}

	private class ImageCutWorker implements Runnable {

		public ImageCutWorker(int horizontalCuttingPoint,
				int verticalCuttingPoint) {
			this.horizontalCuttingPoint = horizontalCuttingPoint;
			this.verticalCuttingPoint = verticalCuttingPoint;
		}

		private int horizontalCuttingPoint;
		private int verticalCuttingPoint;

		@Override
		public void run() {
			BufferedImage bi = new BufferedImage(requiredWidth, requiredHeight,
					cuttingFileImageType);

			for (int i = 0; i < requiredWidth; i++) {
				for (int j = 0; j < requiredHeight; j++) {
					bi.setRGB(i, j, biOfOriginalImage.getRGB(
							horizontalCuttingPoint + i, verticalCuttingPoint
									+ j));
				}
			}

			try {
				ImageIO.write(bi, cuttingFileExtension, new File(
						targetDirectoryOfCuttingFile + File.separator
								+ sourceFileName + "_" + horizontalCuttingPoint
								/ requiredWidth + "_" + verticalCuttingPoint
								/ requiredHeight + "." + cuttingFileExtension));
			} catch (IOException e) {
				e.printStackTrace();
			}

			latch.countDown();
		}
	}
}
