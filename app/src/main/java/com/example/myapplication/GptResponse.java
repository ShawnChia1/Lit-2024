package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GptResponse {
    String id;
    String model;
    ArrayList<Result> results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public class Result {
        boolean flagged;
        Categories categories;
        @SerializedName("category_scores")
        Category_scores categoryScores;

        public boolean isFlagged() {
            return flagged;
        }

        public void setFlagged(boolean flagged) {
            this.flagged = flagged;
        }

        public Categories getCategories() {
            return categories;
        }

        public void setCategories(Categories categories) {
            this.categories = categories;
        }

        public Category_scores getCategoryScores() {
            return categoryScores;
        }

        public void setCategoryScores(Category_scores categoryScores) {
            this.categoryScores = categoryScores;
        }
    }

    public class Categories {
        boolean sexual;
        boolean hate;
        boolean harassment;
        @SerializedName("self-harm")
        boolean isSelf_harm;
        @SerializedName("sexual/minors")
        boolean sexual_minors;
        @SerializedName("hate/threatening")
        boolean hate_threatening;
        @SerializedName("violence/graphic")
        boolean violence_graphic;
        @SerializedName("self-harm/intent")
        boolean isSelf_harm_intent;
        @SerializedName("self-harm/instructions")
        boolean isSelf_harm_instructions;
        @SerializedName("harassment/threatening")
        boolean isHarassment_threatening;
        boolean violence;

        public boolean isSexual() {
            return sexual;
        }

        public void setSexual(boolean sexual) {
            this.sexual = sexual;
        }

        public boolean isHate() {
            return hate;
        }

        public void setHate(boolean hate) {
            this.hate = hate;
        }

        public boolean isHarassment() {
            return harassment;
        }

        public void setHarassment(boolean harassment) {
            this.harassment = harassment;
        }

        public boolean isSelf_harm() {
            return isSelf_harm;
        }

        public void setSelf_harm(boolean self_harm) {
            isSelf_harm = self_harm;
        }

        public boolean isSexual_minors() {
            return sexual_minors;
        }

        public void setSexual_minors(boolean sexual_minors) {
            this.sexual_minors = sexual_minors;
        }

        public boolean isHate_threatening() {
            return hate_threatening;
        }

        public void setHate_threatening(boolean hate_threatening) {
            this.hate_threatening = hate_threatening;
        }

        public boolean isViolence_graphic() {
            return violence_graphic;
        }

        public void setViolence_graphic(boolean violence_graphic) {
            this.violence_graphic = violence_graphic;
        }

        public boolean isSelf_harm_intent() {
            return isSelf_harm_intent;
        }

        public void setSelf_harm_intent(boolean self_harm_intent) {
            isSelf_harm_intent = self_harm_intent;
        }

        public boolean isSelf_harm_instructions() {
            return isSelf_harm_instructions;
        }

        public void setSelf_harm_instructions(boolean self_harm_instructions) {
            isSelf_harm_instructions = self_harm_instructions;
        }

        public boolean isHarassment_threatening() {
            return isHarassment_threatening;
        }

        public void setHarassment_threatening(boolean harassment_threatening) {
            isHarassment_threatening = harassment_threatening;
        }

        public boolean isViolence() {
            return violence;
        }

        public void setViolence(boolean violence) {
            this.violence = violence;
        }
    }

    public class Category_scores {
        double sexual;
        double hate;
        double harassment;
        @SerializedName("self-harm")
        double self_harm;
        @SerializedName("sexual/minors")
        double sexual_minors;
        @SerializedName("hate/threatening")
        double hate_threatening;
        @SerializedName("violence/graphic")
        double violence_graphic;
        @SerializedName("self-harm/intent")
        double self_harm_intent;
        double self_harm_instructions;
        @SerializedName("harassment/threatening")
        double harassment_threatening;
        double violence;

        public double getSexual() {
            return sexual;
        }

        public void setSexual(double sexual) {
            this.sexual = sexual;
        }

        public double getHate() {
            return hate;
        }

        public void setHate(double hate) {
            this.hate = hate;
        }

        public double getHarassment() {
            return harassment;
        }

        public void setHarassment(double harassment) {
            this.harassment = harassment;
        }

        public double getSelf_harm() {
            return self_harm;
        }

        public void setSelf_harm(double self_harm) {
            this.self_harm = self_harm;
        }

        public double getSexual_minors() {
            return sexual_minors;
        }

        public void setSexual_minors(double sexual_minors) {
            this.sexual_minors = sexual_minors;
        }

        public double getHate_threatening() {
            return hate_threatening;
        }

        public void setHate_threatening(double hate_threatening) {
            this.hate_threatening = hate_threatening;
        }

        public double getViolence_graphic() {
            return violence_graphic;
        }

        public void setViolence_graphic(double violence_graphic) {
            this.violence_graphic = violence_graphic;
        }

        public double getSelf_harm_intent() {
            return self_harm_intent;
        }

        public void setSelf_harm_intent(double self_harm_intent) {
            this.self_harm_intent = self_harm_intent;
        }

        public double getSelf_harm_instructions() {
            return self_harm_instructions;
        }

        public void setSelf_harm_instructions(double self_harm_instructions) {
            this.self_harm_instructions = self_harm_instructions;
        }

        public double getHarassment_threatening() {
            return harassment_threatening;
        }

        public void setHarassment_threatening(double harassment_threatening) {
            this.harassment_threatening = harassment_threatening;
        }

        public double getViolence() {
            return violence;
        }

        public void setViolence(double violence) {
            this.violence = violence;
        }
    }
}
