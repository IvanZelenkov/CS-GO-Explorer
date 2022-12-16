package services.lexBotConfiguration;

import software.amazon.awssdk.services.lexmodelsv2.model.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an option that is displayed in an application.
 */
public class Buttons {

    /**
     * Creates buttons representing text and values of the custom-created slot type.
     * @return List of buttons.
     */
    public static List<Button> getButtons(List<String> buttonNamesList) {
        List<Button> buttonList = new ArrayList<>();

        for (String buttonName: buttonNamesList) {
            Button button = Button
                    .builder()
                    .text(buttonName)
                    .value(buttonName)
                    .build();

            buttonList.add(button);
        }
        return buttonList;
    }
}