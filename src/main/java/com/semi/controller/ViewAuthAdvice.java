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
<<<<<<< HEAD
        return memberDetails != null ? memberDetails.getMember().getName() : "Admin";
=======
        return memberDetails != null ? memberDetails.getName() : "Admin";
>>>>>>> 06bd07ce57b7c275cfb7b67c399149dd1ff20276
    }

    @ModelAttribute("currentAdminEmail")
    public String currentAdminEmail() {
        MemberDetails memberDetails = getCurrentMemberDetails();
<<<<<<< HEAD
        return memberDetails != null ? memberDetails.getMember().getEmail() : "admin@dadream.com";
=======
        return memberDetails != null ? memberDetails.getEmail() : "admin@dadream.com";
>>>>>>> 06bd07ce57b7c275cfb7b67c399149dd1ff20276
    }

    @ModelAttribute("currentAdminInitial")
    public String currentAdminInitial() {
        MemberDetails memberDetails = getCurrentMemberDetails();
<<<<<<< HEAD
        String source = memberDetails != null ? memberDetails.getMember().getName() : "A";
=======
        String source = memberDetails != null ? memberDetails.getName() : "A";
>>>>>>> 06bd07ce57b7c275cfb7b67c399149dd1ff20276
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
