package com.example.sportsclubstatisticsfyp.service;

import com.example.sportsclubstatisticsfyp.model.DTOForms.RegisterMemberDTOForm;
import com.example.sportsclubstatisticsfyp.model.entities.Role;
import com.example.sportsclubstatisticsfyp.model.entities.User;
import com.example.sportsclubstatisticsfyp.model.repositories.RoleRepository;
import com.example.sportsclubstatisticsfyp.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //retrieve a customer account from the DB by email address
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Club member not found with email: " + email));

        /*
        Get a list of all the roles associated with the User and convert
        them into a list of GrantedAuthority objects.
        If the authenticated user has two roles, then there will be two GrantedAuthority objects
        in this list etc..
         */
        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                .collect(Collectors.toList());


        //Create and return a new instance of User, this is a class provided by Spring
        //Security that implements the UserDetails interface
        return new org.springframework.security.core.userdetails.User(
                member.getEmail(), //the users email in our app
                member.getPassword(), //the hashed pwd
                //four boolean values for user account status to indicate whether the account is
                true, //enabled
                true, //not expired
                true, //credentials are not expired
                true, //account it not locked
                authorities
        );
    }

    /*
    This method converts a set of Role objects into a collection of GrantedAuthority
    objects prefixed with "ROLE_" for use in authorisation.
    */
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        Set<GrantedAuthority> authorities = new HashSet();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));

        }
        return authorities;
    }

    public User getUserByEmail(String email) {
        Optional<User> member = userRepository.findByEmail(email);
        if(member.isPresent())
        {
            return member.get();
        }else {
            return null;
        }
    }

    public User createUser(RegisterMemberDTOForm member) throws ParseException {

        User newClubMember=new User();
        newClubMember.setFirstName(member.getFirstName());
        newClubMember.setLastName(member.getLastName());
        newClubMember.setDateOfBirth(member.getDateOfBirth());
        newClubMember.setPassword(member.getPassword());
        newClubMember.setEmail(member.getEmail());

        for(Long roleId : member.getRoles()) {
            Role role = roleRepository.getById(roleId);
            newClubMember.getRoles().add(role);
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        newClubMember.setPassword(encoder.encode(member.getPassword()));
        newClubMember.setGender(member.getGender());
        newClubMember.setDateRegistered(new Date());
        return userRepository.save(newClubMember);
    }

    public List<User> getAllTrainers(){
        List<User> users = userRepository.findAll();
        List<User> trainers = new ArrayList<>();
        for(User user : users) {
            Set<Role> userRoles = user.getRoles();
            for(Role role : userRoles) {
                if(role.getRole().equals("TRAINER")) {
                    trainers.add(user);
                }
            }
        }
        return trainers;

    }

    public List<User> getAllPlayers(){
        List<User> users = userRepository.findAll();
        List<User> players = new ArrayList<>();
        for(User user : users) {
            Set<Role> userRoles = user.getRoles();
            for(Role role : userRoles) {
                if(role.getRole().equals("PLAYER")) {
                    players.add(user);
                }
            }
        }
        return players;

    }

    public List<User> getAvailablePlayers(Set<User> activeTeamMembers){
        List<User> players = this.getAllPlayers();
        List<User> availablePlayers = new ArrayList<>();
        for(User user : players) {
            if(!activeTeamMembers.contains(user)) {
                availablePlayers.add(user);
            }
        }
        return availablePlayers;

    }

    public User getUserById (Integer id){
        Optional<User> member = userRepository.findById(id);
        if(member.isPresent())
        {
            return member.get();
        }else {
            return null;
        }
    }

    public List<User> getAllMaleClubMembers() {
        return userRepository.getAllMaleClubMembers();


    }

    public List<User> getAllFemaleClubMembers() {

        return userRepository.getAllFemaleClubMembers();


    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Integer getUsersAgeCountBetweenMinAgeToMaxYears(int minAge , int maxAge){
        List<User> users = userRepository.findAll();

        Integer userBetweenMinAgeToMaxAgeYears = 0;
        LocalDate currentDate = LocalDate.now();

        for (User user : users) {
            Period period = Period.between(user.getDateOfBirth(), currentDate);

            if (period.getYears() >= minAge && period.getYears() <= maxAge) {
                userBetweenMinAgeToMaxAgeYears++;
            }
        }
        return userBetweenMinAgeToMaxAgeYears;
    }


}
