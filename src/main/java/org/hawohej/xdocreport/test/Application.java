package org.hawohej.xdocreport.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Application {

    public static void main(String[] args) {
        try {
            var objectMapper = new ObjectMapper();

            // Read files from resources
            ClassLoader classLoader = Application.class.getClassLoader();
            InputStream opportunitiesJson = classLoader.getResourceAsStream("opportunities.json");
            InputStream templateDocxIS = classLoader.getResourceAsStream("template.docx");

            // Create report and fill context from JSON
            Map<String, Object> opportunitiesData = objectMapper.readValue(opportunitiesJson, Map.class);
            IXDocReport templateReport = XDocReportRegistry.getRegistry().loadReport(templateDocxIS, TemplateEngineKind.Freemarker);
            IContext reportContext = templateReport.createContext();
            opportunitiesData.forEach(reportContext::put);

            // Create result file and generate report
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
            String fileName = String.format("src/main/resources/generated/template_%s.docx", LocalDateTime.now().format(dateTimeFormat));
            var file = new File(fileName);
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            templateReport.process(reportContext, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
