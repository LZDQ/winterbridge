class RawInput:
    # Override the functions.
    def __init__(self): pass
    def click(self, button, t): pass
    def rightClick(self, button, t): pass
    def rightDown(self): pass
    def rightUp(self): pass
    def mouseDown(self, button): pass
    def mouseUp(self, button): pass
    def move(self, position): pass
    def moveTo(self, position): pass
    def press(self, key, t): pass
    def keyDown(self, button): pass
    def keyUp(self, button): pass

    def getKey(self, key_name):
        return self.key_code[key_name]

    def getButton(self, button_name):
        return self.button_code[button_name]

