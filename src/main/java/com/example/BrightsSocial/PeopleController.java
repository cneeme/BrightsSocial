package com.example.BrightsSocial;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
public class PeopleController {

    @Autowired
    PeopleService peopleService;

    @Autowired
    MessageService messageService;


    String rightUser = "admin";
    String rightPassword = "123456";
    @Autowired
    private PeopleRepository peopleRepository;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        boolean loggedIn = Boolean.TRUE == session.getAttribute("loggedIn");
        if (loggedIn) {
            return "redirect:/myprofile";
        }
        return "home";

    }

    @PostMapping("/")
    public String login(HttpSession session, @RequestParam String username, @RequestParam String password) {
       List<People> allPeople = peopleService.getAllPeople();
        System.out.println(allPeople);
        for (People people : allPeople) {
            if (username.equals(people.getUsername().toLowerCase()) && password.equals(people.getPasscode())) {
                String cap = username.substring(0, 1).toUpperCase() + username.substring(1);
                session.setAttribute("username", cap);
                session.setAttribute("password", password);
                session.setAttribute("loggedIn", Boolean.TRUE);
                session.setAttribute("people", people);
                return "redirect:/myprofile";
            }
        }
        return "redirect:/";

    }

    @GetMapping("/login")
    public String login2(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        String user = (String) session.getAttribute("user");


        if (username != null && password != null) {
            return "redirect:/myprofile";
        }

        return "redirect:/login";
    }

    @GetMapping("/myprofile")
    public String name(HttpSession session, Model model) {
        List<People> allPeople = peopleService.getAllPeople();
        List<Message> allMessages = messageService.getAllMessages();
        List<People> usersToShow = new ArrayList<>();
        String username = (String) session.getAttribute("username");
        People loggedInPeople = peopleService.findUser(username);
        for (People people : allPeople) {
            if (!people.getUsername().equals(username)) {
                usersToShow.add(people);
            }
        }
        model.addAttribute("people", loggedInPeople);
        model.addAttribute("message", new Message());
        model.addAttribute("users", usersToShow);
        model.addAttribute("messages", allMessages);
        return "myprofile";

    }

    @PostMapping("/myprofile")
    public String sendMessage(HttpSession session,@ModelAttribute Message message) {
        String name = (String) session.getAttribute("username");

        LocalDateTime time = LocalDateTime.now();
            Message m = new Message( message.getId(), message.getMessageBody(),name,time);
            messageService.saveMessage(m);



        return "redirect:/myprofile";

    }

    @GetMapping("/register")
    public String registerReturn(Model model) {
        model.addAttribute("people", new People());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute People people, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        model.addAttribute("people", people);
        session.setAttribute("people", people);
        session.setAttribute("username", people.getUsername());
        session.setAttribute("loggedIn", Boolean.TRUE);
        peopleService.savePeople(people);
        return "redirect:/myprofile";
    }

    @GetMapping("/profile/{username}")
    public String userProfile(Model model, @PathVariable String username, HttpSession session) {
        People people = peopleService.findUser(username);
        model.addAttribute("people", people);
        if (session.getAttribute("username").equals(username)) {
            return "redirect:/myprofile";
        }
        return "userProfile";
    }

    @GetMapping("/editprofile")
    public String editProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        People getPeople = peopleRepository.findByUsername(username);
        model.addAttribute("people",getPeople);

        return "editProfile";
    }

    @PostMapping("/editprofile")
    public String saveEditProfile(HttpSession session, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String city, @RequestParam String presentation, @RequestParam String passcode) {
        String username = (String) session.getAttribute("username");
        People getPeople = peopleRepository.findByUsername(username);
        getPeople.setFirstName(firstName);
        getPeople.setLastName(lastName);
        getPeople.setCity(city);
        getPeople.setPresentation(presentation);
        getPeople.setPasscode(passcode);
        peopleService.savePeople(getPeople);
        return "redirect:/myprofile";
    }

    @GetMapping("/deleteMessage/{id}")
    public String doStuffMethod(@PathVariable Long id) {

        messageService.deleteMessage(id);
        return "redirect:/myprofile";
    }

    @GetMapping("/editMessage/{id}")
    public String edit(@PathVariable Long id, Model model,HttpSession session) {
        Message m = messageService.getMessageById(id);
        String username = peopleService.findUser((String)session.getAttribute("username")).getUsername();
        model.addAttribute("message", messageService.getMessageById(id));
        model.addAttribute("people", peopleService.findUser((String)session.getAttribute("username")));

        if(m.getSender().equals(username)){
            System.out.println("Success");
        }else{
            return "redirect:/myprofile";
        }


        return "editMessage";
    }

    @PostMapping("/editMessage")
    public String editMessage(@ModelAttribute Message message) {
        LocalDateTime time = LocalDateTime.now();
    //Message m = new Message(message.getId(), message.getMessageBody(),message.getSender(),time );
    message.setMessageBody(message.getMessageBody());
    messageService.saveMessage(message);
    return "redirect:/myprofile";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
