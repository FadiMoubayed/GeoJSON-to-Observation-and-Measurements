import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

/*
In this script I am trying to access the original metadata file.
I am trying to created an xml document with the time period of the mission track as a first step to creating
final xml metadata representation


PROBLEMS
 - When I am parsing the geoJson metadata file that is a FeatureCollection, there is a conflict because
   both features have the same attributes; type, properties...etc and groovy is returning a list of features
   when I am trying to access those features. E.g. [Feature, Feature] for geoJson.features.type
   SOLVED:
   *    println geoJson.features[0].properties.variabels_names

 - The time period is a string and I am not sure if subsetting the string to get each time period seperately
   is a good idea. The time period of the mission is as follows
        "Time_Period": "[2019-11-30 19:25:00 , 2019-12-04 22:40:04]"
        The question is how can I get each time period seperately?
        I can change how it is saved in the R script and put each time stamp in a different variable

 - The method XmlUtil.serialize is writing an xml output the is probably invalid.
   The xml declartation and the root element are being printed on the same line
 */

// Providing the path to the metadata file
def geoJSONfilePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/GeoJSON metadata files/amadeus_20191123_R.nc_metadata.goejson"
def reader = new FileReader(geoJSONfilePath)
def geoJson = new JsonSlurper().parse(reader)
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)


def outputPath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Output XML files/outputfile"
def fileWriter = new FileWriter(outputPath)

// Extracting attributes from the GeoJSON file to be used in the xml document

// Extracting the time period of the entire mission
String missionTimePeriod = geoJson.features[0].properties.Time_Period
String MissionStrartTime = missionTimePeriod.substring(1,20)
String MissionEndTime = missionTimePeriod.substring(23,missionTimePeriod.length() -1)

// Extracting the coordinates of the LineString
/*
It looks like the coordinates are saved in an ArrayList. Each element of this ArrayList is an
ArrayList with two elements containing the coordinates. E.g lat, long
 */
ArrayList LineStringCoordinates = geoJson.features[0].geometry.coordinates
// LineStringCoordinates.forEach(element -> println(element))
//println LineStringCoordinates[1][1]


def gliderID = "glider-" + UUID.randomUUID().toString().substring(0,7)
def phenomenonTimeID = "phenomenonTime-" + UUID.randomUUID().toString().substring(0,7)
def resultTimeID = "resultTime-" + UUID.randomUUID().toString().substring(0,7)
def samplingCurveID = "samplingCurve-" + UUID.randomUUID().toString().substring(0,7)
def lineStringID = "lineString-" + UUID.randomUUID().toString().substring(0,7)


xml.'om:OM_Observation'('gml:id':gliderID,
        'xmlns:swe':'http://www.opengis.net/swe/2.0',
        'xmlns:sos':'http://www.opengis.net/sos/2.0',
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        'xmlns:om':'http://www.opengis.net/om/2.0',
        'xmlns:gco':'http://www.isotc211.org/2005/gco',
        'xmlns:xlink':'http://www.w3.org/1999/xlink',
        // Not sure if this namspace declaration is correct
        'xmlns:sams': "http://www.opengis.net/samplingSpatial/2.0",
        'xmlns:sf':"http://www.opengis.net/sampling/2.0",
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema‐instance"

){


    //'gml:description'("Mission track metadata")
    'om:type'('xlink:href':"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ReferenceObservation")

    'om:phenomenonTime'{
        // What should the id be?
        'gml:TimePeriod'('gml:id':phenomenonTimeID){
            'gml:beginPosition'(MissionStrartTime)
            'gml:endPosition'(MissionEndTime)
        }
    }

    'om:resultTime'{
        // What should the id be?
        'gml:TimeInstant'('gml:id':resultTimeID){
            'gml:timePosition'(MissionEndTime)
        }
    }


    // Are the values here hardcoded??
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

/*
    'gml:LineString'{
        geoJson.features[0].geometry.coordinates.each{
            'gml:pos'(it[0] + " " + it[1])
        }
    }

*/
}

 println writer.toString()

XmlUtil.serialize(writer.toString(),fileWriter)







/*
DRAFT
This part is only for experimenting and should be deleted

ArrayList features = geoJson.features
//println features.getClass()
//println features.size()
//println geoJson.features.type[1]
//println geoJson.features[0].properties.Time_Period.getClass()
//println geoJson.features[0].properties.Time_Period.substring(1,20)


//println features[1]
*/

