import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlSlurper
import groovy.xml.XmlUtil
import groovy.xml.streamingmarkupsupport.StreamingMarkupWriter

def geoJSONfilePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/Json2.json"
def reader = new FileReader(geoJSONfilePath)
def geoJson = new JsonSlurper().parse(reader)
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)



def outputPath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/outputfile"
def fileWriter = new FileWriter(outputPath)

xml.'om:OM_Observation'('gml:id':'glider-0123',
        'xmlns:swe':'http://www.opengis.net/swe/2.0',
        'xmlns:sos':'http://www.opengis.net/sos/2.0',
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        'xmlns:om':'http://www.opengis.net/om/2.0',
        'xmlns:gco':'http://www.isotc211.org/2005/gco',
        'xmlns:xlink':'http://www.w3.org/1999/xlink'
){
    type(geoJson.type)
    features {
        type(geoJson.features.type)
        properties{
            geoJson.features.properties.Time_Stamps.each{
                Time_Stamp(it)
            }
        }
    }
    geometry{
        type(geoJson.features.geometry.type)
        coordinates {
            geoJson.features.geometry.coordinates.each{
                coordinates(it)
            }
        }
    }

}



println writer.toString()

// XmlUtil.serialize(writer.toString(),fileWriter)
