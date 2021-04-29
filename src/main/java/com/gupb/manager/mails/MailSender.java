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
                sendEmailToStudentAfterRound(student);
            }
        }
        sendEmailToCreatorAfterRound(round.getTournament());
    }

    private void sendEmailToStudentAfterRound(Student student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nigerianprince@noscam.com");
        message.setTo(student.getEmailAddress());
        message.setSubject("NO SCAM!!!");
        message.setText("It seems you are the only living relative of a nigerian prince.\n" +
                "Send me 10 bitcoins and after an hour he will send you 20 bitcoins back.");
        mailSender.send(message);
    }

    private void sendEmailToCreatorAfterRound(Tournament tournament) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nigerianprince@noscam.com");
        message.setTo(tournament.getCreatorEmailAddress());
        message.setSubject("NO SCAM!!!");
        message.setText("It seems you are the only living relative of a nigerian prince.\n" +
                "Send me 10 bitcoins and after an hour he will send you 20 bitcoins back.");
        mailSender.send(message);
    }
}
