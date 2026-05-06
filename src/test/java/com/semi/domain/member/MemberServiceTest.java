package com.semi.domain.member;

import com.semi.domain.member.dto.MemberResponse;
import com.semi.domain.member.dto.RegisterRequest;
import com.semi.domain.order.PurchaseOrderRepository;
import com.semi.exception.DuplicateMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private MemberService memberService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "testuser",
                "password123",
                "test@example.com",
                "01012345678",
                "홍길동"
        );
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        given(memberRepository.existsByMemberId(anyString())).willReturn(false);
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        Member savedMember = Member.builder()
                .memberId("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .phone("01012345678")
                .name("홍길동")
                .build();
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        MemberResponse response = memberService.register(validRequest);

        // then
        assertThat(response.memberId()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.role()).isEqualTo(MemberRole.USER);
        then(passwordEncoder).should().encode("password123");
    }

    @Test
    @DisplayName("아이디 중복 시 예외 발생")
    void register_duplicateMemberId() {
        // given
        given(memberRepository.existsByMemberId("testuser")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(validRequest))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessage("이미 사용 중인 아이디입니다.");
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void register_duplicateEmail() {
        // given
        given(memberRepository.existsByMemberId(anyString())).willReturn(false);
        given(memberRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(validRequest))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }
}
