package ym.simulation.cloud;

import java.io.FileWriter;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class WriteTrace {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter("./video.xml"),
					format);

//			Document doc = createDoc();
			Document doc = getFakeTrace();
			writer.write(doc);
			writer.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Document getFakeTrace() {
		Document doc = DocumentHelper.createDocument();
		//root of the data
		Element root = doc.addElement("EncodingTrace");
		
		int videoNum = 1000;
		int presetNum = 3;
		String presets[] = {"medium","fast","ultrafast"};
		double avgCodingTime = 10; // five minutes
		// add video segment
		for (int i = 0; i < videoNum; i++) {
			double codingTime = MyRandom.exponential(avgCodingTime)+1;
			int origSize = (int) (codingTime * 100);
			Element videoSegment = root.addElement("VideoSegment");
			// raw video file size
			videoSegment.addAttribute("videoName", "fakeVideo_" + i)
					.addAttribute("OrigSize", String.valueOf(origSize));
			
			for (int j = 0; j < presetNum; j++) {
				// output file size under preset
				int outputSize = (int)(codingTime * 20*(j+1));
				// coding time under preset
				double codingTime_preset = codingTime/(j+1);
				// psnr under preset
				double psnr = codingTime/(j+1);
				
				videoSegment
						.addElement("codingResult")
						.addAttribute("preset", presets[j])
						.addAttribute("OutputSize",
								String.valueOf( outputSize) )
						.addAttribute("time", String.valueOf(codingTime_preset))
						.addAttribute("psnr", String.valueOf(psnr));
			}

		}
		
		// return end
		return doc;
	}
	
	public static Document createDoc() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("EncodingTrace");
		// add a video segment
		Element videoSegment = root.addElement("VideoSegment")
				.addAttribute("videoName", "vd" + 1)
				.addAttribute("OrigSize", String.valueOf(4321));

		// add a coding result
		videoSegment.addElement("codingResult")
				.addAttribute("preset", "fast")
				.addAttribute("OutputSize", String.valueOf(324))
				.addAttribute("time", String.valueOf(4.5))
				.addAttribute("psnr", String.valueOf(53.12));
		// add a coding result
		videoSegment.addElement("codingResult")
				.addAttribute("preset", "medium")
				.addAttribute("OutputSize", String.valueOf(424))
				.addAttribute("time", String.valueOf(6.5))
				.addAttribute("psnr", String.valueOf(54.12));
		
		// second video
		videoSegment = root.addElement("VideoSegment")
				.addAttribute("videoName", "vd" + 2)
				.addAttribute("OrigSize", String.valueOf(8828));

		// add a coding result
		videoSegment.addElement("codingResult")
				.addAttribute("preset", "fast")
				.addAttribute("OutputSize", String.valueOf(954))
				.addAttribute("time", String.valueOf(10.5))
				.addAttribute("psnr", String.valueOf(56.50));

		return doc;
	}
}
