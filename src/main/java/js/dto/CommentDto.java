package js.dto;

public record CommentDto(
        Integer postId,
        Integer id,
        String name,
        String email,
        String body
) {}