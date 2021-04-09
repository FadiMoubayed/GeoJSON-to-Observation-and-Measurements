import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.util.function.Function
import java.util.stream.Collectors

/*
TODO put all the input and output files in one directory
 */

/*
This script is organised as the following:
    1- The first part extracts the variable names form the Argo project Excel sheet
    2- The second part creates the om:OM_Observation xml metadata file
    3- The third part creates the om:observedProperty (the names of the variables available in the metadata file) xml file
    which will be linked to om:OM_Observation xml file
*/

// Part 1
/*
// Extracting the variables names from the Argo project Excel sheet. The Excel sheet has been converted to csv.

The following script reads a csv file that has the names of the variables that will be put into the Nerc Vocabulary server
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

// Creating a hasmap containing the variable names in addition to the link to the vocabulary server
Map<String,String> allVariabels = new HashMap<String,String>()
// Populating the hasmap with values
for (line in csvData) {
    allVariabels.put(line.parameter_name, line.sdn_parameter_uri)
}

// Printing the hasmap values just for tesing
allVariabels.forEach((key, value )-> println(key + " " +value));



// Part 2
/*
TODO: change the variable names to suit creating 2 XML files
 */


// Converting the metadata GeoJSON file to XML

// Providing the path to the metadata file
def fileName = "amerigo_coconet_R.nc_metadata.goejson"
def geoJSONfilePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/GeoJSON metadata files/" + fileName
def reader = new FileReader(geoJSONfilePath)
def geoJson = new JsonSlurper().parse(reader)
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

// The output XML file
def outputPath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Output XML files/om_OM_Observation.xml"
def fileWriter = new FileWriter(outputPath)

// Extracting attributes from the GeoJSON file to be used in the xml document

// Extracting the time period of the entire mission
String missionTimePeriod = geoJson.features[0].properties.Time_Period
String MissionStrartTime = missionTimePeriod.substring(1,20)
String MissionEndTime = missionTimePeriod.substring(23,missionTimePeriod.length() -1)



// Providing IDs
def gliderID = "glider-" + UUID.randomUUID().toString().substring(0,7)
def phenomenonTimeID = "phenomenonTime-" + UUID.randomUUID().toString().substring(0,7)
def resultTimeID = "resultTime-" + UUID.randomUUID().toString().substring(0,7)
def samplingCurveID = "samplingCurve-" + UUID.randomUUID().toString().substring(0,7)
def lineStringID = "lineString-" + UUID.randomUUID().toString().substring(0,7)

// Creating the xml file
xml.'om:OM_Observation'('gml:id':gliderID,
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        'xmlns:om':'http://www.opengis.net/om/2.0',
        'xmlns:xlink':'http://www.w3.org/1999/xlink',
        'xmlns:sams': "http://www.opengis.net/samplingSpatial/2.0",
        'xmlns:sf':"http://www.opengis.net/sampling/2.0",
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema‐instance"
){
    //'gml:description'("Mission track metadata")
    'om:type'('xlink:href':"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ReferenceObservation")

    'om:phenomenonTime'{
        'gml:TimePeriod'('gml:id':phenomenonTimeID){
            'gml:beginPosition'(MissionStrartTime)
            'gml:endPosition'(MissionEndTime)
        }
    }

    'om:resultTime'{
        'gml:TimeInstant'('gml:id':resultTimeID){
            'gml:timePosition'(MissionEndTime)
        }
    }

    'om:featureOfInterest'{
        'sams:SF_SpatialSamplingFeature'('gml:id':samplingCurveID){
            // Check for the value here!!! SamplingCurve1
            'gml:identifier'('codeSpace':'http://www.uncertweb.org', "SamplingCurve1")
            'sf:type'('xlink:href':"http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve")
            'sf:sampledFeature'('xsi:nil':"true")
            'sams:shape'{
                'gml:LineString'('gml:id':lineStringID, 'srsName':"http://www.opengis.net/def/crs/EPSG/0/4326"){
                    geoJson.features[0].geometry.coordinates.each {
                        'gml:pos'(it[0] + " " + it[1])
                    }
                }
            }
        }

    }
}

println writer.toString()

XmlUtil.serialize(writer.toString(),fileWriter)

// Part 3

// Creating the XML for the om:observedProperty where the variable names available in the metadata file will be saved
def writerOM_ObservedProperty = new StringWriter()
def xmlOM_ObservedProperty = new MarkupBuilder(writerOM_ObservedProperty)
// The output file
def outputPathOM_ObservedProperty = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Output XML files/om_observedProperty"
def fileWriterOM_ObservedProperty = new FileWriter(outputPathOM_ObservedProperty)

/*
    TODO: make sure the name spaces are correct for the OM_ObservedProperty
*/

// Getting the variable names that are available in the metadata file
ArrayList availableVariabels = geoJson.features[0].properties.variabels_names as ArrayList
// Printing the arraylist just for testing
availableVariabels.forEach(element -> println(element))

// Getting the links to the Nerc vocabulary server of the avialable variable names
// This code gets the variable names form the nerc collection (allVariabels map)
Map<String, String> res = availableVariabels.stream()
        .filter(allVariabels::containsKey)
        .collect(Collectors.toMap(Function.identity(), allVariabels::get));
// Printing just for testing
System.out.println(res.toString());



// Creating the xml file
xmlOM_ObservedProperty.'swe:CompositePhenomenon'('xmlns:swe':'http://www.opengis.net/swe/2.0',
        'xmlns:xlink':'http://www.w3.org/1999/xlink'
        //'xsi:schemaLocation':'http://www.opengis.net/swe/1.0.1 http://schemas.opengis.net/sweCommon/1.0.1/swe.xsd',
        //'gml:id':'composite6/',
        //'dimension':'6'
){
    // Creating an xml element for each variable available in the metadata file
    for (String key: res.keySet()) {
        'swe:component'('xlink:href':res.get(key))
    }
}

// Writing file to disk
XmlUtil.serialize(writerOM_ObservedProperty.toString(),fileWriterOM_ObservedProperty)