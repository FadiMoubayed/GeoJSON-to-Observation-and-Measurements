import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.NamespaceBuilder
import groovy.xml.NamespaceBuilderSupport

// Providing the path to the metadata GeoJSON file
def filePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/Json2.json"
// Creating a file reader
def fileReader = new FileReader(filePath)
// Creating a JSON parser
def jsonParser = new JsonSlurper().parse(fileReader)
// Creating a string writer
def writer = new StringWriter()
// Creating an xml builder
def xmlBuilder = new MarkupBuilder(writer)



Map<String,String> map=new HashMap<String,String>();
map.put("xmlns:swe", "http://www.opengis.net/swe/2.0")
map.put("xmlns:sos", "http://www.opengis.net/sos/2.0")

def nsb = new NamespaceBuilderSupport(xmlBuilder)
nsb.declareNamespace(map)


// Creating the xml document
// Here the root element is called root

xmlBuilder.root {
    mkp.xmlDeclaration version: "1.0", encoding: "utf-8"          // adds the xml declaration


    type(jsonParser.type, attributetest: 'this is just a test')
    features {
        type(jsonParser.features.type)
        properties{
            jsonParser.features.properties.Time_Stamps.each{
                Time_Stamp(it)
            }
        }
    }
    geometry{
        type(jsonParser.features.geometry.type)
        coordinates {
            jsonParser.features.geometry.coordinates.each{
                coordinates(it)
            }
        }
    }

}
println writer.toString()