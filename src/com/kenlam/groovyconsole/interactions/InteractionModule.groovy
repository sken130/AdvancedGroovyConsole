package com.kenlam.groovyconsole.interactions

import com.kenlam.groovyconsole.AdvancedGroovyConsole
import java.awt.Component

public abstract class InteractionModule {
	String name
	
	final LinkedHashSet<Closure> nameChangeListeners = new LinkedHashSet()
	
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
	
	public void addNameChangeListener(Closure listener) {
		nameChangeListeners.add(listener)
	}
	public void removeNameChangeListener(Closure listener) {
		nameChangeListeners.remove(listener)
	}
	
	public void setName(String newName) {
		this.name = newName
		nameChangeListeners.each{ Closure nameChangeListener -> nameChangeListener(this.name) }
	}
}