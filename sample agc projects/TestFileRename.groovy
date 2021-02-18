public class DigitSystem {
    public final Map valueToDigitMaps
    public final Map digitToValueMaps
    public final Map<Integer, BigInteger> positionToPlaceValueMap
    
    public DigitSystem(Map _valueToDigitMaps) {
        valueToDigitMaps = _valueToDigitMaps.collectEntries{ int position, List digits ->
            return [(position): digits.asImmutable()]
        }.asImmutable()
        digitToValueMaps = valueToDigitMaps.collectEntries{ int position, List digits ->
            Map digitToValueMap_row = [:]
            digits.eachWithIndex{ String digit, int idx ->
                digitToValueMap_row[digit] = idx
            }
            digitToValueMap_row = digitToValueMap_row.asImmutable()
            return [(position): digitToValueMap_row]
        }.asImmutable()
        
        Map _positionToPlaceValueMap = [:]
        BigInteger currentPlaceValue = 1
        valueToDigitMaps.each{ int position, List digits ->
            _positionToPlaceValueMap[position] = currentPlaceValue
            currentPlaceValue *= digits.size()
        }
        positionToPlaceValueMap = _positionToPlaceValueMap.asImmutable()
    }
    
    public String describeDigitToValueMap() {
        return digitToValueMaps.collect{ int position, Map digitToValueMap_row ->
            return "${position}: ${digitToValueMap_row}"
        }.join("\n")
    }
    
    public BigInteger convertDigitsToValue(String digits) {
        BigInteger value = 0
        digits.eachWithIndex{ String digit, int idx ->
            int digitPosition = digits.length() - 1 - idx
            Map digitToValueMap_row = digitToValueMaps[digitPosition]
            BigInteger digitValue = digitToValueMap_row[digit]
            if (digitValue == null) {
                throw new IllegalArgumentException("Invalid digit ${digit} at digit position ${digitPosition}")
            }
            BigInteger placeValue = positionToPlaceValueMap[digitPosition]
            value += digitValue * placeValue
        }
        return value
    }
    
    public String convertValueToDigits(BigInteger value) {
        BigInteger maxValueSupported = 0
        positionToPlaceValueMap.each{ int position, BigInteger placeValue ->
            List digits = valueToDigitMaps[position]
            BigInteger biggestValue = digits.size() - 1
            maxValueSupported += biggestValue * placeValue
        }
        
        if (value > maxValueSupported) {
            throw new IllegalArgumentException("This DigitSystem can only support values up to ${maxValueSupported}, but encountered ${value}")
        }
        
        BigInteger remainingValue = value
        StringBuilder digitsSB = new StringBuilder()
        new TreeSet(positionToPlaceValueMap.keySet()).descendingIterator().each{ int position ->
            BigInteger placeValue = positionToPlaceValueMap[position]
            // println "placeValue $placeValue"
            BigInteger[] quotientAndRemainder = remainingValue.divideAndRemainder(placeValue)
            
            BigInteger quotient = quotientAndRemainder[0]
            
            String digit = valueToDigitMaps[position][quotient]
            digitsSB.append(digit)
            
            remainingValue = quotientAndRemainder[1]
        }
        return digitsSB.toString()
    }
}

DigitSystem englishDigitSystem = new DigitSystem([
    0: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"],
    1: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
])

DigitSystem normalDigitSystem = new DigitSystem([
    0: ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"],
    1: ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
])

DigitSystem roltonRadioDigitSystem = new DigitSystem([
    0: ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"],
    1: ["C", "D", "A", "B", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
])

/***** Config Start *****/

// File basePath = new File("C:/Users/LAM/Music/To SD Card Radio/Star On 45 Vol 1")
File basePath = new File("D:/µØ·½Ï·Çú")
boolean reallyDoRename = true

/*
DigitSystem oldDigitSystem = normalDigitSystem
DigitSystem newDigitSystem = roltonRadioDigitSystem
*/

/*
DigitSystem oldDigitSystem = roltonRadioDigitSystem
DigitSystem newDigitSystem = normalDigitSystem
*/

DigitSystem oldDigitSystem = normalDigitSystem
DigitSystem newDigitSystem = normalDigitSystem
int digitValueOffset = 1

/***** Config End *****/

List mp3Files = basePath.listFiles().findAll{File file -> file.getName().endsWith(".mp3")}.sort{File file-> file.getName()}

// println englishDigitSystem.describeDigitToValueMap()

mp3Files.each{ File mp3File ->
    println "mp3File ${mp3File}"
    String mp3FileName = mp3File.getName()
    String existingDigits = mp3FileName.substring(19, 21)
    // println "existingDigits ${existingDigits}"
    
    BigInteger existingValue = oldDigitSystem.convertDigitsToValue(existingDigits)
    existingValue += digitValueOffset
    
    String newDigits = newDigitSystem.convertValueToDigits(existingValue)
    println "existingValue ${existingValue}, newDigits ${newDigits}"
    
    StringBuilder newFileNameSb = new StringBuilder(mp3FileName)
    newFileNameSb.replace(19, 21, newDigits)
    String newFileName = newFileNameSb.toString()
    // println "newFileName ${newFileName}"
    
    File newFile = new File(mp3File.getParentFile(), newFileName)
    println "newFile ${newFile}"
    println ""
    
    if (reallyDoRename) {
        mp3File.renameTo(newFile)
    }
    
}

return null