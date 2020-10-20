/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.lwjgl;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.soywiz.kgl.KmlGl;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.soywiz.kgl.KmlGlProxy;
import com.soywiz.kmem.FBuffer;
import com.soywiz.kmem.MemBuffer;
import com.soywiz.korgw.platform.NativeKgl;
import kotlin.random.Random;

/** An implementation of the {@link com.badlogic.gdx.graphics.GL20} interface based on LWJGL. Note that LWJGL shaders and OpenGL ES shaders will not be 100%
 * compatible. Some glGetXXX methods are not implemented.
 * 
 * @author mzechner */
class LwjglGL20 implements com.badlogic.gdx.graphics.GL20 {
	private ByteBuffer buffer = null;
	private FloatBuffer floatBuffer = null;
	private IntBuffer intBuffer = null;
	
	private final KmlGl gl;

	LwjglGL20(KmlGl gl) {
		this.gl = gl;
	}

	private void ensureBufferCapacity (int numBytes) {
		if (buffer == null || buffer.capacity() < numBytes) {
			buffer = com.badlogic.gdx.utils.BufferUtils.newByteBuffer(numBytes);
			floatBuffer = buffer.asFloatBuffer();
			intBuffer = buffer.asIntBuffer();
		}
	}

	private FloatBuffer toFloatBuffer (float[] v, int offset, int count) {
		ensureBufferCapacity(count << 2);
		floatBuffer.clear();
		com.badlogic.gdx.utils.BufferUtils.copy(v, floatBuffer, count, offset);
		return floatBuffer;
	}

	private IntBuffer toIntBuffer (int[] v, int offset, int count) {
		ensureBufferCapacity(count << 2);
		intBuffer.clear();
		com.badlogic.gdx.utils.BufferUtils.copy(v, count, offset, intBuffer);
		return intBuffer;
	}

	public void glActiveTexture (int texture) {
		gl.activeTexture(texture);
	}

	public void glAttachShader (int program, int shader) {
		gl.attachShader(program, shader);
	}

	public void glBindAttribLocation (int program, int index, String name) {
		gl.bindAttribLocation(program, index, name);
	}

	public void glBindBuffer (int target, int buffer) {
		gl.bindBuffer(target, buffer);
	}

	public void glBindFramebuffer (int target, int framebuffer) {
		gl.bindFramebuffer(target, framebuffer);
	}

	public void glBindRenderbuffer (int target, int renderbuffer) {
		gl.bindRenderbuffer(target, renderbuffer);
	}

	public void glBindTexture (int target, int texture) {
		gl.bindTexture(target, texture);
	}

	public void glBlendColor (float red, float green, float blue, float alpha) {
		gl.blendColor(red, green, blue, alpha);
	}

	public void glBlendEquation (int mode) {
		gl.blendEquation(mode);
	}

	public void glBlendEquationSeparate (int modeRGB, int modeAlpha) {
		gl.blendEquationSeparate(modeRGB, modeAlpha);
	}

	public void glBlendFunc (int sfactor, int dfactor) {
		gl.blendFunc(sfactor, dfactor);
	}

	public void glBlendFuncSeparate (int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		gl.blendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	public void glBufferData (int target, int size, Buffer data, int usage) {
		gl.bufferData(target, size, fBuffer(data), usage);
	}

	public void glBufferSubData (int target, int offset, int size, Buffer data) {
		gl.bufferSubData(target, offset, size, fBuffer(data));
	}

	public int glCheckFramebufferStatus (int target) {
		return gl.checkFramebufferStatus(target);
	}

	public void glClear (int mask) {
		gl.clear(mask);
	}

	public void glClearColor (float red, float green, float blue, float alpha) {
		gl.clearColor(red, green, blue, alpha);
	}

	public void glClearDepthf (float depth) {
		gl.clearDepthf(depth);
	}

	public void glClearStencil (int s) {
		gl.clearStencil(s);
	}

	public void glColorMask (boolean red, boolean green, boolean blue, boolean alpha) {
		gl.colorMask(red, green, blue, alpha);
	}

	public void glCompileShader (int shader) {
		gl.compileShader(shader);
	}

	public void glCompressedTexImage2D (int target, int level, int internalformat, int width, int height, int border,
		int imageSize, Buffer data) {
		gl.compressedTexImage2D(target, level, internalformat, width, height, border, imageSize, fBuffer(data));
	}

	public void glCompressedTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format,
		int imageSize, Buffer data) {
		gl.compressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, fBuffer(data));
	}

	public void glCopyTexImage2D (int target, int level, int internalformat, int x, int y, int width, int height, int border) {
		gl.copyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	public void glCopyTexSubImage2D (int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		gl.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public int glCreateProgram () {
		return gl.createProgram();
	}

	public int glCreateShader (int type) {
		return gl.createShader(type);
	}

	public void glCullFace (int mode) {
		gl.cullFace(mode);
	}

	public void glDeleteBuffers (int n, IntBuffer buffers) {
		gl.deleteBuffers(n, fBuffer(buffers));
	}

	@Override
	public void glDeleteBuffer (int buffer) {
		ensureBufferCapacity(4);
		intBuffer.clear();
		intBuffer.put(0, buffer);
		glDeleteBuffers(1, intBuffer);
	}

	public void glDeleteFramebuffers (int n, IntBuffer framebuffers) {
		gl.deleteFramebuffers(n, fBuffer(framebuffers));
	}

	@Override
	public void glDeleteFramebuffer (int framebuffer) {
		ensureBufferCapacity(4);
		intBuffer.clear();
		intBuffer.put(0, framebuffer);
		glDeleteFramebuffers(1, intBuffer);
	}

	public void glDeleteProgram (int program) {
		gl.deleteProgram(program);
	}

	public void glDeleteRenderbuffers (int n, IntBuffer renderbuffers) {
		gl.deleteRenderbuffers(n, fBuffer(renderbuffers));
	}

	public void glDeleteRenderbuffer (int renderbuffer) {
		ensureBufferCapacity(4);
		intBuffer.clear();
		intBuffer.put(0, renderbuffer);
		glDeleteRenderbuffers(1, intBuffer);
	}

	public void glDeleteShader (int shader) {
		gl.deleteShader(shader);
	}

	public void glDeleteTextures (int n, IntBuffer textures) {
		gl.deleteTextures(n, fBuffer(textures));
	}

	@Override
	public void glDeleteTexture (int texture) {
		ensureBufferCapacity(4);
		intBuffer.clear();
		intBuffer.put(0, texture);
		glDeleteTextures(1, intBuffer);
	}

	public void glDepthFunc (int func) {
		gl.depthFunc(func);
	}

	public void glDepthMask (boolean flag) {
		gl.depthMask(flag);
	}

	public void glDepthRangef (float zNear, float zFar) {
		gl.depthRangef(zNear, zFar);
	}

	public void glDetachShader (int program, int shader) {
		gl.detachShader(program, shader);
	}

	public void glDisable (int cap) {
		gl.disable(cap);
	}

	public void glDisableVertexAttribArray (int index) {
		gl.disableVertexAttribArray(index);
	}

	public void glDrawArrays (int mode, int first, int count) {
		gl.drawArrays(mode, first, count);
	}

	public void glDrawElements (int mode, int count, int type, Buffer indices) {
		//FIXME: gl.drawElements() should work directly
		KmlGl myGL=gl;
		while (myGL instanceof KmlGlProxy){
			myGL=((KmlGlProxy)myGL).getParent();
		}
		((NativeKgl)(myGL)).getGl().glDrawElements(mode, count, type, getAddress(indices));
	}

	public void glEnable (int cap) {
		gl.enable(cap);
	}

	public void glEnableVertexAttribArray (int index) {
		gl.enableVertexAttribArray(index);
	}

	public void glFinish () {
		gl.finish();
	}

	public void glFlush () {
		gl.flush();
	}

	public void glFramebufferRenderbuffer (int target, int attachment, int renderbuffertarget, int renderbuffer) {
		gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	public void glFramebufferTexture2D (int target, int attachment, int textarget, int texture, int level) {
		gl.framebufferTexture2D(target, attachment, textarget, texture, level);
	}

	public void glFrontFace (int mode) {
		gl.frontFace(mode);
	}

	public void glGenBuffers (int n, IntBuffer buffers) {
		gl.genBuffers(n, fBuffer(buffers));
	}

	public int glGenBuffer () {
		ensureBufferCapacity(4);
		intBuffer.clear();
		glGenBuffers(1, intBuffer);
		return intBuffer.get(0);
	}

	public void glGenFramebuffers (int n, IntBuffer framebuffers) {
		gl.genFramebuffers(n, fBuffer(framebuffers));
	}

	public int glGenFramebuffer () {
		ensureBufferCapacity(4);
		intBuffer.clear();
		glGenFramebuffers(1, intBuffer);
		return intBuffer.get(0);
	}

	public void glGenRenderbuffers (int n, IntBuffer renderbuffers) {
		gl.genRenderbuffers(n, fBuffer(renderbuffers));
	}

	public int glGenRenderbuffer () {
		ensureBufferCapacity(4);
		intBuffer.clear();
		glGenRenderbuffers(1, intBuffer);
		return intBuffer.get(0);
	}

	public void glGenTextures (int n, IntBuffer textures) {
		gl.genTextures(n, fBuffer(textures));
	}

	public int glGenTexture () {
		ensureBufferCapacity(4);
		intBuffer.clear();
		glGenTextures(1, intBuffer);
		return intBuffer.get(0);
	}

	public void glGenerateMipmap (int target) {
		gl.generateMipmap(target);
	}

	public String glGetActiveAttrib (int program, int index, IntBuffer size, IntBuffer type) {
		FBuffer lenght=FBuffer.Companion.alloc(4);
		FBuffer name=FBuffer.Companion.alloc(256);

		gl.getActiveAttrib(program, index, name.getSize(), lenght, fBuffer(size), fBuffer(type), name);

		byte[] nameArray= new byte[lenght.get(0)];
		name.getMem().getBuffer().get(nameArray);
		return new String(nameArray);
	}

	public String glGetActiveUniform (int program, int index, IntBuffer size, IntBuffer type) {
		FBuffer lenght=FBuffer.Companion.alloc(4);
		FBuffer name=FBuffer.Companion.alloc(256);

		gl.getActiveUniform(program, index, name.getSize(), lenght, fBuffer(size), fBuffer(type), name);

		byte[] nameArray= new byte[lenght.get(0)];
		name.getMem().getBuffer().get(nameArray);
		return new String(nameArray);
	}

	public void glGetAttachedShaders (int program, int maxcount, Buffer count, IntBuffer shaders) {
		gl.getAttachedShaders(program, maxcount, fBuffer(count), fBuffer(shaders));
	}

	public int glGetAttribLocation (int program, String name) {
		return gl.getAttribLocation(program, name);
	}

	public void glGetBooleanv (int pname, Buffer params) {
		gl.getBooleanv(pname, fBuffer(params));
	}

	public void glGetBufferParameteriv (int target, int pname, IntBuffer params) {
		gl.getBufferParameteriv(target, pname, fBuffer(params));
	}

	public int glGetError () {
		return gl.getError();
	}

	public void glGetFloatv (int pname, FloatBuffer params) {
		gl.getFloatv(pname, fBuffer(params));
	}

	public void glGetFramebufferAttachmentParameteriv (int target, int attachment, int pname, IntBuffer params) {
		gl.getFramebufferAttachmentParameteriv(target, attachment, pname, fBuffer(params));
	}

	public void glGetIntegerv (int pname, IntBuffer params) {
		gl.getIntegerv(pname, fBuffer(params));
	}

	public String glGetProgramInfoLog (int program) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
		buffer.order(ByteOrder.nativeOrder());
		ByteBuffer tmp = ByteBuffer.allocateDirect(4);
		tmp.order(ByteOrder.nativeOrder());
		IntBuffer intBuffer = tmp.asIntBuffer();

		gl.getProgramInfoLog(program, tmp.remaining(), fBuffer(tmp), fBuffer(buffer));
		int numBytes = intBuffer.get(0);
		byte[] bytes = new byte[numBytes];
		buffer.get(bytes);
		return new String(bytes);
	}

	public void glGetProgramiv (int program, int pname, IntBuffer params) {
		gl.getProgramiv(program, pname, fBuffer(params));
	}

	public void glGetRenderbufferParameteriv (int target, int pname, IntBuffer params) {
		gl.getRenderbufferParameteriv(target, pname, fBuffer(params));
	}

	public String glGetShaderInfoLog (int shader) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
		buffer.order(ByteOrder.nativeOrder());
		ByteBuffer tmp = ByteBuffer.allocateDirect(4);
		tmp.order(ByteOrder.nativeOrder());
		IntBuffer intBuffer = tmp.asIntBuffer();

		gl.getShaderInfoLog(shader, buffer.remaining(), fBuffer(tmp), fBuffer(buffer));
		int numBytes = intBuffer.get(0);
		byte[] bytes = new byte[numBytes];
		buffer.get(bytes);
		return new String(bytes);
	}

	public void glGetShaderPrecisionFormat (int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
		gl.getShaderPrecisionFormat(shadertype, precisiontype, fBuffer(range), fBuffer(precision));
	}

	public void glGetShaderiv (int shader, int pname, IntBuffer params) {
		gl.getShaderiv(shader, pname, fBuffer(params));
	}

	public String glGetString (int name) {
		return gl.getString(name);
	}

	public void glGetTexParameterfv (int target, int pname, FloatBuffer params) {
		gl.getTexParameterfv(target, pname, fBuffer(params));
	}

	public void glGetTexParameteriv (int target, int pname, IntBuffer params) {
		gl.getTexParameteriv(target, pname, fBuffer(params));
	}

	public int glGetUniformLocation (int program, String name) {
		return gl.getUniformLocation(program, name);
	}

	public void glGetUniformfv (int program, int location, FloatBuffer params) {
		gl.getUniformfv(program, location, fBuffer(params));
	}

	public void glGetUniformiv (int program, int location, IntBuffer params) {
		gl.getUniformiv(program, location, fBuffer(params));
	}

	public void glGetVertexAttribPointerv (int index, int pname, Buffer pointer) {
		gl.getVertexAttribPointerv(index, pname, fBuffer(pointer));
	}

	public void glGetVertexAttribfv (int index, int pname, FloatBuffer params) {
		gl.getVertexAttribfv(index, pname, fBuffer(params));
	}

	public void glGetVertexAttribiv (int index, int pname, IntBuffer params) {
		gl.getVertexAttribiv(index, pname, fBuffer(params));
	}

	public void glHint (int target, int mode) {
		gl.hint(target, mode);
	}

	public boolean glIsBuffer (int buffer) {
		return gl.isBuffer(buffer);
	}

	public boolean glIsEnabled (int cap) {
		return gl.isEnabled(cap);
	}

	public boolean glIsFramebuffer (int framebuffer) {
		return gl.isFramebuffer(framebuffer);
	}

	public boolean glIsProgram (int program) {
		return gl.isProgram(program);
	}

	public boolean glIsRenderbuffer (int renderbuffer) {
		return gl.isRenderbuffer(renderbuffer);
	}

	public boolean glIsShader (int shader) {
		return gl.isShader(shader);
	}

	public boolean glIsTexture (int texture) {
		return gl.isTexture(texture);
	}

	public void glLineWidth (float width) {
		gl.lineWidth(width);
	}

	public void glLinkProgram (int program) {
		gl.linkProgram(program);
	}

	public void glPixelStorei (int pname, int param) {
		gl.pixelStorei(pname, param);
	}

	public void glPolygonOffset (float factor, float units) {
		gl.polygonOffset(factor, units);
	}

	public void glReadPixels (int x, int y, int width, int height, int format, int type, Buffer pixels) {
		gl.readPixels(x, y, width, height, format, type, fBuffer(pixels));
	}

	public void glReleaseShaderCompiler () {
		gl.releaseShaderCompiler();
	}

	public void glRenderbufferStorage (int target, int internalformat, int width, int height) {
		gl.renderbufferStorage(target, internalformat, width, height);
	}

	public void glSampleCoverage (float value, boolean invert) {
		gl.sampleCoverage(value, invert);
	}

	public void glScissor (int x, int y, int width, int height) {
		gl.scissor(x, y, width, height);
	}

	public void glShaderBinary (int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
		gl.shaderBinary(n, fBuffer(shaders), binaryformat, fBuffer(binary), length);
	}

	public void glShaderSource (int shader, String string) {
		gl.shaderSource(shader, string);
	}

	public void glStencilFunc (int func, int ref, int mask) {
		gl.stencilFunc(func, ref, mask);
	}

	public void glStencilFuncSeparate (int face, int func, int ref, int mask) {
		gl.stencilFuncSeparate(face, func, ref, mask);
	}

	public void glStencilMask (int mask) {
		gl.stencilMask(mask);
	}

	public void glStencilMaskSeparate (int face, int mask) {
		gl.stencilMaskSeparate(face, mask);
	}

	public void glStencilOp (int fail, int zfail, int zpass) {
		gl.stencilOp(fail, zfail, zpass);
	}

	public void glStencilOpSeparate (int face, int fail, int zfail, int zpass) {
		gl.stencilOpSeparate(face, fail, zfail, zpass);
	}

	public void glTexImage2D (int target, int level, int internalformat, int width, int height, int border, int format, int type,
		Buffer pixels) {
		gl.texImage2D(target, level, internalformat, width, height, border, format, type, fBuffer(pixels));
	}

	public void glTexParameterf (int target, int pname, float param) {
		gl.texParameterf(target, pname, param);
	}

	public void glTexParameterfv (int target, int pname, FloatBuffer params) {
		gl.texParameterfv(target, pname, fBuffer(params));
	}

	public void glTexParameteri (int target, int pname, int param) {
		gl.texParameteri(target, pname, param);
	}

	public void glTexParameteriv (int target, int pname, IntBuffer params) {
		gl.texParameteriv(target, pname, fBuffer(params));
	}

	public void glTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
		Buffer pixels) {
		gl.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, fBuffer(pixels));
	}

	public void glUniform1f (int location, float x) {
		gl.uniform1f(location, x);
	}

	public void glUniform1fv (int location, int count, FloatBuffer v) {
		gl.uniform1fv(location, count, fBuffer(v));
	}

	public void glUniform1fv (int location, int count, float[] v, int offset) {
		glUniform1fv(location, count, toFloatBuffer(v, offset, count));
	}

	public void glUniform1i (int location, int x) {
		gl.uniform1i(location, x);
	}

	public void glUniform1iv (int location, int count, IntBuffer v) {
		gl.uniform1iv(location, count, fBuffer(v));
	}

	@Override
	public void glUniform1iv (int location, int count, int[] v, int offset) {
		glUniform1iv(location, count, toIntBuffer(v, offset, count));
	}

	public void glUniform2f (int location, float x, float y) {
		gl.uniform2f(location, x, y);
	}

	public void glUniform2fv (int location, int count, FloatBuffer v) {
		gl.uniform2fv(location, count, fBuffer(v));
	}

	public void glUniform2fv (int location, int count, float[] v, int offset) {
		glUniform2fv(location, count, toFloatBuffer(v, offset, count << 1));
	}

	public void glUniform2i (int location, int x, int y) {
		gl.uniform2i(location, x, y);
	}

	public void glUniform2iv (int location, int count, IntBuffer v) {
		gl.uniform2iv(location, count, fBuffer(v));
	}

	public void glUniform2iv (int location, int count, int[] v, int offset) {
		glUniform2iv(location, count, toIntBuffer(v, offset, count << 1));
	}

	public void glUniform3f (int location, float x, float y, float z) {
		gl.uniform3f(location, x, y, z);
	}

	public void glUniform3fv (int location, int count, FloatBuffer v) {
		gl.uniform3fv(location, count, fBuffer(v));
	}

	public void glUniform3fv (int location, int count, float[] v, int offset) {
		glUniform3fv(location, count, toFloatBuffer(v, offset, count * 3));
	}

	public void glUniform3i (int location, int x, int y, int z) {
		gl.uniform3i(location, x, y, z);
	}

	public void glUniform3iv (int location, int count, IntBuffer v) {
		gl.uniform3iv(location, count, fBuffer(v));
	}

	public void glUniform3iv (int location, int count, int[] v, int offset) {
		glUniform3iv(location, count, toIntBuffer(v, offset, count * 3));
	}

	public void glUniform4f (int location, float x, float y, float z, float w) {
		gl.uniform4f(location, x, y, z, w);
	}

	public void glUniform4fv (int location, int count, FloatBuffer v) {
		gl.uniform4fv(location, count, fBuffer(v));
	}

	public void glUniform4fv (int location, int count, float[] v, int offset) {
		glUniform4fv(location, count, toFloatBuffer(v, offset, count << 2));
	}

	public void glUniform4i (int location, int x, int y, int z, int w) {
		gl.uniform4i(location, x, y, z, w);
	}

	public void glUniform4iv (int location, int count, IntBuffer v) {
		gl.uniform4iv(location, count, fBuffer(v));
	}

	public void glUniform4iv (int location, int count, int[] v, int offset) {
		glUniform4iv(location, count, toIntBuffer(v, offset, count << 2));
	}

	public void glUniformMatrix2fv (int location, int count, boolean transpose, FloatBuffer value) {
		gl.uniformMatrix2fv(location, count, transpose, fBuffer(value));
	}

	public void glUniformMatrix2fv (int location, int count, boolean transpose, float[] value, int offset) {
		glUniformMatrix2fv(location, count, transpose, toFloatBuffer(value, offset, count << 2));
	}

	public void glUniformMatrix3fv (int location, int count, boolean transpose, FloatBuffer value) {
		gl.uniformMatrix3fv(location, count, transpose, fBuffer(value));
	}

	public void glUniformMatrix3fv (int location, int count, boolean transpose, float[] value, int offset) {
		glUniformMatrix3fv(location, count, transpose, toFloatBuffer(value, offset, count * 9));
	}

	public void glUniformMatrix4fv (int location, int count, boolean transpose, FloatBuffer value) {
		gl.uniformMatrix4fv(location, count, transpose, fBuffer(value));
	}

	public void glUniformMatrix4fv (int location, int count, boolean transpose, float[] value, int offset) {
		glUniformMatrix4fv(location, count, transpose, toFloatBuffer(value, offset, count << 4));
	}

	public void glUseProgram (int program) {
		gl.useProgram(program);
	}

	public void glValidateProgram (int program) {
		gl.validateProgram(program);
	}

	public void glVertexAttrib1f (int indx, float x) {
		gl.vertexAttrib1f(indx, x);
	}

	public void glVertexAttrib1fv (int indx, FloatBuffer values) {
		gl.vertexAttrib1f(indx, values.get());
	}

	public void glVertexAttrib2f (int indx, float x, float y) {
		gl.vertexAttrib2f(indx, x, y);
	}

	public void glVertexAttrib2fv (int indx, FloatBuffer values) {
		gl.vertexAttrib2f(indx, values.get(), values.get());
	}

	public void glVertexAttrib3f (int indx, float x, float y, float z) {
		gl.vertexAttrib3f(indx, x, y, z);
	}

	public void glVertexAttrib3fv (int indx, FloatBuffer values) {
		gl.vertexAttrib3f(indx, values.get(), values.get(), values.get());
	}

	public void glVertexAttrib4f (int indx, float x, float y, float z, float w) {
		gl.vertexAttrib4f(indx, x, y, z, w);
	}

	public void glVertexAttrib4fv (int indx, FloatBuffer values) {
		gl.vertexAttrib4f(indx, values.get(), values.get(), values.get(), values.get());
	}

	public void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, Buffer buffer) {
		gl.vertexAttribPointer(indx, size, type, normalized, stride, getAddress(buffer));
	}

	public void glViewport (int x, int y, int width, int height) {
		gl.viewport(x, y, width, height);
	}

	public void glDrawElements (int mode, int count, int type, int indices) {
		gl.drawElements(mode, count, type, indices);
	}

	public void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, int ptr) {
		gl.vertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}

	private long getAddress(Buffer buffer) {
		// use a sliced buffer to get the correct address, including relative positioning
		Buffer sliced;
		//TODO: on java 9.x we could do sliced=buffer.slice()
		if (buffer instanceof ByteBuffer) {
			sliced = ((ByteBuffer) buffer).slice();
		} else if (buffer instanceof ShortBuffer) {
			sliced = ((ShortBuffer) buffer).slice();
		} else if (buffer instanceof IntBuffer) {
			sliced = ((IntBuffer) buffer).slice();
		} else if (buffer instanceof FloatBuffer) {
			sliced = ((FloatBuffer) buffer).slice();
		} else if (buffer instanceof DoubleBuffer) {
			sliced = ((DoubleBuffer) buffer).slice();
		} else {
			throw new GdxRuntimeException("Unsupported Buffer Type " + buffer.getClass());
		}
		try {
			Field addressField = Buffer.class.getDeclaredField("address");
			addressField.setAccessible(true);
			return (Long) addressField.get(sliced);//+b.position();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new GdxRuntimeException(e);
		}
	}

	private FBuffer fBuffer(Buffer buffer) {
		ByteBuffer b = getByteBuffer(buffer);
		return FBuffer.Companion.wrap(new MemBuffer(b, b.remaining()), b.remaining());
	}

	private ByteBuffer getByteBuffer(Buffer buffer) {
		if (buffer == null) {
			return null;
		} else if (buffer instanceof ByteBuffer) {
			return (ByteBuffer) buffer;
		} else {
			try {
				Field f = buffer.getClass().getDeclaredField("att");
				f.setAccessible(true);
				return getByteBuffer ((Buffer) f.get(buffer));
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new GdxRuntimeException(e);
			}
		}
	}
}
