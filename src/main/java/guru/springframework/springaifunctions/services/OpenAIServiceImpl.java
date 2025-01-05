package guru.springframework.springaifunctions.services;


import guru.springframework.springaifunctions.functions.WeatherServiceFunction;
import guru.springframework.springaifunctions.model.Answer;
import guru.springframework.springaifunctions.model.Question;
import guru.springframework.springaifunctions.model.WeatherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@RequiredArgsConstructor
@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("${sfg.aiapp.apiNinjasKey}")
    private String apiNinjasKey;

    //final ChatModel chatModel;

    final OpenAiChatModel openAiChatModel;

    @Override
    public Answer getAnswer(Question question) {
        PromptTemplate systemPromptTemplate = new SystemPromptTemplate("You are a weather service, " +
                "where you will return to the user the current city temperature in Fahrenheits degrees not in Celsius," +
                "Formula to convert from Celsius to Fahrenheits is (0°C * 9/5) + 32 "+
                "Example: 12°C   =  12°C * (9/5) = 53.6°F"+
                " also you will return the humidity of the city");

        Message systemMessage = systemPromptTemplate.createMessage();

       var promptOptions = OpenAiChatOptions.builder()
               .functionCallbacks(List.of(FunctionCallback.builder()
                           .function("CurrentWeather",new WeatherServiceFunction(apiNinjasKey))
                           .description("Get the current weather for a location")
                           .inputType(WeatherRequest.class)
                       .build()))
               .build();


        Message userMessage = new PromptTemplate(question.question()).createMessage();

        var response = openAiChatModel.call(new Prompt(List.of(systemMessage,userMessage), promptOptions));
        return new Answer(response.getResult().getOutput().getContent());
    }
}
