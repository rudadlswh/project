package com.example.crossfit.wod;

public record WodResponse(
        Long id,
        String date,
        String title,
        String type,
        String description
) {
    public static WodResponse from(Wod wod) {
        String safeType = wod.getType() == null ? "" : wod.getType();
        return new WodResponse(
                wod.getId(),
                wod.getWodDate().toString(),
                wod.getTitle(),
                safeType,
                wod.getDescription());
    }
}
