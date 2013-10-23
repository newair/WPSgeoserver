package pckg.pckg;


import net.opengis.wps.x100.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class Main {

    public void testExecute() {


        try {
            ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                    Params.WPSURL, Params.processID);
             System.out.println(describeProcessDocument);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(Params.WPSURL);
            ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                    Params.WPSURL, Params.processID);
            // define inputs
            HashMap<String, Object> inputs = new HashMap<String, Object>();
            // complex data by reference
            inputs.put(
                    "data", Params.WFSURL);

            // literal data
            inputs.put("width", Params.width);
            executeProcess(Params.WPSURL, Params.processID,
                    describeProcessDocument, inputs);


        } catch (WPSClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CapabilitiesDocument requestGetCapabilities(String url)
            throws WPSClientException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        wpsClient.connect(url);

        CapabilitiesDocument capabilities = wpsClient.getWPSCaps(url);

        ProcessBriefType[] processList = capabilities.getCapabilities()
                .getProcessOfferings().getProcessArray();


        return capabilities;
    }

    public ProcessDescriptionType requestDescribeProcess(String url,
                                                         String processID) throws IOException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        ProcessDescriptionType processDescription = wpsClient
                .getProcessDescription(url, processID);

        InputDescriptionType[] inputList = processDescription.getDataInputs()
                .getInputArray();


        return processDescription;
    }

    public void executeProcess(String url, String processID,
                               ProcessDescriptionType processDescription,
                               HashMap<String, Object> inputs) throws Exception {
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(
                processDescription);

        for (InputDescriptionType input : processDescription.getDataInputs()
                .getInputArray()) {
            String inputName = input.getIdentifier().getStringValue();
            Object inputValue = inputs.get(inputName);
            if (input.getLiteralData() != null) {
                if (inputValue instanceof String) {
                    executeBuilder.addLiteralData(inputName,
                            (String) inputValue);
                }
            } else if (input.getComplexData() != null) {
                // Complexdata by value
                if (inputValue instanceof FeatureCollection) {
                    IData data = new GTVectorDataBinding(
                            (FeatureCollection) inputValue);
                    executeBuilder
                            .addComplexData(
                                    inputName,
                                    data,
                                    "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
                                    "UTF-8", "text/xml");
                }
                // Complexdata Reference
                if (inputValue instanceof String) {
                    executeBuilder
                            .addComplexDataReference(
                                    inputName,
                                    (String) inputValue,
                                    "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
                                    null, "text/xml");
                }

                if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                    throw new IOException("Property not set, but mandatory: "
                            + inputName);
                }
            }
        }
        executeBuilder.setMimeTypeForOutput("application/WFS", "result");
        //executeBuilder.setEncodingForOutput("UTF-8","result");

        /*  executeBuilder.setEncodingForOutput("UTF-8","result");
        executeBuilder.setSchemaForOutput(
                "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
                "result");
*/
        ExecuteDocument execute = executeBuilder.getExecute();

        execute.getExecute().setService("WPS");
        WPSClientSession wpsClient = WPSClientSession.getInstance();
        Object responseObject = wpsClient.execute(url, execute);
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;

            // System.out.println(responseObject.toString());
            String content = responseObject.toString();


            System.out.println(content);

        }

    }

    public static void main(String[] args) {

        Logger.getRootLogger().setLevel(Level.OFF);

        Main client = new Main();
        client.testExecute();
        //  System.out.println("end of wps");
    }
}