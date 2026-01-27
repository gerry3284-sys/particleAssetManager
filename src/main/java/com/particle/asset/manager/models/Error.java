package com.particle.asset.manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error
{
    private String errorCode, errorDescription;
}
