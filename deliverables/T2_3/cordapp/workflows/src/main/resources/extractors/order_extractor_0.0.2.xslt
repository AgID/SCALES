<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This tool extracts metadata from a Peppol order (for this purpose only order_only is considered) 
to be used by a SCALES node for VAS
			
			Created by: 	InfoCert
			Release:		0.0.2
            Last update: 	2020-05-21
            
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xsd="http://www.w3.org/2001/XMLSchema"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
				xmlns:n1="urn:oasis:names:specification:ubl:schema:xsd:Order-2" 
				xmlns:n2="urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2" 
				xmlns:n3="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"
				exclude-result-prefixes="xsl xsd xsi cac cbc n1 n2 n3"
                version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
	
	<xsl:template name="envelopeMetadata">
		<xsl:param name="orderSender"><xsl:value-of select="n3:StandardBusinessDocumentHeader/n3:Sender/n3:Identifier"/></xsl:param>
		<xsl:param name="orderReceiver"><xsl:value-of select="n3:StandardBusinessDocumentHeader/n3:Receiver/n3:Identifier"/></xsl:param>
		<xsl:param name="orderCreationDateAndTime"><xsl:value-of select="n3:StandardBusinessDocumentHeader/n3:DocumentIdentification/n3:CreationDateAndTime"/></xsl:param>
			
		<!--DataBoundMetadata -->
		<xsl:comment>DataBoundMetadata</xsl:comment>
		<xsl:text>&#xA;</xsl:text>
		
		
			<ENVELOPE>
				<OrderSender><xsl:value-of select="$orderSender"/></OrderSender>
				<OrderReceiver><xsl:value-of select="$orderReceiver"/></OrderReceiver>
				<OrderCreationDateAndTime><xsl:value-of select="$orderCreationDateAndTime"/></OrderCreationDateAndTime>
			</ENVELOPE>
		
	</xsl:template>
	
	
	<!--Structure of order metadata-->
	<xsl:template name="orderMetadata">
		<xsl:param name="orderProfile"><xsl:value-of select="//cbc:ProfileID"/></xsl:param>
		<xsl:param name="orderID"><xsl:value-of select ="//n1:Order/cbc:ID"/></xsl:param>
		<xsl:param name="orderIssueDate"><xsl:value-of select="//cbc:IssueDate"/></xsl:param>
		<xsl:param name="orderTypeCode"><xsl:value-of select="//cbc:OrderTypeCode"/></xsl:param>
		<xsl:param name="orderCurrencyCode"><xsl:value-of select="//cbc:DocumentCurrencyCode"/></xsl:param>
		<xsl:param name="orderDocumentReference"><xsl:value-of select="//cac:OrderDocumentReference/cbc:ID"/></xsl:param>
		<xsl:param name="originatorDocumentReference"><xsl:value-of select="//cac:OriginatorDocumentReference/cbc:ID"/>&#160;<xsl:for-each select="//cac:OrderLine"><xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cbc:ID"/>&#160;</xsl:for-each></xsl:param>
		<xsl:param name="contract"><xsl:value-of select="//cac:Contract/cbc:ID"/></xsl:param>
		<xsl:param name="buyerVATIdentifier"><xsl:value-of select="//cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']/cbc:CompanyID"/></xsl:param>
		<xsl:param name="buyerElectronicAddress"><xsl:value-of select="//cac:BuyerCustomerParty/cac:Party/cbc:EndpointID"/></xsl:param>
		<xsl:param name="buyerElectronicAddressSchemeIdentifier"><xsl:value-of select="//cac:BuyerCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/></xsl:param>
		<xsl:param name="sellerVATIdentifier"><xsl:value-of select="//cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/></xsl:param>
		<xsl:param name="sellerElectronicAddress"><xsl:value-of select="//cac:SellerSupplierParty/cac:Party/cbc:EndpointID"/></xsl:param>
		<xsl:param name="sellerElectronicAddressSchemeIdentifier"><xsl:value-of select="//cac:SellerSupplierParty/cac:Party/cbc:EndpointID/@schemeID"/></xsl:param>
		<xsl:param name="sellerPartyIdentifier"><xsl:value-of select="//cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/></xsl:param>
		<xsl:param name="accountingCustomerVATIdentifier"><xsl:value-of select="//cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']/cbc:CompanyID"/></xsl:param>
		<xsl:param name="accountingCustomerElectronicAddress"><xsl:value-of select="//cac:AccountingCustomerParty/cac:Party/cbc:EndpointID"/></xsl:param>
		<xsl:param name="accountingCustomerElectronicAddressSchemeIdentifier"><xsl:value-of select="//cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/></xsl:param>
		<xsl:param name="payableAmount"><xsl:value-of select="//cac:AnticipatedMonetaryTotal/cbc:PayableAmount"/></xsl:param>
		
	
		<Order>
			
			<OrderProfile>
					<xsl:value-of select="$orderProfile"/>
			</OrderProfile>
			<OrderID><xsl:value-of select="$orderID"/></OrderID>
			<OrderIssueDate><xsl:value-of select="$orderIssueDate"/></OrderIssueDate>
			<OrderTypeCode><xsl:value-of select="$orderTypeCode"/></OrderTypeCode>
			<OrderCurrencyCode><xsl:value-of select="$orderCurrencyCode"/></OrderCurrencyCode>
			<OrderDocumentReference><xsl:value-of select="$orderDocumentReference"/></OrderDocumentReference>
			<OriginatorDocumentReference><xsl:value-of select="$originatorDocumentReference"/></OriginatorDocumentReference>
			<Contract><xsl:value-of select="$contract"/></Contract>
			<BuyerVATIdentifier><xsl:value-of select="$buyerVATIdentifier"/></BuyerVATIdentifier>
			<BuyerElectronicAddress><xsl:value-of select="$buyerElectronicAddress"/></BuyerElectronicAddress>
			<BuyerElectronicAddressSchemeIdentifier><xsl:value-of select="$buyerElectronicAddressSchemeIdentifier"/></BuyerElectronicAddressSchemeIdentifier>
			<SellerVATIdentifier><xsl:value-of select="$sellerVATIdentifier"/></SellerVATIdentifier>
			<SellerElectronicAddress><xsl:value-of select="$sellerElectronicAddress"/></SellerElectronicAddress>
			<SellerElectronicAddressSchemeIdentifier><xsl:value-of select="$sellerElectronicAddressSchemeIdentifier"/></SellerElectronicAddressSchemeIdentifier>
			<SellerPartyIdentifier><xsl:value-of select="$sellerPartyIdentifier"/></SellerPartyIdentifier>
			<AccountingCustomerVATIdentifier><xsl:value-of select="$accountingCustomerVATIdentifier"/></AccountingCustomerVATIdentifier>
			<AccountingCustomerElectronicAddress><xsl:value-of select="$accountingCustomerElectronicAddress"/></AccountingCustomerElectronicAddress>
			<AccountingCustomerAddressSchemeIdentifier><xsl:value-of select="$accountingCustomerElectronicAddressSchemeIdentifier"/></AccountingCustomerAddressSchemeIdentifier>
			<PayableAmount><xsl:value-of select="$payableAmount"/></PayableAmount>
		</Order>
	</xsl:template>
	
	<!-- UBL EU or Peppol BIS 3.0 Order -->
	<xsl:template match="n1:Order |n3:StandardBusinessDocument">
		<xsl:if test="contains(//cbc:ProfileID,'urn:fdc:peppol.eu:poacc:bis:order_only:3')"> <!--order_only for Peppol Order transaction 3.1 T01, it is scalable to other profiles as well -->
		
		<xsl:choose>
			<!--only order without SBDH-->
			<xsl:when test="/n1:Order">
				<xsl:call-template name="orderMetadata"/>
				
			</xsl:when>
			
			<!--order or  with SBDH-->
			<xsl:when test="/n3:StandardBusinessDocument">
				<Root>
					<xsl:call-template name="envelopeMetadata"/>
					<xsl:call-template name="orderMetadata"/>
				</Root>
			</xsl:when>
		</xsl:choose>
		</xsl:if>
	</xsl:template>	
	
</xsl:stylesheet>