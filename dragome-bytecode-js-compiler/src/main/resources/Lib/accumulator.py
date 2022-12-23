
from land.valko.example import Accumulator

class MyAccumulator(Accumulator):
    def __init__(self):
        self._value = 0

    def add(self, value):
        self._value += value

    def getValue(self):
        return self._value
