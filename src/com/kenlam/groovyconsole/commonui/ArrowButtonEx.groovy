package com.kenlam.groovyconsole.commonui

import java.awt.Color
import javax.swing.plaf.basic.BasicArrowButton
import java.awt.Dimension

public class ArrowButtonEx extends BasicArrowButton {
	public Dimension customMaximumSize
	
	public ArrowButtonEx(int direction) {
		super(direction)
	}
	public ArrowButtonEx(int direction, Color background, Color shadow, Color darkShadow, Color highlight) {
		super(direction, background, shadow, darkShadow, highlight)
	}
	
	public Dimension getMaximumSize() {
		if (customMaximumSize) {
			return customMaximumSize;
		}
		return super.getMaximumSize();
	}
}