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

xml.'sheiße' {
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

XmlUtil.serialize(writer.toString(),fileWriter)
