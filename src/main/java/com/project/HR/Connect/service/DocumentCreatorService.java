package com.project.HR.Connect.service;

import com.project.HR.Connect.entitie.Request;
import com.project.HR.Connect.entitie.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spire.doc.Document;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Math.abs;

@Service
public class DocumentCreatorService {
    @Autowired
    UserService userService;

    public Document createDocumentFromExampleTemplate() throws Exception {
        Document document = new Document("Example.docx");

        String[] filedNames = new String[]{"firstName", "lastName"};
        String[] filedValues = new String[]{"John", "Doe"};

        document.getMailMerge().execute(filedNames, filedValues);

        return document;
    }

    public Document createDocumentFromTemplate(Request request, Long numberOfRequests) throws Exception {
        String documentName = new String();
        User requester = request.getRequester();

        switch (request.getType()) {
            case Medical_leave:
                documentName = "cerere-concediu-medical.docx";
                break;
            case Paid_leave:
                documentName = "cerere-concediu.docx";
                break;
            case Employed_status:
                documentName = "adeverinta-salariat.docx";
                break;
            case Resignation:
                documentName = "cerere-demisie.docx";
                break;
            default:
                documentName = "Example.docx";
                break;
        }

        Document document = new Document(documentName);

        String[] filedNames = new String[]{"requestId", "numberOfRequests", "firstName", "lastName", "country",
                "county", "city", "street", "streetNumber","flatNumber","cnp", "series", "number", "issuingAuthority",
                "date", "joinDate", "position", "userId", "numberOfUsers", "requestDetails"};

        String[] filedValues = new String[]{String.valueOf(request.getId()), String.valueOf(numberOfRequests),
                requester.getFirstName(), requester.getLastName(), requester.getAddress().getCountry(),
                requester.getAddress().getCounty(), requester.getAddress().getCity(),
                requester.getAddress().getStreet(), requester.getAddress().getStreetNumber(),
                requester.getAddress().getFlatNumber(), requester.getIdentityCard().getCnp(),
                requester.getIdentityCard().getSeries(), requester.getIdentityCard().getNumber().toString(),
                requester.getIdentityCard().getIssuer(), String.valueOf(requester.getIdentityCard().getIssuingDate()),
                String.valueOf(requester.getJoinDate()), String.valueOf(requester.getPosition()),
                String.valueOf(requester.getId()), String.valueOf(userService.numberOfUsers()), request.getDetails()};

        document.getMailMerge().execute(filedNames, filedValues);
        return document;
    }

    public File convertDocumentToFile(Document document) {
        File outputFile = new File("output.docx");
        document.saveToFile(outputFile.getAbsolutePath(), com.spire.doc.FileFormat.Docx);
        return outputFile;
    }

}