package pposonggil.usedStuff.repository.transactioninformation.simplequery;

import lombok.Data;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.domain.TransactionInformation;

import java.time.LocalDate;

@Data
public class TransactionInformationSimpleQueryDto {
    private Long transactionInformationId;
    private Long memberId;
    private Long chatRoomId;
    private String memberNickName;
    private LocalDate startTime;
    private LocalDate endTime;
    private TransactionAddress address;

    public TransactionInformationSimpleQueryDto(TransactionInformation transactionInformation) {
        transactionInformationId = transactionInformation.getId();
        memberId = transactionInformation.getTransactionMember().getId();
        chatRoomId = transactionInformation.getTransactionChatRoom().getId();
        memberNickName = transactionInformation.getTransactionMember().getNickName();
        startTime = transactionInformation.getStartTime();
        endTime = transactionInformation.getEndTime();
        address = transactionInformation.getAddress();
    }
}
