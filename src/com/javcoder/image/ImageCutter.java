package com.javcoder.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class ImageCutter implements Runnable {

	private static CountDownLatch latch;
	
	private static Map<String, Integer> imageTypes;

	static {
		System.out.println("init image types...");
		imageTypes = new HashMap<String, Integer>();
		imageTypes.put("bmp", 5);
		imageTypes.put("jpg", 5);
		imageTypes.put("png", 6);
		System.out.println("init image types done");
	}

	private static BufferedImage biOfOriginalImage;

	private static String sourceFileName;
	private static int sourceFileImageType;

	private static String targetDirectoryOfCuttingFile;
	private static String cuttingFileExtension;
	private static Integer cuttingFileImageType;

	private static int requiredWidth;
	private static int requiredHeight;

	private int horizontalCuttingPoint;
	private int verticalCuttingPoint;

	public ImageCutter(int horizontalCuttingPoint, int verticalCuttingPoint) {
		this.horizontalCuttingPoint = horizontalCuttingPoint;
		this.verticalCuttingPoint = verticalCuttingPoint;
	}

	@Override
	public void run() {
		BufferedImage bi = new BufferedImage(requiredWidth, requiredHeight,
				cuttingFileImageType);

		for (int i = 0; i < requiredWidth; i++) {
			for (int j = 0; j < requiredHeight; j++) {
				bi.setRGB(i, j, biOfOriginalImage.getRGB(horizontalCuttingPoint
						+ i, verticalCuttingPoint + j));
			}
		}

		try {
			ImageIO.write(bi, cuttingFileExtension, new File(
					targetDirectoryOfCuttingFile + "/" + sourceFileName + "_"
							+ horizontalCuttingPoint / requiredWidth + "_"
							+ verticalCuttingPoint / requiredHeight + "."
							+ cuttingFileExtension));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		latch.countDown();
	}

	public static void cut(String sourceFile, String targetFileDirectory,
			String targetFileExtension, int width, int height)
			throws IOException {

		/*
		 * Init
		 */
		biOfOriginalImage = ImageIO.read(new File(sourceFile));

		sourceFileName = sourceFile.substring(sourceFile.lastIndexOf("\\") + 1,
				sourceFile.lastIndexOf("."));
		
		sourceFileImageType = biOfOriginalImage.getType();
		System.out.println("sourceFileImageType: " + sourceFileImageType);

		targetDirectoryOfCuttingFile = targetFileDirectory;
		cuttingFileExtension = targetFileExtension;
		cuttingFileImageType = imageTypes.get(cuttingFileExtension);
		
		if (cuttingFileImageType == null) {
			System.err.println("can't find image type of cuttingFileExtension: "
							+ cuttingFileExtension);
			cuttingFileImageType = sourceFileImageType;
			System.err.println("using sourceFileImageType: "
					+ sourceFileImageType);
		}
		
		System.out.println("cuttingFileImageType: " + cuttingFileImageType);

		requiredWidth = width;
		requiredHeight = height;

		/*
		 * Cutting
		 */
		ExecutorService exec = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors() + 1);

		int imgWidth = biOfOriginalImage.getWidth();
		int imgHeight = biOfOriginalImage.getHeight();

		int horizontalCuttingNum = imgWidth / requiredWidth;
		int verticalCuttingNum = imgHeight / requiredHeight;

		latch = new CountDownLatch(horizontalCuttingNum * verticalCuttingNum);
		
		System.out.println("cutting...");
		for (int i = 0; i < horizontalCuttingNum; i++) {
			for (int j = 0; j < verticalCuttingNum; j++) {
				exec.execute(new ImageCutter(i * requiredWidth, j
						* requiredHeight));
			}
		}
		exec.shutdown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("cut done!");
	}

	public static void main(String[] args) throws Exception {
		ImageCutter.cut("E:/temp/十八铜人阵.png", "E:/temp/output", "bmp", 200, 200);
	}
}
