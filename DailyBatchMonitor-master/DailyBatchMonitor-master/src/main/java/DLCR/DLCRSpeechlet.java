package DLCR;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

//import helloworld.HelloWorldSpeechlet;

public class DLCRSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(DLCRSpeechlet.class);

	@Override
	public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any initialization logic goes here
	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		return getWelcomeResponse();
	}

	@Override
	public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("DLCRIntent".equals(intentName)) {
			return getDLCRResponse();
		} else if ("AboutOfsaa".equals(intentName)) {
			return getofsaaResponse(intentName);
		} else if ("AboutDLCR".equals(intentName)) {
			return getofsaaResponse(intentName);
		} else if ("OFSAAtoCloud".equals(intentName)) {
			return getofsaaResponse(intentName);
		} else if ("OFSAASlowness".equals(intentName)) {
			return getofsaaResponse(intentName);
		} else if ("StartDLCR".equals(intentName)) {
			return getStartDLCRResponse();
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}

	@Override
	public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any cleanup logic goes here
	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWelcomeResponse() {
		String speechText = "Welcome to the Alexa Skills Kit, ask for DLCR status";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("DLCR");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the hello intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws ClassNotFoundException
	 */
	private SpeechletResponse getDLCRResponse() {
		String speechText = getDBdetails();

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("DLCR Status");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the hello intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws ClassNotFoundException
	 */
	private SpeechletResponse getofsaaResponse(String intentname) {
		String speechText = ofsaafunuttenrances(intentname);

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("OFSAA Ent");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	private SpeechletResponse getStartDLCRResponse(){
	 StartDLCR sd = new StartDLCR();
	 String speechText=null;
	try {
		speechText = sd.createStack();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 // Create the Simple card content.
	 SimpleCard card = new SimpleCard();
	 card.setTitle("DLCR Status");
	 card.setContent(speechText);
	
	 // Create the plain text output.
	 PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	 speech.setText(speechText);
	
	 return SpeechletResponse.newTellResponse(speech, card);
	 }

	private String getDBdetails() {
		String batch_status = null;
		String batch_endtime = null;
		String batch_run_id = null;
		String task_id = null;
		String task_start_time = null;
		String dlcr_status = null;
		SimpleDateFormat dttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		AlexaDateUtil ad = new AlexaDateUtil();

		try {
			Class.forName("com.mysql.jdbc.Driver");
//			Connection connection = DriverManager.getConnection(
//					"jdbc:mysql://mgsmysql.c4jswdfl5jxr.us-east-1.rds.amazonaws.com:3306/ofsaa", "matest", "matest123");
			 Connection connection =
			 DriverManager.getConnection("jdbc:mysql://alexamysql.cotmskdly145.us-east-1.rds.amazonaws.com:3306/ofsaa",
			 "matest", "matest123");
			Statement stmt = connection.createStatement();
			ResultSet rs;
			String _tbl = "use ofsaa";
			stmt.executeQuery(_tbl);
			String _tblbatch = "select * from BATCH_RUN order by ID desc LIMIT 1";
			rs = stmt.executeQuery(_tblbatch);
			while (rs.next()) {
				batch_status = rs.getString("V_BATCH_STATUS");
				batch_run_id = rs.getString("V_BATCH_RUN_ID");
				batch_endtime = rs.getString("V_END_TIME");
			}

			if (batch_status.equals("O")) {
				System.out.println("In condition");
				String _tbltask = "SELECT * from BATCH_TASK_STATUS WHERE V_BATCH_RUN_ID='" + batch_run_id
						+ "' AND V_TASK_STATUS='O'";
				rs = stmt.executeQuery(_tbltask);
				while (rs.next()) {
					task_id = rs.getString("V_TASK_ID");
					task_start_time = rs.getString("V_START_TIME");
				}
				dlcr_status = "DLCR is Ongoing and is running " + task_id + " started at "
						+ ad.getFormattedDate(dttime.parse(task_start_time)) + " "
						+ ad.getFormattedTimeAmPm(dttime.parse(task_start_time));
			} else {
				SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
				if (batch_endtime.split(" ")[0].equals(dt.format(new Date()))) {
					dlcr_status = "DLCR was completed on " + ad.getFormattedDate(dttime.parse(batch_endtime)) + " "
							+ ad.getFormattedTimeAmPm(dttime.parse(batch_endtime));
				} else {
					dlcr_status = "DLCR has not started";
				}
			}
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dlcr_status;

	}

	private String ofsaafunuttenrances(String intentname) {
		String[] aboutofsaa = { "I don’t have an opinion on that", "OFSAA from Oracle, hmm" };
		String[] aboutdlcr = { "I would suggest you cancel your evening plans",
				"I have ordered pizza for dinner as you will be spending night at work" };
		Random rnd = new Random();
		String answer = null;
		switch (intentname) {
		case "AboutOfsaa":
			answer = aboutofsaa[rnd.nextInt(2)];
			break;
		case "AboutDLCR":
			answer = aboutdlcr[rnd.nextInt(2)];
			break;
		case "OFSAAtoCloud":
			answer = "Not sure, you should ask Alan Smithers";
			break;
		case "OFSAASlowness":
			answer = "Someone spilled their beer on the exa data server again";
			break;
		default:
			break;
		}
		return answer;
	}

}
