package fi.utu.ville.exercises.template;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.gwt.json.client.JSONException;

import elemental.json.JsonFactory;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import elemental.json.impl.JreJsonObject;
import fi.utu.ville.exercises.model.ExerciseException;
import fi.utu.ville.exercises.model.PersistenceHandler;
import fi.utu.ville.standardutils.AbstractFile;
import fi.utu.ville.standardutils.TempFilesManager;
import fi.utu.ville.standardutils.XMLHelper;

public final class TemplateXMLHandler implements
		PersistenceHandler<TemplateExerciseData, TemplateSubmissionInfo> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3150033834959652936L;
	public static final TemplateXMLHandler INSTANCE = new TemplateXMLHandler();
	
	private TemplateXMLHandler() {
	
	}
	
	@Override
	public TemplateExerciseData loadExerData(byte[] dataPres,
			TempFilesManager tempManager, ByRefLoader matLoader)
					throws ExerciseException {
					
		TemplateExerciseData res = null;
		
		try {
			
			Document doc = XMLHelper.parseFromBytes(dataPres);
			
			doc.getDocumentElement().normalize();
			
			String parsedQuestion = doc.getDocumentElement().getAttribute(
					"question");
			AbstractFile imgFile = null;
			if (doc.getDocumentElement().hasAttribute("img")) {
				imgFile = matLoader.loadByReference(doc.getDocumentElement()
						.getAttribute("img"));
			}
			
			res = new TemplateExerciseData(parsedQuestion, imgFile);
			
		} catch (ParserConfigurationException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_LOAD_ERROR, e);
		} catch (SAXException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_LOAD_ERROR, e);
		} catch (IOException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_LOAD_ERROR, e);
		}
		
		return res;
	}
	
	@Override
	public byte[] saveExerData(TemplateExerciseData toWrite,
			TempFilesManager tempManager, ByRefSaver matSaver)
					throws ExerciseException {
					
		byte[] res = null;
		try {
			Document doc = XMLHelper.createEmptyDocument();
			
			Element root = doc.createElement("template-exercise");
			
			doc.appendChild(root);
			
			root.setAttribute("question", toWrite.getQuestion());
			
			if (toWrite.getImgFile() != null) {
				String imgRef = matSaver.saveByReference(toWrite.getImgFile());
				root.setAttribute("img", imgRef);
			}
			
			res = XMLHelper.xmlToBytes(doc);
			
		} catch (ParserConfigurationException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_WRITE_ERROR, e);
		} catch (TransformerConfigurationException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_WRITE_ERROR, e);
		} catch (TransformerException e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_WRITE_ERROR, e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new ExerciseException(
					ExerciseException.ErrorType.EXER_WRITE_ERROR, e);
		}
		return res;
	}
	
	@Override
	public byte[] saveSubmission(TemplateSubmissionInfo subm,
			TempFilesManager tempManager) throws ExerciseException {
		try {
			JsonFactory factory = new JreJsonFactory();
			JsonObject json = new JreJsonObject(factory);
			json.put("answer", subm.getAnswer());
			
			return (json.toString()).getBytes("UTF-8");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			// won't happen
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public TemplateSubmissionInfo loadSubmission(byte[] dataPres,
			boolean forStatGiver, TempFilesManager tempManager)
					throws ExerciseException {
		TemplateSubmissionInfo res = null;
		try {
			JsonFactory factory = new JreJsonFactory();
			String jsonStr = new String(dataPres, "UTF-8");
			JsonObject json = factory.parse(jsonStr);
			res = new TemplateSubmissionInfo(json.getString("answer"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// Won't happen
			e.printStackTrace();
		}
		
		return res;
		
	}
}
