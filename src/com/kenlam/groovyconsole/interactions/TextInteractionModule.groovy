package com.kenlam.groovyconsole.interactions

import java.awt.Font
import com.kenlam.groovyconsole.AdvancedGroovyConsole
import javax.swing.JTextArea
import groovy.swing.SwingBuilder

public class TextInteractionModule extends InteractionModule {
	public static final String DEFAULT_NAME_PREFIX = "Text"
	
	protected JTextArea mainTextArea
	
	public TextInteractionModule(AdvancedGroovyConsole console, Map params) {
		super(console, params)
	}
	
	public def getValue() {
		return mainTextArea.getText()
	}
	
	protected void doBuildUI(AdvancedGroovyConsole console) {
		SwingBuilder swing = new SwingBuilder()
		String title = this.name
		def buildResult = swing.build{
			return panel(name: this.name) {
				borderLayout()
				scrollPane(border: emptyBorder(0)) {
					this.mainTextArea = textArea(
						name: 'mainTextArea',
						font: new Font('Monospaced', Font.PLAIN, 12),
						border: emptyBorder(4)
					)
				}
			}
		}
		// println "buildResult ${buildResult} (${buildResult.getClass()})"
		console.projectTabPanel.addTab(title, buildResult)
	}
}