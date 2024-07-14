package study.datajpa.dto;

import lombok.Data;

@Data // +) Data 어노테이션은, 엔티티에는 웬만하면 쓰지 말아야 한다.
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
