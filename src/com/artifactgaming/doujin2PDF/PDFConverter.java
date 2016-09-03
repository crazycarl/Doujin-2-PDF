package com.artifactgaming.doujin2PDF;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFConverter {
	private ImageProvider source;
	private PDDocument document;
	private File targetDirectory;
	
	private String chapter = null;
	
	private boolean multiPDF;
	private int resizeMode;
	private int maxSize = 0;
	
	public PDFConverter(ImageProvider source, File targetDirectory, boolean multiPDF, int resizeMode) throws IOException {
		this.source = source;
		this.targetDirectory = targetDirectory;
		this.multiPDF = multiPDF;
	
		this.resizeMode = resizeMode;
		if (resizeMode == SAME_WIDTH) {
			chapter = source.nextChapter();
			do {
				BufferedImage image;
				while ((image = source.nextPage()) != null) {
					int width = image.getWidth();
					if (width > maxSize) maxSize = width;
				}
			} while ((chapter = source.nextChapter()) != null);
		} else if (resizeMode == SAME_HEIGHT) {
			chapter = source.nextChapter();
			{
				BufferedImage image;
				while ((image = source.nextPage()) != null) {
					int height = image.getHeight();
					if (height > maxSize) maxSize = height;
				}
			} while ((chapter = source.nextChapter()) != null);
		}
		
		source.reset();
		
		//Start by selecting our first chapter.
		chapter = source.nextChapter();
		
		if (multiPDF) {
			targetDirectory.mkdirs();
		}
		
		document = new PDDocument();
	}
	
	public String takeStep() throws IOException {
		
		if (chapter != null) {
			BufferedImage pageImage = source.nextPage();
			
			if (pageImage == null) {
				
				//The first thing we do is save the PDF.
				if (multiPDF) {
					document.save(new File(targetDirectory + "/" + chapter + ".pdf"));
					document.close();
					document = new PDDocument();
				}
				
				//We do nothing when saving as a mulit-pdf.
				
				chapter = source.nextChapter();
				if (chapter == null) return "Job's done.";
				
				return "Moving on to next chapter.";
			}

			PDImageXObject pdImage = LosslessFactory.createFromImage(document, pageImage);
			
			int width = pdImage.getWidth();
			int height = pdImage.getHeight();
			
			if (resizeMode == SAME_WIDTH) {
				float scale = ((float)maxSize) / width;
				width = maxSize;
				height = (int)(height * scale);
			} else if (resizeMode == SAME_HEIGHT) {
				float scale = ((float)maxSize) / height;
				height = maxSize;
				width = (int)(width * scale);
			}
			
			PDRectangle rec = new PDRectangle(width, height);
            PDPage blankPage = new PDPage(rec);
			
			PDPageContentStream contentStream = new PDPageContentStream(document, blankPage, AppendMode.APPEND, true);
			contentStream.drawImage(pdImage, 20, 20, width, height);
			contentStream.close();
			
			document.addPage(blankPage);
			
			return "Inserting chapter " + chapter + " into PDF document.";		
		}
		
		return null;
	}
	
	public void close() throws IOException {
		//We create the new instance of a PDF document without checking
		//if another one is even needed. Because of that, we just have to
		//make a quick cleanup.
		if (document != null) {
			if (!multiPDF) document.save(new File(targetDirectory + ".pdf"));
			document.close();
		}
	}
	
	static public boolean isCompatableFormat(String extension) {
		if (extension.equals("png")) return true;
		if (extension.equals("jpg")) return true;
		if (extension.equals("jpeg")) return true;
		if (extension.equals("gif")) return true;
		
		return false;
	}
	
	public static final int SAME_HEIGHT = 0;
	public static final int SAME_WIDTH = 1;
	public static final int NO_RESIZE = 2;
}
