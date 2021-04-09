/*
This script reads a csv file that has the names of the variables that will be put into the Nerc Vocabulary server
and creates a hasmap that has the variable name as a key and the link to the vocabulary server as a value
*/

@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv

// Providing the path to the CSV file that contains the names of the variables
String csvFilePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Resources from Matthes/Bridges variable names/CVS unprocessed.csv"

// Parsing the csv file
def csvFile = new File(csvFilePath)
def csv_content = csvFile.getText('utf-8')
def csvData = parseCsv(csv_content)

// Creating a hasmap that containg the variable name in addition to the link to the vocabulary server
Map<String,String> allVariabels = new HashMap<String,String>()
// Populating the hasmap with values
for (line in csvData) {
    allVariabels.put(line.parameter_name, line.sdn_parameter_uri)
}

// Printing the hasmap values just for tesing
allVariabels.forEach((key, value )-> println(key + " " +value));