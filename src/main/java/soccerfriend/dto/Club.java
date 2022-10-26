package soccerfriend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club {

    private int id;

    private String name;

    private int leader;

    private int addressId;

    private int point;

    private int monthlyFee;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Club(String name, int addressId, int monthlyFee) {
        this.name = name;
        this.addressId = addressId;
        this.monthlyFee = monthlyFee;
    }
}
