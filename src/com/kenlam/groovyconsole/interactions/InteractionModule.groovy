package com.kenlam.groovyconsole.interactions

import com.kenlam.groovyconsole.AdvancedGroovyConsole
import java.awt.Component

public abstract class InteractionModule {
	String name
	
	protected boolean uiBuiltAlready = false
	
	public InteractionModule (AdvancedGroovyConsole console, Map params) {
		this.name = params.name ?: console.getNextInteractionModuleName(this)
	}
	
	public Component buildUI(AdvancedGroovyConsole console) {
		if (uiBuiltAlready) {
			throw new IllegalStateException("UI already built for ${this}")
		}
		uiBuiltAlready = true
		Component builtUI = doBuildUI(console)
		return builtUI
	}
	
	protected abstract Component doBuildUI(AdvancedGroovyConsole console)
}