<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This tool extracts metadata from an xml invoice (e.g. Fattura Elettronica FPAPR, UBL EU invoice, Peppol invoice, CII EU invoice) 
to be used by a SCALES node for VAS
			
			Created by: 	InfoCert
			Release:		0.1.0
            Last update: 	2020-02-12
            
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xsd="http://www.w3.org/2001/XMLSchema"
				xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
               xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
				xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"	
				xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"				
				xmlns:p="http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2"
				xmlns:LI="Legalinvoice" 
				exclude-result-prefixes="xsl xsd cac cbc rsm ram udt p LI"
                version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
	
	
	<!-- 
        Function for ImportoTotaleDocumento calculation
        
        LI:InvoiceTotalAmountWithVAT_FPRPA12 => FPAPR
        LI:InvoiceTotalAmountWithVAT_FSM10   => FSM

    It returns the calculated ImportoTotaleDocumento
    NB: the value could be negative in case of invoice treated as credit note
      
    In case the select does not find any value the value will be 0
   
    -->
	<xsl:function name="LI:InvoiceTotalAmountWithVAT_FPRPA12" as="xsd:decimal">
		<xsl:param name="DatiBeniServizi"/>
		<!--
        ImponibileImporto = “/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo/ImponibileImporto” 
        Imposta = “/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo/Imposta” 
        Arrotondamento = “/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo/Arrotondamento” 
        
        Context: DatiBeniServizi
        ImportoTotaleDocumento CALCOLATO =  ∑ 𝑰𝒎𝒑𝒐𝒏𝒊𝒃𝒊𝒍𝒆𝑰𝒎𝒑𝒐𝒓𝒕𝒐 + ∑ Imposta (+  ∑ 𝑨𝒓𝒓𝒐𝒕𝒐𝒏𝒅𝒂𝒎𝒆𝒏𝒕𝒐 - non serve) 
               
        sum(DatiBeniServizi/DatiRiepilogo/ImponibileImporto) + sum(DatiBeniServizi/DatiRiepilogo/Imposta) 
        -->
		<xsl:variable name='ImportoTotaleDocumentoCalcolato' as="xsd:decimal" select="sum(xsd:decimal($DatiBeniServizi/DatiRiepilogo/ImponibileImporto)) + sum(xsd:decimal($DatiBeniServizi/DatiRiepilogo/Imposta))" />        
		<xsl:value-of select="$ImportoTotaleDocumentoCalcolato"/>       
	</xsl:function>
	
	
	<xsl:function name="LI:InvoiceTotalAmountWithVAT_FSM10" as="xsd:decimal">
		<xsl:param name="DatiBeniServizi"/>
		<!--
        ImponibileImporto = “/FatturaElettronicaSemplificata/FatturaElettronicaBody/DatiBeniServizi/Importo” 
        Imposta = “/FatturaElettronicaSemplificata/FatturaElettronicaBody/DatiBeniServizi/DatiIVA/Imposta” 
        
        Context: DatiBeniServizi
        ImportoTotaleDocumento CALCOLATO =  ∑ 𝑰𝒎𝒑𝒐𝒓𝒕𝒐 + ∑ Imposta ) 
               
        sum(DatiBeniServizi/Importo) + sum(DatiBeniServizi/DatiIVA/Imposta) 
        -->
		<xsl:variable name='ImportoTotaleDocumentoCalcolato' as="xsd:decimal" select="sum(xsd:decimal($DatiBeniServizi/Importo)) + sum(xsd:decimal($DatiBeniServizi/DatiIVA/Imposta))" />        
		<xsl:value-of select="$ImportoTotaleDocumentoCalcolato"/>
	</xsl:function>
	
	<!--Structure of invoice metadata-->
	<xsl:template name="invoiceMetadata">
		<xsl:param name="invoiceFormat"/>
		<xsl:param name="invoiceNumber"/>
		<xsl:param name="invoiceIssueDate"/>
		<xsl:param name="invoiceTypeCode"/>
		<xsl:param name="invoiceCurrencyCode"/>
		<xsl:param name="projectReference"/>
		<xsl:param name="purchaseOrderReference"/>
		<xsl:param name="tenderOrLotReference"/>
		<xsl:param name="sellerVATIdentifier"/>
		<xsl:param name="sellerTaxRegistrationIdentifier"/>
		<xsl:param name="buyerVATIdentifier"/>
		<xsl:param name="buyerTaxRegistrationIdentifier"/>
		<xsl:param name="buyerTaxRegistrationIdentifierSchemeIdentifier"/>
		<xsl:param name="buyerElectronicAddress"/>
		<xsl:param name="buyerElectronicAddressSchemeIdentifier"/>
		<xsl:param name="paymentDueDate"/>
		<xsl:param name="invoiceTotalAmountWithVAT"/>
		<xsl:param name="amountDueForPayment"/>
		
		<!--DataBoundMetadata -->
		<xsl:comment>DataBoundMetadata</xsl:comment>
		<xsl:text>&#xA;</xsl:text>
		<Invoice>
		<InvoiceFormat>
					<xsl:value-of select="$invoiceFormat"/>
			</InvoiceFormat>
			<InvoiceNumber><xsl:value-of select="$invoiceNumber"/></InvoiceNumber>
			<InvoiceIssueDate><xsl:value-of select="$invoiceIssueDate"/></InvoiceIssueDate>
			<InvoiceTypeCode><xsl:value-of select="$invoiceTypeCode"/></InvoiceTypeCode>
			<InvoiceCurrencyCode><xsl:value-of select="$invoiceCurrencyCode"/></InvoiceCurrencyCode>
			<ProjectReference><xsl:value-of select="$projectReference"/></ProjectReference>
			<PurchaseOrderReference><xsl:value-of select="$purchaseOrderReference"/></PurchaseOrderReference>
			<TenderOrLotReference><xsl:value-of select="$tenderOrLotReference"/></TenderOrLotReference>
			<SellerVATIdentifier><xsl:value-of select="$sellerVATIdentifier"/></SellerVATIdentifier>
			<SellerTaxRegistrationIdentifier><xsl:value-of select="$sellerTaxRegistrationIdentifier"/></SellerTaxRegistrationIdentifier>
			<BuyerVATIdentifier><xsl:value-of select="$buyerVATIdentifier"/></BuyerVATIdentifier>
			<BuyerTaxRegistrationIdentifier><xsl:value-of select="$buyerTaxRegistrationIdentifier"/></BuyerTaxRegistrationIdentifier>
			<BuyerTaxRegistrationIdentifierSchemeIdentifier><xsl:value-of select="$buyerTaxRegistrationIdentifierSchemeIdentifier"/></BuyerTaxRegistrationIdentifierSchemeIdentifier>
			<BuyerElectronicAddress><xsl:value-of select="$buyerElectronicAddress"/></BuyerElectronicAddress>
			<BuyerElectronicAddressSchemeIdentifier><xsl:value-of select="$buyerElectronicAddressSchemeIdentifier"/></BuyerElectronicAddressSchemeIdentifier>
			<PaymentDueDate><xsl:value-of select="$paymentDueDate"/></PaymentDueDate>
			<InvoiceTotalAmountWithVAT><xsl:value-of select="$invoiceTotalAmountWithVAT"/></InvoiceTotalAmountWithVAT>
			<AmountDueForPayment><xsl:value-of select="$amountDueForPayment"/></AmountDueForPayment>
		</Invoice>
	</xsl:template>
	
	<!-- UBL EU or Peppol BIS 3.0 Invoice -->
<xsl:template match="//*[local-name()='Invoice']" name="UBLinvoice">
	<xsl:if test="contains(cbc:CustomizationID,'urn:cen.eu:en16931:2017')">
		<xsl:call-template name="invoiceMetadata">
			<xsl:with-param name="invoiceFormat">UBL_CEN</xsl:with-param>
			<xsl:with-param name="invoiceNumber"><xsl:value-of select="cbc:ID"/></xsl:with-param>
			<xsl:with-param name="invoiceIssueDate"><xsl:value-of select="cbc:IssueDate"/></xsl:with-param>
			<xsl:with-param name="invoiceTypeCode"><xsl:value-of select="cbc:InvoiceTypeCode"/></xsl:with-param>
			<xsl:with-param name="invoiceCurrencyCode"><xsl:value-of select="cbc:DocumentCurrencyCode"/></xsl:with-param>
			<xsl:with-param name="projectReference"><xsl:value-of select="cac:ProjectReference/cbc:ID"/></xsl:with-param>
			<xsl:with-param name="purchaseOrderReference"><xsl:value-of select="cac:OrderReference/cbc:ID"/></xsl:with-param>
			<xsl:with-param name="tenderOrLotReference"><xsl:value-of select="cac:OriginatorDocumentReference/cbc:ID"/></xsl:with-param>
			<xsl:with-param name="sellerVATIdentifier">
				<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']/cbc:CompanyID"/>
			</xsl:with-param>
			<xsl:with-param name="sellerTaxRegistrationIdentifier">
				<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']/cbc:CompanyID"/>
			</xsl:with-param>
			<xsl:with-param name="buyerVATIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/></xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/></xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifierSchemeIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID"/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddress"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID"/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddressSchemeIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/></xsl:with-param>
			<xsl:with-param name="paymentDueDate"><xsl:value-of select="cbc:DueDate"/></xsl:with-param>
			<xsl:with-param name="invoiceTotalAmountWithVAT"><xsl:value-of select="cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount"/></xsl:with-param>
			<xsl:with-param name="amountDueForPayment"><xsl:value-of select="cac:LegalMonetaryTotal/cbc:PayableAmount"/></xsl:with-param>
			
			</xsl:call-template>
	</xsl:if>	
</xsl:template>
	
	<!-- UBL EU or Peppol BIS 3.0 Credit Note -->
	<xsl:template match="//*[local-name()='CreditNote']" name="UBLcreditNote">
		<xsl:if test="contains(cbc:CustomizationID,'urn:cen.eu:en16931:2017')">
			<xsl:call-template name="invoiceMetadata">
				<xsl:with-param name="invoiceFormat">UBLCN_CEN</xsl:with-param>
				<xsl:with-param name="invoiceNumber"><xsl:value-of select="cbc:ID"/></xsl:with-param>
				<xsl:with-param name="invoiceIssueDate"><xsl:value-of select="cbc:IssueDate"/></xsl:with-param>
				<xsl:with-param name="invoiceTypeCode"><xsl:value-of select="cbc:CreditNoteTypeCode"/></xsl:with-param>
				<xsl:with-param name="invoiceCurrencyCode"><xsl:value-of select="cbc:DocumentCurrencyCode"/></xsl:with-param>
				<xsl:with-param name="projectReference"><xsl:value-of select="cac:AdditionalDocumentReference[normalize-space(cbc:DocumentTypeCode)='50']/cbc:ID"/></xsl:with-param>
				<xsl:with-param name="purchaseOrderReference"><xsl:value-of select="cac:OrderReference/cbc:ID"/></xsl:with-param>
				<xsl:with-param name="tenderOrLotReference"><xsl:value-of select="cac:OriginatorDocumentReference/cbc:ID"/></xsl:with-param>
				<xsl:with-param name="sellerVATIdentifier">
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']/cbc:CompanyID"/>
				</xsl:with-param>
				<xsl:with-param name="sellerTaxRegistrationIdentifier">
					<xsl:value-of select="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']/cbc:CompanyID"/>
				</xsl:with-param><xsl:with-param name="buyerVATIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/></xsl:with-param>
				<xsl:with-param name="buyerTaxRegistrationIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/></xsl:with-param>
				<xsl:with-param name="buyerTaxRegistrationIdentifierSchemeIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID"/></xsl:with-param>
				<xsl:with-param name="buyerElectronicAddress"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID"/></xsl:with-param>
				<xsl:with-param name="buyerElectronicAddressSchemeIdentifier"><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/></xsl:with-param>
				<xsl:with-param name="paymentDueDate"><xsl:value-of select="cac:PaymentMeans/cbc:PaymentDueDate"/></xsl:with-param>
				<xsl:with-param name="invoiceTotalAmountWithVAT"><xsl:value-of select="cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount"/></xsl:with-param>
				<xsl:with-param name="amountDueForPayment"><xsl:value-of select="cac:LegalMonetaryTotal/cbc:PayableAmount"/></xsl:with-param>
				
			</xsl:call-template>
		</xsl:if>	
	</xsl:template>
	
	<!-- CII EU Invoice -->
	<xsl:template match="//*[name()='rsm:CrossIndustryInvoice']" name="CIIinvoice">
		<xsl:call-template name="invoiceMetadata">
			<xsl:with-param name="invoiceFormat">CII_CEN</xsl:with-param>
			<xsl:with-param name="invoiceNumber"><xsl:value-of select="./rsm:ExchangedDocument/ram:ID"/></xsl:with-param>
			<xsl:with-param name="invoiceIssueDate"><xsl:variable name="dateToFormat" select="//ram:IssueDateTime/udt:DateTimeString"/>
				<xsl:value-of select="concat(substring($dateToFormat, 1, 4), '-', substring($dateToFormat, 5, 2), '-', substring($dateToFormat, 7, 2))"/>
 			</xsl:with-param>
			<xsl:with-param name="invoiceTypeCode"><xsl:value-of select="./rsm:ExchangedDocument/ram:TypeCode"/></xsl:with-param>
			<xsl:with-param name="invoiceCurrencyCode"><xsl:value-of select="//ram:InvoiceCurrencyCode"/></xsl:with-param>
			<xsl:with-param name="projectReference"><xsl:value-of select="//ram:SpecifiedProcuringProject/ram:ID"/></xsl:with-param>
			<xsl:with-param name="purchaseOrderReference"><xsl:value-of select="//ram:BuyerOrderReferencedDocument/ram:IssuerAssignedID"/></xsl:with-param>
			<xsl:with-param name="tenderOrLotReference"><xsl:value-of select="//ram:AdditionalReferencedDocument[normalize-space(ram:TypeCode) = '50']/ram:IssuerAssignedID"/></xsl:with-param>
			<xsl:with-param name="sellerVATIdentifier"><xsl:value-of select="//ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID[normalize-space(@schemeID) = 'VA']"/></xsl:with-param>
			<xsl:with-param name="sellerTaxRegistrationIdentifier"><xsl:value-of select="//ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID[normalize-space(@schemeID) = 'FC']"/></xsl:with-param>
			<xsl:with-param name="buyerVATIdentifier"><xsl:value-of select="//ram:BuyerTradeParty/ram:SpecifiedTaxRegistration/ram:ID"/></xsl:with-param>
			<!--BT-46 is 0..1 ID if without schemeID, GlobalID if with schemeID -->
			<xsl:with-param name="buyerTaxRegistrationIdentifier"><xsl:value-of select="//ram:BuyerTradeParty/ram:ID"/><xsl:value-of select="//ram:BuyerTradeParty/ram:GlobalID"/></xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifierSchemeIdentifier"><xsl:value-of select="//ram:BuyerTradeParty/ram:GlobalID/@schemeID"/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddress"><xsl:value-of select="//ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID"/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddressSchemeIdentifier"><xsl:value-of select="//ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID/@schemeID"/></xsl:with-param>
			<xsl:with-param name="paymentDueDate"><xsl:variable name="dateToFormat" select="//ram:SpecifiedTradePaymentTerms/ram:DueDateDateTime/udt:DateTimeString"/>
				<xsl:value-of select="concat(substring($dateToFormat, 1, 4), '-', substring($dateToFormat, 5, 2), '-', substring($dateToFormat, 7, 2))"/>
			</xsl:with-param>
			<xsl:with-param name="invoiceTotalAmountWithVAT"><xsl:value-of select="//ram:GrandTotalAmount"/></xsl:with-param>
			<xsl:with-param name="amountDueForPayment"><xsl:value-of select="//ram:DuePayableAmount"/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	

	<!-- Fattura elettronica PAPR Italiana -->
	<xsl:template match="//*[local-name()='FatturaElettronica']" name="fatturaElettronica">
		<xsl:call-template name="invoiceMetadata">
			<xsl:with-param name="invoiceFormat"><xsl:value-of select="@versione"/></xsl:with-param>
			<xsl:with-param name="invoiceNumber"><xsl:value-of select="//Numero"/></xsl:with-param>
			<xsl:with-param name="invoiceIssueDate"><xsl:value-of select="//Data"/></xsl:with-param>
			<xsl:with-param name="invoiceTypeCode"><xsl:value-of select="//TipoDocumento"/></xsl:with-param>
			<xsl:with-param name="invoiceCurrencyCode"><xsl:value-of select="//Divisa"/></xsl:with-param>
			<xsl:with-param name="projectReference"><xsl:value-of select="//DatiContratto/CodiceCUP"/></xsl:with-param>
			<xsl:with-param name="purchaseOrderReference"><xsl:value-of select="//DatiOrdineAcquisto/IdDocumento"/></xsl:with-param>
			<xsl:with-param name="tenderOrLotReference"><xsl:value-of select="//DatiContratto/CodiceCIG"/></xsl:with-param>
			<xsl:with-param name="sellerVATIdentifier"><xsl:value-of select="concat(//CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdPaese,//CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice)"/>
			</xsl:with-param>
			<xsl:with-param name="sellerTaxRegistrationIdentifier"><xsl:value-of select="//CedentePrestatore/DatiAnagrafici/CodiceFiscale"/></xsl:with-param>
			<xsl:with-param name="buyerVATIdentifier"><xsl:value-of select="concat(//CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdPaese,//CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice)"/>
			</xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifier"><xsl:value-of select="//CessionarioCommittente/DatiAnagrafici/CodiceFiscale"/></xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifierSchemeIdentifier">9907</xsl:with-param>
			<xsl:with-param name="buyerElectronicAddress"><xsl:value-of select="//PECDestinatario "/><xsl:value-of select="//CodiceDestinatario "/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddressSchemeIdentifier"><xsl:if test="//PECDestinatario">204</xsl:if><xsl:if test="//CodiceDestinatario">205</xsl:if></xsl:with-param>
			<xsl:with-param name="paymentDueDate"><xsl:value-of select="//DataScadenzaPagamento"/></xsl:with-param>
			<xsl:with-param name="invoiceTotalAmountWithVAT"><xsl:if test="//ImportoTotaleDocumento"><xsl:value-of select="//ImportoTotaleDocumento"/>
			</xsl:if><xsl:if test="(//ImportoTotaleDocumento[.=''])"><xsl:value-of select="LI:InvoiceTotalAmountWithVAT_FPRPA12(*//DatiBeniServizi)"/></xsl:if></xsl:with-param>
			<xsl:with-param name="amountDueForPayment"><xsl:value-of select="//ImportoPagamento"/></xsl:with-param>

		</xsl:call-template>
	</xsl:template>
	
	<!-- Fattura elettronica semplificata FSM Italiana -->
	<xsl:template match="//*[local-name()='FatturaElettronicaSemplificata']" name="fatturaElettronicaSemplificata">
		<xsl:call-template name="invoiceMetadata">
			<xsl:with-param name="invoiceFormat"><xsl:value-of select="@versione"/></xsl:with-param>
			<xsl:with-param name="invoiceNumber"><xsl:value-of select="//Numero"/></xsl:with-param>
			<xsl:with-param name="invoiceIssueDate"><xsl:value-of select="//Data"/></xsl:with-param>
			<xsl:with-param name="invoiceTypeCode"><xsl:value-of select="//TipoDocumento"/></xsl:with-param>
			<xsl:with-param name="invoiceCurrencyCode"><xsl:value-of select="//Divisa"/></xsl:with-param>
			<!--<xsl:with-param name="projectReference"><xsl:value-of select=""/></xsl:with-param-->
			<!--xsl:with-param name="purchaseOrderReference"><xsl:value-of select=""/></xsl:with-param-->
			<!--xsl:with-param name="tenderOrLotReference"><xsl:value-of select=""/></xsl:with-param-->
			<xsl:with-param name="sellerVATIdentifier"><xsl:value-of select="concat(//CedentePrestatore/IdFiscaleIVA/IdPaese,//CedentePrestatore/IdFiscaleIVA/IdCodice)"/>
			</xsl:with-param>
			<xsl:with-param name="sellerTaxRegistrationIdentifier"><xsl:value-of select="//CedentePrestatore/CodiceFiscale"/></xsl:with-param>
			<xsl:with-param name="buyerVATIdentifier"><xsl:value-of select="concat(//CessionarioCommittente/IdentificativiFiscali/IdFiscaleIVA/IdPaese,//CessionarioCommittente/IdentificativiFiscali/IdFiscaleIVA/IdCodice)"/>
			</xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifier"><xsl:value-of select="CessionarioCommittente/IdentificativiFiscali/CodiceFiscale "/></xsl:with-param>
			<xsl:with-param name="buyerTaxRegistrationIdentifierSchemeIdentifier">9907</xsl:with-param>
			<xsl:with-param name="buyerElectronicAddress"><xsl:value-of select="//PECDestinatario "/><xsl:value-of select="//CodiceDestinatario "/></xsl:with-param>
			<xsl:with-param name="buyerElectronicAddressSchemeIdentifier"><xsl:if test="//PECDestinatario">204</xsl:if><xsl:if test="//CodiceDestinatario">205</xsl:if></xsl:with-param>
			<!--xsl:with-param name="paymentDueDate"><xsl:value-of select=""/></xsl:with-param-->
			<xsl:with-param name="invoiceTotalAmountWithVAT"><xsl:value-of select="LI:InvoiceTotalAmountWithVAT_FSM10(*//DatiBeniServizi)"/></xsl:with-param>
			<!--xsl:with-param name="amountDueForPayment"><xsl:value-of select=""/></xsl:with-param-->
			
		</xsl:call-template>
	</xsl:template>
	
	

</xsl:stylesheet>