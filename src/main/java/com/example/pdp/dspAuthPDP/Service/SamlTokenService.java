package com.example.pdp.dspAuthPDP.Service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.Instant;
import java.util.UUID;

@Service
public class SamlTokenService {

    private static final String SAML_NS =
            "urn:oasis:names:tc:SAML:2.0:assertion";

    public String createSamlAssertion(
            String tokenId,
            String authLevel,
            String customerId,
            String sessionCorrelationId,
            String accountType,
            String channel
    ) {
        try {
            String assertionId = "_" + UUID.randomUUID();
            String issueInstant = Instant.now().toString();

            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            // ----- Assertion -----
            Element assertion = doc.createElementNS(SAML_NS, "saml2:Assertion");
            assertion.setAttribute("ID", assertionId);
            assertion.setAttribute("Version", "2.0");
            assertion.setAttribute("IssueInstant", issueInstant);
            doc.appendChild(assertion);

            // ----- Issuer -----
            Element issuer = doc.createElementNS(SAML_NS, "saml2:Issuer");
            issuer.setTextContent("https://www.google.com/");
            assertion.appendChild(issuer);

            // ----- Subject -----
            Element subject = doc.createElementNS(SAML_NS, "saml2:Subject");
            Element nameId = doc.createElementNS(SAML_NS, "saml2:NameID");
            nameId.setAttribute(
                    "Format",
                    "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"
            );
            nameId.setTextContent(customerId);
            subject.appendChild(nameId);
            assertion.appendChild(subject);

            // ----- AuthnStatement -----
            Element authnStatement =
                    doc.createElementNS(SAML_NS, "saml2:AuthnStatement");
            authnStatement.setAttribute("AuthnInstant", issueInstant);

            Element authnContext =
                    doc.createElementNS(SAML_NS, "saml2:AuthnContext");
            Element classRef =
                    doc.createElementNS(
                            SAML_NS,
                            "saml2:AuthnContextClassRef"
                    );
            classRef.setTextContent(authLevel);

            authnContext.appendChild(classRef);
            authnStatement.appendChild(authnContext);
            assertion.appendChild(authnStatement);

            // ----- AttributeStatement -----
            Element attrStatement =
                    doc.createElementNS(
                            SAML_NS,
                            "saml2:AttributeStatement"
                    );

            addAttribute(doc, attrStatement,
                    "sessionCorrelationId", sessionCorrelationId);
            addAttribute(doc, attrStatement,
                    "serviceId", customerId);
            addAttribute(doc, attrStatement,
                    "accountType", accountType);
            addAttribute(doc, attrStatement,
                    "channel", channel);
            addAttribute(doc, attrStatement,
                    "tokenId", tokenId);
            addAttribute(doc, attrStatement,
                    "authLevel", authLevel);

            assertion.appendChild(attrStatement);

            // ----- Serialize XML -----
            Transformer transformer =
                    TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(
                    OutputKeys.OMIT_XML_DECLARATION,
                    "yes"
            );
            transformer.setOutputProperty(
                    OutputKeys.INDENT,
                    "yes"
            );

            StringWriter writer = new StringWriter();
            transformer.transform(
                    new DOMSource(doc),
                    new StreamResult(writer)
            );

            return writer.toString();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate SAML Assertion",
                    e
            );
        }
    }

    private void addAttribute(
            Document doc,
            Element statement,
            String name,
            String value
    ) {
        Element attribute =
                doc.createElementNS(SAML_NS, "saml2:Attribute");
        attribute.setAttribute("Name", name);

        Element attrValue =
                doc.createElementNS(SAML_NS, "saml2:AttributeValue");
        attrValue.setTextContent(value);

        attribute.appendChild(attrValue);
        statement.appendChild(attribute);
    }
}
