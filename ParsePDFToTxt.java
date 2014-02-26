import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import java.io.*;

public class ParsePDFToTxt{

	public static void main(String args[]) {

	    PDFParser parser = null;
	    PDDocument pdDoc = null;
	    COSDocument cosDoc = null;
	    PDFTextStripper pdfStripper;
	    File inputDir = null;
	    File outputDir = null;
	    String parsedText;	

	    //get args	    
	    if (args.length < 2){
	    	System.out.println("Usage: java ParsePDFToTxt <pdf directory> <text output directory>");
	    	return;
	    }else{
	    	//System.out.println(args[0]);
	    	//System.out.println(args[1]);

	    	//check if the input directory exists	    	
	    	inputDir = new File(args[0]);
	    	if (!inputDir.exists() || !inputDir.isDirectory()){
	    		System.out.println("ERROR: Input directory does not exist!");
	    		return;
	    	}

	    	//check if output directory exists, if not, create it
	    	outputDir = new File(args[1]);
	    	if (!outputDir.exists() || !outputDir.isDirectory()){
	    		outputDir.mkdir();
	    	}

	    }

	    //get all files from  from inputDir
	    int count =0;
	    File files[] = inputDir.listFiles();
	    for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory()){
				String filename = files[i].getName();
				System.out.println("Processing " + filename );
                if(filename.toLowerCase().endsWith("pdf")){                	
                	//input file
                	File ifn = new File(inputDir.getPath(), filename);
                	//gen the output filename                	
                	File ofn = new File(outputDir.getPath(), filename.split("\\.")[0] + ".txt"); // . in regular expr means any char, need \\. to split w.r.t. "."                	
                	//System.out.println(ofn.getPath());
                	try {
				        parser = new PDFParser(new FileInputStream(ifn));
				        parser.parse();
				        cosDoc = parser.getDocument();
				        pdfStripper = new PDFTextStripper();
				        pdDoc = new PDDocument(cosDoc);
				        //get the page number
				        int pageNum = pdDoc.getNumberOfPages();
				        //System.out.println("pageNum: " + pageNum);
				        //create file	        
			  			Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(ofn), "UTF8"));
				        
				        //for each page
				        for(int page=1; page<=pageNum; page++){
				        	//get page
				        	//System.out.println(page);
				        	pdfStripper.setStartPage(page);
						    pdfStripper.setEndPage(page);			    
						    String contents = pdfStripper.getText(pdDoc);
						    //remove the page number of each page
						    contents = contents.substring(0, contents.lastIndexOf(" ")) + "\r\n\r\n";
						    
							out.append("=====P." + page + "=====\r\n");
							out.append(contents);

				        }				        

						out.flush();
						out.close();

						try {
				            if (cosDoc != null)
				                cosDoc.close();
				            if (pdDoc != null)
				                pdDoc.close();
				        } catch (Exception e2) {
				            e2.printStackTrace();
				        }


				    } catch (Exception e) {
				        e.printStackTrace();
				        try {
				            if (cosDoc != null)
				                cosDoc.close();
				            if (pdDoc != null)
				                pdDoc.close();
				        } catch (Exception e1) {
				            e1.printStackTrace();
				        }

				    }
                	count++;
                }

            }
        }   
        
        System.out.println("Done! " + count + " pdf files converted to text.");
	    
	}
}