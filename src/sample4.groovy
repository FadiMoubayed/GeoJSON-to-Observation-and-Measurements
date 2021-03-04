import groovy.json.JsonSlurper
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil


def geoJSONfilePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/Json2.json"
def reader = new FileReader(geoJSONfilePath)
def geoJson = new JsonSlurper().parse(reader)

def outputPath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/outputfile"
def fileWriter = new FileWriter(outputPath)
def builder = new StreamingMarkupBuilder()


Map<String,String> namespaces = new HashMap<String,String>()
//namespaces.put("gml:id", "glider-0123")
namespaces.put("swe", "http://www.opengis.net/swe/2.0")
namespaces.put("sos", "http://www.opengis.net/sos/2.0")
namespaces.put("gml", "http://www.opengis.net/gml/3.2")
namespaces.put("om", "http://www.opengis.net/om/2.0")
namespaces.put("gco", "http://www.isotc211.org/2005/gco")
namespaces.put("xlink", "http://www.w3.org/1999/xlink")


 xml = builder.bind{
     //mkp.xmlDeclaration()
     mkp.declareNamespace(namespaces)

     type(geoJson.type) {
         features {
             type(geoJson.features.type)
             propertiesxml {
                 geoJson.features.properties.Time_Stamps.each {
                     Time_Stamp(it)
                 }
             }
         }
         geometry {
             type(geoJson.features.geometry.type)
             coordinates {
                 geoJson.features.geometry.coordinates.each {
                     coordinates(it)
                 }
             }
         }

     }
}


println xml.toString()


XmlUtil.serialize(xml.toString(),fileWriter)
