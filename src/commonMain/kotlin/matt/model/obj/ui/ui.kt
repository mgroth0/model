package matt.model.obj.ui

import matt.model.code.idea.UIIdea

interface UserInterface: UIIdea {
    fun warn(s: String)
}