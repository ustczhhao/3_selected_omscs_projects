import os

class Helper:
    @staticmethod
    def getTemplatesDir():
        return os.path.join(os.path.abspath(os.path.dirname(__file__)),"templates")