package com.kenlam.common

public class Looping {
	public static def loopAndFindFirst(Closure elementGenerator, Closure criteria) {
		boolean success = false
		
		def successfulElement
		while (!success) {
			def nextElement = elementGenerator()
			if (criteria(nextElement) == true) {
				successfulElement = nextElement
				success = true
			}
		}
		
		return successfulElement
	}
}