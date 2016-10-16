package com.kenlam.groovyconsole.interactions

import com.kenlam.groovyconsole.AdvancedGroovyConsole

public abstract class InteractionModule {
	String name
	
	protected boolean uiBuiltAlready = false
	
	public InteractionModule (AdvancedGroovyConsole console, Map params) {
		this.name = params.name ?: console.getNextInteractionModuleName(this)
	}
	
	public void buildUI(AdvancedGroovyConsole console) {
		if (uiBuiltAlready) {
			throw new IllegalStateException("UI already built for ${this}")
		}
		uiBuiltAlready = true
		doBuildUI(console)
	}
	
	protected abstract void doBuildUI(AdvancedGroovyConsole console)
}