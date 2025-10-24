package com.mycompany.tp1sabri.llm;

// A record to hold the response from the LLM API
public record Llminteraction(
    String reponseExtraite, // The text response to display
    String questionJson,    // The JSON request sent to the API
    String reponseJson      // The JSON response returned by the API
) {}
