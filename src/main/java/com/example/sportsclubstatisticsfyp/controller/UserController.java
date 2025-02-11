package com.example.sportsclubstatisticsfyp.controller;

import com.example.sportsclubstatisticsfyp.model.DTOForms.RegisterMemberDTOForm;
import com.example.sportsclubstatisticsfyp.model.entities.User;
import com.example.sportsclubstatisticsfyp.model.entities.Role;
import com.example.sportsclubstatisticsfyp.model.repositories.UserRepository;
import com.example.sportsclubstatisticsfyp.service.RoleService;
import com.example.sportsclubstatisticsfyp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @GetMapping("/registerMemberForm")
    public ModelAndView displayRegisterMemberForm(Model model) {
        RegisterMemberDTOForm newMemberForm = new RegisterMemberDTOForm();
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("roles", roles);

        return new ModelAndView("registerMemberForm", "newMemberForm", newMemberForm );

    }

    @PostMapping("/registerMember")
    public ModelAndView addClubMember(@Valid @ModelAttribute("newMemberForm") RegisterMemberDTOForm clubMember,
                                      RedirectAttributes redirectAttributes ) throws ParseException {


        userService.createUser(clubMember);




            redirectAttributes.addFlashAttribute("message", "Club member account has been created");
            return new ModelAndView("redirect:/");

        }


    @GetMapping("/generateClubMemberStats")
    public ModelAndView getClubMemberStats(Model model){
    List<User> maleClubMembers= userService.getAllMaleClubMembers();
    List<User> femaleClubMembers=userService.getAllFemaleClubMembers();

    model.addAttribute("maleClubMembersCount",maleClubMembers.size());
    model.addAttribute("femaleClubMembersCount",femaleClubMembers.size());

    Integer zeroTo24Years = userService.getUsersAgeCountBetweenMinAgeToMaxYears(0,24);
    Integer twentyFiveTo49Years = userService.getUsersAgeCountBetweenMinAgeToMaxYears(25,49);
    Integer fiftyTo74years = userService.getUsersAgeCountBetweenMinAgeToMaxYears(50,74);
    Integer seventyFiveTo100years = userService.getUsersAgeCountBetweenMinAgeToMaxYears(75,100);

    model.addAttribute("zeroTo24Years",zeroTo24Years);
    model.addAttribute("twentyFiveTo49Years",twentyFiveTo49Years);
    model.addAttribute("fiftyTo74Years",fiftyTo74years);
    model.addAttribute("seventyFiveTo100Years",seventyFiveTo100years);
    return new ModelAndView("clubMemberStats");
    }
}



