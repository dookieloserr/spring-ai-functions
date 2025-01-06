package guru.springframework.springaifunctions.services;


import guru.springframework.springaifunctions.functions.StockQuoteFunction;
import guru.springframework.springaifunctions.functions.WeatherServiceFunction;
import guru.springframework.springaifunctions.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.ModelOptionsUtils;
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
                           .responseConverter(response -> {
                               String schema = ModelOptionsUtils.getJsonSchema(WeatherResponse.class, false);
                               String json = ModelOptionsUtils.toJsonString(response);
                               return schema + "\n" + json;
                           })
                     .build()))
               .build();


        Message userMessage = new PromptTemplate(question.question()).createMessage();

        var response = openAiChatModel.call(new Prompt(List.of(systemMessage,userMessage), promptOptions));
        return new Answer(response.getResult().getOutput().getContent());
    }

    @Override
    public Answer getStockPrice(Question question) {
        var promptOptions = OpenAiChatOptions.builder()
                .functionCallbacks(List.of(FunctionCallback.builder()
                        .function("CurrentStockPrice", new StockQuoteFunction(apiNinjasKey))
                        .description("Get the current stock price for a stock symbol")
                                .inputType(StockPriceRequest.class)
                        .responseConverter((response) -> {
                            String schema = ModelOptionsUtils.getJsonSchema(StockPriceResponse.class, false);
                            String json = ModelOptionsUtils.toJsonString(response);
                            return schema + "\n" + json;
                        })
                        .build()))
                .build();

        Message userMessage = new PromptTemplate(question.question()).createMessage();
        Message systemMessage = new SystemPromptTemplate("You are an agent which returns back a stock price for the given stock symbol (or ticker) include the last updated epoch time, convert epoch time to DateTime").createMessage();

        var response = openAiChatModel.call(new Prompt(List.of(userMessage, systemMessage), promptOptions));

        return new Answer(response.getResult().getOutput().getContent());
    }
}
