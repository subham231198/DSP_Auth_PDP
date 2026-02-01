package com.example.pdp.dspAuthPDP.Service;//package com.example.pdp.Service;
//
//import jakarta.annotation.PostConstruct;
//import org.opensaml.core.xml.XMLObjectBuilderFactory;
//import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
//import org.opensaml.core.config.InitializationService;
//import org.opensaml.core.xml.schema.XSString;
//import org.opensaml.saml.saml2.core.*;
//import org.springframework.stereotype.Service;
//
//
//import javax.xml.namespace.QName;
//import java.time.Instant;
//import java.util.UUID;
//
//@Service
//public class SamlTokenService {
//
//    private XMLObjectBuilderFactory builderFactory;
//
//    @PostConstruct
//    public void init() throws Exception {
//        InitializationService.initialize();
//        builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
//    }
//
//    public Assertion createSamlAssertion(
//            String tokenId,
//            String authLevel,
//            String customerId,
//            String sessionCorrelationId,
//            String accountType,
//            String channel
//    ) {
//
//        // ---- ASSERTION ----
//        Assertion assertion = build(Assertion.DEFAULT_ELEMENT_NAME);
//        assertion.setID("_" + UUID.randomUUID());
//        assertion.setIssueInstant(Instant.now());
//
//        Issuer issuer = build(Issuer.DEFAULT_ELEMENT_NAME);
//        issuer.setValue("https://banking.example.com/idp");
//        assertion.setIssuer(issuer);
//
//        // ---- SUBJECT ----
//        NameID nameID = build(NameID.DEFAULT_ELEMENT_NAME);
//        nameID.setValue(customerId);
//        nameID.setFormat(NameIDType.PERSISTENT);
//
//        Subject subject = build(Subject.DEFAULT_ELEMENT_NAME);
//        subject.setNameID(nameID);
//        assertion.setSubject(subject);
//
//        // ---- AUTHN STATEMENT ----
//        AuthnStatement authnStatement = build(AuthnStatement.DEFAULT_ELEMENT_NAME);
//        authnStatement.setAuthnInstant(Instant.now());
//
//        AuthnContext authnContext = build(AuthnContext.DEFAULT_ELEMENT_NAME);
//        AuthnContextClassRef classRef =
//                build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
//        classRef.setURI(authLevel);
//
//        authnContext.setAuthnContextClassRef(classRef);
//        authnStatement.setAuthnContext(authnContext);
//        assertion.getAuthnStatements().add(authnStatement);
//
//        // ---- ATTRIBUTES ----
//        AttributeStatement attributeStatement =
//                build(AttributeStatement.DEFAULT_ELEMENT_NAME);
//
//        attributeStatement.getAttributes().add(
//                createAttribute("sessionCorrelationId", sessionCorrelationId));
//        attributeStatement.getAttributes().add(
//                createAttribute("serviceId", customerId));
//        attributeStatement.getAttributes().add(
//                createAttribute("accountType", accountType));
//
//        assertion.getAttributeStatements().add(attributeStatement);
//
//        return assertion;
//    }
//
//    // ---------- HELPERS ----------
//
//    private Attribute createAttribute(String name, String value) {
//        Attribute attribute = build(Attribute.DEFAULT_ELEMENT_NAME);
//        attribute.setName(name);
//
//        XSString attrValue = (XSString) builderFactory
//                .getBuilder(XSString.TYPE_NAME)
//                .buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
//
//        attrValue.setValue(value);
//        attribute.getAttributeValues().add(attrValue);
//
//        return attribute;
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> T build(QName qName) {
//        return (T) builderFactory.getBuilder(qName).buildObject(qName);
//    }
//}
