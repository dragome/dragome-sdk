package com.dragome.html.dom.html5canvas;

import org.w3c.dom.Element;

public interface CanvasRenderingContext2D
{
	void beginPath();

	void closePath();

	void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise);

	void arc(double x, double y, double radius, double startAngle, double endAngle);

	void arcTo(double x1, double y1, double x2, double y2, double radius);

	void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

	void clearRect(double x, double y, double width, double height);

	void moveTo(double x, double y);

	void lineTo(double x, double y);

	boolean isPointInPath(double x, double y);

	boolean isPointInStroke(double x, double y);

	void quadraticCurveTo(double cpx, double cpy, double x, double y);

	void rect(double x, double y, double width, double height);

	void scrollPathIntoView();

	void clip();

	ImageData createImageData(double width, double height);

	CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);

	CanvasPattern createPattern(CanvasImageSource image, String repetition);

	CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);

	void drawImage(CanvasImageSource image, double dx, double dy);

	void drawImage(CanvasImageSource image, double dx, double dy, double dw, double dh);

	void drawImage(CanvasImageSource image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);

	boolean drawCustomFocusRing(Element element);

	void drawSystemFocusRing(Element element);

	void putImageData(ImageData imagedata, double dx, double dy, double dirtyX, double dirtyY, double dirtyWidth, double dirtyHeight);

	void putImageData(ImageData imagedata, double dx, double dy);

	ImageData getImageData(double x, double y, double width, double height);

	TextMetrics measureText(String text);

	void fill();

	void fillRect(double x, double y, double width, double height);

	void fillText(String text, double x, double y, double maxWidth);

	void fillText(String text, double x, double y);

	void stroke();

	void strokeRect(double x, double y, double w, double h);

	void strokeText(String text, float x, float y, float maxWidth);

	void strokeText(String text, float x, float y);

	void setTransform(double m11, double m12, double m21, double m22, double dx, double dy);

	void transform(double m11, double m12, double m21, double m22, double dx, double dy);

	void translate(double x, double y);

	void rotate(double angle);

	void scale(double x, double y);

	void save();

	void restore();

	String getFillStyle();

	void setFillStyle(String fillStyle);

	void setFillStyle(CanvasGradient gradient);

	void setFillStyle(CanvasPattern pattern);

	String getLineCap();

	void setLineCap(String lineCap);

	float getLineDashOffset();

	void setLineDashOffset(float lineDashOffset);

	String getLineJoin();

	void setLineJoin(String lineJoin);

	double getLineWidth();

	void setLineWidth(double lineWidth);

	double getMiterLimit();

	void setMiterLimit(double miterLimit);

	String getStrokeStyle();

	void setStrokeStyle(String fillStyle);

	void setStrokeStyle(CanvasGradient gradient);

	void setStrokeStyle(CanvasPattern pattern);

	double getGlobalAlpha();

	void setGlobalAlpha(double globalAlpha);

	String getGlobalCompositeOperation();

	void setGlobalCompositeOperation(String operation);

	double getShadowBlur();

	void setShadowBlur(double shadowBlur);

	String getShadowColor();

	void setShadowColor(String shadowColor);

	double getShadowOffsetX();

	void setShadowOffsetX(double offsetX);

	double getShadowOffsetY();

	void setShadowOffsetY(double offsetY);

	String getFont();

	void setFont(String font);

	String getTextAlign();

	void setTextAlign(String textAlign);

	String getTextBaseline();

	void setTextBaseline(String textBaseline);

	HTMLCanvasElement getCanvas();
}