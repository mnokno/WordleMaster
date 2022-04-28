import os
from typing import List


# Creates new file where each word is flatten
def genFlatWordData():
    # Gets file directory
    readFilePath: str = os.path.dirname(os.path.dirname(os.path.realpath("main.py"))) + "\\app\\src\\main\\res\\raw\\wordle_words.txt"

    # Reads all the words
    with open(readFilePath, 'r') as wordsFile:
        words: List[str] = wordsFile.readlines()

    # List for string flattened words
    flattenedWords: List[str] = list()
    # Flatten all the letters e.g words -> dorsw
    for word in words:
        flattenedWords.append(flatten(word[:-1]))

    # Writes the flatted words to a file
    writeFilePath: str = os.path.dirname(readFilePath) + "\\wordle_words_flattened.txt"
    with open(writeFilePath, 'w') as wordsFlattenedFile:
        wordsFlattenedFile.writelines(str.join('\n', flattenedWords))


# Flattens a word e.g words -> dorsw
def flatten(word: str) -> str:
    # Converts given word to a list of characters
    charArray: List[chr] = list(word)

    # Flattens the word
    for i in range(0, len(word)):
        hasSwapped = False
        for j in range(0, len(word) - 1 - i):
            if compareChat(charArray[j], charArray[j + 1]):
                charArray[j], charArray[j + 1] = charArray[j + 1], charArray[j]
                hasSwapped = True
        if not hasSwapped:
            break

    # Join the characters together and returns the flattened word
    return str.join("", charArray)


# Returns true if a > b
def compareChat(a: chr, b: chr) -> bool:
    return ord(a) > ord(b)
