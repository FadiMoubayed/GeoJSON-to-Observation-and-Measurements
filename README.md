# Converting-GeoJSON-to-XML
Converts the extracted glider metadata from GeoJSON to XML

This code uses groovy to parse the metadata GeoJOSN file at first and then
converts that into an xml document


# Issues
- The output of the created xml document is not well formatted

  The xml declaration and the root element are being printed on the same line

  The problem is most probably in the serialize method
- Using the StreamingMarkupBuilder, it is not possible to add the namesapce

  "gml:id":"glider-0123"


# Code status
This is the initial code. Ther are 5 scripts that need to be deleted. Only one
script should be kept.
