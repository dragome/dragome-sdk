package com.dragome.web.html.dom.w3c;

import com.dragome.w3c.dom.webgl.WebGLRenderingContext;
import com.dragome.w3c.dom.webgl.WebGLUniformLocation;

public interface WebGLRenderingContextExtension extends WebGLRenderingContext
{
    public void uniform1iv(WebGLUniformLocation location, int[] v);
	public void uniform1fv(WebGLUniformLocation loc, float[] v);
	public void uniform2fv(WebGLUniformLocation loc, float[] v);
	public void uniform2iv(WebGLUniformLocation loc, int[] v);
	public void uniform3fv(WebGLUniformLocation loc, float[] v);
	public void uniform3iv(WebGLUniformLocation loc, int[] v);
	public void uniform4fv(WebGLUniformLocation loc, float[] v);
	public void uniform4iv(WebGLUniformLocation loc, int[] v);
	public void uniformMatrix2fv(WebGLUniformLocation loc, boolean transpose, float[] value);
	public void uniformMatrix3fv(WebGLUniformLocation loc, boolean transpose, float[] value);
	public void uniformMatrix4fv(WebGLUniformLocation loc, boolean transpose, float[] value);
}

