<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:android="http://schemas.android.com/apk/res/android"  xmlns:amazon="http://schemas.amazon.com/apk/res/android">

  <xsl:param name="packageName"></xsl:param>

  <xsl:template match="permission[@android:name='c2d_message']">
    <permission android:protectionLevel="signature" android:name="{$packageName}.permission.C2D_MESSAGE"/>
  </xsl:template>

  <xsl:template match="uses-permission[@android:name='packageNamePermissionC2D']">
    <uses-permission android:name="{$packageName}.permission.C2D_MESSAGE"/>
  </xsl:template>

  <xsl:template match="provider[@android:authorities='packageNameFirebaseProvider']">
    <provider android:authorities="{$packageName}.firebaseinitprovider" android:exported="false" android:initOrder="100" android:name="com.google.firebase.provider.FirebaseInitProvider"/>
  </xsl:template>

  <xsl:template match="category[@android:name='packageName']">
    <category android:name="{$packageName}"/>
  </xsl:template>

 <!--    <xsl:strip-space elements="*" />-->
  <xsl:output indent="yes" />
  <xsl:template match="comment()" />
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
