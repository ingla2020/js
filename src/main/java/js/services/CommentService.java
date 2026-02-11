package js.services;

import js.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final RestTemplate restTemplate;

    public List<CommentDto> getComments() {
        String url = "https://jsonplaceholder.typicode.com/comments";
        CommentDto[] comments = restTemplate.getForObject(url, CommentDto[].class);
        return comments != null ? Arrays.asList(comments) : List.of();
    }
}
