package com.study.springboot.dto;

import com.study.springboot.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MemberJoinDto {
    @Nullable // 회원가입시에는 없기에 null 허용
    private Long id;

    @Size(min = 1, max = 10, message = "user_id가 10글자를 넘으면 안됨")
    @NotBlank(message = "user_id에 null, 빈문자열, 스페이스문자열만을 넣을 수 없습니다.")
    private String user_id;

    @NotBlank(message = "user_pw에 null, 빈문자열, 스페이스문자열만을 넣을 수 없습니다.")
    private String user_pw;

    private String user_name;

    @NotBlank(message = "user_role에 null, 빈문자열, 스페이스문자열만을 넣을 수 없습니다.")
    private String user_role;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate joindate;

    public Member toSaveEntity() {
        return Member.builder()
                .user_id(user_id)
                .user_pw(user_pw)
                .user_name(user_name)
                .user_role(user_role)
                .build();
    }

    public Member toUpdateEntity() {

        return Member.builder()
                .id(id)
                .user_id(user_id)
                .user_pw(user_pw)
                .user_name(user_name)
                .user_role(user_role)
                .build();
    }

}
