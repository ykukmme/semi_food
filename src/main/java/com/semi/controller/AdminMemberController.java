package com.semi.controller;

import com.semi.domain.member.MemberService;
import com.semi.domain.member.dto.MemberResponse;
import com.semi.domain.member.dto.UpdateRoleRequest;
import com.semi.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> updateRole(
<<<<<<< HEAD
            @PathVariable("id") Long id,
=======
            @PathVariable Long id,
>>>>>>> 06bd07ce57b7c275cfb7b67c399149dd1ff20276
            @Valid @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal MemberDetails changedBy
    ) {
        MemberResponse response = memberService.updateRole(
                id, 
                request.role(), 
                changedBy.getMember().getMemberId()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(
<<<<<<< HEAD
            @PathVariable("id") Long id,
=======
            @PathVariable Long id,
>>>>>>> 06bd07ce57b7c275cfb7b67c399149dd1ff20276
            @AuthenticationPrincipal MemberDetails deletedBy
    ) {
        memberService.deleteMember(id, deletedBy.getMember().getMemberId());
        return ResponseEntity.ok().build();
    }
}
