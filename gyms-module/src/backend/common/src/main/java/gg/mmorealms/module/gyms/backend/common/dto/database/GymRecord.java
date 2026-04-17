package gg.mmorealms.module.gyms.backend.common.dto.database;


import java.io.Serializable;

public record GymRecord(Long wins, Long losses) implements Serializable {
}