package com.mycompany.tp1sabri.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mycompany.tp1sabri.llm.JsonUtilPourGemini;
import com.mycompany.tp1sabri.llm.Llminteraction;
import com.mycompany.tp1sabri.llm.RequeteException;

@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    private boolean debug = false;
    private String texteRequeteJson;
    private String texteReponseJson;

    @Inject
    private FacesContext facesContext;

    @Inject
    private JsonUtilPourGemini jsonUtil;

    public Bb() {}

  
    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return roleSystemeChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }
public String getRoleSysteme() {
    return roleSysteme;
}

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public void toggleDebug() {
        this.setDebug(!isDebug());
    }

    // --- Main methods ---
    public String envoyer() {
        if (question == null || question.isBlank()) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "Texte question vide",
                                 "Il manque le texte de la question"));
            return null;
        }

        // Send system role if first question
        if (this.conversation.isEmpty()) {
            jsonUtil.setSystemRole(roleSysteme);
            this.roleSystemeChangeable = false;
        }

        try {
            Llminteraction interaction = jsonUtil.envoyerRequete(question);
            this.reponse = interaction.reponseJson();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseExtraite();
        } catch (Exception e) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "Problème de connexion avec l'API du LLM",
                                 "Problème de connexion avec l'API du LLM : " + e.getMessage()));
        }

        afficherConversation();
        return null;
    }

    public String nouveauChat() {
        return "index";
    }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question)
                         .append("\n== Serveur:\n").append(reponse).append("\n");
    }

    // --- Roles systeme ---
    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                You are a helpful assistant. You help the user to find the information they need.
                If the user type a question, you answer it.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user type a French text, you translate it into English.
                If the user type an English text, you translate it into French.
                If the text contains only one to three words, give some examples of usage of these words in English.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));
            
            role = """
                Your are a travel guide. If the user type the name of a country or of a town,
                you tell them what are the main places to visit in the country or the town
                are you tell them the average price of a meal.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
            
            role = """
            Coach motivationnel : Réponds toujours de manière motivante et encourageante.
            Encourage l’utilisateur, donne des conseils pratiques et inspire-le à persévérer.
            """;
            this.listeRolesSysteme.add(new SelectItem(role, "Coach motivationnel"));
        }

        return this.listeRolesSysteme;
    }
}
