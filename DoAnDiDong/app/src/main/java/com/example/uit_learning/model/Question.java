package com.example.uit_learning.model;

public class Question {
    private String Quetion;
    private String oA;
    private String oB;
    private String oC;
    private String oD;
    private String asd;
    private String questionImage;
    private String answer;
    private int isImageQuestion;

    Question(){}

<<<<<<< Updated upstream
    public Question(String quetion, String oA, String oB, String oC, String oD, String asd, String questionImage, int isImageQuestion) {
        Quetion = quetion;
=======
    public Question(String ques, String oA, String oB, String oC, String oD, String asd, String questionImage, String answer, int isImageQuestion) {
        this.ques = ques;
>>>>>>> Stashed changes
        this.oA = oA;
        this.oB = oB;
        this.oC = oC;
        this.oD = oD;
        this.asd = asd;
        this.questionImage = questionImage;
        this.answer = answer;
        this.isImageQuestion = isImageQuestion;
    }

<<<<<<< Updated upstream
    public String getQuetion() {
        return Quetion;
=======
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQues() {
        return ques;
>>>>>>> Stashed changes
    }

    public void setQuetion(String quetion) {
        Quetion = quetion;
    }

    public String getoA() {
        return oA;
    }

    public void setoA(String oA) {
        this.oA = oA;
    }

    public String getoB() {
        return oB;
    }

    public void setoB(String oB) {
        this.oB = oB;
    }

    public String getoC() {
        return oC;
    }

    public void setoC(String oC) {
        this.oC = oC;
    }

    public String getoD() {
        return oD;
    }

    public void setoD(String oD) {
        this.oD = oD;
    }

    public String getAsd() {
        return asd;
    }

    public void setAsd(String asd) {
        this.asd = asd;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public int getIsImageQuestion() {
        return isImageQuestion;
    }

    public void setIsImageQuestion(int isImageQuestion) {
        this.isImageQuestion = isImageQuestion;
    }
}
