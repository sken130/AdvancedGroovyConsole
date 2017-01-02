/*
 *  Copyright 2016 Ken Lam
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kenlam.groovyconsole.commonui

import java.awt.Color
import java.awt.Component
import javax.swing.JPanel
import javax.swing.JButton
import javax.swing.JScrollPane
import javax.swing.JList
import javax.swing.Box
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.plaf.basic.BasicArrowButton
import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.ListModel
import javax.swing.DefaultListModel

import com.google.common.collect.HashBiMap

public class JListPanel extends JPanel {
	protected final JList jList
	public JListPanel(JList jList) {
		super(true)
		this.jList = jList
		JScrollPane jlist_scrollPane = new JScrollPane(jList)
		// jlist_scrollPane.border = BorderFactory.createLineBorder(Color.blue)
		jlist_scrollPane.alignmentX = Component.LEFT_ALIGNMENT
		
		Box hbox = Box.createHorizontalBox()
		hbox.add(jlist_scrollPane)
		Box vbox = Box.createVerticalBox()
		ArrowButtonEx upButton = new ArrowButtonEx(BasicArrowButton.NORTH)
		ArrowButtonEx downButton = new ArrowButtonEx(BasicArrowButton.SOUTH)
		upButton.customMaximumSize = upButton.getPreferredSize()
		downButton.customMaximumSize = downButton.getPreferredSize()
		JButton deleteButton = new JButton("Delete")
		vbox.add(upButton)
		vbox.add(downButton)
		vbox.add(deleteButton)
		hbox.add(vbox)
		
		
		upButton.addActionListener([
			actionPerformed: { ActionEvent e ->
				List selectedIndices = jList.getSelectedIndices().toList()
				if (selectedIndices.size() > 0) {
					moveSelectedIndices(selectedIndices, -1)
				}
			}
		] as ActionListener)
		downButton.addActionListener([
			actionPerformed: { ActionEvent e ->
				List selectedIndices = jList.getSelectedIndices().toList()
				if (selectedIndices.size() > 0) {
					moveSelectedIndices(selectedIndices, 1)
				}
			}
		] as ActionListener)
		
		deleteButton.addActionListener([
			actionPerformed: { ActionEvent e ->
				List selectedIndices = jList.getSelectedIndices().toList()
				if (selectedIndices.size() > 0) {
					selectedIndices.sort{a, b -> b <=> a}
					DefaultListModel jListModel = jList.getModel()
					selectedIndices.each{ int selectedIndex ->
						jListModel.removeElementAt(selectedIndex)
					}
				}
			}
		] as ActionListener)
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS))
		this.add(hbox)
	}
	
	/*
	 * 1 -> 4
	 * 2 -> 5
	 * 4 -> 7
	 * 5 -> 8
	 * 6 -> 9
	 * 8 -> 11
	 * 
	 * 1   O   3
	 * 2   O   7
	 * 3       9
	 * 4   O   O
	 * 5   O   O
	 * 6   O   10
	 * 7       O
	 * 8   O   O
	 * 9       O
	 * 10      11
	 * 11      O
	 * 
	 */
	
	protected moveSelectedIndices(List<Integer> selectedIndices, int moveBy) {
		if (selectedIndices.size() == 0) {
			throw new IllegalArgumentException("selectedIndices must have at least one element.")
		}
		selectedIndices = new ArrayList(selectedIndices)
		selectedIndices.sort()
		DefaultListModel jListModel = jList.getModel()
		// println "jListModel.getSize() ${jListModel.getSize()} (${jListModel.getSize().getClass()})"
		HashBiMap<Integer, Integer> oldIndexToNewIndexMap = HashBiMap.create()
		selectedIndices.each{ int oldIndex ->
			int newIndex = oldIndex + moveBy
			oldIndexToNewIndexMap[oldIndex] = newIndex
		}
		
		LinkedHashSet allOldAndNewIndices = new LinkedHashSet(oldIndexToNewIndexMap.keySet() + oldIndexToNewIndexMap.values())
		// println "allOldAndNewIndices ${allOldAndNewIndices} (${allOldAndNewIndices.getClass()})"
		
		int minIndex = allOldAndNewIndices.min()
		int maxIndex = allOldAndNewIndices.max()
		
		List remainingOldIndices = (minIndex..maxIndex).findAll{ index ->
			return !oldIndexToNewIndexMap.containsKey(index)
		}
		List remainingNewIndices = (minIndex..maxIndex).findAll{ index ->
			return !oldIndexToNewIndexMap.containsValue(index)
		}
		
		// println "remainingOldIndices ${remainingOldIndices} (${remainingOldIndices.getClass()})"
		// println "remainingNewIndices ${remainingNewIndices} (${remainingNewIndices.getClass()})"
		
		assert remainingOldIndices.size() == remainingNewIndices.size()
		
		(0..remainingOldIndices.size()-1).each{ int i ->
			int oldIndex = remainingOldIndices[i]
			int newIndex = remainingNewIndices[i]
			oldIndexToNewIndexMap[oldIndex] = newIndex
		}
		
		Closure debug_oldIndexToNewIndexMap = {
			println "oldIndexToNewIndexMap"
			oldIndexToNewIndexMap.each{ int oldIndex, int newIndex ->
				println "${oldIndex} --> ${newIndex}"
			}
		}
		int fullListMinIndex = 0
		int fullListMaxIndex = jListModel.getSize() - 1
		
		// println "fullListMinIndex ${fullListMinIndex}, fullListMaxIndex ${fullListMaxIndex}"
		
		LinkedHashSet outOfBoundOldIndices = oldIndexToNewIndexMap.keySet().findAll{ int oldIndex ->
			return oldIndex < fullListMinIndex || oldIndex > fullListMaxIndex
		}
		
		// println "outOfBoundOldIndices ${outOfBoundOldIndices} (${outOfBoundOldIndices.getClass()})"
		if (outOfBoundOldIndices.size() > 0) {
			List remainingNewIndices2 = []
			outOfBoundOldIndices.each{ int outOfBoundIndex ->
				int newIndexRemoved = oldIndexToNewIndexMap.remove(outOfBoundIndex)
				remainingNewIndices2.push(newIndexRemoved)
			}
			remainingNewIndices2.sort()
			
			List oldIndicesNeedToMoveAgain
			List newIndicesToFillIn
			if (moveBy < 0) {  // Move up and then out of bound
				int maxEmptyNewIndex = remainingNewIndices2.max()
				oldIndicesNeedToMoveAgain = oldIndexToNewIndexMap.findAll{ int oldIndex, int newIndex ->
					newIndex < maxEmptyNewIndex
				}.keySet().toList()
				
				newIndicesToFillIn = (fullListMinIndex..maxEmptyNewIndex).toList()
			} else if (moveBy > 0) {  // Move up and then out of bound
				int minEmptyNewIndex = remainingNewIndices2.min()
				oldIndicesNeedToMoveAgain = oldIndexToNewIndexMap.findAll{ int oldIndex, int newIndex ->
					newIndex > minEmptyNewIndex
				}.keySet().toList()
				
				newIndicesToFillIn = (minEmptyNewIndex..fullListMaxIndex).toList()
			}
			
			assert oldIndicesNeedToMoveAgain.size() == newIndicesToFillIn.size()
			oldIndicesNeedToMoveAgain.each{ int oldIndex ->
				oldIndexToNewIndexMap.remove(oldIndex)
			}
			
			(0..oldIndicesNeedToMoveAgain.size()-1).each{ int i ->
				int oldIndex = oldIndicesNeedToMoveAgain[i]
				int newIndex = newIndicesToFillIn[i]
				oldIndexToNewIndexMap[oldIndex] = newIndex
			}
		}
		
		// println "jListModel ${jListModel} (${jListModel.getClass()})"
		Map oldIndexToElementMap = oldIndexToNewIndexMap.keySet().collectEntries{ int oldIndex ->
			return [(oldIndex): jListModel.getElementAt(oldIndex)]
		}
		
		oldIndexToNewIndexMap.each{ int oldIndex, int newIndex ->
			jListModel.set(newIndex, oldIndexToElementMap[oldIndex])
		}
		
		List newSelectedIndices = selectedIndices.collect{ int oldIndex -> oldIndexToNewIndexMap[oldIndex] }.sort()
		jList.setSelectedIndices((int[])newSelectedIndices)
	}
}