package com.gupb.manager.python;

import com.gupb.manager.model.Round;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.RoundRepository;
import com.gupb.manager.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OutputProcessing {
    private boolean tracebackFound;

    private final StringBuilder errorMessageStringBuilder = new StringBuilder();

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RoundRepository roundRepository;

    private static final Pattern runNumPattern = Pattern.compile("\\d+/\\d+\\s\\[.*]$");

    public void checkAndProcessTqdmProgressBar(String line, Round round) {
        Matcher matcher = runNumPattern.matcher(line);
        if (matcher.find()) {
            String runString = matcher.group();
            int runNumber = Integer.parseInt(runString.replaceAll("\\s\\[.*]$", "").split("/")[0]);
            round.setCompletedRuns(runNumber);
            roundRepository.save(round);
        }
    }

    private static final String scoreBoardLineRegex = "^\\d+\\.\\s+.+:\\s\\d+\\.\\s?";
    private static final String scoreRegex = "[\\d]+\\.[^\\d]*$";
    private static final String botNameRegex = "\\s+.+:";

    public void checkAndProcessScoreLine(String line) {
        if (line.matches(scoreBoardLineRegex)) {
            final int score = parseInt(findAndTrimSubstring(line, scoreRegex, "[^\\d]"));
            final String playerName = findAndTrimSubstring(line, botNameRegex, "^\\s+", ":");
            Optional<Team> teamOptional = teamRepository.findByPlayerName(playerName);
            if (teamOptional.isPresent()) {
                Team team = teamOptional.get();
                team.setTotalPoints(team.getTotalPoints() + score);
                teamRepository.save(team);
            }
        }
    }

    private static final String tracebackLineRegex = "^Traceback \\(most recent call last\\):$";

    public void checkAndProcessTraceback(String line) {
        if (line.matches(tracebackLineRegex)) {
            setErrorFoundFlag();
        }
        if (tracebackFound) {
            errorMessageStringBuilder.append(line).append("\n");
        }
    }

    private void setErrorFoundFlag() {
        tracebackFound = true;
    }

    public String getErrorMessage() {
        return errorMessageStringBuilder.toString();
    }

    public void reset() {
        tracebackFound = false;
        errorMessageStringBuilder.setLength(0);
    }

    /**
     * Find substring matching regex in string and then remove all parts of the found substring, which match
     * listed regular expressions in order.
     * @param string String to search for pattern in.
     * @param regex Regular expression for searched substring.
     * @param partsToDiscardRegex Regular expressions containing info which parts of the found substring have to be trimmed.
     * @return trimmed substring
     */
    private static String findAndTrimSubstring(String string, String regex, String... partsToDiscardRegex) {
        Matcher matcher = Pattern.compile(regex).matcher(string);

        String foundSubstring = "";
        if (matcher.find()) {
            foundSubstring = matcher.group();
            for (String removalRegex : partsToDiscardRegex) {
                foundSubstring = foundSubstring.replaceAll(removalRegex, "");
            }
        }

        return foundSubstring;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
