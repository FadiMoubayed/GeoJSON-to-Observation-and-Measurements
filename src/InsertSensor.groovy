import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil


// Creating the XML for the om:observedProperty where the variable names available in the metadata file will be saved
def writerInsertSensor = new StringWriter()
def xml_InsertSensor = new MarkupBuilder(writerInsertSensor)
// The output file
def outputPathInsertSensor = "/home/fadi/DataX1/University/WWU/WWU 5/Task 3/Output XML files/InsertSensor"
def fileWriterInsertSensor = new FileWriter(outputPathInsertSensor)

// GML ID
def InsertSensorGmlID = "InsertSensor-" + UUID.randomUUID().toString().substring(0,7)



// Creating the xml file OM_ObservedProperty
xml_InsertSensor.'swes:InsertSensor'(
        'xmlns:swes':'http://www.opengis.net/swes/2.0',
        'xmlns:sos':'http://www.opengis.net/sos/2.0',
        'xmlns:xlink':'http://www.w3.org/1999/xlink',
        'xmlns:gml':'http://www.opengis.net/gml/3.2',
        //Is this the right namesapce for xsi?
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance",
        'xmlns:sml':"http://www.opengis.net/sensorml/2.0",
        'service':"SOS",
        'version':"2.0.0",
){
    'swes:procedureDescriptionFormat'("http://www.opengis.net/sensorML/2.0")

    // Should this be only the link or is it the whole xml sml:PhysicalSystem element in the example?
    // What should the gml id be?
    'swes:procedureDescription'{
        'sml:PhysicalSystem'('xsi:schemaLocation':'http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swesDescribeSensor.xsd http://www.opengis.net/sensorml/2.0 http://schemas.opengis.net/sensorML/2.0/sensorML.xsd http://www.isotc211.org/2005/gmd http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd http://www.isotc211.org/2005/gco http://schemas.opengis.net/iso/19139/20070417/gco/gco.xsd http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd"', 'gml:id':InsertSensorGmlID)
    }

    'swes:observableProperty'('https://link-to-ObservedProperty')

    //should I provide the links with xlink:href or just the link?
    'swes:metadata'{
        'sos:SosInsertionMetadata'{
            'sos:observationType'('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation')
            'sos:featureOfInterestType'('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve')
        }


    }
}

// Writing file to disk
XmlUtil.serialize(writerInsertSensor.toString(),fileWriterInsertSensor)


