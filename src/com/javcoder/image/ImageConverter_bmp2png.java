package com.javcoder.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class ImageConverter_bmp2png {

	public static void main(String[] args) {
		String directory = ".";
		ImageConverter_bmp2png.convert(directory);
	}

	/**
	 * Main Body
	 */
	static ExecutorService exec = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors());

	public static void convert(String directory) {
		String[] fileNames = getFileNames(directory);
		for (String fileName : fileNames) {
			exec.execute(new ImageWorker(directory, fileName));
		}
		exec.shutdown();
	}

	private static String[] getFileNames(String sd) {
		File path = new File(sd);
		String[] list;

		list = path.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".bmp")) {
					return true;
				}
				return false;
			}
		});

		return list;
	}

	/**
	 * Image Converter Filter
	 */
	static class ICFilter extends RGBImageFilter {

		private int rgb;

		public ICFilter(int rgb) {
			this.rgb = rgb;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			if (this.rgb == rgb) {
				return 0;
			}
			return rgb;
		}
	}

	/**
	 * Image Worker
	 */
	static class ImageWorker implements Runnable {
		File sourceFile;
		File targetFile;

		public ImageWorker(String directory, String fileName) {
			super();
			this.sourceFile = new File(directory + "/" + fileName);

			File file = new File(directory + "/output");
			if (!file.exists() || !file.isDirectory()) {
				file.mkdir();
			}

			fileName = fileName.substring(0, fileName.lastIndexOf("."))
					+ ".png";
			this.targetFile = new File(directory + "/output/" + fileName);
		}

		@Override
		public void run() {
			BufferedImage bi = null;
			try {
				bi = ImageIO.read(sourceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ImageFilter imgf = new ICFilter(bi.getRGB(0, 0));
			FilteredImageSource fis = new FilteredImageSource(bi.getSource(),
					imgf);
			Image im = Toolkit.getDefaultToolkit().createImage(fis);
			im.flush();

			BufferedImage newImage = new BufferedImage(bi.getWidth(),
					bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = newImage.createGraphics();
			g.drawImage(im, 0, 0, null);
			g.dispose();
			newImage.flush();

			try {
				ImageIO.write(newImage, "png", targetFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
