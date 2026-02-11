package js.dto;

public record PostDto(
        Integer userId,
        Integer id,
        String title,
        String body
) {}