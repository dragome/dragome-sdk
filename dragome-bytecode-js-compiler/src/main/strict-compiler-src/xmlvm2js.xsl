<?xml version="1.0"?>

<!--
/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
-->

<!--
 * Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
-->

<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
                xmlns:vm = "http://xmlvm.org"
                xmlns:xs = "http://www.w3.org/2001/XMLSchema"
                xmlns:jvm = "http://xmlvm.org/jvm"
                xmlns:dex = "http://xmlvm.org/dex"
                version = "2.0">

<xsl:param name="method-name" />
<xsl:param name="method-signature" />
<xsl:output method="text" indent="no"/>

<xsl:template match="vm:xmlvm">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="vm:class2">
<xsl:for-each select="vm:method[@name=$method-name and @signature=$method-signature]">
      <xsl:apply-templates select="."/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="vm:class">

<!-- Added for Dynamic Loading -->
<xsl:call-template name="checkClass">
  <xsl:with-param name="string" select="@extends"/>
</xsl:call-template>

 <xsl:text>
qx.Class.define("</xsl:text><xsl:call-template name="getPackgePlusClassName"><xsl:with-param name="package" select="@package"/><xsl:with-param name="classname" select="@name"/></xsl:call-template><xsl:text>", {
  extend: </xsl:text><xsl:call-template name="emitScopedName"><xsl:with-param name="string" select="@extends"/></xsl:call-template><xsl:text>,
  construct: function() {},
  implement: [</xsl:text>
  <xsl:call-template name="emitScopedName"><xsl:with-param name="string" select="@interfaces"/></xsl:call-template>
  <xsl:text>],
  statics:
  {</xsl:text>
    <xsl:for-each select="vm:field[count(@isStatic)=1 and @isStatic='true']">
			<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template><xsl:text>: </xsl:text>
        <xsl:if test="(@type='java.lang.String') and (@value)">
          <xsl:text>"</xsl:text>
        </xsl:if>
			<xsl:value-of select="if (@value) then @value else 0"/>
        <xsl:if test="(@type='java.lang.String') and (@value)">
          <xsl:text>"</xsl:text>
        </xsl:if>
       <xsl:text>,</xsl:text>
	</xsl:for-each>
	<xsl:for-each select="vm:method[count(@isStatic)=1 and @isStatic='true']">
  <xsl:if test="position() != 1">
       <xsl:text>,
</xsl:text>
     </xsl:if>/* start of <xsl:value-of select="concat(concat(@name, '#'),@signature)"/>*/
			<xsl:apply-templates select="."/>
      /* end of <xsl:value-of select="concat(concat(@name, '#'),@signature)"/>*/
	</xsl:for-each>
	<xsl:if test="@extends = 'android.app.Activity'">
	  <xsl:text>,
    $main___java_lang_String_ARRAYTYPE: function(args) {
      </xsl:text><xsl:call-template name="getPackgePlusClassName">
	    <xsl:with-param name="package" select="@package"/>
	    <xsl:with-param name="classname" select="@name"/>
	  </xsl:call-template><xsl:text>.launchActivity(null, null);
    },
    launchActivity: function(stageAssistant, sceneAssistant) {
        android_internal_MojoProxy.theStageAssistant = stageAssistant;
        android_internal_MojoProxy.theSceneAssistant = sceneAssistant;
        new </xsl:text>
      <xsl:call-template name="getPackgePlusClassName">
	    <xsl:with-param name="package" select="@package"/>
	    <xsl:with-param name="classname" select="'R$drawable'"/>
	  </xsl:call-template>
	  <xsl:text>();
	    </xsl:text>
      <xsl:call-template name="getPackgePlusClassName">
	    <xsl:with-param name="package" select="@package"/>
	    <xsl:with-param name="classname" select="@name"/>
	  </xsl:call-template>
        <xsl:text>.initClass();
        android_app_Activity.theActivityClassName = "</xsl:text>
      <xsl:call-template name="getPackgePlusClassName">
	    <xsl:with-param name="package" select="@package"/>
	    <xsl:with-param name="classname" select="@name"/>
	  </xsl:call-template>
      <xsl:text>";
        var app = new </xsl:text>
	  <xsl:call-template name="getPackgePlusClassName">
	    <xsl:with-param name="package" select="@package"/>
	    <xsl:with-param name="classname" select="@name"/>
	  </xsl:call-template>
	  <xsl:text>();
	    app.$$init_();
        app.$onContentChanged();
        app.$onCreate___android_os_Bundle([]);
    }
</xsl:text>
	</xsl:if>
<xsl:text>
  }, //statics

  members:
  {</xsl:text>
    <xsl:for-each select="vm:field[count(@isStatic)=0 or @isStatic='false']">
    <xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@name"/><xsl:with-param name="type" select="@type"/></xsl:call-template><xsl:text>: </xsl:text>
      <xsl:if test="(@type='java.lang.String') and (@value)">
          <xsl:text>"</xsl:text>
        </xsl:if>
      <xsl:value-of select="if (@value) then @value else 0"/>
      <xsl:if test="(@type='java.lang.String') and (@value)">
          <xsl:text>"</xsl:text>
        </xsl:if>
       <xsl:text>,</xsl:text>
	</xsl:for-each>
    <xsl:for-each select="vm:method[count(@isStatic)=0 or @isStatic='false']">
    <xsl:if test="position() != 1">
       <xsl:text>,</xsl:text>
     </xsl:if> /* start of <xsl:value-of select="concat(concat(@name, '#'),@signature)"/>*/
      <xsl:apply-templates select="."/>
      /* end of <xsl:value-of select="concat(concat(@name, '#'),@signature)"/>*/
	</xsl:for-each>
<xsl:text>
  } //members
}); //qx.Class.define
</xsl:text>
<!--  <xsl:apply-templates/> -->

</xsl:template>
<!-- END OF TEMPLATE: CLASS -->


<xsl:template match="vm:method">
  <xsl:choose>
    <xsl:when test="1=1 or not(vm:isDuplicateMethod(.))">
      <xsl:call-template name="emitPrototype"/>
      <xsl:apply-templates/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>duplicateMethod: 0</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="vm:method[@isAbstract = 'true']">
 	<xsl:call-template name="emitPrototype"/>
 	<xsl:text>{}</xsl:text>
</xsl:template>


<xsl:template match="vm:method[@isNative = 'true' or count(@nativeInterface) != 0]">
  <xsl:call-template name="emitPrototype"/>
  <xsl:text>
    {
       </xsl:text>
       <xsl:if test="vm:signature/vm:return/@type != 'void'">
         <xsl:text>return </xsl:text>
       </xsl:if>
       <xsl:text>NativeInterface</xsl:text>
       <xsl:value-of select="../@name"/>
       <xsl:text>.</xsl:text>
       <xsl:choose>
         <xsl:when test="count(@nativeInterface) != 0">
           <xsl:value-of select="@nativeInterface"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:value-of select="@name"/>
         </xsl:otherwise>
       </xsl:choose>
       <xsl:text>(</xsl:text>
       <xsl:if test="not(@isStatic = 'true')">
         <xsl:text>this</xsl:text>
         <xsl:if test="count(vm:signature/vm:parameter) != 0">
           <xsl:text>, </xsl:text>
         </xsl:if>
       </xsl:if>
       <xsl:for-each select="vm:signature/vm:parameter">
         <xsl:if test="position() != 1">
           <xsl:text>, </xsl:text>
         </xsl:if>
         <xsl:text> __arg</xsl:text>
         <xsl:value-of select="position()"/>
       </xsl:for-each>
       <xsl:text>);
    }
</xsl:text>
</xsl:template>



<xsl:template match="vm:signature">
  <!-- do nothing -->
</xsl:template>


<xsl:template match="vm:references">
  <!-- do nothing -->
</xsl:template>


<xsl:template match="jvm:code[count(../@nativeInterface) = 0]">
  <xsl:text>
    {
      var __locals = new Array(</xsl:text>
    <xsl:value-of select="@locals"/>
    <xsl:text>);
      var __stack = new Array(</xsl:text>
    <xsl:value-of select="@stack"/>
    <xsl:text>);
      var __sp = 0;
      var __op1;
      var __op2;</xsl:text>
  <xsl:call-template name="initLocals"/>
  <xsl:text>
        var __next_label = -1;
        while (1) {
            switch (__next_label) {
            case -1:</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>
            default:
                alert("XMLVM internal error: reached default of switch");
            }
        }</xsl:text>
  <xsl:text>
    }</xsl:text>        
</xsl:template>



<xsl:template match="jvm:var">
  <!-- do nothing -->
</xsl:template>


<!--
    =============================================================================
  All templates below create code for the various Java VM bytecode instructions
  =============================================================================
-->


<!-- label -->
<xsl:template match="jvm:label|dex:label">
    <xsl:text>
         case </xsl:text>
  <xsl:value-of select="@id"/>
  <xsl:text>:</xsl:text>
</xsl:template>



<!-- aconst_null -->
<xsl:template match="jvm:aconst_null">
    <xsl:text>
            __stack[__sp++] = new java_lang_null();</xsl:text>
</xsl:template>



<!-- athrow -->
<xsl:template match="jvm:athrow">
    <xsl:text>
            throw __stack[--__sp];</xsl:text>
</xsl:template>



<!-- bipush, sipush -->
<xsl:template match="jvm:bipush|jvm:sipush">
  <xsl:text>
            __stack[__sp++] = </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>



<!-- l2i, d2i, f2i -->
<xsl:template match="jvm:l2i|jvm:d2i|jvm:f2i">
<xsl:text>
    __value = __stack[--__sp];
    ___result = Math.floor(__value);
    __stack[__sp++] = ___result;</xsl:text>
</xsl:template>



<!-- i2l, i2d, i2f, d2f -->
<xsl:template match="jvm:i2l|jvm:d2f|jvm:i2d|jvm:i2f">
    <!-- do nothing -->
</xsl:template>



<!-- i2b -->
<xsl:template match="jvm:i2b">
<xsl:text>
    __value = __stack[--__sp];
    ___result = __value &amp; 0xff;
    ___result = (___result > 127) ? ___result | 0xffffff00 : ___result;
    __stack[__sp++] = ___result;</xsl:text>
</xsl:template>



<!-- i2c -->
<xsl:template match="jvm:i2c">
<xsl:text>
    __value = __stack[--__sp];
    ___result = __value &amp; 0xff;
    __stack[__sp++] = ___result;</xsl:text>
</xsl:template>



<!-- catch -->
<xsl:template match="jvm:catch">
    <xsl:text>
            /* try {*/</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>/*} catch (</xsl:text>
    <xsl:call-template name="emitType">
        <xsl:with-param name="type" select="@type"/>
    </xsl:call-template>
    <xsl:text> __ex) {
            __sp = 0;
            __stack[__sp++] = __ex;
            goto __label</xsl:text>
    <xsl:value-of select="@using"/>
    <xsl:text>;
            }*/</xsl:text>
</xsl:template>


<xsl:template match="jvm:nop">
<!-- delete nops -->
</xsl:template>

<!-- dup -->
<xsl:template match="jvm:dup">
  <xsl:text>
            __op1 = __stack[__sp - 1];
            __stack[__sp++] = __op1;</xsl:text>
</xsl:template>



<!-- dup_x1 -->
<xsl:template match="jvm:dup_x1">
    <xsl:text>
            __op1 = __stack[--__sp];
            __op2 = __stack[--__sp];
            __stack[__sp++] = __op1;
            __stack[__sp++] = __op2;
            __stack[__sp++] = __op1;</xsl:text>
</xsl:template>



<!-- field -->
<xsl:template match="vm:field">
<!-- Do nothing (we iterate these in class template -->
</xsl:template>


<!-- getfield -->
<xsl:template match="jvm:getfield">
    <xsl:text>
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op1.</xsl:text>
		<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@field"/><xsl:with-param name="type" select="@type"/></xsl:call-template>            
    <xsl:text>;</xsl:text>
</xsl:template>



<!-- getstatic -->
<xsl:template match="jvm:getstatic">

  <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>
            __stack[__sp++] = </xsl:text>
<!-- <xsl:value-of select="replace(replace(@class-type, 'java.lang.System', 'java.lang.SystemX'), 'java.lang.String', 'java.lang.String')" /> -->
  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@field"/><xsl:with-param name="type" select="@type"/></xsl:call-template>  
  <xsl:text>;</xsl:text>
</xsl:template>



<!-- goto, goto_w -->
<xsl:template match="jvm:goto|jvm:goto_w">
  <xsl:text>
            __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break;</xsl:text>
</xsl:template>  


<xsl:template match="jvm:lookupswitch">
  <xsl:text>    __op1 = __stack[--__sp];
    switch (__op1) {</xsl:text>
  <xsl:for-each select="jvm:case">
    <xsl:text>
        case </xsl:text>
    <xsl:value-of select="@key"/>
    <xsl:text>: __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break;</xsl:text>
  </xsl:for-each>
  <xsl:if test="jvm:default">
    <xsl:text>
        default: __next_label = </xsl:text>
    <xsl:value-of select="jvm:default/@label"/>
    <xsl:text>; break;</xsl:text>
  </xsl:if>
  <xsl:text>
    }
    break;
</xsl:text>
</xsl:template>



<xsl:template match="jvm:tableswitch">
  <xsl:text>    __op1 = __stack[--__sp];
    switch(__op1) {
    </xsl:text>
  <xsl:for-each select="jvm:case">
    <xsl:text>case </xsl:text>
    <xsl:value-of select="../@min + position() - 1"/>
    <xsl:text>: __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break;
    </xsl:text>
  </xsl:for-each>
  <xsl:text>default: __next_label = </xsl:text>
  <xsl:value-of select="jvm:default/@label"/>
  <xsl:text>; break;
    }
    break;
</xsl:text>
</xsl:template>



<!-- ladd, iadd, dadd, fadd -->
<xsl:template match="jvm:ladd|jvm:iadd|jvm:dadd|jvm:fadd">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op1 + __op2;</xsl:text>
</xsl:template>



<!-- lconst, iconst, dconst,fconst -->
<xsl:template match="jvm:lconst|jvm:iconst|jvm:dconst|jvm:fconst">
  <xsl:text>
            __stack[__sp++] = </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>





<!-- ifeq -->
<xsl:template match="jvm:ifeq">
  <xsl:text>
            __op1 = __stack[--__sp];
            if (__op1 == 0) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_acmpeq -->
<xsl:template match="jvm:if_acmpeq">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 === __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_icmpge -->
<xsl:template match="jvm:if_icmpge">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 >= __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_icmpgt -->
<xsl:template match="jvm:if_icmpgt">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 > __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_icmple -->
<xsl:template match="jvm:if_icmple">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 &lt;= __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>


<!-- if_icmpeq -->
<xsl:template match="jvm:if_icmpeq">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 == __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_icmplt -->
<xsl:template match="jvm:if_icmplt">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 &lt; __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- if_icmpne -->
<xsl:template match="jvm:if_icmpne">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 != __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>

<!-- if_acmpne -->
<xsl:template match="jvm:if_acmpne">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if (__op1 !== __op2) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- idiv -->
<xsl:template match="jvm:ldiv|jvm:idiv">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = Math.floor(__op1 / __op2);</xsl:text>
</xsl:template>


<!-- ldiv, fdiv, ddiv -->
<xsl:template match="jvm:fdiv|jvm:ddiv">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op1 / __op2;</xsl:text>
</xsl:template>


<!-- ifgt -->
<xsl:template match="jvm:ifgt">
    <xsl:text>
            if (__stack[--__sp] &gt; 0) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>

<!-- ifge -->
<xsl:template match="jvm:ifge">
    <xsl:text>
            if (__stack[--__sp] &gt;= 0) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>

<!-- ifle -->
<xsl:template match="jvm:ifle">
    <xsl:text>
            if (__stack[--__sp] &lt;= 0) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>

<!-- iflt -->
<xsl:template match="jvm:iflt">
    <xsl:text>
            if (__stack[--__sp] &lt; 0) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>

<!-- ifne -->
<xsl:template match="jvm:ifne">
    <xsl:text>
            if (__stack[--__sp] != 0) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>



<!-- ifnull -->
<xsl:template match="jvm:ifnull">
  <xsl:text>
            if ((__stack[--__sp] instanceof java_lang_null) || (__stack[__sp] == 0)) { __next_label = </xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>; break }</xsl:text>
</xsl:template>


<!-- ifnonnull -->
<xsl:template match="jvm:ifnonnull">
    <xsl:text>
            if (!(__stack[--__sp] instanceof java_lang_null)) { __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break }</xsl:text>
</xsl:template>


<!-- irem, lrem -->
<xsl:template match="jvm:irem|jvm:lrem">
  <xsl:text>
          __value2 = __stack[--__sp];
          __value1 = __stack[--__sp];
          __stack[__sp++] = __value1 - Math.floor(__value1 / __value2) * __value2;
   </xsl:text>
</xsl:template>

<!-- ineg  TODO: if most neg value, result must be most neg value again-->
<xsl:template match="jvm:ineg">
  <xsl:text>
          __value = __stack[--__sp];
          __stack[__sp++] = __value * (-1);
   </xsl:text>
</xsl:template>

<!-- fneg  TODO: if most neg value, result must be most neg value again-->
<xsl:template match="jvm:fneg">
  <xsl:text>
          __value = __stack[--__sp];
          __stack[__sp++] = __value * (-1.0);
   </xsl:text>
</xsl:template>



<!-- iinc -->
<xsl:template match="jvm:iinc">
  <xsl:text>
            __locals[</xsl:text>
  <xsl:value-of select="@index"/>
  <xsl:text>] += </xsl:text>
  <xsl:value-of select="@incr"/>
  <xsl:text>;</xsl:text>
</xsl:template>


<!-- iload, aload, fload, lload, dload -->
<xsl:template match="jvm:iload|jvm:aload|jvm:fload|jvm:lload|jvm:dload">
  <xsl:text>
            __stack[__sp++] = __locals[</xsl:text>
  <xsl:value-of select="@index"/>
  <xsl:text>];</xsl:text>
</xsl:template>



<!-- imul, lmul, dmul, fmul -->
<xsl:template match="jvm:imul|jvm:lmul|jvm:dmul|jvm:fmul">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op1 * __op2;</xsl:text>
</xsl:template>

<!-- TODO: *cmpl and *cmpg are different if result is NaN -->
<!-- dcmpl, dcmpg, fcmpl, fcmpg, lcmp -->
<xsl:template match="jvm:dcmpl|jvm:dcmpg|jvm:fcmpl|jvm:fcmpg|jvm:lcmp">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            if(__op1 &gt; __op2)
            	__stack[__sp++] = 1;
            else if(__op1 == __op2)
            	__stack[__sp++] = 0;
            else if(__op1 &lt; __op2)
            	__stack[__sp++] = -1;</xsl:text>
</xsl:template>


<!-- anewarray, newarray -->
<xsl:template match="jvm:anewarray|jvm:newarray">
  <xsl:text>
    __count = __stack[--__sp];
    __stack[__sp++] = new Array(__count);
  </xsl:text>
</xsl:template>

<!-- castore, iastore, aastore, bastore, dastore -->
<xsl:template match="jvm:castore|jvm:iastore|jvm:aastore|jvm:bastore|jvm:dastore">
  <xsl:text>
    __value    = __stack[--__sp];
    __index    = __stack[--__sp];
    __arrayref = __stack[--__sp];
    __arrayref[__index] = __value;
  </xsl:text>
</xsl:template>

<!-- caload, iaload, aaload, baload, daload -->
<xsl:template match="jvm:caload|jvm:iaload|jvm:aaload|jvm:baload|jvm:daload">
  <xsl:text>
    __index    = __stack[--__sp];
    __arrayref = __stack[--__sp];
    __stack[__sp++] = __arrayref[__index];
  </xsl:text>
</xsl:template>

<!-- ior -->
<xsl:template match="jvm:ior">
  <xsl:text>
    __value1    = __stack[--__sp];
    __value2    = __stack[--__sp];
    __stack[__sp++] = (__value1 || __value2);
  </xsl:text>
</xsl:template>

<!-- ixor -->
<xsl:template match="jvm:ixor">
  <xsl:text>
    __value1    = __stack[--__sp];
    __value2    = __stack[--__sp];
    __stack[__sp++] = (__value1 ^ __value2);
  </xsl:text>
</xsl:template>

<!-- iand -->
<xsl:template match="jvm:iand">
  <xsl:text>
    __value1    = __stack[--__sp];
    __value2    = __stack[--__sp];
    __stack[__sp++] = (__value1 &amp;&amp; __value2);
  </xsl:text>
</xsl:template>

<!-- instanceof -->
<xsl:template match="jvm:instanceof">
  <xsl:text>
    __objectref    = __stack[--__sp];
    __stack[__sp++] = (__objectref</xsl:text>
    <xsl:choose>
      <xsl:when test="contains(@type, '[]')">
        <xsl:text>.constructor.toString().indexOf("Array") != -1</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> instanceof </xsl:text>
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="@type"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>) ? 1 : 0;</xsl:text>
</xsl:template>

<!-- checkcast -->
<xsl:template match="jvm:checkcast">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string">java.lang.ClassCastException</xsl:with-param>
  </xsl:call-template>
  <xsl:text>
    __objectref    = __stack[__sp - 1];
    if (!(__objectref instanceof java_lang_null) &amp;&amp; !(__objectref</xsl:text>
    <xsl:choose>
      <xsl:when test="contains(@type, '[]')">
        <xsl:text>.constructor.toString().indexOf("Array") != -1</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> instanceof </xsl:text>
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="@type"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>)) {throw (new java_lang_ClassCastException).$$init_java_lang_ClassCastException___java_lang_String("ClassCastException");}</xsl:text>
</xsl:template>

<!-- arraylength -->
<xsl:template match="jvm:arraylength">
  <xsl:text>
    __arrayref    = __stack[--__sp];
    __stack[__sp++] = __arrayref.length;
  </xsl:text>
</xsl:template>

<!-- monitorenter -->
<!-- No thread support, just pop value off stack -->
<xsl:template match="jvm:monitorenter">
  <xsl:text>
    --__sp;
  </xsl:text>
</xsl:template>




<!-- invokestatic -->
<xsl:template match="jvm:invokestatic">
  <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  

  <xsl:text>
            __sp -= </xsl:text>
  <xsl:value-of select="count(vm:signature/vm:parameter)"/>
  <xsl:text>;
            </xsl:text>
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
    <xsl:text>__op1 = </xsl:text>
  </xsl:if>
  <xsl:call-template name="emitScopedName">
      <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="emitMethodName">
    <xsl:with-param name="class-type" select="@class-type"/>
    <xsl:with-param name="name" select="@method"/>
    <xsl:with-param name="signature" select="vm:signature" />
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:for-each select="vm:signature/vm:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__stack[__sp + </xsl:text>
    <xsl:value-of select="position() - 1"/>
    <xsl:text>]</xsl:text>
  </xsl:for-each>
  <xsl:text>);</xsl:text>
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
    <xsl:text>
            __stack[__sp++] = __op1;</xsl:text>
  </xsl:if>
</xsl:template>



<!-- invokevirtual|invokeinterface -->
<xsl:template match="jvm:invokevirtual|jvm:invokeinterface">
  <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  
  <xsl:text>
            __sp -= </xsl:text>
  <xsl:value-of select="count(vm:signature/vm:parameter) + 1"/>
  <xsl:text>;
            </xsl:text>
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
    <xsl:text>__op1 = </xsl:text>
  </xsl:if>
  <xsl:text>__stack[__sp].</xsl:text>
  <xsl:call-template name="emitMethodName">
    <xsl:with-param name="class-type" select="@class-type"/>
    <xsl:with-param name="name" select="@method"/>
    <xsl:with-param name="signature" select="vm:signature" />
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:for-each select="vm:signature/vm:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__stack[__sp + </xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:text>]</xsl:text>
  </xsl:for-each>
  <xsl:text>);</xsl:text>  
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
    <xsl:text>
            __stack[__sp++] = __op1;</xsl:text>
  </xsl:if>
</xsl:template>
  

<!-- invokespecial -->

<xsl:template match="jvm:invokespecial">
  <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  
  <xsl:text>
            __sp -= </xsl:text>
  <xsl:value-of select="count(vm:signature/vm:parameter) + 1"/>
  <xsl:text>;
            </xsl:text>
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
  </xsl:if>
  <xsl:text>
            errorCode = 0;
            if(__stack[__sp] == this) {
             try {
               __op1 = __stack[__sp].self(arguments).superclass.prototype.</xsl:text>
  <xsl:call-template name="emitMethodName">
    <!-- <xsl:with-param name="class-type" select="@class-type"/> -->
    <xsl:with-param name="name" select="@method"/>
    <!-- <xsl:with-param name="signature" select="vm:signature" /> -->
  </xsl:call-template>
  <xsl:text>.call(__stack[__sp]</xsl:text>
  
  <xsl:for-each select="vm:signature/vm:parameter">
    <!-- <xsl:if test="position() != 1"> -->
      <xsl:text>, </xsl:text>
    <!-- </xsl:if> -->
    <xsl:text>__stack[__sp + </xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:text>]</xsl:text>
  </xsl:for-each>
  <xsl:text>);
             } catch (e) {
             	errorCode = 1;
             }
            }
            if((errorCode != 0) || (__stack[__sp] != this)){
              __op1 = __stack[__sp].</xsl:text>
  <xsl:call-template name="emitMethodName">
    <!-- <xsl:with-param name="class-type" select="@class-type"/> -->
    <xsl:with-param name="name" select="@method"/>
    <!-- <xsl:with-param name="signature" select="vm:signature" /> -->
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  
  <xsl:for-each select="vm:signature/vm:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__stack[__sp + </xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:text>]</xsl:text>
  </xsl:for-each>
  <xsl:text>);
            }</xsl:text>
 
  <xsl:if test="vm:signature/vm:return/@type != 'void'">
    <xsl:text>
            __stack[__sp++] = __op1;</xsl:text>
  </xsl:if>
</xsl:template>
  


<!-- lreturn, freturn, dreturn, ireturn, areturn -->  
<xsl:template match="jvm:freturn|jvm:lreturn|jvm:dreturn|jvm:ireturn|jvm:areturn">
  <xsl:text>
            return __stack[--__sp];</xsl:text>
</xsl:template>

<xsl:template match="jvm:dstore|jvm:istore|jvm:astore|jvm:fstore|jvm:lstore">
  <xsl:text>
            __locals[</xsl:text>
  <xsl:value-of select="@index"/>
  <xsl:text>] = __stack[--__sp];</xsl:text>
</xsl:template>


<!--  TODO: Overflows -->
<!-- lsub, isub, fsub, dsub -->
<xsl:template match="jvm:lsub|jvm:isub|jvm:fsub|jvm:dsub">
  <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op1 - __op2;</xsl:text>
</xsl:template>


<xsl:template match = "iconst">
 <xsl:text>
        __stack[__sp++] = </xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>;</xsl:text>
</xsl:template>

<!-- ldc, ldc_w, ldc2_w -->
<xsl:template match="jvm:ldc|jvm:ldc_w|jvm:ldc2_w">
  <xsl:choose>
    <xsl:when test="@type = 'java.lang.String'">
      <xsl:call-template name="checkClass">
        <xsl:with-param name="string">java.lang.String</xsl:with-param>
      </xsl:call-template>
      <xsl:text>
            __stack[__sp++] = new java_lang_String("</xsl:text>
      <xsl:variable name="str" select="replace(@value, '&quot;', '\\&#x22;')"/>
      <xsl:value-of select="replace($str, '&#xA;', '\\n')"/>
      <xsl:text>");
            </xsl:text>
    </xsl:when>
    <xsl:when test="@type = 'double'">
        <xsl:text>
        __stack[__sp++] = </xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>;</xsl:text>
    </xsl:when>
    <xsl:when test="@type = 'long'">
        <xsl:text>
        __stack[__sp++] = </xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>;</xsl:text>
    </xsl:when>
    <xsl:when test="@type = 'int'">
        <xsl:text>
        __stack[__sp++] = </xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>;</xsl:text>
    </xsl:when>
    <xsl:when test="@type = 'float'">
        <xsl:text>
        __stack[__sp++] = </xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>
        __stack[__sp++] = new java_lang_Class("</xsl:text>
      <xsl:call-template name="emitScopedName">
        <xsl:with-param name="string" select="@type"/>
      </xsl:call-template>
      <xsl:text>");</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!-- new -->
<xsl:template match="jvm:new">
  <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@type"/>
  </xsl:call-template>

    <xsl:choose>
        <xsl:when test="count(vm:signature) = 0">
          <xsl:text>
            __stack[__sp++] = new </xsl:text>
         <xsl:call-template name="emitScopedName">
            <xsl:with-param name="string" select="@type"/>
          </xsl:call-template>
          <xsl:text>();</xsl:text>
        </xsl:when>
        <xsl:otherwise>
            <xsl:text>ERROR();// new with signature not handled yet</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>



<!-- pop -->
<xsl:template match="jvm:pop">
    <xsl:text>
            __sp--;</xsl:text>
</xsl:template>



<!-- putfield -->
<xsl:template match="jvm:putfield">
    <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __op1.</xsl:text>
		<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@field"/><xsl:with-param name="type" select="@type"/></xsl:call-template>
    <xsl:text> = __op2;</xsl:text>
</xsl:template>



<!-- putstatic -->
<xsl:template match="jvm:putstatic">
    <!-- Added for Dynamic Loading -->
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  
  <xsl:text>
            </xsl:text>
  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@field"/><xsl:with-param name="type" select="@type"/></xsl:call-template>
  <xsl:text> = __stack[--__sp];</xsl:text>
</xsl:template>



<!-- return -->
<xsl:template match="jvm:return">
  <xsl:text>
            return this;</xsl:text>
</xsl:template>



<!-- swap -->
<xsl:template match="jvm:swap">
    <xsl:text>
            __op2 = __stack[--__sp];
            __op1 = __stack[--__sp];
            __stack[__sp++] = __op2;
            __stack[__sp++] = __op1;</xsl:text>
</xsl:template>



<!-- ============================================================================
   All templates below are essentially helper functions that are called by
   various other templates.
   ============================================================================
-->

<!-- Added for Dynamic Loading -->
<xsl:template name="checkClass">
  <xsl:param name="string" />
<!--   <xsl:variable name="typeName"> -->
<!--     <xsl:call-template name="emitScopedName"><xsl:with-param name="string" select="replace($string, '\[\]', '')"/></xsl:call-template>   -->
<!--   </xsl:variable> -->
<!--   <xsl:if test="vm:isObjectRef($typeName)"> -->
<!--     <xsl:text> -->
<!--             if (</xsl:text><xsl:copy-of select="$typeName" /><xsl:text>.$$clinit_ != undefined) {</xsl:text> -->
<!--     <xsl:copy-of select="$typeName" /><xsl:text>.$$clinit_();</xsl:text> -->
<!--     <xsl:copy-of select="$typeName" /><xsl:text>.$$clinit_ = undefined; }</xsl:text> -->
<!--   </xsl:if> -->
</xsl:template>


<!--
   emitPrototype
   =============
   Writes the prototype of a method. Called from within context of tag
   <method>.
-->  
<xsl:template name="emitPrototype">
<xsl:text>
</xsl:text>

<xsl:text>    </xsl:text>
      <xsl:call-template name="emitMethodName">
	    <xsl:with-param name="name" select="@name"/>
    	<xsl:with-param name="signature" select="vm:signature" />
  	    <!-- <xsl:with-param name="class-type" select="../@name"/> -->
	  </xsl:call-template>
     <!--  <xsl:call-template name="emitScopedName">
      	<xsl:with-param name="string" select="../@package"/>
      </xsl:call-template><xsl:text>_</xsl:text>
      <xsl:value-of select="../@name" /> -->
	  <xsl:text> : </xsl:text>


	<xsl:text>function(</xsl:text>
	<xsl:for-each select="vm:signature/vm:parameter">
    	<xsl:if test="position() != 1">
      		<xsl:text>, </xsl:text>
	    </xsl:if>
    	<xsl:text> __arg</xsl:text>
    	<xsl:value-of select="position()"/>
  	</xsl:for-each>
	<xsl:text>)</xsl:text>
</xsl:template>

<!--
   initLocals
   ==========
   This function is called from the template for <jvm:code>. Its task is
   to initialize the local variables. This basically means that the
   actual parameters have to be copied to __locals[i]. If the method
   is not static, 'this' will be copied to __locals[0]. This function
   basically only emits code for 'this'. The remaining parameters
   are copied in the template 'initRemainingLocals' below.
-->

<xsl:template name="initLocals">
    <xsl:for-each select="jvm:var">
		<xsl:choose>    
      		<xsl:when test="@name = 'this'">
      			<xsl:text>
        __locals[</xsl:text>
    			<xsl:value-of select="@id" />
    			<xsl:text>] = this;</xsl:text>
     		</xsl:when>
     		<xsl:otherwise>
     			<xsl:if test="(position()-count(../jvm:var[@name='this'])) &lt;= count(../../vm:signature/vm:parameter)" >
     			  <xsl:text>
     	__locals[</xsl:text>
     	  		  <xsl:value-of select="@id" />
     	  		  <xsl:text>] = __arg</xsl:text>
     	  		  <xsl:value-of select="(position()-count(../jvm:var[@name='this']))" />
     	  		  <xsl:text>;</xsl:text>
     			</xsl:if>
     		</xsl:otherwise>
     	</xsl:choose>
    </xsl:for-each>
</xsl:template>

<xsl:template name="initArguments">
  <xsl:variable name="numRegs" select="@register-size" as="xs:integer"/>
  <xsl:variable name="numArgs" select="count(../vm:signature/vm:parameter)" as="xs:integer"/>
  <xsl:for-each select="1 to $numRegs">
    <xsl:text>    var __r</xsl:text>
    <xsl:value-of select="position() - 1"/>
    <xsl:text>;
</xsl:text>
  </xsl:for-each>
  <xsl:if test="not(../@isStatic = 'true')">
    <!-- Initialize 'this' parameter -->
    <xsl:text>    __r</xsl:text>
    <xsl:value-of select="$numRegs - ($numArgs + 1)"/>
    <xsl:text> = this;
</xsl:text>
    </xsl:if>
    <xsl:for-each select="../vm:signature/vm:parameter">
      <xsl:text>    __r</xsl:text>
      <xsl:value-of select="$numRegs - ($numArgs - position()) - 1"/>
      <xsl:text> = __arg</xsl:text>
      <xsl:value-of select="position()"/>
      <xsl:text>;
</xsl:text>
    </xsl:for-each>
</xsl:template>



<!--
   emitMethodName
   ==============
   Called whenever a method name has to be written. If the method happens
   to be a constructor, this function will generate $$init_() instead.
   Input: 'name': the name of the method to write.
-->
<xsl:template name="emitMethodName">
  <xsl:param name="name"/>
  <xsl:param name="signature" />
  <xsl:param name="class-type" />

  <xsl:choose>
    <xsl:when test="$name = '&lt;init&gt;'">
      <xsl:if test="$class-type = 'java.lang.String'">
        <xsl:text>dragomeJs.StringInit</xsl:text>
      </xsl:if>
      <xsl:text>$$init_</xsl:text>
      <xsl:if test="$class-type != 'java.lang.String'">
       <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="$class-type"/>
      </xsl:call-template>
      </xsl:if>
      <!-- Append signature to constructor name -->
      <xsl:call-template name="appendSignature">
        <xsl:with-param name="signature" select="vm:signature" />
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$name = '&lt;clinit&gt;'">
      <xsl:text>$$clinit_</xsl:text>
      <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="$class-type"/>
      </xsl:call-template>
    </xsl:when>
    
    <xsl:when test="$name = '.cctor'">
      <xsl:text>$$cctor</xsl:text>
      <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="$class-type"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>$</xsl:text><xsl:value-of select="$name"/>
      <!-- Append signature to method name -->
      <xsl:call-template name="appendSignature">
        <xsl:with-param name="signature" select="vm:signature" />
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   emitFieldName
   ==============
-->

<xsl:template name="emitFieldName">
  <xsl:param name="name" />
  <xsl:param name="type" />
  <xsl:text>$$$</xsl:text><xsl:value-of select="$name" />___<xsl:call-template name="emitScopedName"><xsl:with-param name="string" select="replace($type, '\[\]', '_ARRAYTYPE')"/></xsl:call-template>  
</xsl:template>

<!--
   emitScopedName
   ==============
   Writes a scoped name. This function basically translates a Java-style scoped
   name (e.g., java.lang.String) to JavaScript. We simply use name mangling
     for this (e.g., java_lang_String).
   Input: 'string': Java-style scoped name.
-->

<xsl:template name="emitScopedName">
  <xsl:param name="string" />
  <xsl:value-of select="translate($string, '.', '_')" />
</xsl:template>

<!--
   emitType
   ========
   Emits a type reference. Basic Java types are mapped to certain C++ types
   (e.g., int is mapped to XMLVM::INT). Object references are mapped to
   C++ types of the same name with the suffix '__ref'.
   Input: 'type': Java type
-->
<xsl:template name="emitType">
  <xsl:param name="type"/>
  <xsl:choose>
    <xsl:when test="$type = 'void'">
      <xsl:text>void</xsl:text>
    </xsl:when>
    <xsl:when test="$type = 'int'">
      <xsl:text>XMLVM::INT</xsl:text>
    </xsl:when>
    <xsl:otherwise>
          <xsl:call-template name="emitScopedName">
            <xsl:with-param name="string" select="$type"/>
          </xsl:call-template>
          <xsl:text>__ref</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- Replace the substring "replace" by "with" in "text" -->
<xsl:template name="replaceString">
  <xsl:param name="text"/>
  <xsl:param name="replace"/>
  <xsl:param name="with"/>
  <xsl:choose>
    <xsl:when test="contains($text,$replace)">
      <xsl:value-of select="substring-before($text,$replace)"/>
      <xsl:value-of select="$with"/>
      <xsl:call-template name="replaceString">
        <xsl:with-param name="text" select="substring-after($text,$replace)"/>
        <xsl:with-param name="replace" select="$replace"/>
        <xsl:with-param name="with" select="$with"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="appendSignature">
  <xsl:param name="signature" />
  <xsl:choose>
    <xsl:when test="count(vm:signature/vm:parameter) != 0">
      <xsl:text>__</xsl:text>
      <xsl:for-each select="vm:signature/vm:parameter">
        <xsl:text>_</xsl:text>
         <xsl:if test="position() != 1">
            <xsl:text>_</xsl:text>
        </xsl:if>
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string">
            <xsl:call-template name="replaceString">
              <xsl:with-param name="text" select="@type" />
              <xsl:with-param name="replace">[]</xsl:with-param>
              <xsl:with-param name="with">_ARRAYTYPE</xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:when>
  </xsl:choose>
      <xsl:for-each select="vm:signature/vm:return">
        <xsl:text>$</xsl:text>
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string">
            <xsl:call-template name="replaceString">
              <xsl:with-param name="text" select="@type" />
              <xsl:with-param name="replace">[]</xsl:with-param>
              <xsl:with-param name="with">_ARRAYTYPE</xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
</xsl:template>


<xsl:template name="getPackgePlusClassName">
  <xsl:param name="package" />
  <xsl:param name="classname" />

  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="$package"/>
  </xsl:call-template>

  <xsl:choose>
    <xsl:when test="string-length($package) > 0">
      <xsl:text>_</xsl:text>
    </xsl:when>
  </xsl:choose>

<xsl:value-of select="$classname"/>
</xsl:template>


<!--  javac will sometimes generate two methods that only differ in their return type.
      This can happen e.g. with type erasures. Function vm:isDuplicateMethod will determine
      if the given method is a duplicate that is not needed when generating JavaScript.
      A method is a duplicate if it is (1) synthetic, (2) a method with the same name exists
      in the class, and (3) signatures only differ in their return types.  -->
<xsl:function name="vm:isDuplicateMethod" as="xs:boolean">
  <xsl:param name="method" as="node()"/>

  <xsl:choose>
    <xsl:when test="not($method/@isSynthetic = 'true')">
      <xsl:value-of select="false()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="name" select="$method/@name"/>
      <xsl:variable name="methodsWithSameName" select="$method/../vm:method[@name = $name]"/>
      <xsl:variable name="duplicateMethods">
        <xsl:for-each select="$methodsWithSameName">
          <xsl:if test="deep-equal($method/vm:signature/vm:parameter, ./vm:signature/vm:parameter)">
            <xsl:copy-of select="."/>
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="count($duplicateMethods/vm:method) gt 1"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:function>



<!--
 _____    _____  __    __ 
|  _  \  | ____| \ \  / / 
| | |  | | |__    \ \/ /  
| | |  | |  __|    }  {   
| |_|  | | |___   / /\ \  
|_____/  |_____| /_/  \_\ 
-->

<xsl:template match="dex:code">
  <xsl:text>
    {</xsl:text>
  <xsl:if test="../@name = '&lt;clinit&gt;'">
    <xsl:call-template name="getPackgePlusClassName">
      <xsl:with-param name="package" select="../../@package"/>
      <xsl:with-param name="classname" select="../../@name"/>
    </xsl:call-template>
    <xsl:text>.$$clinit_ = function(){return this};
    </xsl:text>
  </xsl:if>
    
  <!--  Declare the registers - START. -->
  <xsl:for-each select="vm:define-register">
   <xsl:if test="position() = 1">
      <xsl:text>
       var </xsl:text>
   </xsl:if>
   <xsl:if test="position() != 1">
     <xsl:text>, </xsl:text>
   </xsl:if>
    <xsl:choose>
      <xsl:when test = "@vartype = 'register'">
        <xsl:text>__r</xsl:text><xsl:value-of select="@num"/>
      </xsl:when>
      <xsl:when test = "@vartype = 'temp'">
        <xsl:text>__rtmp</xsl:text>
      </xsl:when>
      <xsl:when test = "@vartype = 'exception'">
        <xsl:text>__ex</xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="position() = last()">
    <xsl:text>;</xsl:text>
    </xsl:if>
  </xsl:for-each>
  <!--  Declare the registers - END. -->

  <xsl:for-each select="vm:move-argument">
    <xsl:text>
        __r</xsl:text>
    <xsl:value-of select="@vx"/>
    <xsl:if test="@sourceArg = 'self'">
      <xsl:text> = this;</xsl:text>
    </xsl:if>
    <xsl:if test="@sourceArg != 'self'">
      <xsl:text> = __arg</xsl:text>
      <xsl:value-of select="@sourceArg"/>
      <xsl:text>;</xsl:text>
    </xsl:if>
  </xsl:for-each>
  
  <!--
  <xsl:call-template name="initArguments"/>
  -->
  
  <xsl:text>
        var __next_label = -1;
        while (1) {
          switch (__next_label) {
            case -1:</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>
            default:
              alert("XMLVM internal error: reached default of switch");
          }
        }</xsl:text>
  <xsl:text>
    }</xsl:text>
</xsl:template>

<xsl:template match="vm:define-register">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:move-argument">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:tmp-equals-r">
  <xsl:text>
            __rtmp =  __r</xsl:text>
  <xsl:value-of select="@reg" />
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="vm:comment">
  <xsl:text>    //INFO: </xsl:text>
    <xsl:value-of select="@text" />
  <xsl:text>
  </xsl:text>
</xsl:template>

<xsl:template match="vm:reg-release">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:reg-retain">
  <!-- Do nothing -->
</xsl:template>
  
<xsl:template match="vm:i-release">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:s-release">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:a-release">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="vm:set-null">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="dex:monitor-enter">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="dex:monitor-exit">
  <!-- Do nothing -->
</xsl:template>

<xsl:template match="dex:const-class"> 
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@value"/>
  </xsl:call-template>
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="'java_lang_Class'"/>
  </xsl:call-template>
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = java_lang_Class.$forName___java_lang_String$java_lang_Class("</xsl:text>
  <xsl:value-of select="@value"/>
  <xs:text>");</xs:text>
</xsl:template>


<!--  dex:return-void
      ===============  -->
<xsl:template match="dex:return-void">
  <xsl:text>
            return this;</xsl:text>
</xsl:template>

<!--  dex:return-object
      =================  -->
<xsl:template match="dex:return|dex:return-object|dex:return-wide">
  <xsl:text>
            return __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:var
      =======  -->
<xsl:template match="dex:var">
  <!-- do nothing -->
</xsl:template>

<!--  vm:source-position
      =======  -->
<xsl:template match="vm:source-position">
  <!-- TODO -->
</xsl:template>


<!--  dex:const-string
      ================  -->
<xsl:template match="dex:const-string">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = "</xsl:text>
  <xsl:value-of select="replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(@value,'\\','\\\\'),
                           '\\\\011','\\t'),'\\\\012','\\n'),'\\\\015','\\r'),'\\\\014','\\f'),'\\\\010','\\b'),
                           '&quot;','\\&quot;'), '\\\\173', '{'), '\\\\175', '}'), '\\\\134', '\\\\'), '\\\\042', '\\&quot;')"/>
  <xsl:text>";</xsl:text>
</xsl:template>


<!--  dex:const*
      ================  -->
<xsl:template match="dex:const|dex:const-4|dex:const-16|dex:const-wide|dex:const-wide-16|dex:const-wide-32|dex:const-high16|dex:const-wide-high16">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = </xsl:text>
  <xsl:value-of select="@value" />
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:add-int-lit*
      ================  -->
<xsl:template match="dex:add-int-lit8|dex:add-int-lit16">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> + </xsl:text>
  <xsl:value-of select="@value" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:add-int-*
      ================  -->
<xsl:template match="dex:add-int|dex:add-int-2addr|dex:add-double|dex:add-double-2addr|dex:add-float|dex:add-float-2addr|dex:add-long|dex:add-long-2addr">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> + __r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:mul-*
      ================  -->
<xsl:template match="dex:mul-int|dex:mul-int-2addr|dex:mul-double|dex:mul-double-2addr|dex:mul-float|dex:mul-float-2addr|dex:mul-long|dex:mul-long-2addr">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> * __r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:mul-int-lit*
      ================  -->
<xsl:template match="dex:mul-int-lit8|dex:mul-int-lit16">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> * </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:div-int*
      ================  -->
<xsl:template match="dex:div-int|dex:div-int-2addr|dex:div-long|dex:div-long-2addr">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = Math.floor(__r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> / __r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:div-int-lit*
      ================  -->
<xsl:template match="dex:div-int-lit8|dex:div-int-lit16">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = Math.floor(__r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> / </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:div-*
      ================  -->
<xsl:template match="dex:div-double|dex:div-double-2addr|dex:div-float|dex:div-float-2addr">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> / __r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:subint-*
      ================  -->
<xsl:template match="dex:sub-int|dex:sub-int-2addr|dex:sub-double|dex:sub-double-2addr|dex:sub-float|dex:sub-float-2addr|dex:sub-long|dex:sub-long-2addr">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> - __r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:if-lt
      ================  -->
<xsl:template match="dex:if-lt">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &lt; __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-le
      ================  -->
<xsl:template match="dex:if-le">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &lt;= __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-gt
      ================  -->
<xsl:template match="dex:if-gt">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &gt; __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-ne
      ================  -->
<xsl:template match="dex:if-ne">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> != __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-eq
      ================  -->
<xsl:template match="dex:if-eq">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> == __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-ge
      ================  -->
<xsl:template match="dex:if-ge">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &gt;= __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-nez
      ==========  -->
<xsl:template match="dex:if-nez">
  <xsl:text>
            if (!_isNull(__r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text>))</xsl:text>
  <xsl:text> { __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-eqz
      ==========  -->
<xsl:template match="dex:if-eqz">
  <xsl:text>
            if (_isNull(__r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text>)) { __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-gez
      ==========  -->
<xsl:template match="dex:if-gez">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &gt;= 0 ){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-gtz
      ==========  -->
<xsl:template match="dex:if-gtz">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &gt; 0 ){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-lez
      ==========  -->
<xsl:template match="dex:if-lez">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &lt;= 0 ){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:if-ltz
      ==========  -->
<xsl:template match="dex:if-ltz">
  <xsl:text>
            if (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> &lt; 0 ){ __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break; }</xsl:text>
</xsl:template>

<!--  dex:goto
      ========  -->
<xsl:template match="dex:goto">
  <xsl:text>
            __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break;</xsl:text>
</xsl:template>

<!--  dex:goto-16
      ========  -->
<xsl:template match="dex:goto-16">
  <xsl:text>
            __next_label = </xsl:text>
  <xsl:value-of select="@target" />
  <xsl:text>; break;</xsl:text>
</xsl:template>


<!--  dex:invoke-virtual*, dex:invoke-virtual*
      ========================================  -->
<xsl:template match="dex:invoke-virtual|dex:invoke-virtual-range|dex:invoke-interface|dex:invoke-interface-range">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>
            </xsl:text>
  <xsl:if test="dex:move-result/@vx">
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="dex:move-result/@vx" />
    <xsl:text> = </xsl:text>
  </xsl:if>

  <xsl:text>__r</xsl:text>
  <xsl:value-of select="@register" />
  <xsl:text>.</xsl:text>
  <xsl:call-template name="emitMethodName">
    <xsl:with-param name="name" select="@method"/>
  </xsl:call-template>
  <xsl:call-template name="appendSignatureDex">
    <xsl:with-param name="signature" select="dex:parameters"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
    <xsl:for-each select="dex:parameters/dex:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register"/>
  </xsl:for-each>
  <xsl:text>);</xsl:text>
</xsl:template>

<!--  dex:invoke-direct*
      ==================
      This is used to call super-constructors. Hence the special handling. -->
<xsl:template match="dex:invoke-direct|dex:invoke-direct-range">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>
            </xsl:text>
  <xsl:if test="dex:move-result/@vx">
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="dex:move-result/@vx" />
    <xsl:text> = </xsl:text>
  </xsl:if>
    <xsl:choose>
    
   <xsl:when test="(../dex:var[@register=current()/@register]/@name='this') and (../../@name='&lt;init&gt;') and (concat(concat(../../../@package, '.'), ../../../@name) != current()/@class-type) ">
   <xsl:text>
           arguments.callee.self.superclass.prototype</xsl:text>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="emitMethodName">
    <xsl:with-param name="name" select="@method"/>
  </xsl:call-template>
  <xsl:call-template name="appendSignatureDex">
    <xsl:with-param name="signature" select="dex:parameters"/>
  </xsl:call-template>
  
    <xsl:text>.call(this</xsl:text>
    <xsl:for-each select="dex:parameters/dex:parameter">
    <xsl:text>, </xsl:text>
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register"/>
    </xsl:for-each>
  <xsl:text>);</xsl:text>
   </xsl:when>
  <xsl:otherwise>
  <xsl:text>__r</xsl:text>
  <xsl:value-of select="@register" />
    <xsl:choose>
      <xsl:when test="(@class-type = 'java.lang.String') and (@method = '&lt;init&gt;')">
        <xsl:text>= </xsl:text>
        <xsl:call-template name="emitMethodName">
          <xsl:with-param name="name" select="@method"/>
          <xsl:with-param name="class-type" select="@class-type"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>.</xsl:text>
        <xsl:call-template name="emitMethodName">
          <xsl:with-param name="name" select="@method"/>
        </xsl:call-template>
    </xsl:otherwise>
    </xsl:choose>
  <xsl:call-template name="appendSignatureDex">
    <xsl:with-param name="signature" select="dex:parameters"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
    <xsl:for-each select="dex:parameters/dex:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register"/>
  </xsl:for-each>
  <xsl:text>);</xsl:text>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!--  dex:invoke-super*
      ================= -->
<xsl:template match="dex:invoke-super|dex:invoke-super-range">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>
            </xsl:text>
  <xsl:if test="dex:move-result/@vx">
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="dex:move-result/@vx" />
    <xsl:text> = </xsl:text>
  </xsl:if>
  <xsl:if test="../../@isStatic = 'true'">
    <xsl:text>this.superclass.prototype.</xsl:text>
    <xsl:call-template name="emitMethodName">
      <xsl:with-param name="name" select="@method"/>
    </xsl:call-template>
    <xsl:call-template name="appendSignatureDex">
      <xsl:with-param name="signature" select="dex:parameters"/>
    </xsl:call-template>
    <xsl:text>.call(arguments[0]</xsl:text>
  </xsl:if>
  <xsl:if test="not(../../@isStatic = 'true')">
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register" />
    <xsl:text>.self(arguments).superclass.prototype.</xsl:text>
    <xsl:call-template name="emitMethodName">
      <xsl:with-param name="name" select="@method"/>
    </xsl:call-template>
    <xsl:call-template name="appendSignatureDex">
      <xsl:with-param name="signature" select="dex:parameters"/>
    </xsl:call-template>
    <xsl:text>.call(__r</xsl:text>
    <xsl:value-of select="@register" />
 </xsl:if>  
  
  <xsl:for-each select="dex:parameters/dex:parameter">
    <xsl:text>, </xsl:text>
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register"/>
  </xsl:for-each>

  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:invoke-static*
      ==================  -->
<xsl:template match="dex:invoke-static|dex:invoke-static-range">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>

  <xsl:text>
            </xsl:text>
  <xsl:if test="dex:move-result/@vx">
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="dex:move-result/@vx" />
    <xsl:text> = </xsl:text>
  </xsl:if>
  <xsl:choose>
  
  <xsl:when test="ends-with(@class-type, '.javascript.ScriptHelper')">
  <xsl:if test="contains(@method, 'put')">
    <xsl:text>eval("var "+__r</xsl:text><xsl:value-of select="dex:parameters/dex:parameter[position()=1]/@register" /><xsl:text> + "= __r</xsl:text><xsl:value-of select="dex:parameters/dex:parameter[position()=2]/@register" /><xsl:text>");</xsl:text>
  </xsl:if>
  <xsl:if test="starts-with(@method, 'eval')">
    <xsl:text>eval(__r</xsl:text><xsl:value-of select="dex:parameters/dex:parameter[position()=1]/@register" /><xsl:text>);</xsl:text>
  </xsl:if>
  </xsl:when>
    <xsl:otherwise><xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="emitMethodName">
    <xsl:with-param name="name" select="@method"/>
    <xsl:with-param name="class-type" select="@class-type" />
  </xsl:call-template>
  <xsl:call-template name="appendSignatureDex">
    <xsl:with-param name="signature" select="dex:parameters"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
    <xsl:for-each select="dex:parameters/dex:parameter">
    <xsl:if test="position() != 1">
      <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:text>__r</xsl:text>
    <xsl:value-of select="@register"/>
  </xsl:for-each>

  <xsl:text>);</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!--  dex:new-instance
      ================  -->
<xsl:template match="dex:new-instance">
  <xsl:choose>
<xsl:when test="@value  != 'java.lang.String'">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@value"/>
  </xsl:call-template>
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = new </xsl:text>
  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@value"/>
  </xsl:call-template>
  <xsl:text>();</xsl:text>
</xsl:when>
</xsl:choose>
</xsl:template>


<!--  dex:sget-*
      ==========  -->
<xsl:template match="dex:sget|dex:sget-wide|dex:sget-boolean|dex:sget-object|dex:sget-char|dex:sget-short|dex:sget-byte">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
 
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = </xsl:text>
  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.$$clinit_().</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@member-name"/><xsl:with-param name="type" select="@member-type"/></xsl:call-template>  
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:sput-*
      ==========  -->
<xsl:template match="dex:sput|dex:sput-wide|dex:sput-boolean|dex:sput-object|dex:sput-char">
  <xsl:call-template name="checkClass">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>
            </xsl:text>
  <xsl:call-template name="emitScopedName">
    <xsl:with-param name="string" select="@class-type"/>
  </xsl:call-template>
  <xsl:text>.$$clinit_().</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@member-name"/><xsl:with-param name="type" select="@member-type"/></xsl:call-template>  
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:iget*
      ===============  -->
<xsl:template match="dex:iget|dex:iget-wide|dex:iget-object|dex:iget-boolean|dex:iget-byte|dex:iget-char|dex:iget-short">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>.</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@member-name"/><xsl:with-param name="type" select="@member-type"/></xsl:call-template>  
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:iput*
      ================  -->
<xsl:template match="dex:iput|dex:iput-wide|dex:iput-object|dex:iput-boolean|dex:iput-byte|dex:iput-char|dex:iput-short">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>.</xsl:text>
	<xsl:call-template name="emitFieldName"><xsl:with-param name="name" select="@member-name"/><xsl:with-param name="type" select="@member-type"/></xsl:call-template>  
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:new-array
      ==============  -->
<xsl:template match="dex:new-array">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = new Array(__r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>);</xsl:text>
</xsl:template>

<!--  dex:fill-array-data
      ==============  -->
<xsl:template match="dex:fill-array-data">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = [</xsl:text>
  <xsl:for-each select="dex:constant">
    <xsl:value-of select="@value"/>
    <xsl:if test="position()!=last()">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>];</xsl:text>
</xsl:template>

<!--  dex:aget*
      ========  -->
<xsl:template match="dex:aget|dex:aget-wide|dex:aget-boolean|dex:aget-byte|dex:aget-char|dex:aget-object|dex:aget-short|dex:aget-byte">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>[__r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>];</xsl:text>
</xsl:template>

<!--  dex:aput*
      =========  -->
<xsl:template match="dex:aput|dex:aput-wide|dex:aput-boolean|dex:aput-char|dex:aput-object|dex:aput-short|dex:aput-byte">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>[__r</xsl:text>
  <xsl:value-of select="@vz" />
  <xsl:text>] = __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:array-length
      ================  -->
<xsl:template match="dex:array-length">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>.length;</xsl:text>
</xsl:template>

<!--  dex:int-to-byte
      ===============  -->
<xsl:template match="dex:int-to-byte">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> &amp; 0xff;</xsl:text>
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> > 127) ? __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> | 0xffffff00 : __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:int-to-short
      ================  -->
<xsl:template match="dex:int-to-short">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> &amp; 0xffff;</xsl:text>
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = (__r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> > 0x7fff) ? __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> | 0xffff0000 : __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:int-to-char
      ===============  -->
<xsl:template match="dex:int-to-char">
  <xsl:text>    
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &amp; 0xffff;</xsl:text>
</xsl:template>

<!--  dex:*-to-int
      ============  -->
<xsl:template match="dex:long-to-int|dex:double-to-int|dex:float-to-int">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = Math.floor(__r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:float-to-long
      ================  -->
<xsl:template match="dex:float-to-long">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = Math.floor(__r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:double-to-long
      ================  -->
<xsl:template match="dex:double-to-long">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = Math.floor(__r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>);</xsl:text>
</xsl:template>


<!--  dex:double-to-float
      TODO: probably not correct
      ================  -->
<xsl:template match="dex:double-to-float">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> * 1.0;</xsl:text>
</xsl:template>


<!--  dex:int-to-long
      ================  -->
<xsl:template match="dex:int-to-long">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:int-to-float|dex:int-to-double|dex:long-to-double|dex:long-to-float|dex:float-to-double
      (Upcasting should not be an issue)
      ================  -->
<xsl:template match="dex:int-to-float|dex:int-to-double|dex:long-to-double|dex:long-to-float|dex:float-to-double">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx" />
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy" />
  <xsl:text> * 1.0;</xsl:text>
</xsl:template>


<!--  dex:check-cast
      ==============  -->
<xsl:template match="dex:check-cast">
  <!-- TODO should do a runtime type check -->
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:instnce-of
      ==============  -->
<xsl:template match="dex:instance-of">
  <xsl:text>
            __r</xsl:text>
  <xsl:value-of select="@vx"/>
   <xsl:text> = (</xsl:text>
  <xsl:choose>
    <xsl:when test="contains(@value, '[]')">
      <xsl:text>__r</xsl:text>
      <xsl:value-of select="@vy"/>
      <xsl:text>.constructor.toString().indexOf("Array") != -1</xsl:text>
    </xsl:when>
    <xsl:otherwise>
    
      <xsl:text>dragomeJs.isInstanceof(</xsl:text>
      <xsl:text>__r</xsl:text>
      <xsl:value-of select="@vy"/>
      <xsl:text>, </xsl:text>
      <xsl:call-template name="emitScopedName">
        <xsl:with-param name="string" select="@value"/>
      </xsl:call-template>
      <xsl:text>)</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>) ? 1 : 0;</xsl:text>
</xsl:template>

<!--  dex:cmp-long
      ============  -->
<xsl:template match="dex:cmp-long">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 1 : (__r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> == __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 0 : -1);</xsl:text>
</xsl:template>

<!--  dex:cmpg-*
      TODO: Implement NaN bias.
      =========================  -->
<xsl:template match="dex:cmpg-double|dex:cmpg-float">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 1 : (__r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> == __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 0 : -1);</xsl:text>
</xsl:template>

<!--  dex:cmpl-*
      TODO: Implement NaN bias.
      =========================  -->
<xsl:template match="dex:cmpl-double|dex:cmpl-float">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 1 : (__r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> == __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text> ? 0 : -1);</xsl:text>
</xsl:template>


<!--  dex:neg-*
      =========  -->
<xsl:template match="dex:neg-int|dex:neg-long|dex:neg-float|dex:neg-double">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = -__r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:move*
      =========  -->
<xsl:template match="dex:move|dex:move-from16|dex:move-wide|dex:move-wide-from16|dex:move-object|dex:move-object-from16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:rem-*
      ============  -->
<xsl:template match="dex:rem-int|dex:rem-int-2addr|dex:rem-double|dex:rem-double-2addr|dex:rem-float|dex:rem-float-2addr|dex:rem-long|dex:rem-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> % __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>


<!--  dex:rem-int-lit*
      ================  -->
<xsl:template match="dex:rem-int-lit8|dex:rem-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> % </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:and-int*
      ===========  -->
<xsl:template match="dex:and-int|dex:and-int-2addr|dex:and-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &amp; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:and-int-lit*
      ================  -->
<xsl:template match="dex:and-int-lit8|dex:and-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &amp; </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:or-int-lit*
      ===============  -->
<xsl:template match="dex:or-int-lit8|dex:or-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> | </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>


<!-- Dragome -->
<xsl:template match="dex:or-int|dex:or-int-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> | __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;
</xsl:text>
</xsl:template>

  
<xsl:template match="dex:or-long|dex:or-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> | __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;
</xsl:text>
</xsl:template>
<!-- Dragome -->




<!--  dex:xor-int*
      ============  -->
<xsl:template match="dex:xor-int|dex:xor-int-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> ^ __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:xor-int-lit*
      ================  -->
<xsl:template match="dex:xor-int-lit8|dex:xor-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> ^ </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  dex:catches
      ===========  -->
<xsl:template match="dex:catches">
  <!-- do nothing -->
</xsl:template>


<!--  dex:*-switch
      ============  -->
<xsl:template match="dex:sparse-switch|dex:packed-switch">
  <xsl:text>
            var default_case = false;
            switch (__r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text>) {
</xsl:text>
  <xsl:for-each select="dex:case">
    <xsl:text>
            case </xsl:text>
    <xsl:value-of select="@key"/>
    <xsl:text>: __next_label = </xsl:text>
    <xsl:value-of select="@label"/>
    <xsl:text>; break;</xsl:text>
  </xsl:for-each>
  <xsl:text>
            default: default_case = true; break;
            }
            if (!default_case) break;</xsl:text>
</xsl:template>


<!--  dex:try-catch
      =============  -->
<xsl:template match="dex:try-catch">
  <xsl:apply-templates/>
  <xsl:text>else throw dragomeJs.nullSaveException(ex);  }</xsl:text>
</xsl:template>


<!--  dex:try
      =======  -->
<xsl:template match="dex:try">
  <xsl:text>
            var __ex;
            try {
</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>
            } catch(ex) {
            if (false) {}</xsl:text>
</xsl:template>


<!--  dex:catch
      =========  -->
<xsl:template match="dex:catch">
    <xsl:text>
            else if (dragomeJs.isInstanceof(ex, </xsl:text>
       <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string" select="@exception-type"/>
       </xsl:call-template>
           <xsl:text>)) {
              __ex = ex;
              __next_label = </xsl:text>
    <xsl:value-of select="@target"/>
    <xsl:text>;
              break;
            }</xsl:text>
</xsl:template>


<!--  dex:move-exception
      ==================  -->
<xsl:template match="dex:move-exception">
  <xsl:text>     __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __ex;
</xsl:text>
</xsl:template>


<!--  dex:throw
      =========  -->
<xsl:template match="dex:throw">
  <xsl:text>
            throw dragomeJs.nullSaveException(__r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text>);</xsl:text>
</xsl:template>

<xsl:template match="dex:shl-int-lit8|dex:shl-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &lt;&lt; </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="dex:shl-int|dex:shl-int-2addr|dex:shl-long|dex:shl-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &lt;&lt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--  arreglar esto!! -->
<xsl:template match="dex:shr-int-lit8|dex:shr-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt;&gt; </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="dex:shr-int|dex:shr-int-2addr|dex:shr-long|dex:shr-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt;&gt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="dex:ushr-int-lit8|dex:ushr-int-lit16">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt;&gt;&gt; </xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="dex:ushr-int|dex:ushr-int-2addr|dex:ushr-long|dex:ushr-long-2addr">
  <xsl:text>
           __r</xsl:text>
  <xsl:value-of select="@vx"/>
  <xsl:text> = __r</xsl:text>
  <xsl:value-of select="@vy"/>
  <xsl:text> &gt;&gt;&gt; __r</xsl:text>
  <xsl:value-of select="@vz"/>
  <xsl:text>;</xsl:text>
</xsl:template>



<!--  dex:nop
      ===============  -->
<xsl:template match="dex:nop" />



<!--  isObjectRef
      ===========  -->
<xsl:function name="vm:isObjectRef" as="xs:boolean">
  <xsl:param name="type" as="xs:string"/>
  
  <xsl:value-of select="not($type='byte' or $type='short' or $type='int' or $type='float' or $type='long' or $type='double' or
                            $type='char' or $type='boolean' or $type='void')"/>
</xsl:function>



<!--  appendSignatureDex
      ==================  -->
<xsl:template name="appendSignatureDex">
  <xsl:param name="signature" />
  <xsl:choose>
    <xsl:when test="count(dex:parameters/dex:parameter) != 0">
      <xsl:text>__</xsl:text>
      <xsl:for-each select="dex:parameters/dex:parameter">
        <xsl:text>_</xsl:text>
        <xsl:if test="position() != 1">
            <xsl:text>_</xsl:text>
        </xsl:if>        
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string">
            <xsl:call-template name="replaceString">
              <xsl:with-param name="text" select="@type" />
              <xsl:with-param name="replace">[]</xsl:with-param>
              <xsl:with-param name="with">_ARRAYTYPE</xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:when>
  </xsl:choose>
        <xsl:for-each select="dex:parameters/dex:return">
        <xsl:text>$</xsl:text>
        <xsl:call-template name="emitScopedName">
          <xsl:with-param name="string">
            <xsl:call-template name="replaceString">
              <xsl:with-param name="text" select="@type" />
              <xsl:with-param name="replace">[]</xsl:with-param>
              <xsl:with-param name="with">_ARRAYTYPE</xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
  
</xsl:template>


<!--
   Default template. If the XMLVM file should contain an instruction
   that is not handled by this stylesheet, this default template
   will make sure we notice it by writing a special error function
   to the output stream.
-->
<xsl:template match="*">
  <xsl:text>
            ERROR("</xsl:text>
  <xsl:value-of select="name()"/>
    <xsl:text>");</xsl:text>
</xsl:template>

</xsl:stylesheet>
