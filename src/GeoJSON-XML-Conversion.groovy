import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.function.Function
import java.util.stream.Collectors


// Part 1
/*
// Extracting the variables names from the Argo project Excel sheet. The Excel sheet has been converted to csv.

The following script reads a csv file that has the names of the variables that will be put into the Nerc Vocabulary server
and creates a hasmap that has the variable name as a key and the link to the vocabulary server as a value
*/

//Setting the resources and output files
// Providing the path to the metadata file
// The CSV and the metadata GeoJson files are all provided in the resources file provided in the relative path

// Providing the path to the output file where the xml files will be written
String output = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Output XML files/"

@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv

// Providing the path to the CSV file that contains the names of the variables
def csvFilePath = getClass().getResource('CVS_unprocessed.csv').toURI()


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


// Part 2

// Creating om:OM_Observation

//Information about the file on disk
    // Link to the file on the FTP server
    String linkFTP = "http://www.ifremer.fr/co/ego/ego/v2/sg558/sg558_20130601/"
    // File name
    String  fileName = "wallis_mooset01_R.nc_metadata.goejson"

// Parsing the GeoJSON file
// Getting the file's relative path
def geoJSONfilePath = getClass().getResource(fileName).toURI()
// Creating a Java File object from the relative path
def fileToRead = new java.io.File(geoJSONfilePath)
def reader = new FileReader(fileToRead)
def geoJson = new JsonSlurper().parse(reader)
def writerOM_Observation = new StringWriter()
def xmlOM_Observation = new MarkupBuilder(writerOM_Observation)

// The output XML file
def outputPath = output + fileName +"_om_OM_Observation.xml"
def fileWriter = new FileWriter(outputPath)


// Extracting attributes from the GeoJSON file to be used in the xml document

// Extracting the time period of the entire mission
String missionTimePeriod = geoJson.features[0].properties.Time_Period
String MissionStrartTimeString = missionTimePeriod.substring(1,20)
String MissionEndTimeString = missionTimePeriod.substring(23,missionTimePeriod.length() -1)

//Converting the date time format to Java format
// Defining the original and target date-time format
DateFormat orgignalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")

// Parsing date-time to Java format
Date MissionStrartTimeDate = orgignalFormat.parse(MissionStrartTimeString)
Date MissionEndTimeDate = orgignalFormat.parse(MissionEndTimeString)
String MissionStrartTime = targetFormat.format(MissionStrartTimeDate)
String MissionEndTime = targetFormat.format(MissionEndTimeDate)

// Providing IDs
def gliderID = "glider-" + UUID.randomUUID().toString().substring(0,7)
def phenomenonTimeID = "phenomenonTime-" + UUID.randomUUID().toString().substring(0,7)
def resultTimeID = "resultTime-" + UUID.randomUUID().toString().substring(0,7)
def samplingCurveID = "samplingCurve-" + UUID.randomUUID().toString().substring(0,7)
def lineStringID = "lineString-" + UUID.randomUUID().toString().substring(0,7)

//Providing the link to SensorML
//Since the app is not hoseted, the actual link to the file cannot be provided.
String assignedOffering = "the-assigned-offering-from-InsertSensor"

String assignedProcedure = "the-assigned-procedure-from-InsertSensor"

//Providing the link to ObservedProperty
String linkToObservedProperty = "https://link-to-observerdproperties"

// Creating the xml file om:OM_Observation
xmlOM_Observation.'sos:InsertObservation'(
        'xmlns:sos':'http://www.opengis.net/sos/2.0',
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        'xmlns:om':'http://www.opengis.net/om/2.0',
        'xmlns:xlink':'http://www.w3.org/1999/xlink',
        'xmlns:sams': "http://www.opengis.net/samplingSpatial/2.0",
        'xmlns:sf':"http://www.opengis.net/sampling/2.0",
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance",
        'service':"SOS",
        'version':"2.0.0"
){
    mkp.comment "Here goes the swes:assignedOffering from the InsertSensor response"
    'sos:offering'(assignedOffering)

    'sos:observation'{
        'om:OM_Observation'('gml:id':gliderID){
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

            mkp.comment "Here goes the assignedProcedure from the InsertSensor response"
            'om:procedure'('xlink:href':assignedProcedure)

            mkp.comment "This should be replaced with the real link once the app is hosted"
            'om:observedProperty'('xlink:href': linkToObservedProperty)

            'om:featureOfInterest'{
                'sams:SF_SpatialSamplingFeature'('gml:id':samplingCurveID){
                    // Check for the value here!!! SamplingCurve1
                    'gml:identifier'('codeSpace':'http://www.uncertweb.org', "SamplingCurve1")
                    'sf:type'('xlink:href':"http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve")
                    'sf:sampledFeature'('xlink:href':"urn:ogc:def:nil:OGC:unknown")

                    'sams:shape'{
                        'gml:LineString'('gml:id':lineStringID, 'srsName':"http://www.opengis.net/def/crs/EPSG/0/4326"){
                            geoJson.features[0].geometry.coordinates.each {
                                'gml:pos'(it[0] + " " + it[1])
                            }
                        }
                    }
                }

            }

            'om:result'('xlink:href':linkFTP , "xsi:type":"gml:ReferenceType")
        }
    }
}

//println writer.toString()

XmlUtil.serialize(writerOM_Observation.toString(),fileWriter)

// Part 3

// Creating the XML for the om:observedProperty where the variable names available in the metadata file will be saved
def writerOM_ObservedProperty = new StringWriter()
def xmlOM_ObservedProperty = new MarkupBuilder(writerOM_ObservedProperty)
// The output file
def outputPathOM_ObservedProperty = output + fileName + "om_observedProperty.xml"
def fileWriterOM_ObservedProperty = new FileWriter(outputPathOM_ObservedProperty)

/*
    TODO: make sure the name spaces are correct for the OM_ObservedProperty
*/

// Getting the variable names that are available in the metadata file
ArrayList availableVariabels = geoJson.features[0].properties.variabels_names as ArrayList

// Getting the links to the Nerc vocabulary server of the avialable variable names
// This code gets the variable names form the nerc collection (allVariabels map)
Map<String, String> res = availableVariabels.stream()
        .filter(allVariabels::containsKey)
        .collect(Collectors.toMap(Function.identity(), allVariabels::get));




// Creating the xml file OM_ObservedProperty
xmlOM_ObservedProperty.'swe:CompositePhenomenon'('xmlns:swe':'http://www.opengis.net/swe/1.0.1',
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


// Part 4

// Creating the XML for the InsertSensor
def writerInsertSensor = new StringWriter()
def xml_InsertSensor = new MarkupBuilder(writerInsertSensor)
// The output file
def outputPathInsertSensor = output + fileName +"_InsertSensor.xml"
def fileWriterInsertSensor = new FileWriter(outputPathInsertSensor)

// GML ID
def InsertSensorGmlID = "InsertSensor-" + UUID.randomUUID().toString().substring(0,7)



// Creating the xml file for the InserSensor opertation
xml_InsertSensor.'swes:InsertSensor'(
        'xmlns:swes':'http://www.opengis.net/swes/2.0',
        'xmlns:sos':'http://www.opengis.net/sos/2.0',
        'xmlns:xlink':'http://www.w3.org/1999/xlink',
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance",
        'xmlns:sml':"http://www.opengis.net/sensorml/2.0",
        'service':"SOS",
        'version':"2.0.0",
){
    'swes:procedureDescriptionFormat'("http://www.opengis.net/sensorml/2.0")

    // Should this be only the link or is it the whole xml sml:PhysicalSystem element in the example?
    // What should the gml id be?

    'swes:procedureDescription'{
        'sml:PhysicalSystem'('xsi:schemaLocation':'http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swesDescribeSensor.xsd http://www.opengis.net/sensorml/2.0 http://schemas.opengis.net/sensorML/2.0/sensorML.xsd http://www.isotc211.org/2005/gmd http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd http://www.isotc211.org/2005/gco http://schemas.opengis.net/iso/19139/20070417/gco/gco.xsd http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd"', 'gml:id':InsertSensorGmlID){
            mkp.comment "What could the gml description be here??"
            'gml:description'('description')

            'sml:keywords'{
                'sml:KeywordList'{
                    // providing the names of the variables available in the Nerc Vocabulary server
                    for (Map.Entry<String, String> entry : res) {
                        'sml:keyword'(entry.getKey())
                    }
                }
            }
        }
    }

    mkp.comment "This should be replaced with the real link once the app is hosted"
    'swes:observableProperty'('https://link-to-ObservedProperty')

    //should I provide the links with xlink:href or just the link?
    'swes:metadata'{
        'sos:SosInsertionMetadata'{
            'sos:observationType'('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ReferenceObservation')
            'sos:featureOfInterestType'('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve')
        }


    }
}

// Writing file to disk
XmlUtil.serialize(writerInsertSensor.toString(),fileWriterInsertSensor)