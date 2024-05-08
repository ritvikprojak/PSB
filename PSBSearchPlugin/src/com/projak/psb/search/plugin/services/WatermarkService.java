package com.projak.psb.search.plugin.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.ibm.ecm.extension.PluginResponseUtil;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;


public class WatermarkService extends PluginService {

	public String getId() {
		return "WatermarkService";
	}

	public String getOverriddenService() {
		return null;
	}

	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("WatermarkService.execute()");
        String repoId = request.getParameter("repoId");
        System.out.println("repoId: " + repoId);
        Connection connection = callbacks.getP8Connection(repoId);
        Domain domain = callbacks.getP8Domain(repoId, null);
        ObjectStore objectStore = callbacks.getP8ObjectStore(repoId);
        String docId = request.getParameter("docId");

        String userId = request.getRemoteUser();
        String docTitle = "";
        InputStream stream = null;
        JSONResponse jsonResponse = null;
        try {
            Document document = com.filenet.api.core.Factory.Document.fetchInstance(objectStore, docId, null);
            Document d = (Document) document.get_CurrentVersion();
            Properties documentProperties = d.getProperties();
            String documentTitle = documentProperties.getStringValue("DocumentTitle");
            docTitle = documentTitle;
            ContentElementList docContentList = d.get_ContentElements();
            Iterator<?> iter = docContentList.iterator();

            while (iter.hasNext()) {
                ContentTransfer ct = (ContentTransfer) iter.next();
                String fname = ct.get_RetrievalName();
                stream = ct.accessContentStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                PDDocument document1 = null;

                try {
                	String mimeType = ct.get_ContentType();
                    String fileExtension = getExtensionFromMimeType(mimeType);

                    if (fileExtension.equalsIgnoreCase("pdf")) {
                        document1 = PDDocument.load(stream);
                        addWatermarkToPdf(document1, userId);
                    } else if (fileExtension.equalsIgnoreCase("tiff") ||
                            fileExtension.equalsIgnoreCase("tif") ||
                            fileExtension.equalsIgnoreCase("jpeg") ||
                            fileExtension.equalsIgnoreCase("jpg") ||
                            fileExtension.equalsIgnoreCase("png")) {
                        document1 = convertImageToPdf(stream, fileExtension);
                        System.out.println("document1:"+document1.getNumberOfPages());
                        addWatermarkToPdf(document1, userId);
                        System.out.println("completed");
                    } else {
                    	System.out.println("Unsupported file format:");
                        throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
                    }

                    // Save the modified document to a byte array
                    document1.save(byteStream);
                } finally {
                    if (document1 != null) {
                        document1.close();
                    }
                }

                // Set the response headers
                response.setContentType("application/json");
                
             // Remove the existing file extension
                int dotIndex = fname.lastIndexOf(".");
                String filenameWithoutExtension = fname.substring(0, dotIndex);

                // Add the ".pdf" extension
                String filenameWithPdfExtension = filenameWithoutExtension + ".pdf";
                
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filenameWithPdfExtension + "\"");

                // Encode the PDF data to Base64
                byte[] pdfBytes = byteStream.toByteArray();
                String base64Data = Base64.getEncoder().encodeToString(pdfBytes);
               
                // Create the JSON response
                jsonResponse = new JSONResponse();
                jsonResponse.put("success", true);
                jsonResponse.put("filename", filenameWithPdfExtension);
                jsonResponse.put("pdfData", base64Data);
               // jsonResponse.put("repositoryName", repoId);
               // jsonResponse.put("docTitle", docTitle);
               // jsonResponse.put("userId", userId);
                // Send the JSON response
                PluginResponseUtil.writeJSONResponse(request, response, jsonResponse, callbacks, "WatermarkService");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse = new JSONResponse();
            jsonResponse.put("success", false);
            jsonResponse.put("error", e.getMessage());
            PluginResponseUtil.writeJSONResponse(request, response, jsonResponse, callbacks, "WatermarkService");
        }
    }

    private void addWatermarkToPdf(PDDocument doc, String userId) throws IOException {
    	System.out.println("WatermarkService.addWatermarkToPdf()");
        for (PDPage page : doc.getPages()) {
            final PDFont font = PDType1Font.HELVETICA;
            addWatermarkText(doc, page, font, "Downloaded by " + userId);
        }
    }

    private void addWatermarkText(PDDocument doc, PDPage page, PDFont font, String text) throws IOException {
    	System.out.println("WatermarkService.addWatermarkText()");
        PDPageContentStream cs = null;
        try {
            cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
            final float fontHeight = 50; // arbitrary for short text
            final float width = page.getMediaBox().getWidth();
            final float height = page.getMediaBox().getHeight();
            final float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;
            final float diagonalLength = (float) Math.sqrt(width * width + height * height);
            final float angle = (float) Math.atan2(height, width);
            final float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
            final float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
            cs.transform(Matrix.getRotateInstance(angle, 0, 0));
            cs.setFont(font, fontHeight);
            cs.setRenderingMode(RenderingMode.STROKE); // for "hollow" effect

            // Set color
            cs.setNonStrokingColor(Color.LIGHT_GRAY);
            cs.setStrokingColor(Color.LIGHT_GRAY);

            cs.beginText();
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
        } finally {
            if (cs != null) {
                cs.close();
            }
        }
    }

    private PDDocument convertImageToPdf(InputStream imageStream, String format) throws IOException {
    	System.out.println("WatermarkService.convertImageToPdf()");
    	BufferedImage image = ImageIO.read(imageStream);
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        document.addPage(page);
        PDImageXObject imageXObject = LosslessFactory.createFromImage(document, image);
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(imageXObject, 0, 0, image.getWidth(), image.getHeight());
        } finally {
            if (contentStream != null) {
                contentStream.close();
            }
        }
        return document;
    }
    private String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) {
            return "";
        }
        String[] parts = mimeType.split("/");
        if (parts.length > 1) {
            return parts[1];
        }
        return "";
    }

}
