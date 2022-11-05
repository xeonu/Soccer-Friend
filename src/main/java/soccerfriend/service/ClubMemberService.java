package soccerfriend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soccerfriend.dto.ClubMember;
import soccerfriend.exception.exception.BadRequestException;
import soccerfriend.exception.exception.DuplicatedException;
import soccerfriend.mapper.ClubMemberMapper;

import java.util.List;

import static soccerfriend.dto.ClubMember.ClubMemberGrade.*;
import static soccerfriend.exception.ExceptionInfo.*;

@Service
@RequiredArgsConstructor
public class ClubMemberService {

    private final ClubMemberMapper mapper;
    private final MemberService memberService;

    /**
     * clubMember를 추가합니다.
     *
     * @param clubId
     * @Param memberId
     */
    public void addLeader(int clubId, int memberId) {

        ClubMember clubMember = ClubMember.builder()
                                          .clubId(clubId)
                                          .memberId(memberId)
                                          .grade(LEADER)
                                          .approved(true)
                                          .build();

        mapper.insert(clubMember);
    }

    /**
     * clubMember를 추가합니다.
     *
     * @param clubId   가입하고자 하는 club의 id
     * @param memberId 클럽에 가입하고자하는 member
     */
    public void add(int clubId, int memberId) {

        if (isClubMember(clubId, memberId)) {
            throw new DuplicatedException(CLUB_MEMBER_DUPLICATED);
        }

        ClubMember newClubMember = ClubMember.builder()
                                             .clubId(clubId)
                                             .memberId(memberId)
                                             .grade(MEMBER)
                                             .approved(false)
                                             .build();
        mapper.insert(newClubMember);
    }

    /**
     * 해당 member가 해당 클럽의 Leade인지 확인합니다.
     *
     * @param clubId
     * @param memberId
     * @return club의 Leader 혹은 Staff인지 여부
     */
    public boolean isClubLeader(int clubId, int memberId) {
        return mapper.isClubLeader(clubId, memberId);
    }

    /**
     * 해당 member가 해당 클럽의 Leader 혹은 Staff인지 확인합니다.
     *
     * @param clubId
     * @param memberId
     * @return club의 Leader 혹은 Staff인지 여부
     */
    public boolean isClubLeaderOrStaff(int clubId, int memberId) {
        return mapper.isClubStaffOrLeader(clubId, memberId);
    }

    /**
     * 해당 member가 해당 클럽의 member인지 확인합니다.
     *
     * @param clubId
     * @param memberId
     * @return club의 member인지 여부
     */
    public boolean isClubMember(int clubId, int memberId) {
        return mapper.isClubMember(clubId, memberId);
    }

    /**
     * 해당 member가 해당 클럽에 신청했지만 아직 승인되지 않은 상태인지 확인합니다.
     *
     * @param clubId
     * @param memberId
     * @return club의 member인지 여부
     */
    public boolean isApplied(int clubId, int memberId) {
        return mapper.isApplied(clubId, memberId);
    }

    /**
     * 특정 id의 clubMember를 반환합니다.
     *
     * @param id clubMember의 id
     * @return 해당 id의 clubMember 객체
     */
    public ClubMember getClubMemberById(int id) {
        ClubMember clubMember = mapper.getClubMemberById(id);
        if(clubMember == null){
            throw new BadRequestException(CLUB_MEMBER_NOT_EXIST);
        }

        return clubMember;
    }

    /**
     * 승인된 clubMember의 목록을 반환합니다.
     *
     * @param clubId
     * @return 승인된 clubMember의 목록
     */
    public List<ClubMember> getClubMembers(int clubId) {
        List<ClubMember> clubMembers = mapper.getClubMembers(clubId);
        if(clubMembers.isEmpty()){
            throw new BadRequestException(CLUB_MEMBER_NOT_EXIST);
        }

        return clubMembers;
    }

    /**
     * 해당 club에 신청했지만 아직 승인이 안된 clubMember의 목록을 반환합니다.
     *
     * @param clubId
     * @return 신청했지만 아직 승인이 안된 clubMember의 목록
     */
    public List<ClubMember> getNotAcceptedClubMembers(int clubId) {
        List<ClubMember> notApprovedClubMembers = mapper.getNotApprovedClubMembers(clubId);
        if(notApprovedClubMembers.isEmpty()){
            throw new BadRequestException(CLUB_MEMBER_NOT_EXIST);
        }

        return notApprovedClubMembers;
    }

    /**
     * club에 신청한 member를 승인합니다.
     *
     * @param id clubMember의 id
     */
    public void approve(int id) {

        mapper.setApprovedTrue(id);
    }

    public void deleteClubMember(int clubId, int memberId){
        mapper.delete(clubId, memberId);
    }
}
