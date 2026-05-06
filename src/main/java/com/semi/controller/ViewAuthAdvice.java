package com.semi.controller;

import com.semi.security.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewAuthAdvice {

    @ModelAttribute("currentAdminName")
    public String currentAdminName() {
        MemberDetails memberDetails = getCurrentMemberDetails();
        return memberDetails != null ? memberDetails.getName() : "Admin";
    }

    @ModelAttribute("currentAdminEmail")
    public String currentAdminEmail() {
        MemberDetails memberDetails = getCurrentMemberDetails();
        return memberDetails != null ? memberDetails.getEmail() : "admin@dadream.com";
    }

    @ModelAttribute("currentAdminInitial")
    public String currentAdminInitial() {
        MemberDetails memberDetails = getCurrentMemberDetails();
        String source = memberDetails != null ? memberDetails.getName() : "A";
        return source != null && !source.isBlank()
                ? source.substring(0, 1).toUpperCase()
                : "A";
    }

    private MemberDetails getCurrentMemberDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof MemberDetails memberDetails) {
            return memberDetails;
        }

        return null;
    }
}
