package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class UserSummaryDto
{
    private String oid, name, surname, email;

    public UserSummaryDto(String oid, String name, String surname, String email) {
        this.oid = oid;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public UserSummaryDto() {

    }
}
