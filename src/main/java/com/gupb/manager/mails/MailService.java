package com.gupb.manager.mails;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.StudentRepository;
import com.gupb.manager.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MailService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailsAfterRound(Round round) {
        List<Team> teams = teamRepository.findByTournament(round.getTournament());
        for (Team team : teams) {
            List<Student> students = studentRepository.findByTeams_id(team.getId());
            for (Student student : students) {
                sendEmailToStudentAfterRound(student, round);
            }
        }
        sendEmailToCreatorAfterRound(round);
    }

    private void sendEmailToStudentAfterRound(Student student, Round round) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb.manager@gmail.com");
        message.setTo(student.getEmailAddress());
        message.setSubject("Round complete");
        message.setText("The round number " + round.getNumber() + " of tournament " + round.getTournament().getName()
                + " has completed. Go to the manager's website to check the results.");
        javaMailSender.send(message);
    }

    private void sendEmailToCreatorAfterRound(Round round) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb.manager@gmail.com");
        message.setTo(round.getTournament().getCreator().getEmailAddress());
        message.setSubject("Round complete");
        message.setText("The round number " + round.getNumber() + " of tournament " + round.getTournament().getName()
                + " has completed. Go to the manager's website to check the results.");
        javaMailSender.send(message);
    }

    public void sendEmailToCreatorAfterLibraryRequest(Requirement requirement) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb.manager@gmail.com");
        message.setTo(requirement.getTournament().getCreator().getEmailAddress());
        message.setSubject("New library request");
        message.setText("A request for " + requirement.getPackageInfo() + " for tournament " + requirement.getTournament().getName()
                + " has appeared. Go to the manager's website to accept or reject it.");
        javaMailSender.send(message);
    }

    public void sendEmailsToStudentsAfterRequestStatusChange(Requirement requirement) {
        List<Team> teams = teamRepository.findByTournament(requirement.getTournament());
        for (Team team : teams) {
            List<Student> students = studentRepository.findByTeams_id(team.getId());
            for (Student student : students) {
                sendEmailToStudentAfterRequestStatusChange(student, requirement);
            }
        }
    }

    private void sendEmailToStudentAfterRequestStatusChange(Student student, Requirement requirement) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb.manager@gmail.com");
        message.setTo(student.getEmailAddress());
        message.setSubject("Library request resolved");
        message.setText("A request for " + requirement.getPackageInfo() + " for tournament " + requirement.getTournament().getName()
                + " has changed status to " + requirement.getStatus() + ". Go to the manager's website to see all libraries.");
        javaMailSender.send(message);
    }

    public void sendEmailsToStudentsBeforeRoundBegins(Round round) {
        List<Team> teams = teamRepository.findByTournament(round.getTournament());
        for (Team team : teams) {
            List<Student> students = studentRepository.findByTeams_id(team.getId());
            for (Student student : students) {
                sendEmailToStudentBeforeRoundBegins(student, round);
            }
        }
    }

    private void sendEmailToStudentBeforeRoundBegins(Student student, Round round) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gupb.manager@gmail.com");
        message.setTo(student.getEmailAddress());
        message.setSubject("New round begins soon");
        message.setText("A new round in tournament " + round.getTournament().getName() + " will begin on "
                + round.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + ". Make sure that your bot is ready to fight!");
        if(student.getEmailAddress() != null)
            javaMailSender.send(message);
    }
}
