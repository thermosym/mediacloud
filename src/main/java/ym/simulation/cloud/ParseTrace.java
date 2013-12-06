package ym.simulation.cloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ParseTrace {

	public static void main(String[] args) throws Exception {
		String fileName = "author.xml";
		readXMLFile(fileName);
	}

	public static void readXMLFile(String fileName) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			parseXML(document);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseXML(Document document) {
		try {
			System.out.println("start parse XML Document��");

			Element root = document.getRootElement();
			
			List<Element> segmentlist = root.elements("VideoSegment");
			for (Element elmSeg : segmentlist) {
				System.out.println(elmSeg.attributeValue("videoName")
						+ " " +elmSeg.attributeValue("OrigSize"));
				List<Element> result = elmSeg.elements("codingResult");
				for (Element elmRt : result) {
					StringBuffer sBuffer = new StringBuffer();
					sBuffer.append(elmRt.attributeValue("preset")+" ");
					sBuffer.append(elmRt.attributeValue("OutputSize")+" ");
					sBuffer.append(elmRt.attributeValue("time")+" ");
					sBuffer.append(elmRt.attributeValue("psnr")+" ");
					System.out.println("\t"+sBuffer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}