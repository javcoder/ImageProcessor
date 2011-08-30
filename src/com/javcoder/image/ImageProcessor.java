package com.javcoder.image;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ImageProcessor {

	private static JFrame frame = new JFrame("图像处理器");
	private static Container container = frame.getContentPane();

	private static JLabel cutImageFileLabel;
	private static JLabel outputDirectoryLabel;
	private static JLabel cutWidthLabel;
	private static JLabel cutHeightLabel;
	private static JLabel jpgQualityLabel;
	private static JLabel texturePackerLocationLabel;

	private JTextField cutImageFileChooserTextField;
	private JTextField outputDirectoryChooserTextField;
	private JTextField cutWidthTextField;
	private JTextField cutHeightTextField;
	private JTextField jpgQualityTextField;
	private JTextField texturePackerLocationTextField;

	private JButton cutImageFileChooserButton;
	private JButton outputDirectoryChooserButton;
	private JButton texturePackerLocationChooserButton;

	private JButton cutAndConvertButton;

	private JFileChooser fileChooser;

	public ImageProcessor() {
		initWidget();
		addWidget();
		addListener();
		configFrame();
	}

	private void initWidget() {
		container.setLayout(null);

		// new
		cutImageFileLabel = new JLabel("被切割图片的位置: ");
		outputDirectoryLabel = new JLabel("切割后的输出路径: ");
		cutWidthLabel = new JLabel("图片被切割的宽度: ");
		cutHeightLabel = new JLabel("切割被切割的高度: ");
		jpgQualityLabel = new JLabel("图片转换要求质量: ");
		texturePackerLocationLabel = new JLabel("TexturePacker位置: ");

		cutImageFileChooserTextField = new JTextField();
		outputDirectoryChooserTextField = new JTextField();
		cutWidthTextField = new JTextField();
		cutHeightTextField = new JTextField();
		jpgQualityTextField = new JTextField();
		texturePackerLocationTextField = new JTextField();

		cutImageFileChooserButton = new JButton("浏览");
		outputDirectoryChooserButton = new JButton("浏览");
		texturePackerLocationChooserButton = new JButton("浏览");

		cutAndConvertButton = new JButton("切割并转换");

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("C:/"));

		// setBounds
		cutImageFileLabel.setBounds(10, 10, 120, 20);
		outputDirectoryLabel.setBounds(10, 40, 120, 20);
		cutWidthLabel.setBounds(10, 70, 120, 20);
		cutHeightLabel.setBounds(10, 100, 120, 20);
		jpgQualityLabel.setBounds(10, 130, 120, 20);
		texturePackerLocationLabel.setBounds(10, 160, 120, 20);

		cutImageFileChooserTextField.setBounds(140, 10, 150, 20);
		outputDirectoryChooserTextField.setBounds(140, 40, 150, 20);
		cutWidthTextField.setBounds(140, 70, 150, 20);
		cutHeightTextField.setBounds(140, 100, 150, 20);
		jpgQualityTextField.setBounds(140, 130, 150, 20);
		texturePackerLocationTextField.setBounds(140, 160, 150, 20);

		cutImageFileChooserButton.setBounds(300, 10, 60, 20);
		outputDirectoryChooserButton.setBounds(300, 40, 60, 20);
		texturePackerLocationChooserButton.setBounds(300, 160, 60, 20);

		cutAndConvertButton.setBounds(10, 200, 100, 25);
		
		cutImageFileChooserTextField.setEditable(false);
		outputDirectoryChooserTextField.setEditable(false);
		texturePackerLocationTextField.setEditable(false);
	}

	private void addWidget() {
		frame.add(cutImageFileLabel);
		frame.add(outputDirectoryLabel);
		frame.add(cutWidthLabel);
		frame.add(cutHeightLabel);
		frame.add(jpgQualityLabel);
		frame.add(texturePackerLocationLabel);

		frame.add(cutImageFileChooserTextField);
		frame.add(outputDirectoryChooserTextField);
		frame.add(cutWidthTextField);
		frame.add(cutHeightTextField);
		frame.add(jpgQualityTextField);
		frame.add(texturePackerLocationTextField);

		frame.add(cutImageFileChooserButton);
		frame.add(outputDirectoryChooserButton);
		frame.add(texturePackerLocationChooserButton);

		frame.add(cutAndConvertButton);
	}

	private void addListener() {
		ActionListener listener = new FileChooserListener();
		cutImageFileChooserButton.addActionListener(listener);
		outputDirectoryChooserButton.addActionListener(listener);
		texturePackerLocationChooserButton.addActionListener(listener);

		cutAndConvertButton.addActionListener(new CutConvertListener());
	}

	private void configFrame() {
		frame.setSize(400, 280);
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();
		frame.setLocation((int) (screenWidth / 2) - 300,
				(int) (screenHeight / 2) - 250);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private class FileChooserListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(cutImageFileChooserButton)) {
				String path = showFileChooserDialog(false);
				if (path != null) {
					cutImageFileChooserTextField.setText(path);
				}
			} else if (source.equals(outputDirectoryChooserButton)) {
				String path = showFileChooserDialog(true);
				if (path != null) {
					outputDirectoryChooserTextField.setText(path);
				}
			} else if (source.equals(texturePackerLocationChooserButton)) {
				String path = showFileChooserDialog(false);
				if (path != null) {
					texturePackerLocationTextField.setText(path);
				}
			}
		}

		private String showFileChooserDialog(boolean isDir) {
			if (isDir) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}

			int state = fileChooser.showOpenDialog(null);

			if (state == JFileChooser.APPROVE_OPTION) {
				return fileChooser.getSelectedFile().getAbsolutePath();
			} else {
				return null;
			}
		}
	}

	private class CutConvertListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cutImage = cutImageFileChooserTextField.getText().trim();
			String outputDir = outputDirectoryChooserTextField.getText().trim();
			String cutWidth = cutWidthTextField.getText().trim();
			String cutHeight = cutHeightTextField.getText().trim();
			String jpgQuality = jpgQualityTextField.getText().trim();
			String texturePackerLocation = texturePackerLocationTextField
					.getText().trim();

			if (jpgQuality.equals("")) {
				jpgQuality = null;
			}
			
			// validate
			if(empty(cutImage) || empty(outputDir) || empty(cutWidth) || empty(cutHeight) || empty(texturePackerLocation)) {
				JOptionPane.showMessageDialog(null, "除图片质量外，其他都不能为空！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!isNumeric(cutWidth)) {
				JOptionPane.showMessageDialog(null, "切割宽度必须为数字！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!isNumeric(cutHeight)) {
				JOptionPane.showMessageDialog(null, "切割高度必须为数字！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (jpgQuality != null && !isNumeric(jpgQuality)) {
				JOptionPane.showMessageDialog(null, "图片质量必须为数字！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// validate end
			
			String imageName = cutImage.substring(
					cutImage.lastIndexOf("\\") + 1, cutImage.lastIndexOf("."));
			String cutImageOutputDir = outputDir + "\\cut\\" + imageName;
			new File(cutImageOutputDir).mkdirs();

			try {
				ImageCutter.cut(cutImage, cutImageOutputDir, "bmp",
						Integer.valueOf(cutWidth), Integer.valueOf(cutHeight));
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "切割失败！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "切割失败！", "信息提示",
						JOptionPane.ERROR_MESSAGE);
			}

			try {
				ImageConverter_tojpg.process(texturePackerLocation, jpgQuality,
						cutImageOutputDir, outputDir);
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "转换失败", "信息提示",
						JOptionPane.ERROR_MESSAGE);
			}

			JOptionPane.showMessageDialog(null, "切割、转换成功！", "信息提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static boolean empty(String s) {
		if(s.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static void main(String[] args) {
		new ImageProcessor();
	}
}
