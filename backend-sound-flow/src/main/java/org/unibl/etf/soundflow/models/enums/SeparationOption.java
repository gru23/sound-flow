package org.unibl.etf.soundflow.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum SeparationOption {
    FOUR_STEMS(""),
    VOCALS("--two-stems=vocals");

    private final String command;
}
