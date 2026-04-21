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
            @PathVariable Long id,
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
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDetails deletedBy
    ) {
        // TODO: Implement member deletion logic
        // Need to check if member can be deleted (not admin, no active orders, etc.)
        return ResponseEntity.ok().build();
    }
}
