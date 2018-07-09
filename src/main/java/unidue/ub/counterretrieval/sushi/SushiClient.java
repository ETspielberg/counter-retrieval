package unidue.ub.counterretrieval.sushi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

import javax.xml.soap.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SushiClient {

    private final static String namespaceCounter = "http://www.niso.org/schemas/sushi/counter";

    private final static String namespaceSushi = "http://www.niso.org/schemas/sushi";

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final static Logger log = LoggerFactory.getLogger(SushiClient.class);

    private int release;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String reportType;

    private Sushiprovider sushiprovider;

    SushiClient() {
        LocalDateTime TODAY = LocalDateTime.now();
        int timeshift;
        if (TODAY.getDayOfMonth() < 15)
            timeshift = 3;
        else
            timeshift = 2;
        startTime = LocalDateTime.now().minusMonths(timeshift).withDayOfMonth(1);
        endTime = LocalDateTime.now().minusMonths(timeshift - 1).withDayOfMonth(1).minusDays(1);
        release = 4;
        reportType = "JR1";
    }

    public void setSushiprovider(Sushiprovider sushiprovider) {
        this.sushiprovider = sushiprovider;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public SOAPMessage getResponse() throws SOAPException {
        log.info("requesting UsageData for " + this.sushiprovider.getIdentifier() + " for time range " + startTime.format(dtf) + " till " + endTime.format(dtf));

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("coun", namespaceCounter);
        envelope.addNamespaceDeclaration("sus", namespaceSushi);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement reportRequest = soapBody.addChildElement("ReportRequest", "coun");
        reportRequest.setAttribute("ID", sushiprovider.getSushiRequestorID());
        reportRequest.setAttribute("Created", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        SOAPElement requestor = reportRequest.addChildElement("Requestor", "sus");
        SOAPElement requestorID = requestor.addChildElement("ID", "sus");
        requestorID.addTextNode(sushiprovider.getSushiRequestorID());

        if (!sushiprovider.getSushiRequestorName().isEmpty()) {
            SOAPElement requestorName = requestor.addChildElement("Name", "sus");
            requestorName.addTextNode(sushiprovider.getSushiRequestorName());
        }

        if (!sushiprovider.getSushiRequestorEmail().isEmpty()) {
            SOAPElement requestorEmail = requestor.addChildElement("Email", "sus");
            requestorEmail.addTextNode(sushiprovider.getSushiRequestorEmail());
        }

        SOAPElement customerReference = reportRequest.addChildElement("CustomerReference", "sus");
        SOAPElement customerReferenceID = customerReference.addChildElement("ID", "sus");
        customerReferenceID.addTextNode(sushiprovider.getSushiCustomerReferenceID());

        if (!sushiprovider.getSushiCustomerReferenceName().isEmpty()) {
            SOAPElement customerReferenceName = customerReference.addChildElement("Name", "sus");
            customerReferenceName.addTextNode(sushiprovider.getSushiCustomerReferenceName());
        }

        SOAPElement reportDefinition = reportRequest.addChildElement("ReportDefinition", "sus");
        reportDefinition.setAttribute("Release", String.valueOf(release));
        reportDefinition.setAttribute("Name", reportType);

        SOAPElement filters = reportDefinition.addChildElement("Filters", "sus");
        SOAPElement usageDataRange = filters.addChildElement("UsageDateRange", "sus");

        SOAPElement begin = usageDataRange.addChildElement("Begin", "sus");
        begin.addTextNode(startTime.format(dtf));

        SOAPElement end = usageDataRange.addChildElement("End", "sus");
        end.setTextContent(endTime.format(dtf));

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "SushiService:GetReportIn");

        soapMessage.saveChanges();

        SOAPMessage response = soapConnection.call(soapMessage, sushiprovider.getSushiURL());

        soapConnection.close();
        if (response == null)
            log.info("SUSHI-server did not respond.");
        return response;
    }
}
