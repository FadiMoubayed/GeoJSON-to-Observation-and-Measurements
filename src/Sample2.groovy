import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

def filePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/Json2.json"

def reader = new FileReader(filePath)
def ui = new JsonSlurper().parse(reader)
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)


xml.mkp.xmlDeclaration version: "1.0", encoding: "utf-8"


xml.'om:OM_Observation'('gml:id':'glider-0123',
                        'xmlns:swe':"http://www.opengis.net/swe/2.0"
) {
    type(ui.type)
    features {
        type(ui.features.type)
        properties{
            ui.features.properties.Time_Stamps.each{
                Time_Stamp(it)
            }
        }
    }
    geometry{
        type(ui.features.geometry.type)
        coordinates {
            ui.features.geometry.coordinates.each{
                coordinates(it)
            }
        }
    }

}



println writer.toString()

