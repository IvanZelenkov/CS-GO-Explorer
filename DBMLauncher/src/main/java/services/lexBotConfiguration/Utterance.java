package services.lexBotConfiguration;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.lexmodelsv2.model.SampleUtterance;

/**
 * A sample utterance that invokes an intent or respond to a slot elicitation prompt.
 * The sample utterance that Amazon Lex uses to build its machine-learning model to recognize intents.
 */
public class Utterance {

    /**
     * Creates a list of SampleUtterance objects using given list of String utterances.
     * @param sampleUtterances List of String utterances for the specified intent.
     * @return List of SampleUtterance objects.
     */
    public static List<SampleUtterance> createSampleUtterances(List<String> sampleUtterances) {
        List<SampleUtterance> sampleUtterancesListObjects = new ArrayList<>();

        for (String utterance : sampleUtterances) {
            sampleUtterancesListObjects.add(SampleUtterance
                    .builder()
                    .utterance(utterance)
                    .build()
            );
        }
        return sampleUtterancesListObjects;
    }

    /**
     * Creates a list of utterances for the "Greeting" intent.
     * @return List of utterances.
     */
    public static List<String> getGreetingIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Hi");
        sampleUtterances.add("Hello");
        sampleUtterances.add("Good morning");
        sampleUtterances.add("Good afternoon");
        sampleUtterances.add("Good evening");
        sampleUtterances.add("Hey");
        sampleUtterances.add("Nova");
        sampleUtterances.add("Yes");
        sampleUtterances.add("Sure");
        sampleUtterances.add("Yeah");
        sampleUtterances.add("Restart");
        sampleUtterances.add("Start over");
        return sampleUtterances;
    }

    /**
     * Creates a list of utterances for the "GetStudent" intent.
     * @return List of utterances.
     */
    public static List<String> getGetStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Get");
        return sampleUtterances;
    }

    /**
     * Creates a list of utterances for the "RemoveStudent" intent.
     * @return List of utterances.
     */
    public static List<String> getRemoveStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Remove");
        return sampleUtterances;
    }

    /**
     * Creates a list of utterances for the "UpdateStudent" intent.
     * @return List of utterances.
     */
    public static List<String> getUpdateStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Update");
        return sampleUtterances;
    }

    /**
     * Creates a list of utterances for the "InsertStudent" intent.
     * @return List of utterances.
     */
    public static List<String> getInsertStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Insert");
        return sampleUtterances;
    }

    /**
     * Creates a list of utterances for the "EndOfConversation" intent.
     * @return List of utterances.
     */
    public static List<String> getEndOfConversationIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("No");
        sampleUtterances.add("No action");
        sampleUtterances.add("Bye");
        sampleUtterances.add("No, thank you");
        return sampleUtterances;
    }
}