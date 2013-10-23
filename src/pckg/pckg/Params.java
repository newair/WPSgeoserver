package pckg.pckg;

/**
 * Created with IntelliJ IDEA.
 * User: newair
 * Date: 10/19/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Params {

    //parameters are set to default values

    public static String WPSURL = "http://localhost:8093/wps/WebProcessingService";
    public static String processID = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

    public static String WFSURL = "http://localhost:8094/geoserver/topp/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&maxFeatures=50&outputFormat=GML2";//"http://localhost:8094/geoserver/topp/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&maxFeatures=50&outputFormat=GML2";//"http://localhost:8094/geoserver/topp/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_cities&maxFeatures=50&outputFormat=GML2";
    public static String width = "0.01";
    public static String path = "E:\\FYP\\Other\\GMLfilename.txt";
}
