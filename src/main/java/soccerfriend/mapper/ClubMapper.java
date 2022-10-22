package soccerfriend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import soccerfriend.dto.Club;
import soccerfriend.dto.StadiumOwner;
import soccerfriend.utility.InputForm.UpdateStadiumOwnerRequest;

@Mapper
public interface ClubMapper {

    public void insert(Club club);

    public Club getClubByName(String name);

    public boolean isNameExist(String name);

    public boolean isIdExist(int id);
}
