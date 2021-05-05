package com.gupb.manager.mails;

import com.gupb.manager.model.Round;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.StudentRepository;
import com.gupb.manager.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailSender {

    private final JavaMailSender mailSender;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;

    public MailSender() {
        this.mailSender = new JavaMailSenderImpl();
    }

    public void sendEmailsAfterRound(Round round) {
        List<Team> teams = teamRepository.findByTournament(round.getTournament());
        for (Team team : teams) {
            List<Student> students = studentRepository.findByTeam(team);
            for (Student student : students) {
                sendEmailToStudentAfterRound(student, round);
            }
        }
        sendEmailToCreatorAfterRound(round);
    }

    private void sendEmailToStudentAfterRound(Student student, Round round) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb-manager@noreply.com");
        message.setTo(student.getEmailAddress());
        message.setSubject("Round complete");
        message.setText("The round number " + round.getNumber() + " of tournament " + round.getTournament().getName()
                + "has completed. Go to the manager's website to check the results.");
        mailSender.send(message);
    }

    private void sendEmailToCreatorAfterRound(Round round) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb-manager@noreply.com");
        message.setTo(round.getTournament().getCreatorEmailAddress());
        message.setSubject("Round complete");
        message.setText("The round number " + round.getNumber() + " of tournament " + round.getTournament().getName()
                + "has completed. Go to the manager's website to check the results.");
        mailSender.send(message);
    }
}
