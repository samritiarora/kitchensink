package com.mongo.challenge.kitchensink.controller;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.dto.MemberDto;
import com.mongo.challenge.kitchensink.service.IMemberService;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        try {
            memberService.saveMember(member);
        } catch (DuplicateKeyException exception) {
            String message = exception.getMessage();
            if (message.contains("email")) {
                result.rejectValue("email", "error.member", "Duplicate email");
            } else if (message.contains("phone")) {
                result.rejectValue("phoneNumber", "error.phoneNumber", "Duplicate phone");
            }
            return "signup";
        }
        return "redirect:/auth/login?signupSuccess";
    }

    // Admin: View all members
    @GetMapping("/members")
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.getMembers());
        model.addAttribute("member", new Member()); // Add an empty member object
        return "members";
    }

    // All Users: View their own details
    @GetMapping("/members/{id}")
    public String viewMember(@PathVariable String id, Model model) {
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        model.addAttribute("editMember", member);
        return "member"; // Template: member.html
    }

    // Admin: Add a new member
    @PostMapping("/members")
    public String addMember(@Valid @ModelAttribute("member") Member member, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Return the same form with errors
            return "members";
        }
        // Save the member
        try {
            memberService.saveMember(member);
        } catch (DuplicateKeyException exception) {
            String message = exception.getMessage();
            if (message.contains("email")) {
                result.rejectValue("email", "error.member", "Duplicate email");
            } else if (message.contains("phone")) {
                result.rejectValue("phoneNumber", "error.phoneNumber", "Duplicate phone");
            }
            return "members";
        }
        return "redirect:/members";
    }

    @PutMapping("/members/{email}")
    public String updateMember(@PathVariable String email,
                               @Valid @ModelAttribute("editMember") MemberDto editMember,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            // Handle validation errors and keep the modal open
            model.addAttribute("editMember", editMember);
            model.addAttribute("member", memberService.getMemberById(email)); // Ensure view details remain available
            model.addAttribute("showModal", true); // Add a flag to indicate the modal should be open
            return "member"; // Return the same view
        }
        editMember.setEmail(email);
        try {
            memberService.updateMember(editMember);
        } catch (DuplicateKeyException exception) {
            String message = exception.getMessage();
            if (message.contains("email")) {
                result.rejectValue("email", "error.member", "Duplicate email");
            } else if (message.contains("phoneNumber")) {
                result.rejectValue("phoneNumber", "error.phoneNumber", "Duplicate phone");
            }
            model.addAttribute("member", memberService.getMemberById(email)); // Ensure view details remain available
            model.addAttribute("showModal", true);
            return "member";
        }
        return "redirect:/members/" + email; // Redirect back to the member details page
    }


    @PreAuthorize("hasRole('ADMIN')") // Ensure only ADMIN can access
    @DeleteMapping("/members/{email}")
    public String deleteMember(@PathVariable String email, RedirectAttributes redirectAttributes) {
        memberService.deleteMember(email); // Assume deleteMember() handles member deletion
        redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully.");
        return "redirect:/members"; // Redirect to the members page
    }

}
