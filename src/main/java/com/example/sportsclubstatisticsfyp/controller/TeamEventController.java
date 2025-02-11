package com.example.sportsclubstatisticsfyp.controller;

import com.example.sportsclubstatisticsfyp.model.DTOForms.RegisterTeamDTOForm;
import com.example.sportsclubstatisticsfyp.model.DTOForms.RegisterTeamEventDTOForm;
import com.example.sportsclubstatisticsfyp.model.DTOForms.TeamSessionStatsDTO;
import com.example.sportsclubstatisticsfyp.model.entities.*;
import com.example.sportsclubstatisticsfyp.service.TeamEventService;
import com.example.sportsclubstatisticsfyp.service.TeamService;
import com.example.sportsclubstatisticsfyp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/teamEvents")
public class TeamEventController {
    @Autowired
    private TeamEventService teamEventService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;



    @GetMapping("/addTeamEventForm")
    public ModelAndView displayAddTeamEventForm(Model model) {


        User trainer = userService.getUserByEmail(User.getLoggedInEmail());

        RegisterTeamEventDTOForm newTeamEventForm = new RegisterTeamEventDTOForm();
        Set<Team> listOfTeams= trainer.getTrainersListOfTeams();

        model.addAttribute("listOfTeams", listOfTeams);


        return new ModelAndView("addTeamEventForm", "newTeamEventForm", newTeamEventForm );

    }



    @GetMapping("/viewUsersTeamEvents")
    public ModelAndView viewUsersTeamEvents() {

        User user = userService.getUserByEmail(User.getLoggedInEmail());

        List<TeamEvent> userTeamEvents= new ArrayList<TeamEvent>();

        if(!user.getTrainersListOfTeams().isEmpty()) { //User is a trainer
            userTeamEvents=teamEventService.getUsersTeamEvents(user.getTrainersListOfTeams());
        }
        else { //user is a player
            userTeamEvents = teamEventService.getUsersTeamEvents(user.getListOfTeams());
        }
        return new ModelAndView("viewTeamEvents", "userTeamEvents", userTeamEvents);

    }



    @PostMapping("/addTeamEvent")
    public ModelAndView addTeamEvent(@ModelAttribute("newTeamEventForm") RegisterTeamEventDTOForm newTeamEvent, RedirectAttributes redirectAttributes ) throws ParseException {


        teamEventService.createTeamEvent(newTeamEvent);




        redirectAttributes.addFlashAttribute("successMessage", "Team Event has been created");
        return new ModelAndView("redirect:/teams/viewTeamEvents");

    }

    @GetMapping("/goingToTeamEvent/{id}/{attending}")
    public ModelAndView goingToTeamEvent(@PathVariable("id") Integer id,
                                         @PathVariable("attending") boolean attending,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {

        TeamEvent teamEvent = teamEventService.getTeamEventById(id);

        User user = userService.getUserByEmail(User.getLoggedInEmail());
        teamEventService.goingToTeamEvent(teamEvent,user, attending);
        List<TeamEvent> listOfTeamEvents = teamEventService.getUsersTeamEvents(user.getListOfTeams());
        redirectAttributes.addFlashAttribute("successMessage", "Team event attendance recorded");
        return new ModelAndView("redirect:/teams/viewTeamEvents", "userTeamEvents", listOfTeamEvents );

    }

    @GetMapping("/teamEventDrillDown/{id}")
    public ModelAndView teamEventDrillDown(@PathVariable("id") Integer id, Model model) {

        TeamEvent teamEvent = teamEventService.getTeamEventById(id);

        Integer attendingCount = teamEventService.getAttendingRecordCount(teamEvent,true);
        Integer notAttendingCount = teamEventService.getAttendingRecordCount(teamEvent, false);
        Integer attendanceNotRecordedCount = teamEventService.getNotRecordedAttendanceCount(teamEvent);

        model.addAttribute("attendingCount", attendingCount);
        model.addAttribute("notAttendingCount", notAttendingCount);
        model.addAttribute("attendanceNotRecordedCount", attendanceNotRecordedCount);

        return new ModelAndView("teamEventDrillDown", "teamEvent", teamEvent );

    }

    @GetMapping("/editTeamEventForm/{id}")
    public ModelAndView editTeamEventForm(@PathVariable("id") Integer id, Model model) {


        TeamEvent teamEvent = teamEventService.getTeamEventById(id);

        String usersEmail= User.getLoggedInEmail();
        User trainer = userService.getUserByEmail(usersEmail);

        RegisterTeamEventDTOForm editTeamEventForm =teamEventService.editTeamEventForm(teamEvent);
        Team currentTeam = teamService.getTeamById(editTeamEventForm.getTeamId());
        Set<Team> listOfTeams= trainer.getTrainersListOfTeams();


        model.addAttribute("listOfTeams", listOfTeams);
        model.addAttribute("currentTeam", currentTeam);
        return new ModelAndView("editTeamEventForm", "editTeamEventForm", editTeamEventForm );

    }

    @PostMapping("/editTeamEvent")
    public ModelAndView editTeamEvent(@ModelAttribute("editTeamEventForm") RegisterTeamEventDTOForm editTeamEvent,
                                          RedirectAttributes redirectAttributes) {


        User trainer = userService.getUserByEmail(User.getLoggedInEmail());

         teamEventService.editTeamEvent(editTeamEvent);


        redirectAttributes.addFlashAttribute("successMessage", "Team Event has been updated");
        List<TeamEvent> userTeamEvents=teamEventService.getUsersTeamEvents(trainer.getTrainersListOfTeams());

        return new ModelAndView("redirect:/teamEvents/viewUsersTeamEvents","userTeamEvents", userTeamEvents );

    }
    @GetMapping("/removeTeamEvent/{teamEventId}")
    public ModelAndView removeTeamEvent (RedirectAttributes redirectAttributes,
                                         @PathVariable("teamEventId") int teamEventId
                                          ) {



        TeamEvent teamEvent = teamEventService.getTeamEventById(teamEventId);
        teamEventService.removeTeamEvent(teamEvent);

        User trainer = userService.getUserByEmail(User.getLoggedInEmail());
        List<TeamEvent> listOfTeamEvents = teamEventService.getUsersTeamEvents(trainer.getTrainersListOfTeams());





        redirectAttributes.addFlashAttribute("successMessage", "Team Event has been removed");

        return new ModelAndView("redirect:/teamEvents/viewUsersTeamEvents", "userTeamEvents", listOfTeamEvents );
    }

    @GetMapping("/teamSessionStatsForm/{teamEventId}")
    public String displayTeamStatsForm(@PathVariable("teamEventId") Integer teamEventID,Model model){
        TeamEvent teamEvent = teamEventService.getTeamEventById(teamEventID);
        TeamSessionStatsDTO teamSessionStatsDTO= teamEventService.getNewTeamSessionStatsDTO(teamEvent);

        model.addAttribute("teamSessionStatsDTO", teamSessionStatsDTO);
        return "addTeamSessionStatsForm";
    }
@GetMapping("viewTeamSessionStats/{id}")
public String viewTeamSessionStats(@PathVariable("id") Integer id, Model model) {
        TeamEvent teamEvent = teamEventService.getTeamEventById(id);
        model.addAttribute("teamEvent", teamEvent);
        return "viewTeamSessionStats";
}
    @PostMapping("/createTeamSessionStats")
    public String createTeamSessionStats(Model model,
                                         @ModelAttribute TeamSessionStatsDTO teamSessionStatsDTO,
                                         RedirectAttributes redirectAttributes) {


        teamEventService.createTeamSessionStatsForTeamEvent(teamSessionStatsDTO.getTeamSessionStats());
        redirectAttributes.addFlashAttribute("successMessage", "Team Session Stats has been created");
        return "redirect:/teamEvents/viewUsersTeamEvents";

    }




}
