package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class UserSummaryDto
{
    private Long id;
    private String name, surname, email;

    public UserSummaryDto(Long id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public UserSummaryDto() {

    }
}
