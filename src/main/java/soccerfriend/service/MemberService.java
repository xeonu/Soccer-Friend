package soccerfriend.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import soccerfriend.controller.MemberController.UpdatePasswordForm;
import soccerfriend.dto.Member;
import soccerfriend.mapper.MemberMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;

    /**
     * 회원가입을 수행합니다.
     *
     * @param member loginId, password, nickname 값을 포함하는 member 객체
     * @return 회원가입한 member의 id
     */
    public int signUp(Member member) {
        Member encryptedMember = Member.builder()
                                       .loginId(member.getLoginId())
                                       .password(BCrypt.hashpw(member.getPassword(), BCrypt.gensalt()))
                                       .nickname(member.getNickname())
                                       .point(0)
                                       .build();

        mapper.insert(encryptedMember);
        return encryptedMember.getId();
    }

    /**
     * 회원정보를 삭제합니다.
     *
     * @param loginId 삭제할 loginId
     */
    public void delete(String loginId) {
        mapper.delete(loginId);
    }

    /**
     * 해당 loginId를 사용중인 member가 있는지 확인합니다.
     *
     * @param loginId 존재 유무를 확인하려는 loginId
     * @return loginId 존재 유무(true: 있음, false: 없음)
     */
    public boolean isLoginIdExist(String loginId) {
        return mapper.isLoginIdExist(loginId);
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
     * @param loginId
     * @param password
     * @return
     */
    public Optional<Member> getMemberByLoginIdAndPassword(String loginId, String password) {

        if (!isLoginIdExist(loginId)) return Optional.empty();

        Optional<Member> member =
                Optional.ofNullable(mapper.getMemberByLoginId(loginId));

        if (BCrypt.checkpw(password, member.get()
                                           .getPassword())) {
            return member;
        }

        return Optional.empty();
    }

    /**
     * member에게 soccerInfo를 추가합니다.
     *
     * @param loginId      soccerInfo를 추가할 member의 loginId
     * @param soccerInfoId 추가하려는 soccerInfo의 id
     */
    public void setSoccerInfo(String loginId, int soccerInfoId) {
        mapper.setSoccerInfo(loginId, soccerInfoId);
    }

    /**
     * member의 nickname을 수정합니다.
     *
     * @param loginId  nickname을 수정하려는 member의 loginId
     * @param nickname 새로 수정하려는 nickname
     * @return nickname 변경 성공 여부
     */
    public boolean updateNickname(String loginId, String nickname) {
        if (isNicknameExist(nickname)) {
            return false;
        }
        mapper.updateNickname(loginId, nickname);
        return true;
    }

    /**
     * member의 password를 변경합니다.
     *
     * @param loginId      password를 수정하려는 member의 loginId
     * @param passwordForm before(현재 password), after(새로운 password)를 가지는 객체
     * @return password 변경 성공 여부
     */
    public boolean updatePassword(String loginId, UpdatePasswordForm passwordForm) {
        String before = passwordForm.getBefore();
        String after = passwordForm.getAfter();
        String encryptedCurrent = mapper.getMemberByLoginId(loginId).getPassword();

        if (BCrypt.checkpw(after, encryptedCurrent)) {
            return false;
        }

        after = BCrypt.hashpw(after, BCrypt.gensalt());
        mapper.updatePassword(loginId, after);
        return true;
    }
}
