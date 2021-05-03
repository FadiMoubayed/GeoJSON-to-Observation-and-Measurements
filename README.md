# Converting-GeoJSON-to-XML
This groovy script converts the extracted glider metadata from GeoJSON to XML

This script is organised as the following:

    1- The first part extracts the variable names form the Argo project Excel sheet
    2- The second part creates the om:OM_Observation xml metadata file
    3- The third part creates the om:observedProperty (the names of the variables available in the metadata file) xml file
    which will be linked to om:OM_Observation xml file


# Issues
  The xml declaration and the root element are being printed on the same line

  The problem is most probably in the serialize method
