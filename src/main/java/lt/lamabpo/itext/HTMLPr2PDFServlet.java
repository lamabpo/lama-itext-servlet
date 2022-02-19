/*
 * Web Servlet for converting HTML documents using iText to PDF with PDF-LT-V1.0 specification attributes in XMP
 * Copyright (C) 2020 Giedrius Deveikis @ LAMA BPO
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 */
package lt.lamabpo.itext;

import static java.lang.Math.toIntExact;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDeveloperExtension;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPSchemaRegistry;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.pdfa.PdfADocument;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author Giedrius Deveikis @ LAMA BPO
 */
@WebServlet(name="HTMLPr2PDFServlet", urlPatterns={"/HTMLPr2PDFServlet"})
@MultipartConfig(fileSizeThreshold=1024*1024)
public class HTMLPr2PDFServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String AGPL_REPOSITORY_URL = "https://github.com/lamabpo/lama-itext-servlet";
    private static final String NS_LTUd = "http://archyvai.lt/pdf-ltud/2013/metadata/";
    private static final String NS_LTUdEnt="http://archyvai.lt/pdf-ltud/2013/metadata/Entity/";
    private static final String creatorTool = "LAMA BPO iText Servlet";
    private String fontsDirectory;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fontsDirectory = getServletContext().getInitParameter("fontsDirectory");
    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String test = null;
            String docLanguage;
            String docTitleLT;
            String docTitleEN;
            String instTitle;
            String instCode;
            String instAddress;
            String instEmail;
            String studName;
            String studAK;
            String studAddress;
            String studEmail;
            String studIdInfoTitle;
            String studIdInfoValue;
            String html;
            if(request.getHeader("content-type").startsWith("multipart/form-data")){
                html = partToString(request.getPart("html"), null, "UTF-8");
                test = partToString(request.getPart("test"), null, "UTF-8");
                if(test!=null&&test.length()>0){
                    docLanguage = "lt-LT";
                    docTitleLT = "Studijų sutarties nutraukimo prašymas";
                    docTitleEN = "Study Agreement Termination Request";
                    instTitle = "LAMA BPO";
                    instCode = "111961268";
                    instAddress = "Studentų g. 54, LT-51424 Kaunas";
                    instEmail = "info@lamabpo.lt";
                    studName = "Vardenis Pavardenis";
                    studAK = "50001010001";
                    studAddress = "Patiltės g. 125-315, LT-50000 Kaunas";
                    studEmail = "vardenis.pavardenis@gmail.com";
                    studIdInfoTitle = "Mokinio identifikavimo tranzakcija";
                    studIdInfoValue = "01234567-89ab-cdef-0123-456789abcdef";
                }else{
                    docLanguage = partToString(request.getPart("docLanguage"), null, "UTF-8");
                    docTitleLT = partToString(request.getPart("docTitleLT"), null, "UTF-8");
                    docTitleEN = partToString(request.getPart("docTitleEN"), null, "UTF-8");
                    instTitle = partToString(request.getPart("instTitle"), null, "UTF-8");
                    instCode = partToString(request.getPart("instCode"), null, "UTF-8");
                    instAddress = partToString(request.getPart("instAddress"), null, "UTF-8");
                    instEmail = partToString(request.getPart("instEmail"), null, "UTF-8");
                    studName = partToString(request.getPart("studName"), null, "UTF-8");
                    studAK = partToString(request.getPart("studAK"), null, "UTF-8");
                    studAddress = partToString(request.getPart("studAddress"), null, "UTF-8");
                    studEmail = partToString(request.getPart("studEmail"), null, "UTF-8");
                    studIdInfoTitle = partToString(request.getPart("studIdInfoTitle"), null, "UTF-8");
                    studIdInfoValue = partToString(request.getPart("studIdInfoValue"), null, "UTF-8");
                }
            }else{
                html = request.getParameter("html");
                test = request.getParameter("test");
                if(test!=null&&test.length()>0){
                    docLanguage = "lt-LT";
                    docTitleLT = "Studijų sutarties nutraukimo prašymas";
                    docTitleEN = "Study Agreement Termination Request";
                    instTitle = "LAMA BPO";
                    instCode = "111961268";
                    instAddress = "Studentų g. 54, LT-51424 Kaunas";
                    instEmail = "info@lamabpo.lt";
                    studName = "Vardenis Pavardenis";
                    studAK = "50001010001";
                    studAddress = "Patiltės g. 125-315, LT-50000 Kaunas";
                    studEmail = "vardenis.pavardenis@gmail.com";
                    studIdInfoTitle = "Mokinio identifikavimo tranzakcija";
                    studIdInfoValue = "01234567-89ab-cdef-0123-456789abcdef";
                }else{
                    docLanguage = request.getParameter("docLanguage");
                    docTitleLT = request.getParameter("docTitleLT");
                    docTitleEN = request.getParameter("docTitleEN");
                    instTitle = request.getParameter("instTitle");
                    instCode = request.getParameter("instCode");
                    instAddress = request.getParameter("instAddress");
                    instEmail = request.getParameter("instEmail");
                    studName = request.getParameter("studName");
                    studAK = request.getParameter("studAK");
                    studAddress = request.getParameter("studAddress");
                    studEmail = request.getParameter("studEmail");
                    studIdInfoTitle = request.getParameter("studIdInfoTitle");
                    studIdInfoValue = request.getParameter("studIdInfoValue");
                }
            }
        try(InputStream icm=HTMLPr2PDFServlet.class.getResourceAsStream("sRGB_CS_profile.icm");
                InputStream xmpTmplt=HTMLPr2PDFServlet.class.getResourceAsStream("reqmetadata.xml");
                ServletOutputStream sos = response.getOutputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream()) {            
            WriterProperties writerProperties = new WriterProperties();
            writerProperties.setFullCompressionMode(true);
            PdfWriter pdfWriter = new PdfWriter(os,writerProperties);
            PdfOutputIntent oi=new PdfOutputIntent("Custom","","http://www.color.org","sRGB IEC61966-2.1",icm);
            PdfADocument pdfDoc = new PdfADocument(pdfWriter,PdfAConformanceLevel.PDF_A_2B,oi);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            PdfCatalog cat = pdfDoc.getCatalog();
            cat.setLang(new PdfString(docLanguage));
            cat.setPageLayout(PdfName.SinglePage);
            cat.setViewerPreferences(new PdfViewerPreferences().setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.SIMPLEX));
            PdfDeveloperExtension extension=new PdfDeveloperExtension(new PdfName("LTUd"), PdfName.Pdf_Version_1_7, 1);
            cat.addDeveloperExtension(extension);
            PdfDocumentInfo pdfDocInfo = pdfDoc.getDocumentInfo();
            pdfDocInfo.setAuthor(instTitle);
            pdfDocInfo.addCreationDate();
            pdfDocInfo.getProducer();
            pdfDocInfo.setCreator(creatorTool);
            pdfDocInfo.setSubject("lt-LT".equals(docLanguage)?docTitleLT:docTitleEN);
            pdfDocInfo.setTitle("lt-LT".equals(docLanguage)?docTitleLT:docTitleEN);
            pdfDocInfo.setMoreInfo("CreatorTool AGPL source code",AGPL_REPOSITORY_URL);
            if( studIdInfoTitle!=null && (!studIdInfoTitle.isEmpty()) && studIdInfoValue!=null && (!studIdInfoValue.isEmpty()))
              pdfDocInfo.setMoreInfo(studIdInfoTitle, studIdInfoValue);
            XMPSchemaRegistry xsr=XMPMetaFactory.getSchemaRegistry();
            xsr.registerNamespace(NS_LTUd,"LTUd");
            xsr.registerNamespace(NS_LTUdEnt,"LTUdEnt");
            XMPMeta xmpM = XMPMetaFactory.parseFromString(String.format(IOUtils.toString(xmpTmplt,"UTF-8"),
                    "True",//authors/LTUdEnt:individual
                    studName,//authors/LTUdEnt:name
                    studAK,//authors/LTUdEnt:code
                    studAddress,//authors/LTUdEnt:address
                    studEmail,//authors/LTUdEnt:eMail
                    "False",//recipients/LTUdEnt:individual
                    instTitle,//recipients/LTUdEnt:name
                    instCode,//recipients/LTUdEnt:code
                    instAddress,//recipients/LTUdEnt:address
                    instEmail,//recipients/LTUdEnt:eMail
                    docLanguage,//dc:language
                    "lt-LT".equals(docLanguage)?docTitleLT:docTitleEN,//dc:title[x-default]
                    docTitleLT,//dc:title[lt-LT]
                    docTitleEN,//dc:title[en-UK]
                    instTitle,//dc:creator
                    creatorTool//xmp:CreatorTool
            ));
            pdfDoc.setXmpMetadata(xmpM);
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider dfp = new DefaultFontProvider(false, true, false);
            dfp.addDirectory(fontsDirectory);
            converterProperties.setFontProvider(dfp);
            HtmlConverter.convertToPdf(html, pdfDoc, converterProperties);
            response.setContentType("application/pdf");
            response.setContentLength(os.size());
            os.writeTo(sos);
            sos.flush();
        } catch (XMPException ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Reads multipart/form-data part to string
     * 
     * @param input       one part of multipart/form-data
     * @param charsetName source charset of binary part data
     * @return
     * @throws IOException
     */
    public static String partToString(final Part input, final String defaultVal, final String charsetName)
            throws IOException {
        try (InputStream inputStream = input.getInputStream()) {
            byte[] buffer = new byte[toIntExact(input.getSize())];
            inputStream.read(buffer);
            return new String(buffer,charsetName);
        } catch (NullPointerException e) {
            return defaultVal;
        }
    }
}
