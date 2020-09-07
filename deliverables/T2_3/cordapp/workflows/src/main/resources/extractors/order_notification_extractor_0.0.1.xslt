<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This tool extracts metadata from an xml order notification (at the moment only NSO notifications are considered) 
to be used by a SCALES node for VAS
			
			Created by: 	InfoCert
			Release:		0.0.1
            Last update: 	2020-05-21
            
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xsd="http://www.w3.org/2001/XMLSchema"
				xmlns:ns2="http://www.w3.org/2000/09/xmldsig#"
				xmlns:ns3="http://nso.rgs.mef.gov.it/docs/xsd/ordini/messaggi/v1.0"
				exclude-result-prefixes="xsl xsd ns2 ns3"
                version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
	
		
	<!--Structure of order notification metadata-->
	<xsl:template name="orderNotificationMetadata">
		<xsl:param name="idT"/>
		<xsl:param name="currentState"/>
		<xsl:param name="fileName"/>
		<xsl:param name="messageId"/>
		<xsl:param name="dateTimeReceipt"/>
		<xsl:param name="dateTimeDelivery"/>
		<xsl:param name="receiverID"/>
		<xsl:param name="notificationNotes"/>
		<xsl:param name="notificationSignature"/>
		<xsl:param name="orderHash"/>
		<xsl:param name="approvedSubject"/>
		
		
		<!--ProcessBoundMetadata -->
		<xsl:comment>ProcessBoundMetadata</xsl:comment>
		<xsl:text>&#xA;</xsl:text>
		<orderNotification>
			<IdT>
				<xsl:value-of select="$idT"/>
			</IdT>
			<CurrentState><xsl:value-of select="$currentState"/></CurrentState>
			<FileName><xsl:value-of select="$fileName"/></FileName>
			<MessageId><xsl:value-of select="$messageId"/></MessageId>
			<DateTimeReceipt><xsl:value-of select="$dateTimeReceipt"/></DateTimeReceipt>
			<DateTimeDelivery><xsl:value-of select="$dateTimeDelivery"/></DateTimeDelivery>
			<ReceiverID><xsl:value-of select="$receiverID"/></ReceiverID>
			<NotificationNotes><xsl:value-of select="$notificationNotes"/></NotificationNotes>
			<NotificationSignature><xsl:value-of select="$notificationSignature"/></NotificationSignature>
			<OrderHash><xsl:value-of select="$orderHash"/></OrderHash>
			<ApprovedSubject><xsl:value-of select="$approvedSubject"/></ApprovedSubject>
		</orderNotification>
	</xsl:template>
	
	<!-- Notification Order: OS NotificaScarto, OT MetadatiInvioFile, OC RicevutaConsegna, OA AttestazioneTrasmissione, OM MancataConsegna, OV RicevutaConsegna validazione diretta o con terza parte in ingresso -->
	<xsl:template match="*:NotificaScarto | *:MetadatiInvioFile
		| *:RicevutaConsegna |*:AttestazioneTrasmissione |*:NotificaMancataConsegna">
		<xsl:call-template name="orderNotificationMetadata">
			<xsl:with-param name="idT"><xsl:value-of select="//Identificativo"/></xsl:with-param>
			<xsl:with-param name="currentState"><xsl:value-of select="local-name()"/></xsl:with-param>
			<xsl:with-param name="fileName"><xsl:value-of select="//NomeFile"/></xsl:with-param>
			<xsl:with-param name="messageId"><xsl:value-of select="//MessageId"/></xsl:with-param>
			<xsl:with-param name="dateTimeReceipt"><xsl:value-of select="//DataOraRicezione"/></xsl:with-param>
			<xsl:with-param name="dateTimeDelivery"><xsl:value-of select="//DataOraConsegna"/></xsl:with-param>
			<xsl:with-param name="receiverID"><xsl:value-of select="//Ricevente/Codice"/></xsl:with-param>
			<xsl:with-param name="notificationNotes"><xsl:value-of select="//Note"/></xsl:with-param>
			<xsl:with-param name="notificationSignature"><xsl:value-of select="//*:SigningTime"/></xsl:with-param>
			<xsl:with-param name="orderHash"><xsl:value-of select="//HashFileOriginale"/><xsl:value-of select="//HASH"/></xsl:with-param>
			<xsl:with-param name="approvedSubject"></xsl:with-param>
		
		</xsl:call-template>
	</xsl:template>
	
	
	
</xsl:stylesheet>