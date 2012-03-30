<?xml version="1.0" encoding="UTF-8"?>

<!--
   Simples XSL zum Ausdruck eines Laborblattes
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <html>
       <xsl:apply-templates select="Laborblatt"/>
    </html>
  </xsl:template>

  <xsl:template match="Laborblatt">
    <head>
       <title>
 Laborblatt von
 <xsl:value-of select="@Patient"></xsl:value-of>
 </title>
    </head>
    <body>
       <h2>Laborblatt von <xsl:value-of select="@Patient"/></h2>
       <p>Erstellt am <xsl:value-of select="@Erstellt"/></p>
       <table border="1">
       	   <thead>
       	    <th> - </th>
       	    <th>Referenz</th>
  		  	<xsl:apply-templates select="Daten"/>     
  		  	</thead>
		  <xsl:apply-templates select="Gruppe"/>
	   </table>
    </body>
  </xsl:template>

	<xsl:template match="Daten">
		<xsl:for-each select="Datum">
			<th><xsl:value-of select="@Tag"/></th>
		</xsl:for-each>
	</xsl:template>

  <xsl:template match="Gruppe">
    <tr><td><b><xsl:value-of select="@Name"/></b></td></tr>
    	<xsl:for-each select="Parameter">
			<tr>
				<td><xsl:value-of select="@Name"/> (<xsl:value-of select="@Einheit"/>)</td>
				<td><xsl:apply-templates select="Referenz"/></td>
				<xsl:for-each select="Resultat">
					<td><xsl:value-of select="."/> </td>
				</xsl:for-each>
			</tr>
		</xsl:for-each>
  </xsl:template>
 <xsl:template match="Referenz">
 	<xsl:value-of select="@min"/> - <xsl:value-of select="@max"/>
 </xsl:template>

</xsl:stylesheet>