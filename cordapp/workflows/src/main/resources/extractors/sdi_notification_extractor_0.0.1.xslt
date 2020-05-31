<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This tool extracts metadata from an xml invoice notification (at the moment only SDI notifications are considered) 
to be used by a SCALES node for VAS
			
			Created by: 	InfoCert
			Release:		0.1.0
            Last update: 	2020-02-18
            
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xsd="http://www.w3.org/2001/XMLSchema"
				xmlns:ns1="http://www.fatturapa.gov.it/sdi/messaggi/v1.0"
				xmlns:ns2="http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0"
				exclude-result-prefixes="xsl xsd ns1 ns2"
                version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
	
		
	<!--Structure of invoice notification metadata-->
	<xsl:template name="invoiceNotificationMetadata">
		<xsl:param name="competentAuthorityUniqueIdentifier"/>
		<xsl:param name="currentState"/>
		<xsl:param name="fileName"/>
		<xsl:param name="messageId"/>
		<xsl:param name="dateTimeReceipt"/>
		<xsl:param name="dateTimeDelivery"/>
		<xsl:param name="notificationNotes"/>
		<xsl:param name="notificationSignature"/>
		<xsl:param name="invoiceHash"/>
		<xsl:param name="invoiceOwner"/>
		<xsl:param name="invoiceSignature"/>
		<xsl:param name="approvedSubject"/>
		
		
		<!--ProcessBoundMetadata -->
		<xsl:comment>ProcessBoundMetadata</xsl:comment>
		<xsl:text>&#xA;</xsl:text>
		<InvoiceNotification>
			<CompetentAuthorityUniqueIdentifier>
				<xsl:value-of select="$competentAuthorityUniqueIdentifier"/>
			</CompetentAuthorityUniqueIdentifier>
			<CurrentState><xsl:value-of select="$currentState"/></CurrentState>
			<FileName><xsl:value-of select="$fileName"/></FileName>
			<MessageId><xsl:value-of select="$messageId"/></MessageId>
			<DateTimeReceipt><xsl:value-of select="$dateTimeReceipt"/></DateTimeReceipt>
			<DateTimeDelivery><xsl:value-of select="$dateTimeDelivery"/></DateTimeDelivery>
			<NotificationNotes><xsl:value-of select="$notificationNotes"/></NotificationNotes>
			<NotificationSignature><xsl:value-of select="$notificationSignature"/></NotificationSignature>
			<InvoiceHash><xsl:value-of select="$invoiceHash"/></InvoiceHash>
			<InvoiceOwner><xsl:value-of select="$invoiceOwner"/></InvoiceOwner>
			<InvoiceSignature><xsl:value-of select="$invoiceSignature"/></InvoiceSignature>
			<ApprovedSubject><xsl:value-of select="$approvedSubject"/></ApprovedSubject>
		</InvoiceNotification>
	</xsl:template>
	
	<!-- Notification Fattura elettronica PA12 Italiana: AttestazioneTrasmissioneFattura; NotificaDecorrenzaTermini -->
	<!-- Notification Fattura elettronica PR12 and semplificata FSM10 Italiana: Ricevutaconsegna; RicevutaImpossibiltarecapito -->
	<xsl:template match="*:AttestazioneTrasmissioneFattura | *:NotificaDecorrenzaTermini
		| *:RicevutaConsegna |*:RicevutaImpossibilitaRecapito">
		<xsl:call-template name="invoiceNotificationMetadata">
			<xsl:with-param name="competentAuthorityUniqueIdentifier"><xsl:value-of select="//IdentificativoSdI"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="fileName"><xsl:value-of select="//NomeFile"/></xsl:with-param>
			<xsl:with-param name="messageId"><xsl:value-of select="//MessageId"/></xsl:with-param>
			<xsl:with-param name="dateTimeReceipt"><xsl:value-of select="//DataOraRicezione"/></xsl:with-param>
			<xsl:with-param name="dateTimeDelivery"><xsl:value-of select="//DataOraConsegna"/></xsl:with-param>
			<xsl:with-param name="notificationNotes"><xsl:value-of select="//Note"/></xsl:with-param>
			<xsl:with-param name="notificationSignature"><xsl:value-of select="//*:SigningTime"/></xsl:with-param>
			<xsl:with-param name="invoiceHash"><xsl:value-of select="//HashFileOriginale"/><xsl:value-of select="//Hash"/></xsl:with-param>
			<xsl:with-param name="invoiceOwner"></xsl:with-param>
			<xsl:with-param name="invoiceSignature"></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
		
		</xsl:call-template>
	</xsl:template>
	
	<!-- Notification Fattura elettronica PA12 Italiana: NotificaEsito with Eisto=EC01-->
	<xsl:template match="*:NotificaEsito">
		<xsl:if test="//Esito='EC01'">	
		<xsl:call-template name="invoiceNotificationMetadata">
			<xsl:with-param name="competentAuthorityUniqueIdentifier"><xsl:value-of select="./IdentificativoSdI"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="fileName"><xsl:value-of select="//NomeFile"/></xsl:with-param>
			<xsl:with-param name="messageId"><xsl:value-of select="//MessageId"/></xsl:with-param>
			<xsl:with-param name="dateTimeReceipt"><xsl:value-of select="//DataOraRicezione"/></xsl:with-param>
			<xsl:with-param name="dateTimeDelivery"><xsl:value-of select="//DataOraConsegna"/></xsl:with-param>
			<xsl:with-param name="notificationNotes"><xsl:value-of select="//Note"/></xsl:with-param>
			<xsl:with-param name="notificationSignature"><xsl:value-of select="//SigningTime"/></xsl:with-param>
			<xsl:with-param name="invoiceHash"><xsl:value-of select="//HashFileOriginale"/><xsl:value-of select="//Hash"/></xsl:with-param>
			<xsl:with-param name="invoiceOwner"></xsl:with-param>
			<xsl:with-param name="invoiceSignature"></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	
	<!-- Notification Fattura elettronica PA Italiana -->
	<!--xsl:template match="//*[local-name()='NotificaEsito'] | //*[local-name()='AttestazioneTrasmissioneFattura'] | //*[local-name()='NotificaDecorrenzaTermini'] " name="FPA12Notification">
		<xsl:if test="//Esito='' or //Esito='EC01'">	
		<xsl:call-template name="invoiceNotificationMetadata">
			<xsl:with-param name="competentAuthorityUniqueIdentifier"><xsl:value-of select="//IdentificativoSdI"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="invoiceOwner"></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template-->
	
	<!-- Notification Fattura elettronica PR Italiana -->
	<!--xsl:template match="//*[local-name()='RicevutaConsegna'] | //*[local-name()='RicevutaImpossibilitaRecapito']" name="FPR12Notification">
		<xsl:call-template name="invoiceNotificationMetadata">
			<xsl:with-param name="competentAuthorityUniqueIdentifier"><xsl:value-of select="//IdentificativoSdI"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="invoiceOwner"></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
		</xsl:call-template>
	</xsl:template-->
	
	<!-- Notification Fattura elettronica semplificata FSM Italiana  -->
	<!--xsl:template match="//*[local-name()='FatturaElettronica']" name="FSM10Notification">
		<xsl:call-template name="invoiceNotificationMetadata">
			<xsl:with-param name="competentAuthorityUniqueIdentifier"><xsl:value-of select="//IdentificativoSdI"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="invoiceOwner"><xsl:value-of select="//TipoDocumento"/></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
		</xsl:call-template>
	</xsl:template-->
	
</xsl:stylesheet>