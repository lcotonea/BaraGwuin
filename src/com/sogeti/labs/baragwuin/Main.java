package com.sogeti.labs.baragwuin;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * User: Loïc Cotonéa
 * Date: 23/03/14 - 12:16
 * Description: A sample main to demonstrate TTS and Speech recognizer
 * Use: FreeTTS + MBROLA + CMUSphinx 4
 * Licence: GPL v3
 * Copyright: Loïc Cotonéa
 */
public class Main {

    private static final String VOICE_NAME = "mbrola_us1";
    private static final String SPHINX_CONFIG = "../../../../sphinx_config.xml";
    private static final String MBROLA_BASE = "../../../../mbrola";

    public static void main(String[] args) {
        ConfigurationManager cm;

        if (args.length > 0) {
            cm = new ConfigurationManager(args[0]);
        } else {
            cm = new ConfigurationManager(Main.class.getResource(SPHINX_CONFIG));
        }

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();

        // start the microphone or exit if the programm if this is not possible
        Microphone microphone = (Microphone) cm.lookup("microphone");
        microphone.startRecording();

        // The VoiceManager manages all the voices for FreeTTS. We use MBROLA to get a pretty voice.
        System.setProperty("mbrola.base", Main.class.getResource(MBROLA_BASE).getPath());
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice helloVoice = voiceManager.getVoice(VOICE_NAME);
        if (helloVoice == null) {
            System.err.println(
                    "Cannot find a voice named "
                            + VOICE_NAME + ".  Please specify a different voice."
            );
            System.exit(1);
        }
        helloVoice.allocate();

        // loop the recognition until the program exits.
        helloVoice.speak("Hello! It's cool. +OK+?");
        while (true) {
            helloVoice.speak("I listen you.");
            Result result = recognizer.recognize();

            if (result != null && !result.getBestFinalResultNoFiller().trim().isEmpty()) {
                String resultText = result.getBestFinalResultNoFiller();

                if (resultText.equals("quit")) {
                    break;
                }

                helloVoice.speak("You said " + resultText);

                // we can maybe do something now with this resultText.

            } else {
                helloVoice.speak("I can't understand what you said.");
            }
        }
        helloVoice.speak("see you soon");

        microphone.stopRecording();
        helloVoice.deallocate();
    }
}
