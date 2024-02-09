package com.rocketseat.certification_nlw.modules.students.useCases;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.certification_nlw.modules.questions.entities.QuestionEntity;
import com.rocketseat.certification_nlw.modules.questions.repositories.QuestionReponsitory;
import com.rocketseat.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private QuestionReponsitory questionReponsitory;

    public StudentCertificationAnswerDTO execute(StudentCertificationAnswerDTO dto) {

        //Search for alternative questions and validate whether the answer correct or incorrect
        List<QuestionEntity> questionsEntity = questionReponsitory.findByTechnology(dto.getTechnology());

        dto.getQuestionsAnswers()
        .stream().forEach(questionAnswer -> {
           var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID()));

           var findCorrectAlternative = question.findFirst().get().getAlternatives().stream()
           .filter(alternative -> alternative.isCorrect()).findFirst().get();

           if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                questionAnswer.setCorrect(true);
           } else {
            questionAnswer.setCorrect(false);
           }
        });

        return dto;
    }
}
