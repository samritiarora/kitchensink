package com.mongo.challenge.kitchensink.controller;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.dto.MemberDto;
import com.mongo.challenge.kitchensink.service.IMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberWebControllerTest {

    @Mock
    private IMemberService memberService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private MemberWebController memberWebController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIndex() {
        String viewName = memberWebController.index();
        assertEquals("index", viewName);
    }

    @Test
    public void testLogin() {
        String viewName = memberWebController.login();
        assertEquals("login", viewName);
    }

    @Test
    public void testSignup() {
        String viewName = memberWebController.signup(model);
        assertEquals("signup", viewName);
        verify(model).addAttribute(eq("member"), any(Member.class));
    }

    @Test
    public void testProcessSignup_Success() {
        Member member = new Member();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = memberWebController.processSignup(member, bindingResult, model);

        assertEquals("redirect:/auth/login?signupSuccess", viewName);
        verify(memberService).saveMember(member);
    }

    @Test
    public void testProcessSignup_DuplicateEmail() {
        Member member = new Member();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateKeyException("email")).when(memberService).saveMember(any(Member.class));

        String viewName = memberWebController.processSignup(member, bindingResult, model);

        assertEquals("signup", viewName);
        verify(bindingResult).rejectValue(eq("email"), eq("error.member"), eq("Duplicate email"));
    }

    @Test
    public void testListMembers() {
        String viewName = memberWebController.listMembers(model);
        assertEquals("members", viewName);
        verify(model).addAttribute(eq("members"), anyList());
        verify(model).addAttribute(eq("member"), any(Member.class));
    }

    @Test
    public void testViewMember() {
        String memberId = "1";
        Member member = new Member();
        when(memberService.getMemberById(memberId)).thenReturn(member);

        String viewName = memberWebController.viewMember(memberId, model);

        assertEquals("member", viewName);
        verify(model).addAttribute("member", member);
        verify(model).addAttribute("editMember", member);
    }

    @Test
    public void testAddMember_Success() {
        Member member = new Member();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = memberWebController.addMember(member, bindingResult, model);

        assertEquals("redirect:/members", viewName);
        verify(memberService).saveMember(member);
    }

    @Test
    public void testAddMember_DuplicateEmail() {
        Member member = new Member();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateKeyException("email")).when(memberService).saveMember(any(Member.class));

        String viewName = memberWebController.addMember(member, bindingResult, model);

        assertEquals("members", viewName);
        verify(bindingResult).rejectValue(eq("email"), eq("error.member"), eq("Duplicate email"));
    }

    @Test
    public void testUpdateMember_Success() {
        String email = "test@example.com";
        MemberDto editMember = new MemberDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = memberWebController.updateMember(email, editMember, bindingResult, model);

        assertEquals("redirect:/members/" + email, viewName);
        verify(memberService).updateMember(editMember);
    }

    @Test
    public void testUpdateMember_ValidationErrors() {
        String email = "test@example.com";
        MemberDto editMember = new MemberDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = memberWebController.updateMember(email, editMember, bindingResult, model);

        assertEquals("member", viewName);
        verify(model).addAttribute("editMember", editMember);
        verify(model).addAttribute("showModal", true);
    }

    @Test
    public void testDeleteMember() {
        String email = "test@example.com";

        String viewName = memberWebController.deleteMember(email, redirectAttributes);

        assertEquals("redirect:/members", viewName);
        verify(memberService).deleteMember(email);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Member deleted successfully.");
    }
}