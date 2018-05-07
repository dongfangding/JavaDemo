package jdk.juc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DDf on 2018/5/7
 */
public class VoteTest {

    public static void main(String[] args) {
        VoteEntity voteEntity1 = new VoteEntity("丁东方", 0);
        VoteEntity voteEntity2 = new VoteEntity("尘", 0);
        List<VoteEntity> voteEntityList = new ArrayList<>();
        voteEntityList.add(voteEntity1);
        voteEntityList.add(voteEntity2);
        Vote vote = new Vote(voteEntityList);

        for (int i = 1; i <= 10 ; i ++) {
            new Thread().start();
        }
    }


}

class Vote {
    private List<VoteEntity> voteEntityList;

    public Vote(List<VoteEntity> voteEntityList) {
        this.voteEntityList = voteEntityList;
    }

    public synchronized void calcTicket(String name, Integer ticketNum) {
        voteEntityList.forEach(voteEntity -> {
            if (voteEntity.getName().equals(name)) {
                voteEntity.setTicketNum(voteEntity.getTicketNum() + ticketNum);
            }
        });
    }
}


class VoteEntity {
    private String name;
    private Integer ticketNum;

    public VoteEntity(String name, Integer ticketNum) {
        this.name = name;
        this.ticketNum = ticketNum;
    }

    public VoteEntity() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(Integer ticketNum) {
        this.ticketNum = ticketNum;
    }
}
