package com.kenlam.groovyconsole.interactions

import com.kenlam.groovyconsole.AdvancedGroovyConsole

public class InteractionModule {
	String customName
	
	public InteractionModule (Map params) {
		this.customName = params.customName
	}
	
	public void buildUI(AdvancedGroovyConsole console) {
		
	}
}