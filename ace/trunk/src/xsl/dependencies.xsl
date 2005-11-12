<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:artifact="antlib:org.apache.maven.artifact.ant">
  
  <xsl:output indent="yes"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="project/artifact:dependencies"/>
  </xsl:template>
  
  <xsl:template match="/project/artifact:dependencies[@pathId='dependency.classpath']">
    <project name="depend" default="extract">
      <target name="extract">
        <xsl:for-each select="dependency">
          <xsl:choose>
            <xsl:when test="@scope = 'test'"></xsl:when>
            <xsl:otherwise>
              <xsl:element name="copy">
                <xsl:attribute name="file"><xsl:text>${maven.repo}</xsl:text>/<xsl:value-of select="@groupId"/>/<xsl:value-of select="@artifactId"/>/<xsl:value-of select="@version"/>/<xsl:value-of select="@artifactId"/>-<xsl:value-of select="@version"/>.jar</xsl:attribute>
                <xsl:attribute name="todir"><xsl:text>${target.dir}</xsl:text></xsl:attribute>
              </xsl:element>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </target>
    </project>
  </xsl:template>

</xsl:stylesheet>