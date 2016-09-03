package com.artifactgaming.doujin2PDF;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

public class Doujin2PDFApplication extends WindowAdapter {
	
	private JFrame mainWindow;
	
	private JButton inputBrowseButton;
	private JButton outputBrowseButton;
	private JButton startConversionButton;
	
	private JLabel inputTextField;
	private JLabel outputTextField;
	
	private File inputFile;
	private File outputFile;
	
	private JRadioButton radioButtonMultiPDF;
	private JRadioButton radioButtonSinglePDF;
	
	private JRadioButton radioAllSameHeight;
	private JRadioButton radioAllSameWidth;
	private JRadioButton radioNoResize;
	
	private JFrame loadingWindow;
	private JProgressBar progressBar;
	
	public Doujin2PDFApplication() {
		mainWindow = new JFrame("Doujin -2- PDF");
		mainWindow.setSize(800,330);
		mainWindow.setMinimumSize(new Dimension(600, 330));
		mainWindow.setLocationRelativeTo(null);;
		mainWindow.setLayout(new GridBagLayout());
        GridBagConstraints layoutConstraint = new GridBagConstraints();
        layoutConstraint.gridwidth = GridBagConstraints.REMAINDER;
        layoutConstraint.weightx = 1;
        layoutConstraint.fill = GridBagConstraints.HORIZONTAL;
		
		mainWindow.addWindowListener(this);
		
		//Input selection stuff.
		JPanel inputContainer = new JPanel();
		inputContainer.setLayout(new GridBagLayout());
		Border inputBorder = BorderFactory.createTitledBorder("Input File");
		inputContainer.setBorder(inputBorder);
		
		inputTextField = new JLabel();
		inputTextField.setText("None selected.");
		
		inputBrowseButton = new JButton("Browse");
		
		inputBrowseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				browseForInputFile();
			}
		});

		
		GridBagConstraints inputTextConstraint = new GridBagConstraints();
		inputTextConstraint.gridwidth = 2;
		inputTextConstraint.gridheight = 1;
		inputTextConstraint.anchor = GridBagConstraints.LINE_START;
		inputTextConstraint.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints inputButtonConstraint = new GridBagConstraints();
		inputButtonConstraint.gridwidth = 2;
		inputButtonConstraint.gridheight = 1;
		inputButtonConstraint.weightx = 1;
		inputButtonConstraint.anchor = GridBagConstraints.LINE_END;
		
		inputContainer.add(inputTextField, inputTextConstraint);
		inputContainer.add(inputBrowseButton, inputButtonConstraint);

		mainWindow.add(inputContainer, layoutConstraint);
		
		//Output selection stuff.
		JPanel outputContainer = new JPanel();
		outputContainer.setLayout(new GridBagLayout());
		Border outputBorder = BorderFactory.createTitledBorder("Output Folder");
		outputContainer.setBorder(outputBorder);
		
		outputTextField = new JLabel();
		outputTextField.setText("None selected.");
		
		outputBrowseButton = new JButton("Browse");
		
		outputBrowseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				browseForOutputFile();
			}
		});
		
		GridBagConstraints outputTextConstraint = new GridBagConstraints();
		outputTextConstraint.gridwidth = 2;
		outputTextConstraint.gridheight = 1;
		outputTextConstraint.anchor = GridBagConstraints.LINE_START;
		outputTextConstraint.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints outputButtonConstraint = new GridBagConstraints();
		outputButtonConstraint.gridwidth = 2;
		outputButtonConstraint.gridheight = 1;
		outputButtonConstraint.weightx = 1;
		outputButtonConstraint.anchor = GridBagConstraints.LINE_END;

		outputContainer.add(outputTextField, outputTextConstraint);
		outputContainer.add(outputBrowseButton, outputButtonConstraint);
		
		mainWindow.add(outputContainer, layoutConstraint);
		
		//Chapter organization radio buttons
		Border chapterOrganizationBorder = BorderFactory.createTitledBorder("Chapter Organization");
		JPanel chapterOrganizationContainer = new JPanel();
		chapterOrganizationContainer.setBorder(chapterOrganizationBorder);
		chapterOrganizationContainer.setLayout(new GridLayout(2, 1));
		
		radioButtonMultiPDF = new JRadioButton("Output each chapter as it's own PDF.", true);
		radioButtonSinglePDF = new JRadioButton("Combine all chapters into a single PDF.");
		
		ButtonGroup chapterOrganizationGroup = new ButtonGroup();
		chapterOrganizationGroup.add(radioButtonMultiPDF);
		chapterOrganizationGroup.add(radioButtonSinglePDF);
		
		chapterOrganizationContainer.add(radioButtonMultiPDF);
		chapterOrganizationContainer.add(radioButtonSinglePDF);
		
		mainWindow.add(chapterOrganizationContainer, layoutConstraint);
		
		//Image resize radio buttons
		Border pageResizeBorder = BorderFactory.createTitledBorder("Page Resize Options");
		JPanel pageResizeContainer = new JPanel();
		pageResizeContainer.setBorder(pageResizeBorder);
		pageResizeContainer.setLayout(new GridLayout(1, 2));
		
		JPanel resizeRadioButtons = new JPanel();
		resizeRadioButtons.setLayout(new GridLayout(3, 1));
		
		radioAllSameWidth = new JRadioButton("All pages are to be the same width.", true);
		radioAllSameHeight = new JRadioButton("All pages are to be the same height.");
		radioNoResize = new JRadioButton("Do not change page sizes.");
		
		ButtonGroup pageResizeGroup = new ButtonGroup();
		pageResizeGroup.add(radioAllSameWidth);
		pageResizeGroup.add(radioAllSameHeight);
		pageResizeGroup.add(radioNoResize);
		
		resizeRadioButtons.add(radioAllSameWidth);
		resizeRadioButtons.add(radioAllSameHeight);
		resizeRadioButtons.add(radioNoResize);
		
		pageResizeContainer.add(resizeRadioButtons);
		
		JLabel resizeExplination = new JLabel();
		resizeExplination.setText("<html>Some images provided may be awkwardly large or small. " + 
                				  "I can resize them so they all fit relatively well on your " +
                				  "screen. I can make them all the same height, all the same " +
								  "width, or just not modify them at all.</html>");
		pageResizeContainer.add(resizeExplination);
		
		mainWindow.add(pageResizeContainer, layoutConstraint);
		
		//Start button.
		startConversionButton = new JButton("Start Conversion");
		startConversionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				convertToPDF();
			}
		});
		
		mainWindow.add(startConversionButton);
		
		//Render the loading window.
		loadingWindow = new JFrame("Converting");
		loadingWindow.setSize(500, 150);
		loadingWindow.setResizable(false);
		loadingWindow.setLayout(new GridLayout(1, 1));
		loadingWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		loadingWindow.add(progressBar);
	}
	
	public void run() {
		mainWindow.setVisible(true);
	}
	
	/************************************************************************
	 * This will get the input zip file the user wishes to convert into a PDF.
	 * It stores that information in inputFile and automatically updates inputTextField
	 * to reflect the user's input. It will also auto-generate an output destination
	 * based on the input file destination.
	 * Parameters: void
	 * Returns: void
	 ************************************************************************/
	private void browseForInputFile() {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (!file.isDirectory()) {
					
			        String name = file.getName();
			        int i = name.lastIndexOf('.');
					
			        //Make sure there was an extension.
		        	if (i > 0 &&  i < name.length() - 1) {
						return name.substring(i + 1).toLowerCase().equals("zip");	
		        	}
		        	
		        	return false;
				}
				
				return true;
			}

			@Override
			public String getDescription() {
				return "ZIP files and directories";
			}
			
		});
		
        int returnVal = fc.showOpenDialog(mainWindow);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	inputFile = fc.getSelectedFile();
        	inputTextField.setText(inputFile.getAbsolutePath());
        	
			String outputFileName = null;
	        String name = inputFile.getAbsolutePath();
	        int i = name.lastIndexOf('.');
	        //We don't check if the .zip is actually there, because the file chooser should have insured that for us.
	        outputFileName = name.substring(0, i);
        	
        	outputFile = new File(outputFileName);
        	outputTextField.setText(outputFile.getAbsolutePath());
        }
	}
	
	/************************************************************************
	 * This will get the output folder destination from the user. It will
	 * store the user's selection to outputFile and automatically update
	 * outputTextField to reflect the user's selection.
	 * Parameters: void
	 * Returns: void
	 ************************************************************************/
	private void browseForOutputFile() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
        int returnVal = fc.showOpenDialog(mainWindow);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	outputFile = fc.getSelectedFile();
        	outputTextField.setText(outputFile.getAbsolutePath());
        }
	}
	
	public void convertToPDF() {
		new Thread() {//We have to do this in a thread or the graphics will freeze as we process this.
			public void run() {
				mainWindow.setVisible(false);
				
				loadingWindow.setLocationRelativeTo(null);
				progressBar.setIndeterminate(true);
				progressBar.setString("Getting started.");
				loadingWindow.setVisible(true);
				int progress = 0;
				
				if (inputFile == null) {
					JOptionPane.showMessageDialog(mainWindow, "Please select an input file to create the PDF from.", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (outputFile == null) {
					JOptionPane.showMessageDialog(mainWindow, "Please select an output folder to put PDFs in.", "Error", JOptionPane.ERROR_MESSAGE);
				} else try {//We can't have this throw, because it's called by a listener.
					ImageProvider provider = new ZipFileImageProvider(inputFile);
					
					int resizeMode = PDFConverter.NO_RESIZE;
					
					if (radioAllSameHeight.isSelected()) {
						resizeMode = PDFConverter.SAME_HEIGHT;
					} else if (radioAllSameWidth.isSelected()) {
						resizeMode = PDFConverter.SAME_WIDTH;
					}
					
					PDFConverter converter = new PDFConverter(provider, outputFile, radioButtonMultiPDF.isSelected(), resizeMode);
					progressBar.setMaximum(provider.totalPages());
					progressBar.setValue(0);
					progressBar.setIndeterminate(false);
					
					String status;
					while ((status = converter.takeStep())  != null) {
						progressBar.setValue(progress++);
						progressBar.setString(status);
					}
					
					converter.close();
					provider.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				loadingWindow.setVisible(false);
				mainWindow.setVisible(true);
			}
		}.start();
	}
	
	public void windowClosing(WindowEvent windowEvent){
        System.out.println("Window Closing.");
        System.exit(0);
    }

	public static void main(String[] args) {
		Doujin2PDFApplication app = new Doujin2PDFApplication();
		app.run();
	}

}
