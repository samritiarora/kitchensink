package com.mongo.challenge.kitchensink.controller;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.service.IMemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberWebController {

    private final IMemberService memberService;

    public MemberWebController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("/auth/signup")
    public String signup(Model model) {
        model.addAttribute("member", new Member());
        return "signup";
    }

    @PostMapping("/auth/signup")
    public String processSignup(@Valid @ModelAttribute("member") Member member, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        member.setRoles(List.of("ROLE_USER"));
        memberService.saveMember(member);
        return "redirect:/auth/login?signupSuccess";
    }

    // Admin: View all members
    @GetMapping("/members")
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.getMembers());
        return "members";
    }

    // All Users: View their own details
    @GetMapping("/members/{id}")
    public String viewMember(@PathVariable String id, Model model) {
        model.addAttribute("member", memberService.getMemberById(id));
        return "member"; // Template: member.html
    }

    // Admin: Add a new member
    @PostMapping("/members")
    public String addMember(@ModelAttribute Member member) {
        member.setPassword("adminsetpassword");
        memberService.saveMember(member);
        return "redirect:/members"; // Redirect to the list of members
    }
}
