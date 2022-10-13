package soccerfriend.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import soccerfriend.controller.MemberController.UpdatePasswordRequest;
import soccerfriend.dto.Member;
import soccerfriend.exception.ExceptionCode;
import soccerfriend.exception.member.IdDuplicatedException;
import soccerfriend.exception.member.NicknameDuplicatedException;
import soccerfriend.exception.member.PasswordSameException;
import soccerfriend.mapper.MemberMapper;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static soccerfriend.exception.ExceptionCode.*;
import static soccerfriend.service.LoginService.LOGIN_MEMBER;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final HttpSession httpSession;
    private final MemberMapper mapper;

    /**
     * 회원가입을 수행합니다.
     *
     * @param member memberId, password, nickname, positionsId, addressId를 포함하는 member 객체
     * @return 회원가입한 member의 id
     */
    public int signUp(Member member) {
        if (isMemberIdExist(member.getMemberId())) {
            throw new IdDuplicatedException(ID_DUPLICATED);
        }
        if (isNicknameExist(member.getNickname())) {
            throw new NicknameDuplicatedException(NICKNAME_DUPLICATED);
        }
        Member encryptedMember = Member.builder()
                                       .memberId(member.getMemberId())
                                       .password(BCrypt.hashpw(member.getPassword(), BCrypt.gensalt()))
                                       .nickname(member.getNickname())
                                       .positionsId(member.getPositionsId())
                                       .addressId(member.getAddressId())
                                       .point(0)
                                       .build();

        mapper.insert(encryptedMember);
        return encryptedMember.getId();
    }

    /**
     * 회원정보를 삭제합니다.
     */
    public void delete() {
        String memberId = (String) httpSession.getAttribute(LOGIN_MEMBER);
        mapper.delete(memberId);
    }

    /**
     * 해당 loginId를 사용중인 member가 있는지 확인합니다.
     *
     * @param memberId 존재 유무를 확인하려는 memberId
     * @return memberId 존재 유무(true: 있음, false: 없음)
     */
    public boolean isMemberIdExist(String memberId) {
        return mapper.isMemberIdExist(memberId);
    }

    /**
     * 해당 nickname을 사용중인 member가 있는지 확인합니다.
     *
     * @param nickname 존재 유무를 확인하려는 nickname
     * @return nickname 존재 유무(true: 있음, false: 없음)
     */
    public boolean isNicknameExist(String nickname) {
        return mapper.isNicknameExist(nickname);
    }

    /**
     * loginId와 password를 입력받아 해당 member를 반환합니다.
     *
     * @param memberId
     * @param password
     * @return member의 Optional 객체
     */
    public Optional<Member> getMemberByLoginIdAndPassword(String memberId, String password) {

        if (!isMemberIdExist(memberId)) return Optional.empty();

        Optional<Member> member =
                Optional.ofNullable(mapper.getMemberByMemberId(memberId));

        if (BCrypt.checkpw(password, member.get().getPassword())) {
            return member;
        }

        return Optional.empty();
    }

    /**
     * member의 nickname을 수정합니다.
     *
     * @param nickname 새로 수정하려는 nickname
     */
    public void updateNickname(String nickname) {
        String memberId = (String) httpSession.getAttribute(LOGIN_MEMBER);
        if (isNicknameExist(nickname)) {
            throw new NicknameDuplicatedException(NICKNAME_DUPLICATED);
        }
        mapper.updateNickname(memberId, nickname);
    }

    /**
     * member의 password를 변경합니다.
     *
     * @param passwordForm before(현재 password), after(새로운 password)를 가지는 객체
     */
    public void updatePassword(UpdatePasswordRequest passwordForm) {
        String memberId = (String) httpSession.getAttribute(LOGIN_MEMBER);
        String before = passwordForm.getBefore();
        String after = passwordForm.getAfter();
        String encryptedCurrent = mapper.getMemberByMemberId(memberId).getPassword();

        if (BCrypt.checkpw(after, encryptedCurrent)) {
            throw new PasswordSameException(PASSWORD_SAME);
        }

        after = BCrypt.hashpw(after, BCrypt.gensalt());
        mapper.updatePassword(memberId, after);
    }
}
