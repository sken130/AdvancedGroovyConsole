package com.kenlam.groovyconsole.interactions

public class InteractionModule {
	String customName
	
	public InteractionModule (Map params) {
		this.customName = params.customName
	}
	
	public void buildUI(AdvancedGroovyConsole console) {
		
	}
}