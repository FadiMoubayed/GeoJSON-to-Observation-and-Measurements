import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import groovy.xml.streamingmarkupsupport.StreamingMarkupWriter

def filePath = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Json to xml test/Json2.json"

def reader = new FileReader(filePath)
def ui = new JsonSlurper().parse(reader)
def writer = new StringWriter()


Map<String,String> namespaces = new HashMap<String,String>();
namespaces.put("swe", "http://www.opengis.net/swe/2.0")
namespaces.put("sos", "http://www.opengis.net/sos/2.0")



xmlDocument = new StreamingMarkupBuilder().bind{

    mkp.xmlDeclaration()
    mkp.declareNamespace(namespaces)

    languages {

        English {
            grammer{

            }
        }
    }

}


// println XmlUtil.serialize(xmlDocument.toString())


//xmlDocument.encoding = "UTF-8"
//println xmlDocument.encoding
////xmlDocument.println()



/*

xmlDocument = new StreamingMarkupBuilder().bind {
    mkp.xmlDeclaration(declaration)   // does not work
    mkp.declareNamespace(map)



    languages {
        comment << "this is just a test comment"
        English {

        }
    }



}
xmlDocument.encoding = "UTF-8"

println xmlDocument.encoding
 */


//println xmlDocument.toString()





