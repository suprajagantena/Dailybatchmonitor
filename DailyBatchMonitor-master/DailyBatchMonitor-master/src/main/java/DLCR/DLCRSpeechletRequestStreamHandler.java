package DLCR;

import java.util.HashSet;
import java.util.Set;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import DLCR.DLCRSpeechlet;


public final class DLCRSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler{
	private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.d8f29dcf-597c-430a-89c2-aa831ebf6069");
    }

    public DLCRSpeechletRequestStreamHandler() {
        super(new DLCRSpeechlet(), supportedApplicationIds);
    }

}
