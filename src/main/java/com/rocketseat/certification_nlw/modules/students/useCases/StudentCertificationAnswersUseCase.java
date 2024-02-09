package com.rocketseat.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.certification_nlw.modules.questions.entities.QuestionEntity;
import com.rocketseat.certification_nlw.modules.questions.repositories.QuestionReponsitory;
import com.rocketseat.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.rocketseat.certification_nlw.modules.students.entities.AnswerCertificationsEntity;
import com.rocketseat.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.rocketseat.certification_nlw.modules.students.entities.StudentEntity;
import com.rocketseat.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.rocketseat.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionReponsitory questionReponsitory;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) {

        //Search for alternative questions and validate whether the answer correct or incorrect
        List<QuestionEntity> questionsEntity = questionReponsitory.findByTechnology(dto.getTechnology());
        List<AnswerCertificationsEntity> answersCertifications = new ArrayList<>();

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

           var answerCertificationsEntity = AnswerCertificationsEntity.builder()
           .answerID(questionAnswer.getAlternativeID())
           .questionID(questionAnswer.getQuestionID())
           .isCorrect(questionAnswer.isCorrect()).build();

           answersCertifications.add(answerCertificationsEntity);

        });

        //Checks if student exists with email
        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentID;
        if (student.isEmpty()) {
            var studentCreated =StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        }else {
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity
        = CertificationStudentEntity.builder()
        .technology(dto.getTechnology())
        .studentID(studentID)
        .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated ;
    }
}
