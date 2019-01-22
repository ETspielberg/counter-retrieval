package unidue.ub.counterretrieval;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unidue.ub.counterretrieval.model.data.Counter;
import unidue.ub.counterretrieval.model.data.DatabaseCounter;
import unidue.ub.counterretrieval.model.data.EbookCounter;
import unidue.ub.counterretrieval.model.data.JournalCounter;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helpful tools for handling of COUNTER reports
 * @author Eike Spielberg
 *
 */
public class CounterTools {

    private static final Namespace namespaceSushiCounter = Namespace.getNamespace("http://www.niso.org/schemas/sushi/counter");

    private static final Namespace namespaceCounter = Namespace.getNamespace("http://www.niso.org/schemas/counter");

    private static final Namespace namespaceSOAP = Namespace.getNamespace("http://schemas.xmlsoap.org/soap/envelope/");

    public final static Logger log = LoggerFactory.getLogger(CounterTools.class);

    /**
     * returns a list of <code>Counter</code> objects generated from the response of a SUSHI request.
     * @param sushi the SUSHI response
     * @return counters the list of COUNTER reports
     * @exception SOAPException thrown upon errors occurring parsing the SUSHI response
     * @exception IOException thrown upon errors occurring writing of the SUSHI response to the SAX-Buuilder
     * @exception JDOMException thrown upon errors parsing the xml structure of the SUSHI response
     * @exception CounterConversionException thrown if the CounterTools
     */
    public static List<? extends Counter> convertSOAPMessageToCounters(SOAPMessage sushi) throws SOAPException, IOException, JDOMException, CounterConversionException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sushi.writeTo(out);
        String sushiString = new String(out.toByteArray());
        SAXBuilder builder = new SAXBuilder();
        Document sushiDoc = builder.build(new StringReader(sushiString));
        Element sushiElement = sushiDoc.detachRootElement().clone();
        try {
            Element report = sushiElement.getChild("Body", namespaceSOAP).getChild("ReportResponse", namespaceSushiCounter).getChild("Report", namespaceSushiCounter).getChild("Report", namespaceCounter);
            String type;
            if (report.getAttributeValue("Name") != null)
                type = report.getAttributeValue("Name");
            else
                type = report.getAttributeValue("ID");
            List<Element> reportItems = report.getChild("Customer", namespaceCounter).getChildren("ReportItems", namespaceCounter);
            switch (type) {
                case "JR1":
                    return convertCounterElementsToJournalCounters(reportItems);
                case "BR1":
                    return convertCounterElementsToEbookCounters(reportItems);
                case "BR2":
                    return convertCounterElementsToEbookCounters(reportItems);
                case "DR1":
                    return convertCounterElementsToDatabaseCounters(reportItems);
            }
        } catch (Exception e) {
            try {
                String error = sushiElement.getChild("Body", namespaceSOAP).getChild("ReportResponse", namespaceSushiCounter).getChild("Exception", namespaceSushiCounter).getChildText("Message", namespaceCounter);
                throw new CounterConversionException("could not convert SOAP response: " + error);
            } catch (Exception ex) {
                throw new CounterConversionException("could not convert SOAP response: " + sushiString);
            }
        }
        return null;
    }

    private static List<DatabaseCounter> convertCounterElementsToDatabaseCounters(List<Element> reportItems) {
        List<DatabaseCounter> counters = new ArrayList<>();
        for (Element item : reportItems) {
            String sushiprovider = "";
            if (item.getChild("Itemsushiprovider", namespaceCounter) != null)
                sushiprovider = item.getChild("Itemsushiprovider", namespaceCounter).getValue();
            String platform = "";
            if (item.getChild("ItemPlatform", namespaceCounter) != null)
                platform = item.getChild("ItemPlatform", namespaceCounter).getValue();
            String title = "";
            if (item.getChild("ItemName", namespaceCounter) != null)
                title = item.getChild("ItemName", namespaceCounter).getValue();
            List<Element> itemPerformances = item.getChildren("ItemPerformance", namespaceCounter);
            for (Element itemPerformance : itemPerformances) {
                Element period = itemPerformance.getChild("Period", namespaceCounter);
                String startDate = period.getChild("Begin", namespaceCounter).getValue();
                List<Element> instances = itemPerformance.getChildren("Instance", namespaceCounter);
                int year = Integer.parseInt(startDate.substring(0, 4));
                int month = Integer.parseInt(startDate.substring(5, 7));
                DatabaseCounter counter = new DatabaseCounter(sushiprovider,platform,month,year);
                counter.setTitle(title);

                for (Element instance : instances) {
                    long value = Long.parseLong(instance.getChild("Count", namespaceCounter).getValue().trim());
                    String metricType = instance.getChild("MetricType", namespaceCounter).getValue();
                    switch (metricType) {
                        case "record_view": {
                            counter.setRecordViews(value);
                            break;
                        }
                        case "result_click": {
                            counter.setResultClicks(value);
                            break;
                        }
                        case "search_reg": {
                            counter.setRegularSearches(value);
                            break;
                        }
                        case "search_fed": {
                            counter.setFederatedAndAutomatedSearches(value);
                            break;
                        }
                        default: {
                            log.info(metricType + " is not a categorized metric.");
                        }
                    }
                }
                counters.add(counter);
            }
        }
        return counters;
    }

    private static List<EbookCounter> convertCounterElementsToEbookCounters(List<Element> reportItems) {
        List<EbookCounter> counters = new ArrayList<>();
        for (Element item : reportItems) {
            String sushiprovider = "";
            if (item.getChild("Itemsushiprovider", namespaceCounter) != null)
                sushiprovider = item.getChild("Itemsushiprovider", namespaceCounter).getValue();
            String platform = "";
            if (item.getChild("ItemPlatform", namespaceCounter) != null)
                platform = item.getChild("ItemPlatform", namespaceCounter).getValue();
            String title = "";
            if (item.getChild("ItemName", namespaceCounter) != null)
                title = item.getChild("ItemName", namespaceCounter).getValue();
            List<Element> itemPerformances = item.getChildren("ItemPerformance", namespaceCounter);
            List<Element> identifiers = item.getChildren("ItemIdentifier", namespaceCounter);
            String onlineIsbn = "";
            String doi = "";
            String printIsbn = "";
            String isni = "";
            String proprietary = "";
            for (Element identifier : identifiers) {
                String identifierType = identifier.getChild("Type", namespaceCounter).getValue();
                String value = identifier.getChild("Value", namespaceCounter).getValue();
                switch (identifierType) {
                    case "Online_ISBN" : {
                        onlineIsbn = value;
                        break;
                    }
                    case "Print_ISBN" : {
                        printIsbn = value;
                        break;
                    }
                    case "ISNI" : {
                        isni = value;
                        break;
                    }
                    case "DOI" : {
                        doi = value;
                        break;
                    }
                    case "Proprietary" : {
                        proprietary = value;
                        break;
                    }default: {
                        log.info(identifierType + " is not a categorized identifier type.");
                    }
                }
            }
            for (Element itemPerformance : itemPerformances) {
                Element period = itemPerformance.getChild("Period", namespaceCounter);
                String startDate = period.getChild("Begin", namespaceCounter).getValue();
                List<Element> instances = itemPerformance.getChildren("Instance", namespaceCounter);
                int year = Integer.parseInt(startDate.substring(0, 4));
                int month = Integer.parseInt(startDate.substring(5, 7));
                EbookCounter counter = new EbookCounter(onlineIsbn,platform,month,year);
                counter.setDoi(doi).setProprietary(proprietary).setIsni(isni).setPrintIsbn(printIsbn).setPublisher(sushiprovider).setTitle(title);
                for (Element instance : instances) {
                    long value = Long.parseLong(instance.getChild("Count", namespaceCounter).getValue().trim());
                    String metricType = instance.getChild("MetricType", namespaceCounter).getValue();
                    switch (metricType) {
                        case "ft_pdf": {
                            counter.setPdfRequests(value);
                            break;
                        }
                        case "ft_pdf_mobile": {
                            counter.setPdfRequestsMobile(value);
                            break;
                        }
                        case "ft_html": {
                            counter.setHtmlRequests(value);
                            break;
                        }
                        case "ft_html_mobile": {
                            counter.setHtmlRequestsMobile(value);
                            break;
                        }
                        case "ft_ps": {
                            counter.setPsRequests(value);
                            break;
                        }
                        case "ft_ps_mobile": {
                            counter.setPsRequestsMobile(value);
                            break;
                        }
                        case "ft_epub": {
                            counter.setEpubRequest(value);
                            break;
                        }
                        case "ft_total": {
                            counter.setTotalRequests(value);
                            break;
                        }
                        default: {
                            log.info(metricType + " is not a categorized metric.");
                        }
                    }
                }
                long totalRequests = counter.getHtmlRequests() + counter.getHtmlRequestsMobile() + counter.getPdfRequests() + counter.getPdfRequestsMobile() + counter.getPsRequests() + counter.getPsRequestsMobile() + counter.getEpubRequest();
                if (counter.getTotalRequests() != totalRequests) {
                    log.warn("sum of individual requests (" + totalRequests + ") does not match total requests (" + counter.getTotalRequests() + ")!");
                    counter.setTotalRequests(Math.max(totalRequests,counter.getTotalRequests()));
                }
                counters.add(counter);
            }
        }
        return counters;
    }

    private static List<JournalCounter> convertCounterElementsToJournalCounters(List<Element> reportItems) {
        List<JournalCounter> counters = new ArrayList<>();
        for (Element item : reportItems) {
            String fullname = "";
            if (item.getChild("ItemName", namespaceCounter) != null)
                fullname = item.getChild("ItemName", namespaceCounter).getValue();
            String sushiprovider = "";
            if (item.getChild("Itemsushiprovider", namespaceCounter) != null)
                sushiprovider = item.getChild("Itemsushiprovider", namespaceCounter).getValue();
            String platform = "";
            if (item.getChild("ItemPlatform", namespaceCounter) != null)
                platform = item.getChild("ItemPlatform", namespaceCounter).getValue();
            String type = "";
            if (item.getChild("ItemDataType", namespaceCounter) != null)
                type = item.getChild("ItemDataType", namespaceCounter).getValue();
            List<Element> identifiers = item.getChildren("ItemIdentifier", namespaceCounter);
            String onlineISSN = "";
            String printISSN = "";
            String proprietary = "";
            String doi = "";
            for (Element identifier : identifiers) {
                String identifierType = identifier.getChild("Type", namespaceCounter).getValue();
                String value = identifier.getChild("Value", namespaceCounter).getValue();
                switch (identifierType) {
                    case "Online_ISSN" :  {
                        onlineISSN = value;
                        break;
                    }
                    case "Print_ISSN" : {
                        printISSN = value;
                        break;
                    }
                    case "Proprietary" : {
                        proprietary = value;
                        break;
                    }
                    case "DOI" : {
                        doi = value;
                        break;
                    }
                    default: {
                        log.info(identifierType + " is not a categorized identifier type.");
                    }
                }
            }
            List<Element> itemPerformances = item.getChildren("ItemPerformance", namespaceCounter);
            for (Element itemPerformance : itemPerformances) {
                Element period = itemPerformance.getChild("Period", namespaceCounter);
                String startDate = period.getChild("Begin", namespaceCounter).getValue();
                List<Element> instances = itemPerformance.getChildren("Instance", namespaceCounter);
                int year = Integer.parseInt(startDate.substring(0, 4));
                int month = Integer.parseInt(startDate.substring(5, 7));
                JournalCounter counter;
                if (onlineISSN.isEmpty()) {
                    if (printISSN.isEmpty()) {
                        if (doi.isEmpty())
                            counter = new JournalCounter(proprietary,"proprietary",platform,month,year);
                        else
                            counter = new JournalCounter(doi,"doi",platform,month,year);
                    } else
                        counter = new JournalCounter(printISSN,"printIssn",platform,month,year);
                } else
                    counter = new JournalCounter(onlineISSN,"onlineIssn",platform,month,year);
                counter.caluclateId();
                counter.setFullName(fullname).setType(type).setPrintIssn(printISSN).setAbbreviation(proprietary).setPublisher(sushiprovider).setDoi(doi);

                for (Element instance : instances) {
                    long value = Long.parseLong(instance.getChild("Count", namespaceCounter).getValue().trim());
                    String metricType = instance.getChild("MetricType", namespaceCounter).getValue();
                    switch (metricType) {
                        case "ft_html": {
                            counter.setHtmlRequests(value);
                            break;
                        }
                        case "ft_html_mobile": {
                            counter.setHtmlRequestsMobile(value);
                            break;
                        }
                        case "ft_pdf": {
                            counter.setPdfRequests(value);
                            break;
                        }
                        case "ft_pdf_mobile": {
                            counter.setPdfRequestsMobile(value);
                            break;
                        }
                        case "ft_ps": {
                            counter.setPsRequests(value);
                            break;
                        }
                        case "ft_ps_mobile": {
                            counter.setPsRequestsMobile(value);
                            break;
                        }
                        case "ft_total": {
                            counter.setTotalRequests(value);
                            break;
                        }
                        default: {
                            log.info(metricType + " is not a categorized metric.");
                        }
                    }
                    counters.add(counter);

                }
                long totalRequests = counter.getHtmlRequests() + counter.getHtmlRequestsMobile() + counter.getPdfRequests() + counter.getPdfRequestsMobile() + counter.getPsRequests() + counter.getPsRequestsMobile();
                if (counter.getTotalRequests() != totalRequests) {
                    log.warn("sum of individual requests (" + totalRequests + ") does not match total requests (" + counter.getTotalRequests() + ")!");
                    counter.setTotalRequests(Math.max(totalRequests,counter.getTotalRequests()));
                }
            }
        }
        return counters;
    }
}
