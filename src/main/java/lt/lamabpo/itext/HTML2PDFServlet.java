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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 * @author Giedrius Deveikis @ LAMA BPO
 */
@WebServlet(name="HTML2PDFServlet", urlPatterns={"/HTML2PDFServlet"})
@MultipartConfig
public class HTML2PDFServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String AGPL_REPOSITORY_URL = "https://github.com/lamabpo/lama-itext-servlet";
    private static final String NS_LTUd = "http://archyvai.lt/pdf-ltud/2013/metadata/";
    private static final String NS_LTUdEnt="http://archyvai.lt/pdf-ltud/2013/metadata/Entity/";
    private static final SimpleDateFormat XMPDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
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
        try {
            String test = null;
            String docLanguage;
            String docTitleLT;
            String docTitleEN;
            String instTitle;
            String instCode;
            String instAddress;
            String instEmail;
            String regNr;
            String regDate;
            String regOtherInst;
            String studName;
            String studAK;
            String studAddress;
            String studEmail;
            String studIdInfoTitle;
            String studIdInfoValue;
            String mokIdInfoTitle;
            String mokIdInfoValue;
            String html;
            if(request.getHeader("content-type").startsWith("multipart/form-data")){
                html = IOUtils.toString(request.getPart("html").getInputStream(), "UTF-8");
                try{
                    test = IOUtils.toString(request.getPart("test").getInputStream(), "UTF-8");
                    docLanguage = "lt-LT";
                    docTitleLT = "Studijų sutartis";
                    docTitleEN = "Study Agreement";
                    instTitle = "LAMA BPO";
                    instCode = "111961268";
                    instAddress = "Studentų g. 54, LT-51424 Kaunas";
                    instEmail = "info@lamabpo.lt";
                    regNr = "MR-1234";
                    regDate = XMPDateFormat.format(new Date());
                    regOtherInst = "<LTUdReg:code>305238040</LTUdReg:code>";//NŠA
                    studName = "Vardenis Pavardenis";
                    studAK = "50001010001";
                    studAddress = "Patiltės g. 125-315, LT-50000 Kaunas";
                    studEmail = "vardenis.pavardenis@gmail.com";
                    studIdInfoTitle = "Mokinio identifikavimo tranzakcija";
                    studIdInfoValue = "01234567-89ab-cdef-0123-456789abcdef";
                    mokIdInfoTitle = "PMI atstovo identifikavimo tranzakcija";
                    mokIdInfoValue = "98765432-10fe-dcba-9876-543210fedcba";
                }catch(IOException | NullPointerException e){
                    docLanguage = IOUtils.toString(request.getPart("docLanguage").getInputStream(), "UTF-8");
                    docTitleLT = IOUtils.toString(request.getPart("docTitleLT").getInputStream(), "UTF-8");
                    docTitleEN = IOUtils.toString(request.getPart("docTitleEN").getInputStream(), "UTF-8");
                    instTitle = IOUtils.toString(request.getPart("instTitle").getInputStream(), "UTF-8");
                    instCode = IOUtils.toString(request.getPart("instCode").getInputStream(), "UTF-8");
                    instAddress = IOUtils.toString(request.getPart("instAddress").getInputStream(), "UTF-8");
                    instEmail = IOUtils.toString(request.getPart("instEmail").getInputStream(), "UTF-8");
                    regNr = IOUtils.toString(request.getPart("regNr").getInputStream(), "UTF-8");
                    regDate = IOUtils.toString(request.getPart("regDate").getInputStream(), "UTF-8");
                    regOtherInst = IOUtils.toString(request.getPart("regOtherInst").getInputStream(), "UTF-8");
                    if(regOtherInst.length()>0&&regOtherInst.length()<=11)
                        regOtherInst="<LTUdReg:code>"+regOtherInst+"</LTUdReg:code>";
                    studName = IOUtils.toString(request.getPart("studName").getInputStream(), "UTF-8");
                    studAK = IOUtils.toString(request.getPart("studAK").getInputStream(), "UTF-8");
                    studAddress = IOUtils.toString(request.getPart("studAddress").getInputStream(), "UTF-8");
                    studEmail = IOUtils.toString(request.getPart("studEmail").getInputStream(), "UTF-8");
                    studIdInfoTitle = IOUtils.toString(request.getPart("studIdInfoTitle").getInputStream(), "UTF-8");
                    studIdInfoValue = IOUtils.toString(request.getPart("studIdInfoValue").getInputStream(), "UTF-8");
                    mokIdInfoTitle = IOUtils.toString(request.getPart("mokIdInfoTitle").getInputStream(), "UTF-8");
                    mokIdInfoValue = IOUtils.toString(request.getPart("mokIdInfoValue").getInputStream(), "UTF-8");
                }
            }else{
                html = request.getParameter("html");
                test = request.getParameter("test");
                if(test!=null&&test.length()>0){
                    docLanguage = "lt-LT";
                    docTitleLT = "Studijų sutartis";
                    docTitleEN = "Study Agreement";
                    instTitle = "LAMA BPO";
                    instCode = "111961268";
                    instAddress = "Studentų g. 54, LT-51424 Kaunas";
                    instEmail = "info@lamabpo.lt";
                    regNr = "MR-1234";
                    regDate = XMPDateFormat.format(new Date());
                    regOtherInst = "<LTUdReg:code>305238040</LTUdReg:code>";//NŠA
                    studName = "Vardenis Pavardenis";
                    studAK = "50001010001";
                    studAddress = "Patiltės g. 125-315, LT-50000 Kaunas";
                    studEmail = "vardenis.pavardenis@gmail.com";
                    studIdInfoTitle = "Mokinio identifikavimo tranzakcija";
                    studIdInfoValue = "01234567-89ab-cdef-0123-456789abcdef";
                    mokIdInfoTitle = "PMI atstovo identifikavimo tranzakcija";
                    mokIdInfoValue = "98765432-10fe-dcba-9876-543210fedcba";
                }else{
                    docLanguage = request.getParameter("docLanguage");
                    docTitleLT = request.getParameter("docTitleLT");
                    docTitleEN = request.getParameter("docTitleEN");
                    instTitle = request.getParameter("instTitle");
                    instCode = request.getParameter("instCode");
                    instAddress = request.getParameter("instAddress");
                    instEmail = request.getParameter("instEmail");
                    regNr = request.getParameter("regNr");
                    regDate = request.getParameter("regDate");
                    regOtherInst = request.getParameter("regOtherInst");
                    if(regOtherInst.length()>0&&regOtherInst.length()<=11)
                        regOtherInst="<LTUdReg:code>"+regOtherInst+"</LTUdReg:code>";
                    studName = request.getParameter("studName");
                    studAK = request.getParameter("studAK");
                    studAddress = request.getParameter("studAddress");
                    studEmail = request.getParameter("studEmail");
                    studIdInfoTitle = request.getParameter("studIdInfoTitle");
                    studIdInfoValue = request.getParameter("studIdInfoValue");
                    mokIdInfoTitle = request.getParameter("mokIdInfoTitle");
                    mokIdInfoValue = request.getParameter("mokIdInfoValue");
                }
            }
            getServletContext().log(fontsDirectory==null?"null":fontsDirectory);
            getServletContext().log("test="+test);
            getServletContext().log("html.length()="+(html==null?0:html.length()));
            getServletContext().log(html==null?"null":html);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            WriterProperties writerProperties = new WriterProperties();
            writerProperties.setFullCompressionMode(true);
            PdfWriter pdfWriter = new PdfWriter(os,writerProperties);
            PdfOutputIntent oi=new PdfOutputIntent("Custom","","http://www.color.org","sRGB IEC61966-2.1",HTML2PDFServlet.class.getResourceAsStream("sRGB_CS_profile.icm"));
            PdfADocument pdfDoc = new PdfADocument(pdfWriter,PdfAConformanceLevel.PDF_A_2B,oi);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            PdfCatalog cat = pdfDoc.getCatalog();
            cat.setLang(new PdfString(docLanguage));
            cat.setPageLayout(PdfName.TwoColumnLeft);
            cat.setViewerPreferences(new PdfViewerPreferences().setDuplex(PdfViewerPreferences.PdfViewerPreferencesConstants.DUPLEX_FLIP_LONG_EDGE));
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
            if( mokIdInfoTitle!=null && (!mokIdInfoTitle.isEmpty()) && mokIdInfoValue!=null && (!mokIdInfoValue.isEmpty()))
              pdfDocInfo.setMoreInfo(mokIdInfoTitle, mokIdInfoValue);

            /*
            //V1 - create XMP from template pieces and definitions
            XMPMeta xmpM = XMPMetaFactory.parseFromBuffer(pdfDoc.getXmpMetadata(true));
            XMPMeta tmpMeta = XMPMetaFactory.parse(HTML2PDFServlet.class.getResourceAsStream("LTUdSchema.xml"));
            XMPUtils.appendProperties(tmpMeta, xmpM, true, true);
            XMPSchemaRegistry xsr=XMPMetaFactory.getSchemaRegistry();
            xsr.registerNamespace(NS_LTUd,"LTUd");
            xsr.registerNamespace(NS_LTUdEnt,"LTUdEnt");
            tmpMeta = XMPMetaFactory.parse(HTML2PDFServlet.class.getResourceAsStream("atitiktis.xml"));
            XMPUtils.appendProperties(tmpMeta, xmpM, true, true);
            PropertyOptions poSeparateNode = new PropertyOptions(PropertyOptions.SEPARATE_NODE);
            xmpM.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2",poSeparateNode);
            xmpM.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, "B",poSeparateNode);
            xmpM.setProperty(XMPConst.NS_XMP, PdfConst.CreatorTool, "LAMA BPO ITextPDF",poSeparateNode);
            xmpM.setProperty(XMPConst.NS_DC, PdfConst.Creator, "Giedrius Deveikis",poSeparateNode);
            xmpM.setLocalizedText(XMPConst.NS_DC, PdfConst.Title,null, "lt-LT", "Studijų sutartis");
            xmpM.setLocalizedText(XMPConst.NS_DC, PdfConst.Title,null, "en-UK", "Study Agreement");
            xmpM.appendArrayItem(XMPConst.NS_DC, PdfConst.Language, new PropertyOptions(PropertyOptions.ARRAY), "lt-LT", null);
            tmpMeta = XMPMetaFactory.parseFromString(String.format(IOUtils.toString(HTML2PDFServlet.class.getResourceAsStream("ltudinfo.xml"),"UTF-8"),
                    "False",//individual
                    instTitle,//name
                    instCode,//code
                    instAddress,//address
                    instEmail//eMail
            ));
            XMPUtils.appendProperties(tmpMeta, xmpM, true, true);
            */
            //V2 - create XMP from template
            XMPSchemaRegistry xsr=XMPMetaFactory.getSchemaRegistry();
            xsr.registerNamespace(NS_LTUd,"LTUd");
            xsr.registerNamespace(NS_LTUdEnt,"LTUdEnt");
            XMPMeta xmpM = XMPMetaFactory.parseFromString(String.format(IOUtils.toString(HTML2PDFServlet.class.getResourceAsStream("fullmetadata.xml"),"UTF-8"),
                    "False",//LTUdEnt:individual
                    instTitle,//LTUdEnt:name
                    instCode,//LTUdEnt:code
                    instAddress,//LTUdEnt:address
                    instEmail,//LTUdEnt:eMail
                    regDate,//LTUdReg:date - format:2011-11-30T21:32:18+02:00
                    regNr,//LTUdReg:number
                    regOtherInst,//"<LTUdReg:code>012345678</LTUdReg:code>" or empty string
                    "True",//LTUdEnt:individual
                    studName,//LTUdEnt:name
                    studAK,//LTUdEnt:code
                    studAddress,//LTUdEnt:address
                    studEmail,//LTUdEnt:eMail
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
            os.writeTo(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (XMPException ex) {
            throw new ServletException(ex);
        }
    }
}
