package com.client.profiler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionType {
    POLL(1),
    REVIEW(3),
    BURN(10),
    RAFFLE(1),
    WIN_RAFFLE(5),
    VAULT(1),
    ADD_REVIEW(1);

    private final Integer value;
}